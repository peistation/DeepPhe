package org.apache.ctakes.cancer.ae;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.ctakes.assertion.medfacts.cleartk.PolarityCleartkAnalysisEngine;
import org.apache.ctakes.clinicalpipeline.ClinicalPipelineFactory;
import org.apache.ctakes.clinicalpipeline.ClinicalPipelineFactory.CopyNPChunksToLookupWindowAnnotations;
import org.apache.ctakes.clinicalpipeline.ClinicalPipelineFactory.RemoveEnclosedLookupWindows;
import org.apache.ctakes.clinicalpipeline.ae.ExtractionPrepAnnotator;
import org.apache.ctakes.dependency.parser.ae.ClearNLPDependencyParserAE;
import org.apache.ctakes.dictionary.lookup.ae.UmlsDictionaryLookupAnnotator;
import org.apache.ctakes.temporal.ae.BackwardsTimeAnnotator;
import org.apache.ctakes.temporal.ae.DocTimeRelAnnotator;
import org.apache.ctakes.temporal.ae.EventAnnotator;
import org.apache.ctakes.typesystem.type.refsem.Event;
import org.apache.ctakes.typesystem.type.refsem.EventProperties;
import org.apache.ctakes.typesystem.type.textsem.EventMention;
import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import com.google.common.collect.Lists;

public class CancerPipelineTest {

	// LOG4J logger based on class name
	private Logger LOGGER = Logger.getLogger(getClass().getName());

	@Test
	public void test() throws Exception {

		// String note = "patient took 50mg of aspirin for pain in right knee.";
		File dir = new File("../apache-ctakes/trunk/ctakes-examples/data/");
		// Get the default pipeline
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(ClinicalPipelineFactory.getTokenProcessingPipeline());
		builder.add(AnalysisEngineFactory
				.createEngineDescription(CopyNPChunksToLookupWindowAnnotations.class));
		builder.add(AnalysisEngineFactory
				.createEngineDescription(RemoveEnclosedLookupWindows.class));

		builder.add(UmlsDictionaryLookupAnnotator
				.createAnnotatorDescription());
		builder.add(ClearNLPDependencyParserAE.createAnnotatorDescription());
		
		// Add BackwardsTimeAnnotator
		builder.add(BackwardsTimeAnnotator
				.createAnnotatorDescription("/org/apache/ctakes/temporal/ae/timeannotator/model.jar"));
		// Add EventAnnotator
		builder.add(EventAnnotator
				.createAnnotatorDescription("/org/apache/ctakes/temporal/ae/eventannotator/model.jar"));
		//link event to eventMention
		builder.add(AnalysisEngineFactory.createEngineDescription(AddEvent.class));
		// Add Document Time Relative Annotator
		builder.add(DocTimeRelAnnotator
				.createAnnotatorDescription("/org/apache/ctakes/temporal/ae/doctimerel/model.jar"));
		
		builder.add(PolarityCleartkAnalysisEngine.createAnnotatorDescription());
		builder.add(AnalysisEngineFactory.createEngineDescription(ExtractionPrepAnnotator.class));			
		
		//ADD XMI CAS CONSUMER HERE?
		
		AnalysisEngine aggregateAE = builder.createAggregate();		
		
		
		for (File f : dir.listFiles()) {
			LOGGER.info("Processing: " + f.getName());
			 FileReader fileReader = new FileReader(f);
			 BufferedReader br = new BufferedReader(fileReader);
				StringBuffer sb = new StringBuffer();
			 String line = null;
			 while ((line = br.readLine()) != null) {
				 sb.append(line);
			 }			
			JCas jcas = aggregateAE.newJCas();
			jcas.setDocumentText(sb.toString());
			aggregateAE.process(jcas);
			jcas.reset();
		}

	}

	public static class AddEvent extends org.apache.uima.fit.component.JCasAnnotator_ImplBase {
		@Override
		public void process(JCas jCas) throws AnalysisEngineProcessException {
			for (EventMention emention : Lists.newArrayList(JCasUtil.select(
					jCas,
					EventMention.class))) {
				EventProperties eventProperties = new org.apache.ctakes.typesystem.type.refsem.EventProperties(jCas);

				// create the event object
				Event event = new Event(jCas);

				// add the links between event, mention and properties
				event.setProperties(eventProperties);
				emention.setEvent(event);

				// add the annotations to the indexes
				eventProperties.addToIndexes();
				event.addToIndexes();
			}
		}
	}

}
