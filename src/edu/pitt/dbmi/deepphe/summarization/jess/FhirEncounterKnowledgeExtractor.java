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
import edu.pitt.dbmi.deepphe.summarization.jess.kb.SummaryFactory;
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
	
	public void execute() {
		if(patient == null || reports == null)
			return;
		
		// clear previous encounters
		patient.clearEncounters();
		
		// assume that all reports are from a given patient for now
		int n = 0;
		for(Report r: reports){
			// create an encounter representing the document in question
			Encounter encounter = SummaryFactory.getEncounter(r);
			encounter.setPatientId(patient.getId());
			encounter.setSequence(n++);
			patient.addEncounter(encounter);
		}

	}

}
