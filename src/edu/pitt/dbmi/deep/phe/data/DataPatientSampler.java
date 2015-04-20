package edu.pitt.dbmi.deep.phe.data;

import java.io.*;
import java.util.*;

import edu.pitt.dbmi.deep.phe.data.model.*;
import edu.pitt.dbmi.deep.phe.util.TextUtils;
import edu.pitt.dbmi.nlp.noble.tools.TextTools;

public class DataPatientSampler {
	public static int MIN_NUM_DOCS = 18; //8
	public static int MAX_NUM_DOCS = 1000; //30
	public static int DOCS_AFTER_CA = 5;
	public static int TIME_AFTER_CA = 6; //month
	
	private Map<String,Patient> patients = new HashMap<String, Patient>();
	private List<Patient> filteredPatients;
	private List<Filter> filters;
	private interface Filter {
		/**
		 * @param p patient to look at
		 * @return true if it passes a filter
		 */
		public boolean filter(Patient p);
	}
	
	
	/**
	 * Load dataset of BAR dataset
	 * @param dataFile
	 * @throws IOException 
	 */
	public Map<String,Patient> loadBARDataset(File dataFile) throws IOException {
		Map<String,Patient> results = new HashMap<String, Patient>();
		
		BufferedReader r = new BufferedReader(new FileReader(dataFile));
		boolean inReport = false;
		StringBuffer b = new StringBuffer();
		for(String l=r.readLine();l != null;l=r.readLine()){
			l = l.trim();
			if("S_O_H".equals(l)){
				inReport = true;
				b.append(l+"\n");
			}else if("E_O_R".equals(l)){
				b.append(l+"\n");
				Report rp = Report.readBARformat(b.toString());
				Patient p = results.get(rp.getPatient().getMedicalRecordNumber());
				if(p == null){
					p = rp.getPatient();
					results.put(p.getMedicalRecordNumber(),p);
				}
				p.addReport(rp);
				// reset
				inReport = false;
				b = new StringBuffer();
			}else if(inReport){
				b.append(l+"\n");
			}
			
		}
		r.close();
		return results;
	}
	
	
	/**
	 * Load dataset of BAR dataset
	 * @param dataFile
	 * @throws IOException 
	 */
	public Map<String,Patient> loadDelimitedDataset(File dataFile) throws IOException {
		Map<String,Patient> results = new HashMap<String, Patient>();
		
		BufferedReader r = new BufferedReader(new FileReader(dataFile));
		List<String> names = null;
		for(String l=r.readLine();l != null;l=r.readLine()){
			l = l.trim();
			if(names == null){
				names =  TextTools.parseCSVline(l,'|');
			}else{
				List<String> values = TextTools.parseCSVline(l,'|');
				Map<String,String> rmap = new LinkedHashMap<String, String>();
				for(int i=0;i<values.size();i++){
					String key = names.get(i);
					String val = values.get(i).trim();
					rmap.put(key,val);
				}
				
				Report rp = Report.readMAPformat(rmap);
				Patient p = results.get(rp.getPatient().getMedicalRecordNumber());
				if(p == null){
					p = rp.getPatient();
					results.put(p.getMedicalRecordNumber(),p);
				}
				p.addReport(rp);
			}
		}
		r.close();
		return results;
	}
	
	
	public List<Filter> getFilters() {
		if(filters == null){
			filters = new ArrayList<DataPatientSampler.Filter>();
			// check if report number is in good report
			filters.add(new Filter(){
				public boolean filter(Patient p) {
					int n = p.getReportCounts(Arrays.asList("PGN","SP","RAD","DS"));
					return MIN_NUM_DOCS < n && n < MAX_NUM_DOCS ;
				}
			});
			
			// check 5 docs after cancer
			filters.add(new Filter(){
				public boolean filter(Patient p) {
					int n = 0;
					for(Report r: p.getReports()){
						if(r.getEventDate() != null && p.getCancerDate() != null && r.getEventDate().after(p.getCancerDate())){
							n++;
						}
					}
					return n >= DOCS_AFTER_CA;
				}
			});
			// check if last report at least 6 month after cancer
			filters.add(new Filter(){
				public boolean filter(Patient p) {
					Date last = p.getReports().get(p.getReportCount()-1).getEventDate();
					if(last == null || p.getCancerDate() == null)
						return false;
					long time = last.getTime()-p.getCancerDate().getTime();
					return (time/(1000*60*60*24*30.5)) >= TIME_AFTER_CA;
				}
			});
			// check if first report is before cancer
			filters.add(new Filter(){
				public boolean filter(Patient p) {
					Date first = p.getReports().get(0).getEventDate();
					return first != null && first.before(p.getCancerDate());
				}
			});
			
			// do we have one PGN or DS???
			filters.add(new Filter(){
				public boolean filter(Patient p) {
					return p.getReportCounts().containsKey("PGN") || p.getReportCounts().containsKey("DS");
				}
			});
		}
		return filters;
	}

