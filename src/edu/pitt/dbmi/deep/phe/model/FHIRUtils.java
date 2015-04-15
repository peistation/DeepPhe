package edu.pitt.dbmi.deep.phe.model;


import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.apache.ctakes.typesystem.type.refsem.Date;
import org.apache.ctakes.typesystem.type.refsem.OntologyConcept;
import org.apache.ctakes.typesystem.type.refsem.Time;
import org.apache.ctakes.typesystem.type.refsem.UmlsConcept;
import org.apache.ctakes.typesystem.type.relation.BinaryTextRelation;
import org.apache.ctakes.typesystem.type.relation.ElementRelation;
import org.apache.ctakes.typesystem.type.relation.Relation;
import org.apache.ctakes.typesystem.type.textsem.IdentifiedAnnotation;
import org.apache.ctakes.typesystem.type.textsem.TimeMention;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.Coding;
import org.hl7.fhir.instance.model.DateAndTime;
import org.hl7.fhir.instance.model.DateType;
import org.hl7.fhir.instance.model.Narrative;
import org.hl7.fhir.instance.model.Narrative.NarrativeStatus;
import org.hl7.fhir.utilities.xhtml.NodeType;
import org.hl7.fhir.utilities.xhtml.XhtmlNode;

import edu.pitt.dbmi.nlp.noble.tools.TextTools;
import edu.pitt.dbmi.nlp.noble.util.DeIDUtils;


public class FHIRUtils {
	public static final String DEFAULT_LANGUAGE = "English";
	public static final String DEFAULT_UMLS = "UMLS";
	public static final CodeableConcept CONDITION_CATEGORY_DIAGNOSIS = getCodeableConcept("Diagnosis","diagnosis","http://hl7.org/fhir/condition-category");
	public static final CodeableConcept CONDITION_CATEGORY_FINDING = getCodeableConcept("Finding","finding","http://hl7.org/fhir/condition-category");
	public static final CodeableConcept CONDITION_CATEGORY_SYMPTOM = getCodeableConcept("Symptom","symptom","http://hl7.org/fhir/condition-category");
	public static final CodeableConcept CONDITION_CATEGORY_COMPLAINT = getCodeableConcept("Complaint","complaint","http://hl7.org/fhir/condition-category");
	private static Map<String,CodeableConcept> reportTypes;
	
	/**
	 * get document type
	 * @param type
	 * @return
	 */
	public static CodeableConcept getDocumentType(String type){
		if(reportTypes == null){
			reportTypes = new HashMap<String,CodeableConcept>();
			reportTypes.put("SP",getCodeableConcept("Pathology Report","C0807321",DEFAULT_UMLS));
			reportTypes.put("RAD",getCodeableConcept("Radiology Report","C1299496",DEFAULT_UMLS));
			reportTypes.put("PGN",getCodeableConcept("Progress Note","C0747978",DEFAULT_UMLS));
		}
		return reportTypes.get(type);
	}
	
	/**
	 * get codable concept that has a name and code from UMLS
	 * @param cui
	 * @param name
	 * @return
	 */
	public static CodeableConcept getCodeableConcept(String name,String cui,String scheme){
		CodeableConcept c = new CodeableConcept();
		c.setTextSimple(name);
		Coding cc = c.addCoding();
		cc.setCodeSimple(cui);
		cc.setDisplaySimple(name);
		cc.setSystemSimple(scheme);
		return c;
	}
	
	/**
	 * get FHIR date object from cTAKES time mention
	 * @param tm
	 * @return
	 */
	public static DateAndTime getDate(TimeMention tm){
		//Date dt = tm.getDate();
		Time t  = tm.getTime();
		//TODO: implement
		return new DateAndTime(new Date());
	}
	
	/**
	 * get FHIR date object from cTAKES time mention
	 * @param tm
	 * @return
	 */
	public static DateAndTime getDate(Date dt){
		return new DateAndTime(dt);
	}
	
	/**
	 * parse date from string
	 * @param text
	 * @return
	 */
	public static Date getDate(String text){
		return DeIDUtils.parseDate(text);
	}
	
	/**
	 * get codeblce concept form OntologyConcept annotation
	 * @param c
	 * @return
	 */
	public static CodeableConcept getCodeableConcept(IdentifiedAnnotation ia){
		CodeableConcept cc = new CodeableConcept();
		cc.setTextSimple(ia.getCoveredText());
		
		// go over mapped concepts (merge them into multiple coding systems)
		List<String> cuis = new ArrayList<String>();
		for(int i=0;i<ia.getOntologyConceptArr().size();i++){
			OntologyConcept c = ia.getOntologyConceptArr(i);
			// add coding for this concept
			Coding ccc = cc.addCoding();
			ccc.setCodeSimple(c.getCode());
			ccc.setDisplaySimple(ia.getCoveredText());
			ccc.setSystemSimple(c.getCodingScheme());
			cuis.add(c.getCode());
			
			// add codign for UMLS
			if(c instanceof UmlsConcept){
				String cui = ((UmlsConcept)c).getCui();
				if(!cuis.contains(cui)){
					Coding cccc = cc.addCoding();
					cccc.setCodeSimple(cui);
					cccc.setDisplaySimple(((UmlsConcept)c).getPreferredText());
					cccc.setSystemSimple(DEFAULT_UMLS);
					cuis.add(cui);
				}
			}
		}
		return cc;
	}
	
	/**
	 * get related item from cTAKES
	 * @param source
	 * @param relation
	 * @return
	 */
	public static IdentifiedAnnotation getRelatedItem(IdentifiedAnnotation source , Relation relation){
		if(relation != null ){
			if(relation instanceof BinaryTextRelation){
				BinaryTextRelation r = (BinaryTextRelation) relation;
				if(r.getArg1().getArgument().equals(source))
					return (IdentifiedAnnotation) r.getArg2().getArgument();
				else
					return (IdentifiedAnnotation) r.getArg1().getArgument();
			}
		}
		return null;
	}

	/**
	 * create a narrative from the text
	 * @param coveredText
	 * @return
	 */
	public static Narrative getNarrative(String text) {
		Narrative n = new Narrative();
		n.setStatusSimple(NarrativeStatus.generated);
		XhtmlNode xn = new XhtmlNode(NodeType.Element);
		xn.setName("pre");
		xn.setContent(text);
		n.setDiv(xn);
		return n;
	}
	
	
	/**
	 * parse specially formatted document to extract header information
	 * @param text
	 * @return
	 */
	public static Map<String,String> getHeaderValues(String text){
		Map<String,String> map = new java.util.LinkedHashMap<String,String>();
		Pattern h = Pattern.compile("([\\w\\s]+)\\.+([\\w\\s]+)");
		Pattern p = Pattern.compile("={5,}(.*)={5,}",Pattern.DOTALL|Pattern.MULTILINE);
		Matcher m = p.matcher(text);
		if(m.find()){
			String header = m.group(1);
			for(String l: header.split("\n")){
				m = h.matcher(l);
				if(m.matches()){
					map.put(m.group(1).trim(),m.group(2).trim());
				}
			}
		}
		return map;
	}
	public static void main(String [] args) throws Exception{
		System.out.println(getHeaderValues(TextTools.getText(new FileInputStream(new File("/home/tseytlin/Work/DeepPhe/data/sample/docs/doc1.txt")))));
	}
}
