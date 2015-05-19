package edu.pitt.dbmi.deep.phe.model;

import java.util.*;

import org.apache.ctakes.cancer.type.textsem.CancerSize;
import org.apache.ctakes.cancer.type.textsem.ReceptorStatus;
import org.apache.ctakes.typesystem.type.textsem.DiseaseDisorderMention;
import org.apache.ctakes.typesystem.type.textsem.IdentifiedAnnotation;
import org.apache.ctakes.typesystem.type.textsem.MedicationMention;
import org.apache.ctakes.typesystem.type.textsem.ProcedureMention;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.jcas.tcas.DocumentAnnotation;

import edu.pitt.dbmi.deep.phe.util.OntologyUtils;
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
	private OntologyUtils ontologyUtils;
	
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

	public OntologyUtils getOntologyUtils(){
		if(ontologyUtils == null && ontology != null){
			ontologyUtils = new OntologyUtils(ontology);
		}
		return ontologyUtils;
	}
	
	/**
	 * create a Report object from DocumentAnnotation
	 * @param doc
	 * @return
	 */
	public Report getReport(JCas cas) {
		Report r = getReport(Utils.getDocumentText(cas));
		
		// oh, well no document title available
		//r.setTitle(value);
		
		// find patient if available
		Patient patient = getPatient(cas);
		if(patient != null){
			r.setPatient(patient);
		}
		
		// now find all observations found in a report
		for(Observation ob: getObservations(cas)){
			r.addReportElement(ob);
		}
		
		// now find all observations found in a report
		for(Finding ob: getFindings(cas)){
			r.addReportElement(ob);
		}		
		
		// find all procedures mentioned in each report
		for(Procedure p: getProcedures(cas)){
			r.addReportElement(p);
		}
		
		// now find all observations found in a report
		for(Medication ob: getMedications(cas)){
			r.addReportElement(ob);
		}		
		
		// now find all primary diagnosis that are found in a report
		for(Diagnosis dx: getDiagnoses(cas)){
			r.addReportElement(dx);
		}			
				
		
		
		
		return r;
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
	
	
	public Patient getPatient(JCas cas) {
		Patient p =  getPatient(Utils.getDocumentText(cas));
		// TODO: age and gender
		return p;
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
	private List<Diagnosis> getDiagnoses(Document doc) {
		List<Diagnosis> list = new ArrayList<Diagnosis>();
		for(Mention m : Utils.getMentionsByType(doc,Utils.DIAGNOSIS)){
			Diagnosis  dx = new Diagnosis();
			dx.initialize(m);
			list.add(dx);
		}
		return list;
	}

	
	private List<Procedure> getProcedures(Document doc) {
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


	private List<Procedure> getProcedures(JCas cas) {
		List<Procedure> list = new ArrayList<Procedure>();
		for(IdentifiedAnnotation m: Utils.getAnnotationsByType(cas, ProcedureMention.type)){
			Procedure  dx = new Procedure();
			dx.initialize((ProcedureMention) m);
			list.add(dx);
		}
		return list;
	}
	
	/**
	 * 
	 */
	private List<Diagnosis> getDiagnoses(JCas cas) {
		List<Diagnosis> list = new ArrayList<Diagnosis>();
		for(IdentifiedAnnotation m: Utils.getAnnotationsByType(cas, DiseaseDisorderMention.type)){
			Diagnosis  dx = new Diagnosis();
			dx.initialize((DiseaseDisorderMention) m);
			list.add(dx);
		}
		return list;
	}
	
	private List<Medication> getMedications(JCas cas) {
		List<Medication> list = new ArrayList<Medication>();
		for(IdentifiedAnnotation m: Utils.getAnnotationsByType(cas,MedicationMention.type)){
			Medication  dx = new Medication();
			dx.initialize((MedicationMention) m);
			list.add(dx);
		}
		return list;
	}

	private List<Finding> getFindings(JCas cas) {
		// TODO Auto-generated method stub
		return Collections.EMPTY_LIST;
	}

	private List<Observation> getObservations(JCas cas) {
		List<Observation> list = new ArrayList<Observation>();
		List<IdentifiedAnnotation> annotations = new ArrayList<IdentifiedAnnotation>();
		annotations.addAll(Utils.getAnnotationsByType(cas,CancerSize.type));
		annotations.addAll(Utils.getAnnotationsByType(cas,ReceptorStatus.type));
		for(IdentifiedAnnotation m: annotations ){
			Observation  dx = new Observation();
			dx.initialize(m);
			list.add(dx);
		}
		return list;
	}
	
}
