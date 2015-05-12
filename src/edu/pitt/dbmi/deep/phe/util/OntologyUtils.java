package edu.pitt.dbmi.deep.phe.util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.pitt.dbmi.nlp.noble.ontology.IClass;
import edu.pitt.dbmi.nlp.noble.ontology.IOntology;
import edu.pitt.dbmi.nlp.noble.ontology.IOntologyException;
import edu.pitt.dbmi.nlp.noble.ontology.owl.OOntology;
import edu.pitt.dbmi.nlp.noble.terminology.Concept;

public class OntologyUtils {
	private IOntology ontology;
	private Map<String,IClass> clsMap;
	
	
	public OntologyUtils(IOntology ont){
		ontology = ont;
	}
	
	/**
	 * get class for a given concept code
	 * @param code
	 * @return
	 */
	public IClass getClass(String code){
		return getClassMap().get(code);
	}
	
	
	private Map<String, IClass> getClassMap() {
		if(clsMap == null){
			clsMap = new HashMap<String, IClass>();
			for(IClass c: ontology.getRoot().getSubClasses()){
				String code = getCode(c);
				if(code != null){
					clsMap.put(code,c);
				}
			}
		}
		return clsMap;
	}

	/**
	 * save dictionary as BSV file
	 * @param root
	 * @param output
	 * @throws IOException 
	 */
	public void saveDictionary(File output) throws IOException{
		saveDictionary(ontology.getRoot(), output);
	}
	
	/**
	 * save dictionary as BSV file
	 * @param root
	 * @param output
	 * @throws IOException 
	 */
	public static void saveDictionary(IClass root, File output) throws IOException{
		// write out BSV file
		BufferedWriter w = new BufferedWriter(new FileWriter(output));
		w.write(convertCls(root));
		for(IClass c: root.getSubClasses()){
			w.write(convertCls(c));
		}
		w.close();
	}
	
	
	private static String getCode(IClass cls){
		// find UMLS CUIS
		String cui = null;
		for(Object cc : cls.getConcept().getCodes().values()){
			Matcher m = Pattern.compile("(CL?\\d{6,7})( .+)?").matcher(cc.toString());
			if(m.matches()){
				cui = m.group(1);
				break;
			}
		}
		return cui;
	}
	
	/**
	 * convert Class to BSV entry
	 * @param root
	 * @return
	 */
	private static String convertCls(IClass cls) {
		Concept c = cls.getConcept();
		// find UMLS CUIS
		String cui = getCode(cls);
		if(cui != null){
			String tui = "";
			if(c.getSemanticTypes().length > 0)
				tui = c.getSemanticTypes()[0].getCode();
			StringBuffer b = new StringBuffer();
			for(String s: c.getSynonyms()){
				b.append(cui+"|"+tui+"|"+s+"\n");
			}
			return b.toString();
		}else{
			System.out.println("No CUI in cls "+cls.getName());
		}
		return "";
	}



	public static void main(String [] args) throws Exception{
		File f = new File("/home/tseytlin/Work/DeepPhe/ontologies/breastCancer.owl");
		File of = new File("/home/tseytlin/Work/DeepPhe/ontologies/breastCancer.bsv");
		
		IOntology ont = OOntology.loadOntology(f);
		//OntologyUtils ou = new OntologyUtils(ont);
		saveDictionary(ont.getClass("Element"),of);
		System.out.println("done");
		//System.out.println(ou.getClass("C0441960"));
	}
}
