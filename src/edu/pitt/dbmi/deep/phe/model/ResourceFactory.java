package edu.pitt.dbmi.deep.phe.model;

import java.util.*;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.jcas.tcas.DocumentAnnotation;

import edu.pitt.dbmi.nlp.noble.coder.model.*;
import edu.pitt.dbmi.nlp.noble.ontology.IClass;
import edu.pitt.dbmi.nlp.noble.ontology.IOntology;
import edu.pitt.dbmi.nlp.noble.terminology.Concept;

/**
 * factory pattern to create FHIR objects from either disease mentions or other methods
 * @author tseytlin
 *
 */
public class ResourceFactory {
	private static ResourceFactory instance;
	private IOntology ontology;
	
	public ResourceFactory(IOntology ont){
		ontology = ont;
		instance = this;
	}
	
	public static ResourceFactory getInstance(){
		return instance;
	}
	
	
	public IOntology getOntology() {
		return ontology;
	}

	public void setOntology(IOntology ontology) {
		this.ontology = ontology;
	}

	/**
	 * create a Report object from DocumentAnnotation
	 * @param doc
	 * @return
	 */
	public Report getReport(JCas cas) {
		return getReport(Utils.getDocumentText(cas));
	}
	
	/**
	 * create a Report object from DocumentAnnotation
	 * @param doc
	 * @return
	 */
	public Report getReport(Document doc) {
		Report r = getReport(doc.getText());
		r.setTitleSimple(doc.getTitle());
		
		// find patient if available
		Patient patient = getPatient(doc);
		if(patient != null){
			r.setPatient(patient);
		}
		
		// now find all observations found in a report
		for(Observation ob: getObservations(doc)){
			r.addReportElement(ob);
		}
		
		// now find all observations found in a report
		for(Finding ob: getFindings(doc)){
			r.addReportElement(ob);
		}		
		
		// find all procedures mentioned in each report
		for(Procedure p: getProcedures(doc)){
			r.addReportElement(p);
		}
		
		// now find all observations found in a report
		for(Medication ob: getMedications(doc)){
			r.addReportElement(ob);
		}		
		
		// now find all primary diagnosis that are found in a report
		for(Diagnosis dx: getDiagnoses(doc)){
			r.addReportElement(dx);
		}			
		
		return r;
	}

	/**
	 * create a Report object from DocumentAnnotation
	 * @param doc
	 * @return
	 */
	public Report getReport(String text) {
		if(text == null)
			return null;
		
		Report r = new Report();
		r.setTextSimple(text);
		// some hard-coded report type values
		Map<String,String> header = Utils.getHeaderValues(text);
		String dt = header.get(Utils.DOCUMENT_HEADER_PRINCIPAL_DATE);
		if(dt != null)
			r.setDate(Utils.getDate(dt));
		String tp = header.get(Utils.DOCUMENT_HEADER_REPORT_TYPE);
		if(tp != null)
			r.setType(tp);
		return r;
	}
	
	/**
	 * get patient from the document
	 * @param doc
	 * @return
	 */
	public Patient getPatient(Document doc) {
		Patient pt = getPatient(doc.getText());
		// see if there is a gender anywhere
		for(Mention m: Utils.getMentionsByType(doc,Utils.AGE,false)){
			pt.setAge(m);
		}
		for(Mention m: Utils.getMentionsByType(doc,Utils.GENDER,false)){
			pt.setGender(m);
		}
		
		return pt;
	}
	
	/**
	 * get patient from the document
	 * @param doc
	 * @return
	 */
	public static Patient getPatient(org.hl7.fhir.instance.model.Patient p) {
		Patient patient = new Patient();
		patient.copy(p);
		return patient;
	}
	
	/**
	 * get patient from the document
	 * @param doc
	 * @return
	 */
	public Patient getPatient(String text) {
		if(text == null)
			return null;
		Map<String,String> header = Utils.getHeaderValues(text);
		String pn = header.get(Utils.DOCUMENT_HEADER_PATIENT_NAME);
		if(pn != null){
			Patient p = new Patient();
			p.setNameSimple(pn);
			return p;
		}
		return null;
	}
	
	public Patient getPatient(JCas cas) {
		return getPatient(Utils.getDocumentText(cas));
	}

	/**
	 * get concept class from a default ontology based on Concept
	 * @param c
	 * @return
	 */
	public IClass getConceptClass(Concept c){
		String code = c.getCode();
		if(code.contains(":"))
			code = code.substring(code.indexOf(':')+1);
		return ontology.getClass(code);
	}
	
	/**
	 * get a list of primary diagnosis mentioned in this document
	 * @param doc
	 * @return
	 */
	public List<Diagnosis> getDiagnoses(Document doc) {
		List<Diagnosis> list = new ArrayList<Diagnosis>();
		for(Mention m : Utils.getMentionsByType(doc,Utils.DIAGNOSIS)){
			Diagnosis  dx = new Diagnosis();
			dx.initialize(m);
			list.add(dx);
		}
		return list;
	}

	
	public List<Procedure> getProcedures(Document doc) {
		List<Procedure> list = new ArrayList<Procedure>();
		for(Mention m : Utils.getMentionsByType(doc,Utils.PROCEDURE)){
			Procedure  dx = new Procedure();
			dx.initialize(m);
			list.add(dx);
		}
		return list;
	}
	

	private  List<Finding> getFindings(Document doc) {
		List<Finding> list = new ArrayList<Finding>();
		for(Mention m : Utils.getMentionsByType(doc,Utils.FINDING)){
			Finding  dx = new Finding();
			dx.initialize(m);
			list.add(dx);
		}
		return list;
	}

	private List<Observation> getObservations(Document doc) {
		List<Observation> list = new ArrayList<Observation>();
		for(Mention m : Utils.getMentionsByType(doc,Utils.OBSERVATION)){
			Observation  dx = new Observation();
			dx.initialize(m);
			list.add(dx);
		}
		return list;
	}
	
	private List<Medication> getMedications(Document doc) {
		List<Medication> list = new ArrayList<Medication>();
		for(Mention m : Utils.getMentionsByType(doc,Utils.MEDICATION)){
			Medication  dx = new Medication();
			dx.initialize(m);
			list.add(dx);
		}
		return list;
	}


	public List<Procedure> getProcedures(JCas cas) {
		// TODO Auto-generated method stub
		return Collections.EMPTY_LIST;
	}


	public static List<Diagnosis> getDiagnoses(JCas cas) {
		// TODO Auto-generated method stub
		return Collections.EMPTY_LIST;
	}
	
	
	
}
