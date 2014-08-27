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
					str.append(process(l)+"\n");
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
	
	/**
	 * process line of text to scrub DeID tags
	 * @param l
	 * @return
	 */
	
	private String process(String line) {
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
	 * scrub individual DeID tag
	 * @param group
	 * @return
	 */
	private String scrub(String tag) {
		Matcher m = Pattern.compile("\\*?\\*\\*([A-Z\\-]+)(\\[.*\\])?").matcher(tag);
		if(m.matches()){
			String tagName = m.group(1);
			// do different things
			if("NAME".equals(tagName)){
				return getName(m.group(2));
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
		if(places == null){
			places = getList(DEID_PLACES);
		}
		return places.get(0);
	}
	
	private String getInstitution() {
		if(institutions == null){
			institutions = getList(DEID_INSTITUTIONS);
		}
		return institutions.get(0);
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
		if(availableDoubleName == null){
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
		// if we have a mention of name return it
		if(nameMap.containsKey(deid))
			return nameMap.get(deid);
		
		// else choose a new name
		String name = generateName(deid.contains(" "));
		nameMap.put(deid,name);
		
		return name;
	}
	
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String f = "/home/tseytlin/Data/DeepPhe/DeepPheSample.deid";
		String ff = "/home/tseytlin/Data/DeepPhe/DeepPheSample.scrubbed";
		
		DeIDTagScrubber scrubber = new DeIDTagScrubber();
		String text = scrubber.process(new File(f));
		
		BufferedWriter w = new BufferedWriter(new FileWriter(new File(ff)));
		w.write(text);
		w.close();
		
		System.out.println("done");
		//System.out.println(scrubber.nameMap);
		
	}



}
