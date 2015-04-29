package edu.pitt.dbmi.deep.phe.deid;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import edu.pitt.dbmi.deep.phe.data.model.*;
import edu.pitt.dbmi.deep.phe.util.TextUtils;

public class DeIDReportSplitter {

	private String getHeaderField(String header,String field){
		Pattern pt = Pattern.compile(field+"\\.{2,}(.+)");
		Matcher mt = pt.matcher(header);
		if(mt.find()){
			return mt.group(1);
		}
		return "";
	}
	
	/**
	 * split document into multiple files
	 * @param fs
	 * @param fd
	 */
	public void split(File fs, File fd) throws IOException {
		Map<String,Patient> list = new HashMap<String,Patient>();
		BufferedReader r = new BufferedReader(new FileReader(fs));
		StringBuffer report = null;
		Report currentReport = null;
		String currentPatient = null;
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
					currentPatient = getHeaderField(report.toString(),"Patient ID");
					currentReport.setName(getHeaderField(report.toString(),"Patient Name"));
					currentReport.setEventDate(TextUtils.parseDateString(getHeaderField(report.toString(),"Principal Date")));
					currentReport.setDocumentType(getHeaderField(report.toString(),"Record Type"));
				}
			}else if(l.equals("E_O_R")){
				currentReport.setText(report.toString());
				
				Patient patient = list.get(currentPatient);
				if(patient == null){
					patient = currentReport.getPatient();
					list.put(currentPatient,patient);
				}
				patient.addReport(currentReport);
				
				// done with a report
				report = null;
				currentReport = null;
				currentPatient = null;
			}
			
			// add to report
			if(report != null){
				report.append(l+"\n");
			}
			
		}
		r.close();
		
		
		// create directory
		if(!fd.exists())
			fd.mkdirs();
		
		int n = 1;
		for(Patient p: list.values()){
			p.sortReports();
			// create patient directory
			File pf = new File(fd,String.format("patient%02d",n));
			if(!pf.exists())
				pf.mkdirs();
			// now save all of the reports
			int rn = 1;
			for(Report rp: p.getReports()){
				File rf = new File(pf,String.format("patient%02d_report%03d_%s.txt",n,rn,rp.getDocumentType()));
				System.out.println(rf.getName()+"..");
				BufferedWriter w = new BufferedWriter(new FileWriter(rf));
				w.write(rp.getText()+"\n");
				w.close();
				rn ++;
			}
			n ++;
		}
	}
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String type = "Breast";
		String ver = "scrubbed";
		//File fs = new File("/home/tseytlin/Data/DeepPhe/"+type+"/"+type.toLowerCase()+"_patient_sample.scrubbed");
		//File fd = new File("/home/tseytlin/Data/DeepPhe/"+type+"/"+type.toLowerCase()+"_patient_sample_scrubbed");
		
//		File fs = new File("/home/tseytlin/Data/DeepPhe/"+type+"/sample/"+type.toLowerCase()+"_12doc_sample."+ver);
//		File fd = new File("/home/tseytlin/Data/DeepPhe/"+type+"/sample/"+type.toLowerCase()+"_12doc_sample_"+ver);
		
/*		File fs = new File("/home/tseytlin/Data/DeepPhe/"+type+"/"+type.toLowerCase()+"_new_sample.bar");
		File fd = new File("/home/tseytlin/Data/DeepPhe/"+type+"/"+type.toLowerCase()+"_new_sample_id");*/
		
		File fs = new File("/home/tseytlin/Data/DeepPhe/Samples/CARe_Sample_Apr-2015/"+type+"/"+type.toLowerCase()+"_sample_filtered_addendum."+ver);
		File fd = new File("/home/tseytlin/Data/DeepPhe/Samples/CARe_Sample_Apr-2015/"+type+"/"+type.toLowerCase()+"_sample_filtered_addendum_"+ver);
		
		DeIDReportSplitter splitter = new DeIDReportSplitter();
		splitter.split(fs,fd);
	}

	

}
