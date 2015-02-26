package edu.pitt.dbmi.deep.phe.summary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.ctakes.clinicalpipeline.ClinicalPipelineFactory;
import org.apache.ctakes.core.cc.XmiWriterCasConsumerCtakes;
import org.apache.ctakes.core.cr.FilesInDirectoryCollectionReader;
import org.apache.ctakes.core.util.CtakesFileNamer;
import org.apache.ctakes.core.util.JCasUtil;
import org.apache.ctakes.typesystem.type.refsem.OntologyConcept;
import org.apache.ctakes.typesystem.type.refsem.OntologyConcept_Type;
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
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.cleartk.util.cr.FilesCollectionReader;
import org.hl7.fhir.instance.formats.XmlComposer;
import org.hl7.fhir.instance.formats.XmlComposerBase;
import org.hl7.fhir.instance.model.Composition;
import org.hl7.fhir.instance.model.Resource;

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
	
	public void saveFHIR(Resource r, File file) throws FileNotFoundException, Exception{
		XmlComposer xml = new XmlComposer();
		xml.compose(new FileOutputStream(file), r, true);
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
		File file = new File("/home/tseytlin/Data/DeepPhe/Model/reports/doc1.txt");
		File file_cas = new File("/home/tseytlin/Data/DeepPhe/Model/reports_cas/doc1.xmi");
		File file_fhir = new File("/home/tseytlin/Data/DeepPhe/Model/reports_fhir/doc1.xml");
		
		DocumentSummarizer summarizer = new DocumentSummarizer();
		JCas cas = summarizer.loadCAS(file_cas);
		//DocumentElement doc = summarizer.process(cas);
		//System.out.println(doc.getText());
		//summarizer.saveFHIR(doc.getModel(),file_fhir);
		/*for(DiagnosisElement dx : doc.getDiagnoses()){
			summarizer.saveFHIR(dx.getModel(),new File(file_fhir.getParentFile(),dx.getName()+".xml"));
		}*/
		
		System.out.println("done");
	}
	
	
}
