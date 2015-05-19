package edu.pitt.dbmi.deep.phe.model;

import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ctakes.cancer.type.textsem.CancerSize;
import org.apache.ctakes.cancer.type.textsem.ReceptorStatus;
import org.apache.ctakes.cancer.type.textsem.SizeMeasurement;
import org.apache.ctakes.typesystem.type.textsem.AnatomicalSiteMention;
import org.apache.ctakes.typesystem.type.textsem.IdentifiedAnnotation;
import org.apache.ctakes.typesystem.type.textsem.ProcedureMention;
import org.apache.uima.jcas.cas.FSArray;
import org.hibernate.type.FloatType;
import org.hl7.fhir.instance.model.*;

import edu.pitt.dbmi.nlp.noble.coder.model.Mention;
import edu.pitt.dbmi.nlp.noble.ontology.IClass;

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
		return "Observation:\t"+getDisplaySimple()+" | value: "+getValueSimple();
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
			setValue(mm.group(1),mm.group(2));
		}
		// set positive/negative
		for(String st: Arrays.asList("Positive", "Negative","Unknown")){
			if(m.getConcept().getName().endsWith(st)){
				setValue(new StringType(st));
				break;
			}
		}
	}
	
	/**
	 * Initialize diagnosis from a DiseaseDisorderMention in cTAKES typesystem
	 * @param dx
	 */
	public void initialize(IdentifiedAnnotation dm){
		// set some properties
		setName(Utils.getCodeableConcept(dm));
		setIdentifier(Utils.createIdentifier(this,dm));
		
		// extract value using free text if necessary
		String text = dm.getCoveredText();
		Pattern pp = Pattern.compile("(?i)(\\d*\\.\\d+)\\s*([cm]{1,2})");
		Matcher mm = pp.matcher(text);
		if(mm.find()){
			setValue(mm.group(1),mm.group(2));
		}
		
		// set positive/negative
		for(String st: Arrays.asList("Positive", "Negative","Unknown")){
			if(Utils.getConceptName(dm).contains(st)){
				setValue(new StringType(st));
				break;
			}
		}
		
		// if cancer size, then use their value
		if(dm instanceof CancerSize){
			FSArray arr = ((CancerSize)dm).getMeasurements();
			if(arr != null){
				for(int i=0;i<arr.size();i++){
					SizeMeasurement sm = (SizeMeasurement) arr.get(i);
					setValue(sm);
					break;
				}
			}
		}
	}

	public void setValue(SizeMeasurement num){
		setValue(num.getValue(),num.getUnit());
		String ident = getClass().getSimpleName().toUpperCase()+"_"+getDisplaySimple(); 
		setIdentifier(Utils.createIdentifier((ident+"_"+getValueSimple()).replaceAll("\\W+","_")));
	}
	
	public void setValue(String value, String unit){
		setValue(Double.parseDouble(value),unit);
		String ident = getClass().getSimpleName().toUpperCase()+"_"+getDisplaySimple(); 
		setIdentifier(Utils.createIdentifier((ident+"_"+getValueSimple()).replaceAll("\\W+","_")));
	}
	
	public void setValue(double value, String unit){
		Quantity q = new Quantity();
		q.setValueSimple(new BigDecimal(value,MathContext.DECIMAL32));
		q.setUnitsSimple(unit);
		setValue(q);
	}
	public IClass getConceptClass(){
		return Utils.getConceptClass(getName());
	}
	public String getValueSimple(){
		Type t = getValue();
		if(t != null){
			if( t instanceof StringType)
				return ((StringType)t).getValue();
			else if( t instanceof DecimalType)
				return ""+((DecimalType)t).getValue();
			else if( t instanceof Quantity)
				return ((Quantity)t).getValueSimple().doubleValue()+" "+((Quantity)t).getUnitsSimple();
			else
				return t.toString();
		}
		return null;
	}
	
	
	public void save(File dir) throws Exception {
		Utils.saveFHIR(this,getIdentifierSimple(),dir);
	}

}
