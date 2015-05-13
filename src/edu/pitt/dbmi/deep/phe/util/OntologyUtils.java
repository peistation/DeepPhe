package edu.pitt.dbmi.deep.phe.util;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
				for(String code : getUMLS_Codes(c)){
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
	
	public static List<String> getUMLS_Codes(IClass cls){
		List<String> codes = new ArrayList<String>();
		// find UMLS CUIS
		for(Object cc : cls.getConcept().getCodes().values()){
			Matcher m = Pattern.compile("(CL?\\d{6,7})( .+)?").matcher(cc.toString());
			if(m.matches()){
				codes.add(m.group(1));
			}
		}
		return codes;
	}
	
	public static List<String> getRXNORM_Codes(IClass cls){
		return getRXNORM_Codes(cls.getConcept());
	}
	
	public static List<String> getRXNORM_Codes(Concept cls){
		List<String> codes = new ArrayList<String>();
		// find UMLS CUIS
		for(Object cc : cls.getCodes().values()){
			Matcher m = Pattern.compile("(\\d+) \\[RXNORM\\]").matcher(cc.toString());
			if(m.matches()){
				codes.add(m.group(1));
			}
		}
		return codes;
	}
	
	
	
	
	public static String getCode(IClass cls){
		List<String> codes = getUMLS_Codes(cls);
		return codes == null || codes.isEmpty()?null:codes.get(0);
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
