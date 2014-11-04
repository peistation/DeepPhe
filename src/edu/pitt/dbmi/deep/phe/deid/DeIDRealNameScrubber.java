package edu.pitt.dbmi.deep.phe.deid;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DeIDRealNameScrubber {
	private List<List<String>> realNames;
	private int currentReport;
	private static List<String> falseNames = Arrays.asList("physician","white","gray","brown");
	
	/**
	 * load real names into a data structure
	 * @param file
	 * @throws Exception
	 */
	public void loadRealNames(File file) throws Exception {
		realNames = new ArrayList<List<String>>();
		BufferedReader r = new BufferedReader(new FileReader(file));
		StringBuffer header = null;
		for(String l=r.readLine(); l!= null;l=r.readLine()){
			l = l.trim();
			if("S_O_H".equals(l)){
				header = new StringBuffer();
			}else if("E_O_H".equals(l)){
				String p [] = header.toString().split("\\|");
				List<String> list = new ArrayList<String>();
				for(int x : Arrays.asList(2,6,40,47,48,51,63,64,65,66,67)){
					if(x < p.length && p[x].trim().length() > 0){
						String nm = p[x].trim();
						for(String n: nm.split(" ")){
							n = n.trim().toLowerCase();
							if(n.length() > 2 && !n.matches("\\d+") && !falseNames.contains(n)){
								list.add(n);
							}
						}
					}
				}
				realNames.add(list);
				header = null;
			}else if(header != null){
				header.append(l);
			}
		}
		r.close();
	}
	
	private String scrub(String l) {
		String ll = l.toLowerCase();
		// check real names
		List<String> names = realNames.get(currentReport);
		for(String n: names){
			if(ll.indexOf(n) > -1){
				l = l.replaceAll("\\b"+n+"\\b"," ");
				if(ll.length() != l.length()){
					System.out.println("Warning: real name found -> "+n);
				}
			}
		}
		// check known DeID fuck-ups
		Pattern p = Pattern.compile("\\*\\*NAME\\[[A-Z]+\\]\\s{2,5}([A-Z][a-z]+)");
		Matcher m = p.matcher(l);
		if(m.find()){
			String n = m.group(1);
			l = l.replaceAll(n," ");
			if(ll.length() != l.length()){
				System.out.println("Warning: DeID missed name found -> "+n);
			}
		}
		
		return l;
	}
	
	/**
	 * process DeID input and fix it
	 * @param input
	 * @param output
	 * @throws Exception
	 */
	private void process(File input, File output) throws Exception {
		currentReport=0;
		BufferedReader r = new BufferedReader(new FileReader(input));
		BufferedWriter w = new BufferedWriter(new FileWriter(output));
		for(String l=r.readLine(); l!= null;l=r.readLine()){
			w.write(scrub(l)+"\n");
			if(l.trim().equals("E_O_R"))
				currentReport++;
		}
		r.close();
		w.close();
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String type = "Ovarian";
		File fd = new File("/home/tseytlin/Data/DeepPhe/"+type+"/"+type.toLowerCase()+"_patient_sample.deid");
		File fr = new File("/home/tseytlin/Data/DeepPhe/"+type+"/"+type.toLowerCase()+"_patient_sample.bar");
		File ff = new File("/home/tseytlin/Data/DeepPhe/"+type+"/"+type.toLowerCase()+"_patient_sample.deid.fixed");
		
		DeIDRealNameScrubber scrubber = new DeIDRealNameScrubber();
		scrubber.loadRealNames(fr);
		scrubber.process(fd,ff);
	}

}
