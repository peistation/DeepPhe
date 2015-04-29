package edu.pitt.dbmi.deep.phe.deid;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;

import edu.pitt.dbmi.deep.phe.util.TextUtils;

/**
 * post process DeIDed Text and scrub out text 
 * @author tseytlin
 *
 */
public class DeIDTagScrubber {
	private static final String DEID_NAMES = "/resources/DeID-names.txt";
	private static final String DEID_PLACES = "/resources/DeID-places.txt";
	private static final String DEID_INSTITUTIONS = "/resources/DeID-institutions.txt";
	private static final String DEID_TAG_PATTERN = "\\*?\\*\\*[A-Z\\-]+(\\[[^\\*]*\\])?";
	
	private Map<String,String> nameMap;
	private Set<String> realNames;
	private String currentPatientID, currentPatientName;
	private Iterator<String> availableDoubleName,availableSingleName;
	private List<String> places, institutions;
	
	
	/**
	 * process text
	 * @param file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private String process(File file) throws FileNotFoundException, IOException {
		if(file.isFile()){
			StringBuffer str = new StringBuffer();
			BufferedReader r = null;
			try{
				r = new BufferedReader(new FileReader(file));
				for(String l=r.readLine();l!=null;l=r.readLine()){
					str.append(process(l)+"\n"); //scrubRealNames(l)
				}
			}catch(IOException ex){
				throw ex;
			}finally{
				if(r != null)
					r.close();
			}
			return str.toString();
		}
		return null;
	}
	
	private void setupCurrentPatient(String line){
		Pattern p = Pattern.compile("Patient ID\\.+([^\\s]+)");
		Matcher m = p.matcher(line);
		if(m.matches()){
			currentPatientID = m.group(1);
		}
		p = Pattern.compile("Patient Name\\.+([^\\.]+)");
		m = p.matcher(line);
		if(m.matches()){
			currentPatientName = extractTagParts(m.group(1))[1];
		}
	}
	
	/**
	 * process line of text to scrub DeID tags
	 * @param l
	 * @return
	 */
	
	private String process(String line) {
		// OK, now find special DeID fields to resolve
		// patient names properly
		setupCurrentPatient(line);
		
		// find and replace all tags
		Pattern pt = Pattern.compile(DEID_TAG_PATTERN);
		Matcher mt = pt.matcher(line);
		StringBuffer buffer = new StringBuffer();
		int st = 0;
		while(mt.find()){
			buffer.append(line.substring(st,mt.start()));
			buffer.append(scrub(mt.group()));
			st = mt.end();
			//buffer.replace(mt.start(),mt.end(),scrub(mt.group()));
		}
		buffer.append(line.substring(st,line.length()));
		return buffer.toString();
	}

	/**
	 * return tag name=vale
	 * @param tag name [0], value[1]
	 * @return
	 */
	private String [] extractTagParts(String tag){
		Matcher m = Pattern.compile("\\*?\\*\\*([A-Z\\-]+)(\\[.*\\])?").matcher(tag);
		if(m.matches()){
			return new String[] { m.group(1),m.group(2)};
		}
		return new String [0];
	}
	
	/**
	 * scrub individual DeID tag
	 * @param group
	 * @return
	 */
	private String scrub(String tag) {
		String [] parts = extractTagParts(tag);
		if(parts.length > 0){
			String tagName = parts[0];
			// do different things
			if("NAME".equals(tagName)){
				return getName(parts[1]);
			}else if("PLACE".equals(tagName)){
				return getPlace();
			}else if("INSTITUTION".equals(tagName)){
				return getInstitution();
			}else if ("PATH-NUMBER".equals(tagName)){
				return tag;
			}
		}
		// scrub the rest
		return "";
	}

	private String getPlace() {
		/*if(places == null){
			places = getList(DEID_PLACES);
		}
		return places.get(0);*/
		return "Location";
	}
	
	private String getInstitution() {
		/*if(institutions == null){
			institutions = getList(DEID_INSTITUTIONS);
		}
		return institutions.get(0);*/
		return "Institution";
	}
	
