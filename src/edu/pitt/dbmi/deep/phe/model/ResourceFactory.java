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
	private IOntology ontology;
	
	public ResourceFactory(IOntology ont){
		ontology = ont;
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
		return getReport(doc.getText());
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
		return getPatient(doc.getText());
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
		List<Diagnosis> dxs = new ArrayList<Diagnosis>();
		for(Mention m: doc.getMentions()){
			Concept c = m.getConcept();
			IClass cls = getConceptClass(c);
			if(cls != null && cls.hasSuperClass(ontology.getClass("DiseaseDisorder"))){
				// make sure there is no negation 
				if(!m.isNegated()){
					Diagnosis  dx = new Diagnosis();
					dx.initialize(m);
					dxs.add(dx);
				}
			}
		}
		return dxs;
	}

	
	public List<Procedure> getProcedures(Document doc) {
		List<Procedure> dxs = new ArrayList<Procedure>();
		for(Mention m: doc.getMentions()){
			Concept c = m.getConcept();
			IClass cls = getConceptClass(c);
			if(cls != null && cls.hasSuperClass(ontology.getClass("Procedure"))){
				// make sure there is no negation 
				if(!m.isNegated()){
					Procedure  dx = new Procedure();
					dx.initialize(m);
					dxs.add(dx);
				}
			}
		}
		return dxs;
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
