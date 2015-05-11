package edu.pitt.dbmi.deep.phe.model;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.instance.model.*;



/**
 * This element represents a cancer Phenotype object that contains a set of tumors
 * @author tseytlin
 *
 */
public class Cancer extends Diagnosis {
	protected List<Tumor> tumors = new ArrayList();
	
			
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
	}
	
	
}