	private List<String> getList(String file){
		String text = null;
		try {
			text = TextUtils.getText(getClass().getResourceAsStream(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<String>(Arrays.asList(text.split("\n")));
	}

	/**
	 * generate new name from a list
	 * @param full
	 * @return
	 */
	private String generateName(boolean full){
		/*if(availableDoubleName == null){
			List<String> doubleNames = new ArrayList<String>();
			List<String> singleNames = new ArrayList<String>();
			try {
				String text = TextUtils.getText(getClass().getResourceAsStream(DEID_NAMES));
				for(String s: text.split("\n")){
					if(s.trim().indexOf(" ") > -1)
						doubleNames.add(s.trim());
					else
						singleNames.add(s.trim());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			availableDoubleName = doubleNames.iterator();
			availableSingleName = singleNames.iterator();
		}
		
		
		String name = null;
		if(full){
			name = availableDoubleName.next();
			if(name == null)
				name = availableSingleName.next();
		}else{
			name = availableSingleName.next();
			if(name == null)
				name = availableDoubleName.next();
		}
		
		if(name == null)
			name = "Noname Nobody";
		
		return name;	
			*/
		return "Person"+(nameMap.size()+1);
	}
	
	/**
	 * scrub name
	 * @param group
	 * @return
	 */
	private String getName(String deid) {
		if(nameMap == null){
			nameMap = new HashMap<String, String>();
		}
		// 
		if(deid == null)
			return "";
		
		
		// do patient specific stuff
		String p = "";
		if(deid.equals(currentPatientName)){
			p = currentPatientID+"|";
		}
		
		
		// get rid of brackets and remove commas and periods
		deid = deid.substring(1,deid.length()-1).trim();
		deid = deid.replaceAll("[^A-Z ]\\s*"," ");
		
		// find known patient
		if(deid.contains("AAA") || deid.contains("BBB")){
			for(String d: nameMap.keySet()){
				if(d.contains(currentPatientID) && d.contains("AAA")){
					if(p.length() > 0)
						deid = d.split("\\|")[1].trim();
					else
						deid = d;
					break;
				}
			}
		}
		
		// if we have a mention of name return it
		if(nameMap.containsKey(p+deid)){
			return  nameMap.get(p+deid);
		}
		
		// else choose a new name
		String name = generateName(deid.contains(" "));
		nameMap.put(p+deid,name);
		
		// lets add name without middle name initial
		//if(deid.matches("[A-Z]+ [A-Z]+ [A-Z]+"))
		//	nameMap.put(p+deid.substring(0,deid.lastIndexOf(" ")),name);
		
		// lets add just last name
		//if(deid.contains(" ") && name.contains(" "))
		//	nameMap.put(p+deid.substring(0,deid.indexOf(" ")),name);
			//nameMap.put(p+deid.substring(0,deid.indexOf(" ")),name.substring(name.indexOf(" ")+1));
		
		return name;
	}
	
	/**
	 * load real names for filtering
	 * @param fn
	 *
	private void loadRealNames(File file) throws Exception {
		realNames = new LinkedHashSet<String>();
		BufferedReader r = new BufferedReader(new FileReader(file));
		for(String l=r.readLine();l!=null;l=r.readLine()){
			for(String p : l.split("\\s+")){
				if(p.trim().length() > 3){
					realNames.add(p.trim().toLowerCase());
				}
			}
		}
		r.close();
	}
	*/

	/*
	private String scrubRealNames(String text){
		for(String name: realNames){
			if(text.toLowerCase().indexOf(name) > -1){
				String otext = text;
				text = text.replaceAll("(?i)\\b"+name+"\\b"," ");
				if(!otext.equals(text))
					System.out.println("Warning: found a real name in text: "+name);
			}
		}
		return text;
	}
	*/

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String type = "Ovarian";
		File fd = new File("/home/tseytlin/Data/DeepPhe/Samples/CARe_Sample_Apr-2015/"+type+"/"+type.toLowerCase()+"_sample_filtered_addendum.deid.fixed");
		File fs = new File("/home/tseytlin/Data/DeepPhe/Samples/CARe_Sample_Apr-2015/"+type+"/"+type.toLowerCase()+"_sample_filtered_addendum.scrubbed");
		/*File fd = new File("/home/tseytlin/Data/DeepPhe/"+type+"/"+type.toLowerCase()+"_patient_sample.deid.fixed");
		File fs = new File("/home/tseytlin/Data/DeepPhe/"+type+"/"+type.toLowerCase()+"_patient_sample.scrubbed");*/
		
		DeIDTagScrubber scrubber = new DeIDTagScrubber();
		//scrubber.loadRealNames(fn);
		String text = scrubber.process(fd);
		
		BufferedWriter w = new BufferedWriter(new FileWriter(fs));
		w.write(text);
		w.close();
		
		System.out.println(type+"\t"+fd.getParentFile().getAbsolutePath());
		//System.out.println(scrubber.nameMap);
		
	}



}
