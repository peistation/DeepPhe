package edu.pitt.dbmi.deep.phe.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.instance.model.*;
import org.hl7.fhir.instance.model.Condition.ConditionLocationComponent;
import org.hl7.fhir.instance.model.Condition.ConditionStatus;

import scala.Array;
import scala.actors.threadpool.Arrays;
import edu.pitt.dbmi.deep.phe.util.TextUtils;



/**
 * This element represents a cancer Phenotype object that contains a set of tumors
 * @author tseytlin
 *
 */
public class Cancer extends Diagnosis {
	protected List<Tumor> tumors = new ArrayList();
	public Cancer(){
		setCategory(Utils.CONDITION_CATEGORY_DIAGNOSIS);
		setLanguageSimple(Utils.DEFAULT_LANGUAGE); // we only care about English
		setStatusSimple(ConditionStatus.confirmed); // here we only deal with 'confirmed' dx
		//Utils.createIdentifier(addIdentifier(),this);
	}
			
	public List<Tumor> getTumors() {
		return this.tumors;
	}

	public Tumor addTumor() {
		Tumor t = new Tumor();
		this.tumors.add(t);
		return t;
	}
	
	/**
	 * Tumor class definition
	 * @author tseytlin
	 */
	public static class Tumor extends BackboneElement  {
		protected CodeableConcept type;
		protected List<ConditionLocationComponent> location = new ArrayList();
		protected List<ConditionEvidenceComponent> genomicFactors = new ArrayList();
		protected List<ConditionEvidenceComponent> treatmentFactors = new ArrayList();
		protected List<ConditionEvidenceComponent> relatedFactors = new ArrayList();
		protected List<ConditionEvidenceComponent> phenotypicFactors = new ArrayList();
		
		
		public List<ConditionEvidenceComponent> getGenomicFactors() {
			return this.genomicFactors;
		}
		public List<ConditionEvidenceComponent> getTreatmentFactors() {
			return this.treatmentFactors;
		}
		public List<ConditionEvidenceComponent> getPhenotypicFactors() {
			return this.phenotypicFactors;
		}
		public List<ConditionEvidenceComponent> getRelatedFactors() {
			return this.relatedFactors;
		}
		public ConditionEvidenceComponent addGenomicFactors() {
			ConditionEvidenceComponent t = new ConditionEvidenceComponent();
			this.genomicFactors.add(t);
			return t;
		}
		public ConditionEvidenceComponent addTreatmentFactors() {
			ConditionEvidenceComponent t = new ConditionEvidenceComponent();
			this.treatmentFactors.add(t);
			return t;
		}
		public ConditionEvidenceComponent addPhenotypicFactors() {
			ConditionEvidenceComponent t = new ConditionEvidenceComponent();
			this.phenotypicFactors.add(t);
			return t;
		}
		public ConditionEvidenceComponent addRelatedFactors() {
			ConditionEvidenceComponent t = new ConditionEvidenceComponent();
			this.relatedFactors.add(t);
			return t;
		}
				
		public List<ConditionLocationComponent> getLocation() {
			return this.location;
		}

		public ConditionLocationComponent addLocation() {
			ConditionLocationComponent t = new ConditionLocationComponent();
			this.location.add(t);
			return t;
		}
		public CodeableConcept getType() {
			return this.type;
		}

		public Tumor setType(CodeableConcept value) {
			this.type = value;
			return this;
		}
		