	/**
	 * Is this PatientElement kosher for our selection
	 * @param p
	 * @return
	 */
	public boolean filter(Patient p){
		for(Filter f: getFilters()){
			if(!f.filter(p))
				return false;
		}
		return true;
	}
	

	public List<Patient> getPatients(){
		return new ArrayList<Patient>(patients.values());
	}
	
	public List<Patient> getFilteredPatients(){
		if(filteredPatients == null){
			filteredPatients = new ArrayList<Patient>();
			for(Patient p : patients.values()){
				if(filter(p))
					filteredPatients.add(p);
			}
		}
		return filteredPatients;
	}
	
	/**
	 * get rando  patient sample
	 * @param pat
	 * @param size
	 * @return
	 */
	public List<Patient> getPatientSample(List<Patient> pat, int size){
		if(pat.size() <= size)
			return pat;
		
		List<Patient> filtered = new ArrayList<Patient>();
		while(filtered.size() < size){
			int x = (int)(Math.random()*pat.size());
			Patient p = pat.get(x);
			if(!filtered.contains(p)){
				filtered.add(p);
			}
		}
		return filtered;
	}
	
	
	
	/**
	 * Load dataset of BAR dataset
	 * @param dataFile
	 * @throws IOException 
	 */
	public void loadSummaryDataset(File dataFile) throws IOException {
		Map<String,Patient> results = new HashMap<String, Patient>();
		
		BufferedReader r = new BufferedReader(new FileReader(dataFile));
		for(String l=r.readLine();l != null;l=r.readLine()){
			Report rp = Report.readSummaryformat(l.trim());
			if(rp != null){
				Patient p = results.get(rp.getPatient().getMedicalRecordNumber());
				if(p == null){
					p = rp.getPatient();
					results.put(p.getMedicalRecordNumber(),p);
				}
				p.addReport(rp);
			}
			
		}
		r.close();
		patients = results;
	}

	
	/**
	 * Load dataset of BAR dataset
	 * @param dataFile
	 * @throws IOException 
	 */
	public void loadCancerDates(File dataFile) throws IOException {
		BufferedReader r = new BufferedReader(new FileReader(dataFile));
		for(String l=r.readLine();l != null;l=r.readLine()){
			String [] p = l.trim().split(",");
			if(p.length > 1){
				String mrn = p[0].trim();
				String dt = p[1].trim();
				Patient pt = patients.get(mrn);
				if(pt != null && pt.getCancerDate() == null){
					pt.setCancerDate(TextUtils.parseDateString(dt));
				}
			}
		}
		r.close();
	}
	
	public void sortReports(){
		for(Patient p: patients.values()){
			p.sortReports();
		}
	}
	
	public void save(List<Patient> pt, String outfile) throws Exception {
		BufferedWriter w = new BufferedWriter(new FileWriter(outfile));
		for(Patient p: pt){
			w.write(p.getMedicalRecordNumber()+"\n");
		}
		w.close();
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		//String infile = "/home/tseytlin/Data/MARS/caties_summary.bar";
		// Ovarian/54, Breast/45, Melanoma/34
		String domain = "Ovarian";  
		//DataPatientSampler.MAX_NUM_DOCS = 34; 
		
		String infile = "/home/tseytlin/Data/DeepPhe/Data/"+domain+"/"+domain.toLowerCase()+"_cases_summary.txt";
		//String infile2 = "/home/tseytlin/Data/DeepPhe/Data/"+domain+"/"+domain.toLowerCase()+"_patients+dates.csv";
		String infile2 = "/home/tseytlin/Data/DeepPhe/Samples/Sample-Jan-2015/"+domain+"/"+domain.toLowerCase()+"_patients+dates.csv";
		String outfile = "/home/tseytlin/Data/DeepPhe/Samples/Sample-Jan-2015/"+domain+"/"+domain.toLowerCase()+"_patient_sample.who";
		
		DataPatientSampler ds = new DataPatientSampler();
	
		System.out.println("loading dataset .. "+infile);
		
		// load patient/report data
		ds.loadSummaryDataset(new File(infile));
		ds.loadCancerDates(new File(infile2));
		ds.sortReports();
		
		// now do filtering & sampling
		List<Patient> patients = ds.getPatientSample(ds.getFilteredPatients(),50);
		
		// lets look at them 
		System.out.println("..");
		for(Patient p: patients){
			System.out.println(p.getMedicalRecordNumber()+" -> "+p.getReports().size()+" reports"+" .. "+p.getCancerDate()+" | "+p.getReports().get(0).getEventDate() +" .. "+p.getReports().get(p.getReportCount()-1).getEventDate());
		}
		System.out.println("..");
		
		// just save the MRNs for extraction
		ds.save(patients,outfile);
	}

}
