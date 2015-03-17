package edu.pitt.dbmi.deep.phe.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.crypto.Data;

import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.hl7.fhir.instance.formats.XmlComposer;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.Composition;
import org.hl7.fhir.instance.model.DateAndTime;
import org.hl7.fhir.instance.model.Encounter;
import org.hl7.fhir.instance.model.Identifier;
import org.hl7.fhir.instance.model.Resource;
import org.hl7.fhir.instance.model.ResourceReference;
import org.hl7.fhir.instance.model.Encounter.EncounterClass;
import org.hl7.fhir.instance.model.Encounter.EncounterState;

/**
 * represents a medical document that contains a set of 
 * Diagnosis along with Evidence, Signs and Symptoms
 * @author tseytlin
 *
 */
public class Report extends Composition implements DeepPheModel{
	private CompositionEventComponent event;
	private List<Diagnosis> diagnoses;
	private List<Procedure> procedures;
	
	/**
	 * create a default report object
	 */
	public Report(){
		setStatusSimple(CompositionStatus.final_);
		setLanguageSimple(Utils.DEFAULT_LANGUAGE);
		event = new CompositionEventComponent();
		setEvent(event);
		setIdentifier(Utils.createIdentifier(this));
	}

	
	/**
	 * set document text
	 * @param text
	 */
	public void setTextSimple(String text){
		setText(Utils.getNarrative(text));
	}
	
	public String getTextSimple(){
		return Utils.getText(getText());
	}
	
	/**
	 * set principal date
	 * @param dt
	 */
	public void setDate(Date dt){
		setDateSimple(Utils.getDate(dt));
	}
	
	/**
	 * set patient that this document is describing
	 */
	public void setPatient(Patient p){
		setSubject(p.getReference());
		setSubjectTarget(p);
		// set DateTime
		DateAndTime dt = getDateSimple();
		if(dt != null)
			p.setCurrentDate(Utils.getDate(dt));
	}
	
	public Patient getPatient(){
		Resource r = getSubjectTarget();
		if(r == null)
			return null;
		if(r instanceof Patient)
			return (Patient) r;
		if(r instanceof org.hl7.fhir.instance.model.Patient)
			return ResourceFactory.getPatient((org.hl7.fhir.instance.model.Patient) r);
		return null;
	}
	
	/**
	 * set report type
	 * @param type
	 */
	public void setType(String type){
		CodeableConcept tp = Utils.getDocumentType(type);
		if(tp != null)
			setType(tp);
	}

	
	public List<Procedure> getProcedures() {
		if(procedures == null)
			procedures = new ArrayList<Procedure>();
		return procedures;
	}

	/**
	 * get a set of defined diagnoses for this report
	 * @return
	 */
	public List<Diagnosis> getDiagnoses(){
		if(diagnoses == null)
			diagnoses = new ArrayList<Diagnosis>();
		return diagnoses;
	}
	
	/**
	 * add diagnosis that this report is describing
	 * @param dx
	 */
	public void addDiagnosis(Diagnosis dx){
		if(getDiagnoses().contains(dx))
			return;
		
		// add to list of diagnosis
		getDiagnoses().add(dx);
		
		// persist other info
		Patient p = getPatient();
		if(p != null){
			dx.setSubject(Utils.getResourceReference(p));
			dx.setSubjectTarget(p);
		}
			
		// add reference
		ResourceReference r = event.addDetail();
		Utils.getResourceReference(r,dx);
	}

	/**
	 * add procedure to this report
	 * @param pr
	 */
	public void addProcedure(Procedure pr) {
		if(getProcedures().contains(pr))
			return;
		
		getProcedures().add(pr);
		
		// persist other info
		Patient p = getPatient();
		if(p != null){
			pr.setSubject(Utils.getResourceReference(p));
			pr.setSubjectTarget(p);
		}
		// add reference
		ResourceReference r = event.addDetail();
		Utils.getResourceReference(r,pr);

	}

	public String getDisplaySimple() {
		return getTextSimple();
	}

	public String getIdentifierSimple() {
		return Utils.getIdentifier(getIdentifier());
	}

	public String getSummary() {
		StringBuffer st = new StringBuffer();
		st.append("Report:\n"+getDisplaySimple()+"\n");
		st.append(getPatient().getSummary()+"\n");
		for(Diagnosis dx: getDiagnoses()){
			st.append(dx.getSummary()+"\n");
		}
		for(Procedure p: getProcedures()){
			st.append(p.getSummary()+"\n");
		}
		return st.toString();
	}


	public Resource getResource() {
		return this;
	}
	
	/**
	 * persist this object to a directory
	 * @param dir
	 * @throws Exception 
	 * @throws FileNotFoundException 
	 */
	public void saveFHIR(File dir) throws FileNotFoundException, Exception{
		Utils.saveFHIR(this,getIdentifierSimple(),dir);
		
		// go over components
		Patient pt = getPatient();
		if(pt != null)
			pt.saveFHIR(dir);
		for(Diagnosis dx: getDiagnoses())
			dx.saveFHIR(dir);
		for(Procedure p: getProcedures()){
			p.saveFHIR(dir);
		}
		
	}
	
}