		public String getSummary() {
			StringBuffer st = new StringBuffer();
			st.append("Tumor:\t"+getType().getTextSimple());
			for(ConditionLocationComponent l: getLocation()){
				st.append(" | location: "+l.getCode().getTextSimple());
			}
			if(!getPhenotypicFactors().isEmpty()){
				st.append("\nPhenotypic Factors\n");
				for(ConditionEvidenceComponent ev: getPhenotypicFactors()){
					st.append("\tFactor:\t"+ev.getCode().getTextSimple()+"\n");
				}
			}
			if(!getTreatmentFactors().isEmpty()){
				st.append("\nTreatment Factors\n");
				for(ConditionEvidenceComponent ev: getTreatmentFactors()){
					st.append("\tFactor:\t"+ev.getCode().getTextSimple()+"\n");
				}
			}
			if(!getGenomicFactors().isEmpty()){
				st.append("\nGenomic Factors\n");
				for(ConditionEvidenceComponent ev: getGenomicFactors()){
					st.append("\tFactor:\t"+ev.getCode().getTextSimple()+"\n");
				}
			}
			if(!getRelatedFactors().isEmpty()){
				st.append("\nRelated Factors\n");
				for(ConditionEvidenceComponent ev: getRelatedFactors()){
					st.append("\tFactor:\t"+ev.getCode().getTextSimple()+"\n");
				}
			}
			return st.toString();
		}
	
		protected void listChildren(List<Property> childrenList) {
			super.listChildren(childrenList);
			childrenList.add(new Property("type", "CodeableConcept","A manifestation or symptom that led to the recording of this condition.", 0, 2147483647,this.type));
			childrenList.add(new Property("location", "", "The anatomical location where this condition manifests itself.", 0, 2147483647, this.location));
			childrenList.add(new Property("phenotypicFactors","","Supporting Evidence / manifestations that are the basis on which this condition is suspected or confirmed.",0, 2147483647, this.phenotypicFactors));
			childrenList.add(new Property("genomicFactors","","Supporting Evidence / manifestations that are the basis on which this condition is suspected or confirmed.",0, 2147483647, this.genomicFactors));
			childrenList.add(new Property("treatmentFactors","","Supporting Evidence / manifestations that are the basis on which this condition is suspected or confirmed.",0, 2147483647, this.treatmentFactors));
			childrenList.add(new Property("relatedFactors","","Supporting Evidence / manifestations that are the basis on which this condition is suspected or confirmed.",0, 2147483647, this.relatedFactors));
		}
		
	}
	
	protected void listChildren(List<Property> childrenList) {
		super.listChildren(childrenList);
		childrenList.add(new Property("tumors","","List of tumors in this Cancer pheonotype",0, 2147483647, this.tumors));
	}
	
	public String getSummary() {
		StringBuffer st = new StringBuffer();
		st.append("Cancer:\t"+getDisplaySimple());
		for(ConditionLocationComponent l: getLocation()){
			st.append(" | location: "+l.getCode().getTextSimple());
		}
		Stage s = getStage();
		if(s != null){
			st.append(" | stage: "+s.getSummary().getTextSimple());
		}
		st.append("\n");
		for(Tumor t: getTumors()){
			st.append(t.getSummary()+"\n");
		}
		return st.toString();
	}
	
	
	public List<Element> getFactorElements(){
		List<Element> list = new ArrayList<Element>();
		for(Tumor t: getTumors()){
		
			List<ConditionEvidenceComponent> evidence = new ArrayList<Condition.ConditionEvidenceComponent>();
			evidence.addAll(t.getPhenotypicFactors());
			evidence.addAll(t.getTreatmentFactors());
			evidence.addAll(t.getGenomicFactors());
			evidence.addAll(t.getRelatedFactors());
			
			
			for(ConditionEvidenceComponent c : evidence){
				for(Resource r: c.getDetailTarget()){
					if(r instanceof Element)
						list.add((Element)r);
				}
			}
			
		}
		return list;
	}
	
	/**
	 * persist this object to a directory
	 * @param dir
	 * @throws Exception 
	 * @throws FileNotFoundException 
	 */
	public void save(File dir) throws Exception{
		super.save(dir);
		
		// go over components
		Patient pt = (Patient) getSubjectTarget();
		if(pt != null)
			pt.save(dir);
		for(Element e: getFactorElements()){
			e.save(dir);
		}
	}
}
