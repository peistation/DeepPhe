package edu.pitt.dbmi.deepphe.summarization.jess;

import java.util.HashMap;
import java.util.List;

import edu.pitt.dbmi.deepphe.summarization.jess.kb.Encounter;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.Identified;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.Patient;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.Summarizable;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.Summary;

public class KnowledgeToDagAssembler {
	
	private Patient patient;
	private HashMap<Integer, Identified> knowledgeMap = new HashMap<>();
	
	public void execute() {
		clearPatientAssembly();
		clearSummarizableAssembly();
		assemblePatientEncounters();
		assembleSummaries();
	}
	
	private void clearPatientAssembly() {
		for (Identified identified : knowledgeMap.values()) {
			if (Patient.class.isInstance(identified)) {
				patient = (Patient) identified;
				patient.clearEncounters();
				break;  // should only be one patient
			}
		}
	}
	
	private void clearSummarizableAssembly() {
		for (Identified identified : knowledgeMap.values()) {
			if (Summarizable.class.isInstance(identified)) {
				Summarizable summarizable = (Summarizable) identified;
				summarizable.clearSummaries();
			}
		}
	}

	private void assemblePatientEncounters() {
		for (Identified identified : knowledgeMap.values()) {
			if (Encounter.class.isInstance(identified)) {
				Encounter encounter = (Encounter) identified;
				Patient patient = (Patient) knowledgeMap.get(encounter.getPatientId());
				patient.addEncounter(encounter);
			}
		}
	}

	private void assembleSummaries() {
		for (Identified identified : knowledgeMap.values()) {
			if (Summary.class.isInstance(identified)) {
				Summary summary = (Summary) identified;
				Summarizable summarizable = (Summarizable) knowledgeMap.get(summary.getSummarizableId());
				summarizable.addSummary(summary);
			}
		}
	}
	
	public void add(Identified identified) {
		knowledgeMap.put(identified.getId(), identified);
	}
	
	public void addAll(List<Identified> identifiedsForMapping) {
		for (Identified identified : identifiedsForMapping) {
			knowledgeMap.put(identified.getId(), identified);
		}
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

}
