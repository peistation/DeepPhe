package edu.pitt.dbmi.deep.phe.model;

import java.io.File;

import org.hl7.fhir.instance.model.Condition;
import org.hl7.fhir.instance.model.Resource;
import org.hl7.fhir.instance.model.Condition.ConditionStatus;

import edu.pitt.dbmi.nlp.noble.coder.model.Mention;
import edu.pitt.dbmi.nlp.noble.ontology.IClass;

public class Finding extends Condition  implements Element{

	public Finding(){
		setCategory(Utils.CONDITION_CATEGORY_FINDING);
		setLanguageSimple(Utils.DEFAULT_LANGUAGE); // we only care about English
		setStatusSimple(ConditionStatus.confirmed); // here we only deal with 'confirmed' dx
	}
	public String getDisplaySimple() {
		return getCode().getTextSimple();
	}

	public String getIdentifierSimple() {
		return Utils.getIdentifier(getIdentifier());
	}

	public String getSummary() {
		StringBuffer st = new StringBuffer();
		st.append("Finding:\t"+getDisplaySimple());
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
		setCode(Utils.getCodeableConcept(m));
		// create identifier
		Utils.createIdentifier(addIdentifier(),this,m);
				
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
		/*DateAndTime d = r.getDateSimple();
		if( d != null){
			setIssuedSimple(d);
		}*/
	}
	public void save(File dir) throws Exception {
		Utils.saveFHIR(this,getIdentifierSimple(),dir);
	}
	public IClass getConceptClass(){
		return Utils.getConceptClass(getCode());
	}
}
