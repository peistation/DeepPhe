package edu.pitt.dbmi.deep.phe.summary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.ctakes.cancer.pipelines.CancerPipelineRunner;
import org.apache.ctakes.clinicalpipeline.ClinicalPipelineFactory;
import org.apache.ctakes.core.cc.XmiWriterCasConsumerCtakes;
import org.apache.ctakes.core.cr.FilesInDirectoryCollectionReader;
import org.apache.ctakes.core.util.CtakesFileNamer;
import org.apache.ctakes.core.util.JCasUtil;
import org.apache.ctakes.typesystem.type.refsem.OntologyConcept;
import org.apache.ctakes.typesystem.type.refsem.OntologyConcept_Type;
import org.apache.ctakes.typesystem.type.structured.Demographics;
import org.apache.ctakes.typesystem.type.textsem.DiseaseDisorderMention;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.*;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.*;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.cleartk.util.cr.FilesCollectionReader;
import org.hl7.fhir.instance.formats.XmlComposer;
import org.hl7.fhir.instance.formats.XmlComposerBase;
import org.hl7.fhir.instance.model.Composition;
import org.hl7.fhir.instance.model.Resource;

import edu.pitt.dbmi.deep.phe.model.*;
import edu.pitt.dbmi.deep.phe.util.TextUtils;
import edu.pitt.dbmi.deepphe.summarization.jess.FhirEncounterKnowledgeExtractor;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.Encounter;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.Patient;
import edu.pitt.dbmi.nlp.noble.coder.NobleCoder;
import edu.pitt.dbmi.nlp.noble.coder.model.Document;
import edu.pitt.dbmi.nlp.noble.ontology.IOntology;
import edu.pitt.dbmi.nlp.noble.ontology.owl.OOntology;
import edu.pitt.dbmi.nlp.noble.terminology.impl.NobleCoderTerminology;
import edu.pitt.dbmi.nlp.noble.tools.TextTools;

/**
 * this class uses a processesed annotated document
 * and produces a document level summary on the Encounter level.
 * @author tseytlin
 *
 */
public class DocumentSummarizer {
	private ResourceFactory resourceFactory;

	public DocumentSummarizer(IOntology ontology){
		resourceFactory = new ResourceFactory(ontology);
	}
	
	
	/**
	 * process document and produce
	 * @param document
	 * @return
	 * @throws ResourceInitializationException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws CASRuntimeException 
	 * @throws AnalysisEngineProcessException 
	 */
	public JCas process(File document) throws ResourceInitializationException, CASRuntimeException, FileNotFoundException, IOException, AnalysisEngineProcessException{
		AnalysisEngineDescription clinicalPipeDescriptor = ClinicalPipelineFactory.getDefaultPipeline();
		AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(clinicalPipeDescriptor);
		JCas jcas = ae.newJCas();
		jcas.setDocumentText(TextTools.getText(new FileInputStream(document)));
		ae.process(jcas);
		return jcas;
	}
	
	/**
	 * load CAS object from XMO
	 * @param document
	 * @param typesystem
	 * @return
	 * @throws UIMAException
	 * @throws IOException
	 */
	public JCas loadCAS(File document, File typesystem) throws UIMAException, IOException{
		TypeSystemDescription tp = TypeSystemDescriptionFactory.createTypeSystemDescriptionFromPath(typesystem.getAbsolutePath());
		return JCasFactory.createJCas(document.getAbsolutePath(),tp);
	}
	
	/**
	 * Create Report object from NobleCoder annotated document
	 * @param doc
	 * @return
	 */
	public Report process(Document doc){
		return resourceFactory.getReport(doc);
	}
	
	/**
	 * Create Report object from NobleCoder annotated document
	 * @param doc
	 * @return
	 */
	public Report process(JCas cas){
		return resourceFactory.getReport(cas);
	}
	
	/**
	 * test out this 
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws ResourceInitializationException 
	 * @throws CASRuntimeException 
	 * @throws AnalysisEngineProcessException 
	 */
	
	public static void main(String [] args ) throws Exception{
		File project = new File("/home/tseytlin/Work/DeepPhe/");
		File ontology = new File(project,"ontologies/breastCancer.owl");//breastCAEx.owl
		File sample = new File(project,"data/sample");
		File out = new File(sample,"fhir");
		File types = new File(project,"data/desc/TypeSystem.xml");
		
		// load ontology
		System.out.println("loading ontology .."+ontology.getName());
		IOntology ont = OOntology.loadOntology(ontology);
		DocumentSummarizer summarizer = new DocumentSummarizer(ont);
		
		File [] docs = new File(sample,"xmi").listFiles();
		Arrays.sort(docs);
		// process reports
		List<Report> reports = new ArrayList<Report>();
		for(File file: docs){
			System.out.println("reading XMI file .."+file.getName());
			JCas cas = summarizer.loadCAS(file,types);
			System.out.println("generating summary ..");;
			Report report = summarizer.process(cas);
			report.setTitleSimple(TextUtils.stripSuffix(file.getName()));
			report.save(new File(out,"CT"));
			System.out.println(report.getSummary());
			reports.add(report);
		}
		
		
		// process reports using NobleCoder
		NobleCoder coder = new NobleCoder(new NobleCoderTerminology(ont));
		docs = new File(sample,"docs").listFiles();
		Arrays.sort(docs);
		for(File file: docs){
			System.out.println("coding document .."+file.getName());
			Document doc = coder.process(file);
			System.out.println("generating summary ..");
			Report report = summarizer.process(doc);
			System.out.println(report.getSummary());
			report.save(new File(out,"NC"));
		}
		
		
		// generate the reasoning objects
		System.out.println("generating reasoning models ..");
		Patient p = new Patient();
		FhirEncounterKnowledgeExtractor ext = new FhirEncounterKnowledgeExtractor();
		ext.setPatient(p);
		//ext.setFHIR_Reports(reports);
		ext.execute();
		System.out.println("\tpatient:\t"+p.fetchInfo());
		for(Encounter e: p.getEncounters()){
			System.out.println("\tencounter:\t"+e.fetchInfo());
		}
		
		
		System.out.println("done");
	}
	
	
}
