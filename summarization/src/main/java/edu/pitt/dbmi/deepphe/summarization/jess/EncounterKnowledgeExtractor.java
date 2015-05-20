package edu.pitt.dbmi.deepphe.summarization.jess;

import edu.pitt.dbmi.deepphe.summarization.jess.kb.Patient;

public interface EncounterKnowledgeExtractor {
	public void setPatient(Patient patient);
	public void execute();
	
}
