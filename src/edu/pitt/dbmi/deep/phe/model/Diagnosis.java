package edu.pitt.dbmi.deep.phe.model;

import org.apache.ctakes.typesystem.type.refsem.OntologyConcept;
import org.apache.ctakes.typesystem.type.refsem.UmlsConcept;
import org.apache.ctakes.typesystem.type.textsem.AnatomicalSiteMention;
import org.apache.ctakes.typesystem.type.textsem.DiseaseDisorderMention;
import org.apache.ctakes.typesystem.type.textsem.TimeMention;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.Coding;
import org.hl7.fhir.instance.model.Condition;
import org.hl7.fhir.instance.model.DateType;
import org.hl7.fhir.instance.model.StringType;

/**
 * This class represents a diagnosis mention in a report
 * @author tseytlin
 */
public class Diagnosis extends Condition {
	
	/**
	 * Initialize diagnosis from a DiseaseDisorderMention in cTAKES typesystem
	 * @param dx
	 */
	public Diagnosis(DiseaseDisorderMention dm){
		// set some properties
		setCode(FHIRUtils.getCodeableConcept(dm));
		setCategory(FHIRUtils.CONDITION_CATEGORY_DIAGNOSIS);
		setLanguageSimple(FHIRUtils.DEFAULT_LANGUAGE); // we only care about English
		setStatusSimple(ConditionStatus.confirmed); // here we only deal with 'confirmed' dx
		//setCertainty(); --> dm.getConfidence()
		//setSeverity(value); -- > dm.getSeverity()???
		
		// perhaps have annotation from Document time
		TimeMention tm = dm.getStartTime();
		if(tm != null){
			setDateAssertedSimple(FHIRUtils.getDate(tm));
		}
			
		// now lets take a look at the location of this diagnosis
		AnatomicalSiteMention as = (AnatomicalSiteMention) FHIRUtils.getRelatedItem(dm,dm.getBodyLocation());
		if(as != null){
			ConditionLocationComponent location = addLocation();
			location.setCode(FHIRUtils.getCodeableConcept(as));
		}
		
		// now lets add observations
		//addEvidence();
		//addRelatedItem();
		
	}
}
