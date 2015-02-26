package edu.pitt.dbmi.deep.phe.model;

import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.hl7.fhir.instance.model.Composition;
import org.hl7.fhir.instance.model.Composition.SectionComponent;

/**
 * represents a medical document that contains a set of 
 * Diagnosis along with Evidence, Signs and Symptoms
 * @author tseytlin
 *
 */
public class Report extends Composition {
	/**
	 * Initialize report with a document annotation
	 * @param doc
	 */
	public Report(DocumentAnnotation doc){
		setStatusSimple(CompositionStatus.final_);
		setLanguageSimple(doc.getLanguage());
		setText(FHIRUtils.getNarrative(doc.getCoveredText()));
		
		// extract some related values
		//setDateSimple(value);
		//setType(FHIRUtils.getCodeableConcept(name, cui, scheme));
		//setTitleSimple();
		//setSubjectTarget(value);
		//setEvent(CompositionEventComponent)
	}
	
	/**
	 * add diagnosis that this report is describing
	 * @param dx
	 */
	public void addDiagnosis(Diagnosis dx){
		SectionComponent section = addSection();
		//section.setTitle(new StringType("Final Diagnosis"));
		//section.setContentTarget(dx.getModel());
	}
	
	/**
	 * set patient that this document is describing
	 */
	public void setPatient(){
		//setSubjectTarget(patient.getModel());
	}
	
}
