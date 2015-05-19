package edu.pitt.dbmi.deep.phe.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.ctakes.typesystem.type.textsem.AnatomicalSiteMention;
import org.apache.ctakes.typesystem.type.textsem.DiseaseDisorderMention;
import org.apache.ctakes.typesystem.type.textsem.ProcedureMention;
import org.apache.ctakes.typesystem.type.textsem.TimeMention;
import org.apache.uima.jcas.JCas;
import org.hl7.fhir.instance.formats.XmlComposer;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.DateAndTime;
import org.hl7.fhir.instance.model.Period;
import org.hl7.fhir.instance.model.Resource;
import org.hl7.fhir.instance.model.Condition.ConditionLocationComponent;

import edu.pitt.dbmi.nlp.noble.coder.model.Document;
import edu.pitt.dbmi.nlp.noble.coder.model.Mention;
import edu.pitt.dbmi.nlp.noble.ontology.IClass;

public class Procedure extends org.hl7.fhir.instance.model.Procedure  implements Element{
	
	/**
	 * initialize 
	 * @param m
	 */
	public void initialize(Mention m){
		setType(Utils.getCodeableConcept(m));
		Utils.createIdentifier(addIdentifier(),this,m);
		// find annatomic location
		Mention al = Utils.getNearestMention(m,m.getSentence().getDocument(),Utils.ANATOMICAL_SITE);
		if(al != null){
			CodeableConcept location = addBodySite();
			Utils.setCodeableConcept(location,al);
		}
	}
	
	/**
	 * Initialize diagnosis from a DiseaseDisorderMention in cTAKES typesystem
	 * @param dx
	 */
	public void initialize(ProcedureMention dm){
		// set some properties
		setType(Utils.getCodeableConcept(dm));
		Utils.createIdentifier(addIdentifier(),this,dm);
				
		// now lets take a look at the location of this diagnosis
		AnatomicalSiteMention as = (AnatomicalSiteMention) Utils.getRelatedItem(dm,dm.getBodyLocation());
		if(as == null)
			as = Utils.getAnatimicLocation(dm);
		if(as != null){
			CodeableConcept location = addBodySite();
			Utils.setCodeableConcept(location,as);
		}
		// now lets add observations
		//addEvidence();
		//addRelatedItem();
	}
	

	public String getDisplaySimple() {
		return getType().getTextSimple();
	}

	public String getIdentifierSimple() {
		return Utils.getIdentifier(getIdentifier());
	}

	public String getSummary() {
		StringBuffer st = new StringBuffer();
		st.append("Procedure:\t"+getDisplaySimple());
		for(CodeableConcept l: getBodySite()){
			st.append(" | location: "+l.getTextSimple());
		}
		return st.toString();
	}
	public Resource getResource() {
		return this;
	}

	public void save(File dir) throws Exception {
		Utils.saveFHIR(this,getIdentifierSimple(),dir);
		
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
			Period pp = new Period();
			pp.setStartSimple(d);
			pp.setEndSimple(d);
			setDate(pp);
		}
	}
	
	public IClass getConceptClass(){
		return Utils.getConceptClass(getType());
	}
	
}
