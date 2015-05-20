package edu.pitt.dbmi.deepphe.summarization.jess;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import jess.JessException;
import jess.Rete;
import jess.Value;

import org.apache.commons.io.FileUtils;

import edu.pitt.dbmi.deepphe.summarization.jess.kb.Goal;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.Identified;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.Patient;

public class JessPatientKnowledgeExtractor implements PatientKnowledgeExtractor {

	private JessTextOutputer jessTextOutputer;
	private List<Patient> patients;
	private Iterator<Patient> patientIterator;
	private Rete engine;
	
	private final HashMap<Integer, Identified> knowledgeMap = new HashMap<Integer, Identified>();
	private Patient currentPatient;
	
	public JessPatientKnowledgeExtractor() {
	}

	@Override
	public void execute() {
		try {
			for (Patient patient : patients) {
				currentPatient = patient;
				executePatient();
			}		
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
	
	private void executePatient() throws JessException {
		disassembleDagToKnowledge();
		appendGoalEstablishPlan();
		loadKnowledgeToJess();
		executeJess("(run)");
		assembleKnowledgeToDag();
	}
	
	public void loadSinglePatient() {
		try {
			disassembleDagToKnowledge();
			loadKnowledgeToJess();
		} catch (JessException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void assembleKnowledgeToDag() {
		KnowledgeToDagAssembler assembler = new KnowledgeToDagAssembler();
		assembler.setPatient(currentPatient);	
		Iterator<jess.Fact> factsIterator = getJessEngine().listFacts();
		while (factsIterator.hasNext()) {
			jess.Fact fact = (jess.Fact) factsIterator.next();
			Object factObj = extractObjectSlot(fact);
			if (factObj != null) {
				if (Identified.class.isAssignableFrom(factObj.getClass())) {
					assembler.add((Identified) factObj);
					jessTextOutputer.appendText(factObj.toString());
				}				
			}
		}		
		assembler.execute();	
	}

	private void loadKnowledgeToJess() throws JessException {
		for (Identified identified :  knowledgeMap.values()) {
			System.out.println("Adding a " + identified.getClass().getSimpleName() + " to working memory.");
			engine.add(identified);
		}
	}
	
	private void disassembleDagToKnowledge() {
		DagToKnowledgeDisassembler disassembler = new DagToKnowledgeDisassembler();
		knowledgeMap.clear();
		disassembler.setKnowledgeMap(knowledgeMap);
		disassembler.setPatient(currentPatient);
		disassembler.execute();
	}
	
	private void appendGoalEstablishPlan() {
		Goal goal = new Goal();
		goal.setName("establish-plan");
		goal.setIsActive(1);
		goal.setPriority(0);
		knowledgeMap.put(goal.getId(),goal);
	}

	private Object extractObjectSlot(jess.Fact fact) {
		Object factObj = null;
		try {
			Value v = fact.getSlotValue("OBJECT");
			factObj = v.javaObjectValue(getJessEngine().getGlobalContext());
		}
		catch (Exception x) {
			factObj = null;
		}
		return factObj;
	}

	@Override
	public void clearJess() {
		executeJess("clear");
	}

	@Override
	public void executeJess(String command) {
		try {
			Value result = getJessEngine().eval(command);
			if (!jess.FactIDValue.class.isInstance(result)) {
				jessTextOutputer.appendText(result.stringValue(getJessEngine()
						.getGlobalContext()));
			} else {
				jess.Fact f = result.factValue(getJessEngine().getGlobalContext());
				jessTextOutputer.appendText(f.toStringWithParens() + "\n");
			}

		} catch (JessException e1) {
			jessTextOutputer.displayException(e1);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void displayDeftemplates() {
		Iterator<jess.Deftemplate> defTemplatesIterator = getJessEngine()
				.listDeftemplates();
		while (defTemplatesIterator.hasNext()) {
			jess.Deftemplate template = (jess.Deftemplate) defTemplatesIterator
					.next();
			jessTextOutputer.appendText(template.toString());
		}	
	}

	@SuppressWarnings("unchecked")
	public void displayFacts2() {
		Iterator<jess.Fact> factsIterator = getJessEngine().listFacts();
		while (factsIterator.hasNext()) {
			jess.Fact fact = (jess.Fact) factsIterator.next();
			jessTextOutputer.appendText(fact.toStringWithParens());
		}	
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void displayFacts() {
		Iterator<jess.Fact> factsIterator = getJessEngine().listFacts();
		while (factsIterator.hasNext()) {
			jess.Fact fact = (jess.Fact) factsIterator.next();
			Object factObj = extractObjectSlot(fact);
			if (factObj != null) {
				if (Identified.class.isAssignableFrom(factObj.getClass())) {
					jessTextOutputer.appendText(factObj.toString());
				}				
			}
		}		
	}
	

	@Override
	public void loadProductionClipsFiles() {
		try {
			if (this.engine != null) {
				File productionClipsDirectory = new File(SummarizationGui.PROJECT_LOCATION + File.separator + "summarization/src/main/jess/autoload");
				if (productionClipsDirectory.isDirectory()) {
					List<File> fileList = Arrays.asList(productionClipsDirectory.listFiles());
					Collections.sort(fileList, new Comparator<File>() {
						@Override
						public int compare(File o1, File o2) {
							return o1.getName().compareTo(o2.getName());
						}});
					for (File f : fileList) {
						if (f.getAbsolutePath().endsWith(".clp"));
						String text = FileUtils.readFileToString(f, "utf-8");
						engine.eval(text);
					}
				}
			}
			
		} catch(Exception x) {
			x.printStackTrace();
		}			
	}


	@Override
	public void setJessTextOutputer(JessTextOutputer jessTextOutputer) {
		this.jessTextOutputer = jessTextOutputer;
		
	}
	

	public Rete getJessEngine() {
		return engine;
	}


	@Override
	public void setJessEngine(Rete engine) {
		this.engine = engine;
		
	}

	@Override
	public void setPatients(
			List<edu.pitt.dbmi.deepphe.summarization.jess.kb.Patient> patients) {
		this.patients = patients;
		
	}

	@Override
	public void iteratePatients() {
		patientIterator = patients.iterator();
		
	}

	@Override
	public void nextPatient() {
		currentPatient = patientIterator.next();
		
	}

	@Override
	public boolean hasMorePatients() {	
		return patientIterator.hasNext();
	}



}
