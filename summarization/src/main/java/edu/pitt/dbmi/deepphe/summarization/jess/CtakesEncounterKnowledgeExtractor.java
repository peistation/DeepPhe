package edu.pitt.dbmi.deepphe.summarization.jess;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.ctakes.assertion.medfacts.cleartk.ConditionalCleartkAnalysisEngine;
import org.apache.ctakes.assertion.medfacts.cleartk.GenericCleartkAnalysisEngine;
import org.apache.ctakes.assertion.medfacts.cleartk.HistoryCleartkAnalysisEngine;
import org.apache.ctakes.assertion.medfacts.cleartk.PolarityCleartkAnalysisEngine;
import org.apache.ctakes.assertion.medfacts.cleartk.SubjectCleartkAnalysisEngine;
import org.apache.ctakes.assertion.medfacts.cleartk.UncertaintyCleartkAnalysisEngine;
import org.apache.ctakes.clinicalpipeline.ClinicalPipelineFactory;
import org.apache.ctakes.clinicalpipeline.ClinicalPipelineFactory.CopyNPChunksToLookupWindowAnnotations;
import org.apache.ctakes.clinicalpipeline.ClinicalPipelineFactory.RemoveEnclosedLookupWindows;
import org.apache.ctakes.constituency.parser.ae.ConstituencyParser;
import org.apache.ctakes.core.cc.XmiWriterCasConsumerCtakes;
import org.apache.ctakes.dependency.parser.ae.ClearNLPDependencyParserAE;
import org.apache.ctakes.dictionary.lookup.ae.UmlsDictionaryLookupAnnotator;
import org.apache.ctakes.typesystem.type.textsem.IdentifiedAnnotation;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;

import edu.pitt.dbmi.deepphe.summarization.jess.kb.Encounter;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.Patient;

public class CtakesEncounterKnowledgeExtractor implements EncounterKnowledgeExtractor {

	private Patient patient; 
	
	private AnalysisEngine ae = null;
	private CollectionReader collectionReader = null;
	
	@Override
	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	@Override
	public void execute() {
		try {
			buildCtakesAnalysisEngine();
			PatientCollectionReader patientCollectionReader = new PatientCollectionReader();
			patientCollectionReader.setPatient(patient);
			Iterator<Encounter> encounterIterator = patient.getEncounters().iterator();
			while (collectionReader.hasNext() && encounterIterator.hasNext()) {
				Encounter encounter = encounterIterator.next();
				JCas jCas = ae.newJCas();
				collectionReader.getNext(jCas.getCas());
				ae.process(jCas);
				AnnotationIndex<Annotation> annots = jCas
						.getAnnotationIndex(IdentifiedAnnotation.type);
				for (Annotation annot : annots) {
					Type uimaType = annot.getType();
					System.out.println("(" + annot.getBegin() + "," + annot.getEnd() + ") => " + uimaType.getName());
				}
			}
			
		} catch (ResourceInitializationException | InvalidXMLException
				| IOException e) {
			e.printStackTrace();
		} catch (AnalysisEngineProcessException e) {
			e.printStackTrace();
		} catch (CollectionException e) {
			e.printStackTrace();
		}		
	}
	
	private void buildCtakesAnalysisEngine() throws ResourceInitializationException, InvalidXMLException, IOException {
		if (ae == null) {
			File patientNotesXmiDirectory = new File(
					"\\src\\main\\resources\\summarization\\xmi");
			AggregateBuilder builder = new AggregateBuilder();
			builder.add(ClinicalPipelineFactory.getTokenProcessingPipeline());
			builder.add(AnalysisEngineFactory
					.createEngineDescription(CopyNPChunksToLookupWindowAnnotations.class));
			builder.add(AnalysisEngineFactory
					.createEngineDescription(RemoveEnclosedLookupWindows.class));
			builder.add(AnalysisEngineFactory
					.createEngineDescription(ConstituencyParser.class));
			builder.add(UmlsDictionaryLookupAnnotator.createAnnotatorDescription());
			builder.add(AnalysisEngineFactory
					.createEngineDescriptionFromPath("desc\\pae\\TnmAnalysisEngine.xml"));
			builder.add(ClearNLPDependencyParserAE.createAnnotatorDescription());
			builder.add(PolarityCleartkAnalysisEngine.createAnnotatorDescription());
			builder.add(UncertaintyCleartkAnalysisEngine
					.createAnnotatorDescription());
			builder.add(HistoryCleartkAnalysisEngine.createAnnotatorDescription());
			builder.add(ConditionalCleartkAnalysisEngine
					.createAnnotatorDescription());
			builder.add(GenericCleartkAnalysisEngine.createAnnotatorDescription());
			builder.add(SubjectCleartkAnalysisEngine.createAnnotatorDescription());
			builder.add(AnalysisEngineFactory.createEngineDescription(
					XmiWriterCasConsumerCtakes.class,
					XmiWriterCasConsumerCtakes.PARAM_OUTPUTDIR,
					patientNotesXmiDirectory));
			AnalysisEngineDescription aaeDesc = builder
					.createAggregateDescription();
			ae = UIMAFramework.produceAnalysisEngine(aaeDesc);
		}
		
	}

}
