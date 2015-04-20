package edu.pitt.dbmi.deep.phe.summary;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import edu.pitt.dbmi.deep.phe.i2b2.DataDbManager;
import edu.pitt.dbmi.deep.phe.i2b2.I2b2OntologyBuilder;
import edu.pitt.dbmi.deep.phe.i2b2.PartialPath;
import edu.pitt.dbmi.deep.phe.model.Diagnosis;
import edu.pitt.dbmi.deep.phe.model.Report;
import edu.pitt.dbmi.deep.phe.model.ResourceFactory;
import edu.pitt.dbmi.nlp.noble.coder.NobleCoder;
import edu.pitt.dbmi.nlp.noble.coder.model.Document;
import edu.pitt.dbmi.nlp.noble.ontology.IOntology;
import edu.pitt.dbmi.nlp.noble.ontology.IOntologyException;
import edu.pitt.dbmi.nlp.noble.ontology.owl.OOntology;
import edu.pitt.dbmi.nlp.noble.terminology.TerminologyException;
import edu.pitt.dbmi.nlp.noble.terminology.impl.NobleCoderTerminology;

/**
 * Iterates back in time across patient notes to discover first meaningful
 * TNM scores.  Definitive scores for clinical and pathological TNM assessments are
 * propagated to I2B2 for subsequent analysis.  Also generates FHIR XML representations
 * for each encounter.
 * 
 * @author kjm84
 *
 */
public class I2B2DocumentSummarizer {
	
	private static final String CONST_PROJECT_PATH = "C:\\Users\\kjm84\\git\\DeepPhe\\";
	private static final String CONST_ONTOLOGY_PATH = "ontologies\\BreastCancer.owl";
	private static final String CONST_FHIR_OUTPUT_PATH = "data\\sample\\fhir";
	private static final String CONST_EHR_CORPUS_PATH = "data\\sample\\docs";
	
	private File projectDirectory;
	
	private File fhirOutputDirectory = new File(projectDirectory,CONST_FHIR_OUTPUT_PATH);
	private File ontologyPath = new File(projectDirectory,CONST_ONTOLOGY_PATH);//breastCAEx.owl
	
	private ResourceFactory resourceFactory;
	private IOntology ontology;
	private NobleCoder coder;
	
	public static void main(String [] args ) throws Exception{
		I2B2DocumentSummarizer i2b2DocumentSummarizer = new I2B2DocumentSummarizer();
		i2b2DocumentSummarizer.initialize();
		i2b2DocumentSummarizer.execute();
	}
	
	private void initialize() throws IOntologyException, IOException, TerminologyException {
		projectDirectory = new File(CONST_PROJECT_PATH);
		ontologyPath = new File(projectDirectory,CONST_ONTOLOGY_PATH);//breastCAEx.owl
		fhirOutputDirectory = new File(projectDirectory,CONST_FHIR_OUTPUT_PATH);		
		System.out.println("loading ontology .."+ontologyPath.getName());
		ontology = OOntology.loadOntology(ontologyPath);
		resourceFactory = new ResourceFactory(ontology);
		coder = new NobleCoder(new NobleCoderTerminology(ontology));	
	}

	private void execute() throws Exception {
		final List<Report> reports = extractInformationForDiscreteEncounters();
		String tStage = null;
		String nStage = null;
		String mStage = null;
		for (int reportIdx = reports.size() - 1; reportIdx >= 0; reportIdx--) {
			Report report = reports.get(reportIdx);
			for (Diagnosis diagnosis : report.getDiagnoses()) {
				if (diagnosis.getStage() != null) {					
					 tStage = "umls:C1707031";
	                 nStage = "umls:C1707010";
	                 mStage = "umls:C2981890";	
	                 break;
				}
			}
			if (tStage != null) {
				break;
			}
		}
		
		I2b2OntologyBuilder ontologyBuilder = new I2b2OntologyBuilder();
		ontologyBuilder.setOntologyPath(ontologyPath.getAbsolutePath());
		ontologyBuilder.execute();
		final HashMap<String,PartialPath> partialPathMap = ontologyBuilder.getPartialPathMap();
		final TreeSet<PartialPath> partialPaths = new TreeSet<PartialPath>();
		PartialPath tStageConcept = partialPathMap.get(tStage);
		PartialPath nStageConcept = partialPathMap.get(nStage);
		PartialPath mStageConcept = partialPathMap.get(mStage);
		partialPaths.add(tStageConcept);
		partialPaths.add(nStageConcept);
		partialPaths.add(mStageConcept);
	
			
		DataDbManager dataDbManager = new DataDbManager();
		dataDbManager.setPatientNum(4001000);
		dataDbManager.setOntologyConcepts(partialPaths);
		dataDbManager.execute();
		
		System.out.println("done");
		
	}
	
	private List<Report> extractInformationForDiscreteEncounters() throws Exception {
		final File [] timeSortedFileDescriptors = new File(projectDirectory,CONST_EHR_CORPUS_PATH).listFiles();
		Arrays.sort(timeSortedFileDescriptors);
		final List<Report> reports = new ArrayList<Report>();
		for(File file: timeSortedFileDescriptors){
			System.out.println("coding document .."+file.getName());			
			Document doc = coder.process(file);
			System.out.println("generating summary ..");
			Report report = resourceFactory.getReport(doc);
			System.out.println(report.getSummary());
			report.save(fhirOutputDirectory);	
			reports.add(report);
		}
		return reports;
	}

	public I2B2DocumentSummarizer(){		
	}
		
}
