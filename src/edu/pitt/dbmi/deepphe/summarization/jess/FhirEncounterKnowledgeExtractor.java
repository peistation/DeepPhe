package edu.pitt.dbmi.deepphe.summarization.jess;

import java.util.*;

import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.Quantity;

import edu.pitt.dbmi.deep.phe.model.Observation;
import edu.pitt.dbmi.deep.phe.model.Report;
import edu.pitt.dbmi.deep.phe.model.Stage;
import edu.pitt.dbmi.deep.phe.model.Utils;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.Diagnosis;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.Encounter;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.Er;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.Her2;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.Patient;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.Pr;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.Summarizable;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.Summary;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.TnmMgrade;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.TnmNgrade;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.TnmTgrade;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.TumorSize;

/**
 * fill in Summarization Patient/Encounter objects from FHIR objects
 * @author tseytlin
 *
 */

public class FhirEncounterKnowledgeExtractor implements EncounterKnowledgeExtractor {
	private Patient patient;
	private List<Report> reports;
	
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	public void setFHIR_Reports(List<Report> reports){
		this.reports = reports;
	}
	
	/**
	 * 
	 * @param code
	 * @param s
	 */
	private void setSummaryFromCode(CodeableConcept code, Summary s){
		String cui = Utils.getConceptCode(code);
		String name = Utils.getConceptName(code);
		s.setCode(cui);
		s.setBaseCode(cui);
		s.setPreferredTerm(name);
	}
	
	
	public void execute() {
		if(patient == null || reports == null)
			return;
		
		// clear previous encounters
		patient.clearEncounters();
		
		// assume that all reports are from a given patient for now
		int n = 0;
		for(Report r: reports){
			// create an encounter representing the document in question
			Encounter encounter = new Encounter();
			encounter.setPatientId(patient.getId());
			encounter.setContent(r.getTextSimple());
			encounter.setSequence(n++);
			encounter.setKind(r.getType().getTextSimple());
			encounter.setUri(r.getIdentifierSimple());
			patient.addEncounter(encounter);
			
			// add report elements to encounter
			for(edu.pitt.dbmi.deep.phe.model.Diagnosis d: r.getDiagnoses()){
				// add diagnosis
				Diagnosis diagnosis = new Diagnosis();
				diagnosis.setSummarizableId(encounter.getId());
				setSummaryFromCode(d.getCode(),diagnosis);
				encounter.addSummary(diagnosis);
				
				// if there is a stage associated, then add it as well
				Stage st = d.getStage();
				if(st != null){
					TnmTgrade tnmTgrade = new TnmTgrade();
					setSummaryFromCode(st.getPrimaryTumorStageCode(),tnmTgrade);
					tnmTgrade.setGroupIndex(1);
					tnmTgrade.setProvidingDepartment("Clinical");
					tnmTgrade.setSummarizableId(encounter.getId());
					tnmTgrade.setUnitOfMeasure("NA");
					tnmTgrade.setValue(st.getPrimaryTumorStage());
					encounter.addSummary(tnmTgrade);
					
					TnmNgrade tnmNgrade = new TnmNgrade();
					setSummaryFromCode(st.getRegionalLymphNodeStageCode(),tnmNgrade);
					tnmNgrade.setGroupIndex(1);
					tnmNgrade.setProvidingDepartment("Clinical");
					tnmNgrade.setSummarizableId(encounter.getId());
					tnmNgrade.setUnitOfMeasure("NA");
					tnmNgrade.setValue(st.getRegionalLymphNodeStage());
					encounter.addSummary(tnmNgrade);
					
					TnmMgrade tnmMgrade = new TnmMgrade();
					setSummaryFromCode(st.getDistantMetastasisStageCode(), tnmMgrade);
					tnmMgrade.setGroupIndex(1);
					tnmMgrade.setProvidingDepartment("Clinical");
					tnmMgrade.setSummarizableId(encounter.getId());
					tnmMgrade.setUnitOfMeasure("NA");
					tnmMgrade.setValue(st.getDistantMetastasisStage());
					encounter.addSummary(tnmMgrade);
				}
				
			}
			// add observations
			for(Observation o: r.getObservations()){
				String name = o.getName().getTextSimple();
				Summary obs = null;
				if(name.toLowerCase().contains("estrogen receptor"))
					obs = new Er();
				else if(name.toLowerCase().contains("progesterone receptor"))
					obs = new Pr();
				else if(name.toLowerCase().contains("her2"))
					obs = new Her2();
				else if(name.toLowerCase().contains("tumor size")){
					obs = new TumorSize();
					Quantity q = (Quantity) o.getValue();
					((TumorSize)obs).setGreatestDimension(q.getValueSimple().doubleValue());
					((TumorSize)obs).setUnitOfMeasure(q.getUnitsSimple());
				}
			
				obs.setSummarizableId(encounter.getId());
				obs.setValue(o.getValueSimple());
				setSummaryFromCode(o.getName(),obs);
				encounter.addSummary(obs);
			}
		}

	}

}
