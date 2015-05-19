package edu.pitt.dbmi.deep.phe.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.Composition;
import org.hl7.fhir.instance.model.DateAndTime;
import org.hl7.fhir.instance.model.Resource;
import org.hl7.fhir.instance.model.ResourceReference;

import edu.pitt.dbmi.deep.phe.util.TextUtils;
import edu.pitt.dbmi.nlp.noble.ontology.IClass;

/**
 * represents a medical document that contains a set of 
 * Diagnosis along with Evidence, Signs and Symptoms
 * @author tseytlin
 *
 */
public class Report extends Composition implements Element{
	private CompositionEventComponent event;
	private List<Element> reportElements;
	
	/**
	 * create a default report object
	 */
	public Report(){
		setStatusSimple(CompositionStatus.final_);
		setLanguageSimple(Utils.DEFAULT_LANGUAGE);
		event = new CompositionEventComponent();
		setEvent(event);
		
	}

	
	/**
	 * set document text
	 * @param text
	 */
	public void setTextSimple(String text){
		setText(Utils.getNarrative(text));
	}
	
	public Composition setTitleSimple(String value) {
		setIdentifier(Utils.createIdentifier(getClass().getSimpleName().toUpperCase()+"_"+TextUtils.stripSuffix(value)));
		return super.setTitleSimple(value);
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

	/**
	 * get report elements
	 * @return
	 */
	public List<Element> getReportElements() {
		if(reportElements == null)
			reportElements = new ArrayList<Element>();
		return reportElements;
	}


	/**
	 * get procedures
	 * @return
	 */
	public List<Procedure> getProcedures() {
		return (List<Procedure>) Utils.getSubList(getReportElements(),Procedure.class);
	}

	/**
	 * get a set of defined diagnoses for this report
	 * @return
	 */
	public List<Diagnosis> getDiagnoses(){
		return (List<Diagnosis>) Utils.getSubList(getReportElements(),Diagnosis.class);
	}
	
	/**
	 * get a set of defined diagnoses for this report
	 * @return
	 */
	public List<Observation> getObservations(){
		return (List<Observation>) Utils.getSubList(getReportElements(),Observation.class);
	}
	
	/**
	 * get a set of defined diagnoses for this report
	 * @return
	 */
	public List<Finding> getFindings(){
		return (List<Finding>) Utils.getSubList(getReportElements(),Finding.class);
	}

	/**
	 * get a set of defined diagnoses for this report
	 * @return
	 */
	public List<Medication> getMedications(){
		return (List<Medication>) Utils.getSubList(getReportElements(),Medication.class);
	}
	
	/**
	 * add diagnosis that this report is describing
	 * @param dx
	 */
	public void addReportElement(Element el){
		if(getReportElements().contains(el))
			return;
		
		// add to list of diagnosis
		getReportElements().add(el);
		el.setReport(this);
			
		// add reference
		ResourceReference r = event.addDetail();
		Utils.getResourceReference(r,el);
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
		for(Finding dx: getFindings()){
			st.append(dx.getSummary()+"\n");
		}
		for(Observation p: getObservations()){
			st.append(p.getSummary()+"\n");
		}
		for(Medication p: getMedications()){
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
	public void save(File dir) throws Exception{
		String id = getIdentifierSimple();
		
		dir = new File(dir,TextUtils.stripSuffix(getTitleSimple()));
		Utils.saveFHIR(this,id,dir);
		
		// go over components
		Patient pt = getPatient();
		if(pt != null)
			pt.save(dir);
		for(Element e: getReportElements()){
			e.save(dir);
		}
	}


	public void setReport(Report r) {
	}
	
	public IClass getConceptClass(){
		return ResourceFactory.getInstance().getOntology().getClass(Utils.COMPOSITION);
	}
	
}
