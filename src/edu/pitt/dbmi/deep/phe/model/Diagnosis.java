package edu.pitt.dbmi.deep.phe.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ctakes.cancer.type.relation.TnmStageTextRelation;
import org.apache.ctakes.cancer.type.textsem.TnmClassification;
import org.apache.ctakes.typesystem.type.relation.LocationOfTextRelation;
import org.apache.ctakes.typesystem.type.relation.LocationOfTextRelation_Type;
import org.apache.ctakes.typesystem.type.relation.Relation;
import org.apache.ctakes.typesystem.type.textsem.AnatomicalSiteMention;
import org.apache.ctakes.typesystem.type.textsem.DiseaseDisorderMention;
import org.apache.ctakes.typesystem.type.textsem.IdentifiedAnnotation;
import org.apache.ctakes.typesystem.type.textsem.TimeMention;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.hl7.fhir.instance.model.Condition;
import org.hl7.fhir.instance.model.DateAndTime;
import org.hl7.fhir.instance.model.Resource;

import edu.pitt.dbmi.nlp.noble.coder.model.Mention;
import edu.pitt.dbmi.nlp.noble.ontology.IClass;

/**
 * This class represents a diagnosis mention in a report
 * @author tseytlin
 */
public class Diagnosis extends Condition implements Element {
	
	public Diagnosis(){
		setCategory(Utils.CONDITION_CATEGORY_DIAGNOSIS);
		setLanguageSimple(Utils.DEFAULT_LANGUAGE); // we only care about English
		setStatusSimple(ConditionStatus.confirmed); // here we only deal with 'confirmed' dx
		//Utils.createIdentifier(addIdentifier(),this);
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
		
		// create identifier
		Utils.createIdentifier(addIdentifier(),this,dm);
		
		
		// perhaps have annotation from Document time
		TimeMention tm = dm.getStartTime();
		if(tm != null){
			setDateAssertedSimple(Utils.getDate(tm));
		}
			
		// now lets take a look at the location of this diagnosis
		AnatomicalSiteMention as = (AnatomicalSiteMention) Utils.getRelatedItem(dm,dm.getBodyLocation());
		if(as == null){
			as = Utils.getAnatimicLocation(dm);
		}
		if(as != null){
			ConditionLocationComponent location = addLocation();
			location.setCode(Utils.getCodeableConcept(as));
			location.setDetailSimple(as.getCoveredText());
		}
	
		// now lets get the location relationships
		for(Annotation  a: Utils.getRelatedAnnotationsByType(dm,TnmStageTextRelation.class)){
			Stage stage = new Stage();
			stage.initialize((TnmClassification) a);
			setStage(stage);
		}
	}
	
	
	/**
	 * initialize from concept mention
	 * @param m
	 */
	public void initialize(Mention m){
		setCode(Utils.getCodeableConcept(m));
		
		// create identifier
		Utils.createIdentifier(addIdentifier(),this,m);
		
		// find annatomic location
		Mention al = Utils.getNearestMention(m,m.getSentence().getDocument(),Utils.ANATOMICAL_SITE);
		if(al != null){
			ConditionLocationComponent location = addLocation();
			location.setCode(Utils.getCodeableConcept(al));
			location.setDetailSimple(al.getText());
		}
		
		// find relevant stage
		Mention st = Utils.getNearestMention(m,m.getSentence().getDocument(),Utils.STAGE);
		if(st != null){
			Stage stage = new Stage();
			stage.initialize(st);
			setStage(stage);
		}
	}
	
	/**
	 * initialize from class in the ontology
	 * @param cls
	 */
	public void initialize(IClass cls){
		setCode(Utils.getCodeableConcept(cls));
		Utils.createIdentifier(addIdentifier(),this,cls);
	}
	
	
	public Stage getStage(){
		return (Stage) super.getStage();
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
		for(ConditionLocationComponent l: getLocation()){
			st.append(" | location: "+l.getCode().getTextSimple());
		}
		Stage s = getStage();
		if(s != null){
			st.append(" | stage: "+s.getSummary().getTextSimple());
			//+" T:"+s.getPrimaryTumorStage()+" N: "+s.getRegionalLymphNodeStage()+" M: "+s.getDistantMetastasisStage());
		}
		return st.toString();
	}
	public void save(File dir) throws Exception {
		Utils.saveFHIR(this,getIdentifierSimple(),dir);
		
	}

	public Resource getResource() {
		return this;
	}

	/**
	 * assign report instance and add appropriate information from there
	 */
	public void setReport(Report r) {
		Patient p = r.getPatient();
		if(p != null){
			setSubject(Utils.getResourceReference(p));
			setSubjectTarget(p);
		}
		// set date
		DateAndTime d = r.getDateSimple();
		if( d != null){
			setDateAssertedSimple(d);
		}
	}
	public IClass getConceptClass(){
		return Utils.getConceptClass(getCode());
	}
}
