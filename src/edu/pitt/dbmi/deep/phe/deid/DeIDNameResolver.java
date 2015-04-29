package edu.pitt.dbmi.deep.phe.deid;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import scala.Array;
import edu.pitt.dbmi.deep.phe.data.model.Patient;
import edu.pitt.dbmi.deep.phe.data.model.Report;
import edu.pitt.dbmi.deep.phe.util.TextUtils;

/**
 * Resolve Real Name - Fake name link 
 * @author tseytlin
 *
 */
public class DeIDNameResolver {
	/**
	 * get a list of mappings between two already processed files
	 * Assumes that reports are in the same sequence
	 * @param identified
	 * @param scrubbed
	 * @return
	 * @throws IOException 
	 */
	public List<Map<String,String>> resolve(File identified, File scrubbed) throws IOException{
		List<Report> identified_list = loadBARDataset(identified);
		List<Report> deidentified_list = loadDeIDDataset(scrubbed);
		List<Map<String,String>> link = new ArrayList<Map<String,String>>();
		
		if(identified_list.size() != deidentified_list.size()){
			System.err.println("Error: WTF? The lists should align.");
			return null;
		}
		// go over a list
		for(int i=0;i<identified_list.size();i++){
			Report r1 = identified_list.get(i);
			Report r2 = deidentified_list.get(i);
			Map<String,String> map = new LinkedHashMap<String,String>();
			map.put("Name",r1.getName());
			map.put("MRN",r1.getMedicalRecordNumber());
			map.put("Fake Name",r2.getName());
			map.put("DeID",r2.getMedicalRecordNumber());
			map.put("DeID Report",r2.getRecordId());
			link.add(map);
		}
		
		return link;
	}

	/**
	 * for each entry find a corresponding file in the split directory structure
	 * @param result
	 * @param fd
	 * @throws Exception 
	 */
	private void resolve(List<Map<String, String>> result, File fd) throws Exception {
		Map<String,File> mapping = new LinkedHashMap<String,File>();
		for(File d: fd.listFiles()){
			if(d.isDirectory()){
				for(File f: d.listFiles()){
					String id = getReportID(f);
					mapping.put(id,f);
				}
			}
		}
		// now do the cross-referencing
		for(Map<String,String> map: result){
			String id = map.get("DeID Report");
			File f = mapping.get(id);
			map.put("Dir",f.getParentFile().getName());
			map.put("File",f.getName());
		}
		
	}

	/**
	 * save link file
	 * @throws IOException 
	 */
	public void saveLink(List<Map<String,String>> link, File out ) throws IOException{
		char S = '\t';
		if(link.isEmpty())
			return;
		
		BufferedWriter w = new BufferedWriter(new FileWriter(out));
		for(Map<String,String> map: link){
			for(String key: map.keySet()){
				w.write(map.get(key)+S);
			}
			w.write("\n");
		}
		w.close();
	}
	
	
	/**
	 * Load dataset of BAR dataset
	 * @param dataFile
	 * @throws IOException 
	 */
	public List<Report> loadBARDataset(File dataFile) throws IOException {
		List<Report>results = new ArrayList<Report>();
		
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
				results.add(rp);
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
	private String getHeaderField(String header,String field){
		Pattern pt = Pattern.compile(field+"\\.{2,}(.+)");
		Matcher mt = pt.matcher(header);
		if(mt.find()){
			return mt.group(1);
		}
		return "";
	}
	
	
	private String getReportID(File f) throws Exception {
		String id = null;
		BufferedReader r = new BufferedReader(new FileReader(f));
		StringBuffer report = null;
		for(String l=r.readLine(); l != null; l = r.readLine()){
			if(l.matches("={4,}")){
				// start of header
				if(report == null){
					report = new StringBuffer();
				// end of header	
				}else {
					// parse existing header
					id = getHeaderField(report.toString(),"Report ID");
				}
			}else if(l.equals("E_O_R")){
				break;
			}
			
			// add to report
			if(report != null){
				report.append(l+"\n");
			}
			
		}
		r.close();
		
		return id;
	}
	
	/**
	 * load dataset processed by DeID
	 * @param deid
	 * @return
	 * @throws IOException
	 */
	public  List<Report> loadDeIDDataset(File deid) throws IOException {
		List<Report> result = new ArrayList<Report>();
		
		BufferedReader r = new BufferedReader(new FileReader(deid));
		StringBuffer report = null;
		Report currentReport = null;
		for(String l=r.readLine(); l != null; l = r.readLine()){
			if(l.matches("={4,}")){
				// start of header
				if(report == null){
					report = new StringBuffer();
					currentReport = new Report();
				// end of header	
				}else {
					// parse existing header
					/*
					Report ID.....................2,HuUnE+zzpudA
					Patient ID....................HuUnE+zzpudA
					Patient Name..................**NAME[AAA BBB M]
					Principal Date................20060223 1325
					Record Subtype................PVS06-2730
					Record Type...................SP
					*/
					currentReport.setRecordId(getHeaderField(report.toString(),"Report ID"));
					currentReport.setMedicalRecordNumber(getHeaderField(report.toString(),"Patient ID"));
					currentReport.setName(getHeaderField(report.toString(),"Patient Name"));
					currentReport.setEventDate(TextUtils.parseDateString(getHeaderField(report.toString(),"Principal Date")));
					currentReport.setDocumentType(getHeaderField(report.toString(),"Record Type"));
				}
			}else if(l.equals("E_O_R")){
				currentReport.setText(report.toString());
				
				result.add(currentReport);
				
				// done with a report
				report = null;
				currentReport = null;
			}
			
			// add to report
			if(report != null){
				report.append(l+"\n");
			}
			
		}
		r.close();
		
		
		return result;
	}
	
	
	public static void main(String[] args) throws Exception{
		String type = "Melanoma";
		String sample = "CARe_Sample_Apr-2015";//"Report_Filter_Sample (Dec 2014)"; //"Sample-Jan-2015"; //"CARe_Sample_Apr-2015";
		File fr = new File("/home/tseytlin/Data/DeepPhe/Samples/"+sample+"/"+type+"/"+type.toLowerCase()+"_sample_filtered_addendum.bar");
		File ff = new File("/home/tseytlin/Data/DeepPhe/Samples/"+sample+"/"+type+"/"+type.toLowerCase()+"_sample_filtered_addendum.scrubbed");
		File fd = new File("/home/tseytlin/Data/DeepPhe/Samples/"+sample+"/"+type+"/"+type.toLowerCase()+"_sample_filtered_addendum_scrubbed");
		File fo = new File("/home/tseytlin/Data/DeepPhe/Samples/"+sample+"/"+type+"/"+type.toLowerCase()+"_sample_filtered_addendum.link");

		DeIDNameResolver resolver = new DeIDNameResolver();
		System.out.println("reading in data files .. ");
		List<Map<String,String>> result = resolver.resolve(fr, ff);
		System.out.println("resolve with  data directory .. ");
		resolver.resolve(result,fd);
		
		
		System.out.println("saving a link file .. ");
		resolver.saveLink(result, fo);
	}

	
}
