package edu.pitt.dbmi.deep.phe.model;

import java.io.File;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hl7.fhir.instance.model.*;

import edu.pitt.dbmi.nlp.noble.coder.model.Mention;

/**
 * Observation object
 * @author tseytlin
 */
public class Observation extends org.hl7.fhir.instance.model.Observation implements Element{
	private Report report;
	
	public Observation(){
		setStatusSimple(ObservationStatus.final_);
		setLanguageSimple(Utils.DEFAULT_LANGUAGE); // we only care about English

	}
	
	public String getDisplaySimple() {
		return getName().getTextSimple();
	}

	public String getIdentifierSimple() {
		return Utils.getIdentifier(getIdentifier());
	}

	public String getSummary() {
		StringBuffer st = new StringBuffer();
		st.append("Observation:\t"+getDisplaySimple());
		StringType t = getValue();
		if(t != null)
			st.append(" | value: "+t.getValue());
		return st.toString();
	}
	public Resource getResource() {
		return this;
	}

	public void setReport(Report r) {
		report = r;
		// set patient
		Patient p = r.getPatient();
		if(p != null){
			setSubject(Utils.getResourceReference(p));
			setSubjectTarget(p);
		}
		// set date
		DateAndTime d = r.getDateSimple();
		if( d != null){
			setIssuedSimple(d);
		}
	}

	/**
	 * initialize 
	 * @param m
	 */
	public void initialize(Mention m){
		// set name for this observation
		setName(Utils.getCodeableConcept(m));
		
		// create identifier
		setIdentifier(Utils.createIdentifier(this,m));
				
		
		//TODO: this should be defined in the ontology
		// extract value if available
		// for NOW, lets just hard-coded it
		String text = m.getText();
		Pattern pp = Pattern.compile("(?i)(\\d*\\.\\d+)\\s*([cm]{1,2})");
		Matcher mm = pp.matcher(text);
		if(mm.find()){
			setValue(new StringType(mm.group()));
		}
		// set positive/negative
		for(String st: Arrays.asList("Positive", "Negative","Unknown")){
			if(m.getConcept().getName().endsWith(st)){
				setValue(new StringType(st));
				break;
			}
		}
	}
	
	public StringType getValue(){
		return (StringType) super.getValue();
	}
	
	
	public void save(File dir) throws Exception {
		Utils.saveFHIR(this,getIdentifierSimple(),dir);
	}

}
