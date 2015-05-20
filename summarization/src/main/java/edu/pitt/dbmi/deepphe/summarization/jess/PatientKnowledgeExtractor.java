package edu.pitt.dbmi.deepphe.summarization.jess;

import java.util.List;

import edu.pitt.dbmi.deepphe.summarization.jess.kb.Patient;
import jess.Rete;


public interface PatientKnowledgeExtractor {
	public void setPatients(List<Patient> patients);
	public void iteratePatients();
	public void nextPatient();
	public boolean hasMorePatients();
	public void loadSinglePatient();
	public void setJessEngine(Rete jessRete);	
	public void execute();
	public void clearJess();
	public void executeJess(String command);
	public void displayFacts();
	public void displayDeftemplates();
	public void setJessTextOutputer(JessTextOutputer outputer);
	public void loadProductionClipsFiles();
	

}
