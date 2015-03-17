package edu.pitt.dbmi.deep.phe.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.ctakes.typesystem.type.refsem.OntologyConcept;
import org.apache.ctakes.typesystem.type.refsem.UmlsConcept;
import org.apache.ctakes.typesystem.type.textsem.AnatomicalSiteMention;
import org.apache.ctakes.typesystem.type.textsem.DiseaseDisorderMention;
import org.apache.ctakes.typesystem.type.textsem.TimeMention;
import org.apache.uima.jcas.JCas;
import org.hl7.fhir.instance.formats.XmlComposer;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.Coding;
import org.hl7.fhir.instance.model.Condition;
import org.hl7.fhir.instance.model.DateType;
import org.hl7.fhir.instance.model.Resource;
import org.hl7.fhir.instance.model.ResourceReference;
import org.hl7.fhir.instance.model.StringType;

import edu.pitt.dbmi.nlp.noble.coder.model.Document;
import edu.pitt.dbmi.nlp.noble.coder.model.Mention;
import edu.pitt.dbmi.nlp.noble.ontology.IClass;

/**
 * This class represents a diagnosis mention in a report
 * @author tseytlin
 */
public class Diagnosis extends Condition implements DeepPheModel {
	private IClass cls;
	private List<BodyLocation> locations;
	private List<Observation> observations;
	private List<Finding> findings;
	private Patient patient;
	private Stage stage;
	
	
	public Diagnosis(){
		setCategory(Utils.CONDITION_CATEGORY_DIAGNOSIS);
		setLanguageSimple(Utils.DEFAULT_LANGUAGE); // we only care about English
		setStatusSimple(ConditionStatus.confirmed); // here we only deal with 'confirmed' dx
		Utils.createIdentifier(addIdentifier(),this);
	}
	
	/**
	 * Initialize diagnosis from a DiseaseDisorderMention in cTAKES typesystem
	 * @param dx
	 */
	public void initialize(DiseaseDisorderMention dm){
		// set some properties
		setCode(Utils.getCodeableConcept(dm));
		//setCertainty(); --> dm.getConfidence()
		//setSeverity(value); -- > dm.getSeverity()???
		
		// perhaps have annotation from Document time
		TimeMention tm = dm.getStartTime();
		if(tm != null){
			setDateAssertedSimple(Utils.getDate(tm));
		}
			
		// now lets take a look at the location of this diagnosis
		AnatomicalSiteMention as = (AnatomicalSiteMention) Utils.getRelatedItem(dm,dm.getBodyLocation());
		if(as != null){
			ConditionLocationComponent location = addLocation();
			location.setCode(Utils.getCodeableConcept(as));
		}
		
		// now lets add observations
		//addEvidence();
		//addRelatedItem();
	}
	
	/**
	 * initialize 
	 * @param m
	 */
	public void initialize(Mention m){
		setCode(Utils.getCodeableConcept(m.getConcept()));
	}
	
	
	public void assertRelatedData(Document doc) {
		// TODO Auto-generated method stub
		
	}
	public void assertRelatedData(JCas cas) {
		// TODO Auto-generated method stub
		
	}
	public void inferRelatedData() {
		// TODO Auto-generated method stub
		
	}

	public String getDisplaySimple() {
		return getCode().getTextSimple();
	}

	public String getIdentifierSimple() {
		return Utils.getIdentifier(getIdentifier());
	}

	public String getSummary() {
		StringBuffer st = new StringBuffer();
		st.append("Diagnosis:\t"+getDisplaySimple());
		return st.toString();
	}
	public Resource getResource() {
		return this;
	}

	public void saveFHIR(File dir) throws FileNotFoundException, Exception {
		Utils.saveFHIR(this,getIdentifierSimple(),dir);
		
	}
}
