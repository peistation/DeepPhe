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

	
	/**
	 * Load dataset of BAR dataset
	 * @param dataFile
	 * @throws IOException 
	 */
	public static void convert(File input, File output) throws IOException {
		Map<String,String> facilites = new HashMap<String, String>();
		facilites.put("29","MWH");
		List<String> titles = new ArrayList<String>();
		BufferedWriter w = new BufferedWriter(new FileWriter(output));
		BufferedReader r = new BufferedReader(new FileReader(input));
		for(String l=r.readLine();l != null;l=r.readLine()){
			String [] p = l.trim().split(",");
			if(p.length > 1){
				if(titles.isEmpty()){
					for(String t: p){
						titles.add(t.trim());
					}
				}else{
					String mrn = p[4]+facilites.get(p[0]);
					for(int i=7;i<17;i++){
						if(p[i].length()>0)
							w.write(mrn+","+convertDate(p[i])+","+titles.get(i)+"\n");
					}
					// add date of death
					if("Expired".equals(p[p.length-1])){
						w.write(mrn+","+convertDate(p[p.length-2])+",Date of Death\n");
					}
				}
			}
		}
		r.close();
		w.close();
	}
	
	
	private static String convertDate(String dt) {
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
		File in  = new File("/home/tseytlin/Data/DeepPhe/Sample Data w Add_Dates.csv");
		File out = new File("/home/tseytlin/Data/DeepPhe/patients+dates.csv");
		System.out.print("converting "+in.getAbsolutePath()+" ..");
		convert(in,out);
		System.out.println("done");
	}

}
