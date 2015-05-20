package edu.pitt.dbmi.deepphe.summarization.jess;

import java.io.File;
import java.util.*;

import org.apache.uima.jcas.JCas;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.Quantity;

import edu.pitt.dbmi.deep.phe.model.Observation;
import edu.pitt.dbmi.deep.phe.model.Report;
import edu.pitt.dbmi.deep.phe.model.Stage;
import edu.pitt.dbmi.deep.phe.model.Utils;
import edu.pitt.dbmi.deep.phe.summary.DocumentSummarizer;
import edu.pitt.dbmi.deep.phe.util.TextUtils;
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
import edu.pitt.dbmi.nlp.noble.ontology.IOntology;
import edu.pitt.dbmi.nlp.noble.ontology.owl.OOntology;

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
	
	public void execute() {
		//TODO: THIS SHOULD NOT BE HARD-CODED IN THE FUTURE!!!!
		File project = new File(SummarizationGui.PROJECT_LOCATION);
		File ontology = new File(project,"ontologies"+File.separator+"breastCancer.owl");//breastCAEx.owl
		File sample = new File(project,"data"+File.separator+"sample");
		File types = new File(project,"data"+File.separator+"desc"+File.separator+"TypeSystem.xml");
		
		// load ontology
		try{
			System.out.println("loading ontology .."+ontology.getName());
			IOntology ont = OOntology.loadOntology(ontology);
			DocumentSummarizer summarizer = new DocumentSummarizer(ont);
			
			File [] docs = new File(sample,"xmi").listFiles();
			Arrays.sort(docs);
			// process reports
			reports = new ArrayList<Report>();
			for(File file: docs){
				System.out.println("reading XMI file .."+file.getName());
				JCas cas = summarizer.loadCAS(file,types);
				System.out.println("generating summary ..");;
				Report report = summarizer.process(cas);
				report.setTitleSimple(TextUtils.stripSuffix(file.getName()));
				System.out.println(report.getSummary());
				reports.add(report);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
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
