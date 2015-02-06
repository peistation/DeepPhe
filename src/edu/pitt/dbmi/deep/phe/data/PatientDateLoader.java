package edu.pitt.dbmi.deep.phe.data;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * convert a CSV file with 1 row per subject, into  
 * @author tseytlin
 */
public class PatientDateLoader {
	private static Map<String,String> facilites = new HashMap<String, String>();
	static{
		// hard-coded values from cancer registry
		/*
		 *  1	St. Margaret	SMH
			5	Passavant	PAS
			7	Presby	PUH
			11	Shadyside	SHY
			24	McKeesport	MCH
			27	Horizon	HHS
			29	Magee	MWH
			62	Northwest	NWH
		 */
		facilites.put("1","SMH");
		facilites.put("5","PAS");
		facilites.put("7","PUH");
		facilites.put("11","SHY");
		facilites.put("24","MCH");
		facilites.put("27","HHS");
		facilites.put("29","MWH");
		facilites.put("62","NWH");
	}
	
	/**
	 * save dates to the writer
	 * @param w
	 * @param lastMRN
	 * @param dates
	 * @throws IOException
	 */
	private static void saveDates(Writer w, String lastMRN, Map<String,Set<String>> dates) throws IOException{
		for(String key: dates.keySet()){
			Set<String> val = dates.get(key);
			if(val.size() == 1){
				w.write(lastMRN+","+convertDate(val.iterator().next())+","+key+"\n");
			}else{
				int n = 1;
				for(String d: val){
					w.write(lastMRN+","+convertDate(d)+","+key+" "+(n++)+"\n");
				}
			}
		}
	}
	
	/**
	 * Load dataset of BAR dataset
	 * @param dataFile
	 * @throws IOException 
	 */
	public static void convert(File input, File output) throws IOException {
		String SEP = ",";
		if(input.getName().endsWith(".tsv"))
			SEP = "\t";
		List<String> titles = new ArrayList<String>();
		BufferedWriter w = new BufferedWriter(new FileWriter(output));
		BufferedReader r = new BufferedReader(new FileReader(input));
		String lastMRN = null;
		Map<String,Set<String>> dates = new LinkedHashMap<String,Set<String>>();
		for(String l=r.readLine();l != null;l=r.readLine()){
			String [] p = l.trim().split(SEP);
			if(p.length > 18){
				if(titles.isEmpty()){
					for(String t: p){
						titles.add(t.trim());
					}
				}else{
					String mrn = p[4].trim()+facilites.get(p[0].trim());
					// write previous MRN if we have new MRN
					if(lastMRN != null && !mrn.equals(lastMRN)){
						saveDates(w,lastMRN,dates);
						dates.clear();
					}
					// save anything that looks like a date from rows 7 : 17			
					for(int i=7;i<18;i++){
						String t = titles.get(i);
						String d = p[i].trim();
						// if date matches date format ...
						if(d.matches("\\d{6,8}") && !d.matches("9{6,8}")){
							Set<String> v = dates.get(t);
							if(v == null)
								v = new LinkedHashSet<String>();
							v.add(d);
							dates.put(t,v);
						}
					}
					// add date of death, if patient died (value of the last column)
					String status = p[p.length-1].trim().toLowerCase();
					if(Arrays.asList("dead","expired").contains(status)){
						String d = p[p.length-2].trim();
						if(d.matches("\\d{6,8}") && !d.matches("9{6,8}"))
							dates.put("Date of Death",Collections.singleton(d));
					}
					lastMRN = mrn;
				}
			}
		}
		if(lastMRN != null && !dates.isEmpty())
			saveDates(w,lastMRN,dates);
		r.close();
		w.close();
	}
	
	
	private static String convertDate(String dt) {
		dt = dt.trim();
		Pattern pt = Pattern.compile("(\\d+)/(\\d+)/(\\d+)");
		Matcher mt = pt.matcher(dt);
		if(mt.matches()){
			String m = mt.group(1);
			String d = mt.group(2);
			String y = mt.group(3);
			if(y.length()==2)
				y = "20"+y;
			if(m.length() == 1)
				m = "0"+m;
			if(d.length() == 1)
				d = "0"+d;
			return y+m+d;
		}
		return dt;
	}


	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		File dir = new File("/home/tseytlin/Data/DeepPhe/Samples/Sample-Jan-2015/Breast/");
		File in  = new File(dir,"breast.tsv");
		File out = new File(dir,"breast_patients+dates.csv");
		System.out.print("converting "+in.getAbsolutePath()+" ..");
		convert(in,out);
		System.out.println("done");
	}

}
