package edu.pitt.dbmi.deep.phe.model;

import java.io.File;

import org.hl7.fhir.instance.model.*;
import org.hl7.fhir.instance.model.Observation;

import edu.pitt.dbmi.deep.phe.util.TextUtils;
import edu.pitt.dbmi.nlp.noble.coder.model.Mention;

public class Medication extends org.hl7.fhir.instance.model.Medication implements Element {
	protected Identifier identifier;
	public Medication(){
		setLanguageSimple(Utils.DEFAULT_LANGUAGE); // we only care about English
	}
	
	public String getDisplaySimple() {
		return getCode().getTextSimple();
	}

	public String getIdentifierSimple() {
		return Utils.getIdentifier(getIdentifier());
	}
	
	public Identifier getIdentifier() {
		return this.identifier;
	}

	public Medication setIdentifier(Identifier value) {
		this.identifier = value;
		return this;
	}


	public String getSummary() {
		StringBuffer st = new StringBuffer();
		st.append("Medication:\t"+getDisplaySimple());
		return st.toString();
	}
	public Resource getResource() {
		return this;
	}
	/**
	 * initialize 
	 * @param m
	 */
	public void initialize(Mention m){
		setNameSimple(m.getConcept().getName());
		setCode(Utils.getCodeableConcept(m));
		setIdentifier(Utils.createIdentifier(this,m));
	}

	/**
	 * assign report instance and add appropriate information from there
	 */
	public void setReport(Report r) {
	
	}
	public void save(File dir) throws Exception {
		Utils.saveFHIR(this,getIdentifierSimple(),dir);
	}
}
