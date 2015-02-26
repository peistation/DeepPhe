package edu.pitt.dbmi.deep.phe.model;


import java.util.ArrayList;
import java.util.List;

import org.apache.ctakes.typesystem.type.refsem.Date;
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


public class FHIRUtils {
	public static final String DEFAULT_LANGUAGE = "English";
	public static final String DEFAULT_UMLS = "UMLS";
	public static final CodeableConcept CONDITION_CATEGORY_DIAGNOSIS = getCodeableConcept("Diagnosis","diagnosis","http://hl7.org/fhir/condition-category");
	public static final CodeableConcept CONDITION_CATEGORY_FINDING = getCodeableConcept("Finding","finding","http://hl7.org/fhir/condition-category");
	public static final CodeableConcept CONDITION_CATEGORY_SYMPTOM = getCodeableConcept("Symptom","symptom","http://hl7.org/fhir/condition-category");
	public static final CodeableConcept CONDITION_CATEGORY_COMPLAINT = getCodeableConcept("Complaint","complaint","http://hl7.org/fhir/condition-category");
	
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
		Date dt = tm.getDate();
		Time t  = tm.getTime();
		//TODO: implement
		return new DateAndTime(new java.util.Date());
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
}
