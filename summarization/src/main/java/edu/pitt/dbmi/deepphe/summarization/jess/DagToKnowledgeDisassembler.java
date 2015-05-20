package edu.pitt.dbmi.deepphe.summarization.jess;

import java.util.HashMap;

import edu.pitt.dbmi.deepphe.summarization.jess.kb.Encounter;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.Identified;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.Patient;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.Summary;

public class DagToKnowledgeDisassembler {

	private HashMap<Integer, Identified> knowledgeMap;
	private Patient patient;

	public void execute() {
		knowledgeMap.clear();
		knowledgeMap.put(patient.getId(), patient);
		for (Summary summary : patient.getSummaries()) {
			knowledgeMap.put(summary.getId(), summary);
		}
		for (Encounter encounter : patient.getEncounters()) {
			knowledgeMap.put(encounter.getId(), encounter);
			for (Summary summary : encounter.getSummaries()) {
				knowledgeMap.put(summary.getId(), summary);
			}
		}
	}
	
	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}


	public HashMap<Integer, Identified> getKnowledgeMap() {
		return knowledgeMap;
	}

	public void setKnowledgeMap(HashMap<Integer, Identified> knowledgeMap) {
		this.knowledgeMap = knowledgeMap;
	}

}
