package edu.pitt.dbmi.deep.phe.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.uima.jcas.JCas;
import org.hl7.fhir.instance.formats.XmlComposer;
import org.hl7.fhir.instance.model.Resource;

import edu.pitt.dbmi.nlp.noble.coder.model.Document;
import edu.pitt.dbmi.nlp.noble.coder.model.Mention;

public class Procedure extends org.hl7.fhir.instance.model.Procedure  implements DeepPheModel{
	public Procedure(){
		Utils.createIdentifier(addIdentifier(),this);
	}
	
	/**
	 * initialize 
	 * @param m
	 */
	public void initialize(Mention m){
		setType(Utils.getCodeableConcept(m.getConcept()));
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
		return getType().getTextSimple();
	}

	public String getIdentifierSimple() {
		return Utils.getIdentifier(getIdentifier());
	}

	public String getSummary() {
		StringBuffer st = new StringBuffer();
		st.append("Procedure:\t"+getDisplaySimple());
		return st.toString();
	}
	public Resource getResource() {
		return this;
	}

	public void saveFHIR(File dir) throws FileNotFoundException, Exception {
		Utils.saveFHIR(this,getIdentifierSimple(),dir);
		
	}
	
}
