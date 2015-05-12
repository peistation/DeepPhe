package edu.pitt.dbmi.deep.phe.summary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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
	
	public JCas loadCAS(File document) throws UIMAException, IOException{
		String ts = "/home/tseytlin/Work/ctakes/ctakes-type-system/src/main/resources/org/apache/ctakes/typesystem/types/TypeSystem.xml";
		TypeSystemDescription tp = TypeSystemDescriptionFactory.createTypeSystemDescriptionFromPath(ts);
		return JCasFactory.createJCas(document.getAbsolutePath(),tp);
	}
	

	/**
	 * processing CAS and build a FHIR object model out of it
	 * @param cas
	 * @return
	 *
	public Report process(JCas cas){
		Report report = null;
		// process given CAS document
		Iterator<Annotation> it = cas.getAnnotationIndex(DocumentAnnotation.type).iterator();
		if(it.hasNext()){
			report = resourceFactory.getReport(it.next());
			if(report != null){
				// now examine varies types of annotations and how they can relate
				Demographics demo = getPrimaryPatient(cas);
				if(demo != null){
					//Patient patient = resourceFactory.getPatient(demo);
					
				}
			}
		}
		return null;
	}
	*/
	
	
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
		//File ontology = new File("/home/tseytlin/Data/DeepPhe/Model/BreastCancerModel.owl");
		File project = new File("/home/tseytlin/Work/DeepPhe/");
		File ontology = new File(project,"ontologies/breastCancer.owl");//breastCAEx.owl
		//File ontology = new File(project,"data/sample/ontology/BreastCancerModel.owl");
		File out = new File(project,"data/sample/fhir");
		File [] docs = new File(project,"data/sample/docs").listFiles();
		Arrays.sort(docs);
		
		// process reports
		/*DocumentSummarizer summarizer = new DocumentSummarizer();
		for(File f: dir.listFiles()){
			JCas cas = summarizer.process(f);
			
		}*/
		
		System.out.println("loading ontology .."+ontology.getName());
		IOntology ont = OOntology.loadOntology(ontology);
		NobleCoder coder = new NobleCoder(new NobleCoderTerminology(ont));
		DocumentSummarizer summarizer = new DocumentSummarizer(ont);
		for(File file: docs){
			System.out.println("coding document .."+file.getName());
			Document doc = coder.process(file);
			System.out.println("generating summary ..");
			Report report = summarizer.process(doc);
			System.out.println(report.getSummary());
			report.save(out);
		}
		
		
		
		
		//JCas cas = summarizer.loadCAS(file_cas);
		//DocumentElement doc = summarizer.process(cas);
		//System.out.println(doc.getText());
		//summarizer.saveFHIR(doc.getModel(),file_fhir);
		/*for(DiagnosisElement dx : doc.getDiagnoses()){
			summarizer.saveFHIR(dx.getModel(),new File(file_fhir.getParentFile(),dx.getName()+".xml"));
		}*/
		
		System.out.println("done");
	}
	
	
}
