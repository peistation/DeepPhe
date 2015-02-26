package org.apache.ctakes.cancer.pipelines;

import java.io.File;
import java.io.IOException;

import org.apache.ctakes.clinicalpipeline.ClinicalPipelineFactory;
import org.apache.ctakes.constituency.parser.ae.ConstituencyParser;
import org.apache.ctakes.core.cc.XmiWriterCasConsumerCtakes;
import org.apache.ctakes.core.cr.FilesInDirectoryCollectionReader;
import org.apache.ctakes.dependency.parser.ae.ClearNLPSemanticRoleLabelerAE;
import org.apache.ctakes.relationextractor.ae.DegreeOfRelationExtractorAnnotator;
import org.apache.ctakes.relationextractor.ae.LocationOfRelationExtractorAnnotator;
import org.apache.ctakes.relationextractor.ae.ModifierExtractorAnnotator;
import org.apache.ctakes.temporal.ae.BackwardsTimeAnnotator;
import org.apache.ctakes.temporal.ae.DocTimeRelAnnotator;
import org.apache.ctakes.temporal.ae.EventAnnotator;
import org.apache.ctakes.typesystem.type.textsem.EventMention;
import org.apache.ctakes.temporal.ae.EventEventRelationAnnotator;
import org.apache.ctakes.temporal.ae.EventTimeRelationAnnotator;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.ml.jar.GenericJarClassifierFactory;

import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;

public class RunCancerPipeline {
	static interface Options {
		@Option(
				shortName = "i",
				description = "specify the path to the directory containing the clinical notes to be processed")
		public String getInputDirectory();

		@Option(
				shortName = "o",
				description = "specify the path to the directory where the output xmi files are to be saved")
		public String getOutputDirectory();

		@Option(
				shortName = "e",
				description = "specify the path to the directory where the trained event model is located",
				defaultValue="org/apache/ctakes/temporal/ae/eventannotator/")
		public String getEventModelDirectory();

		@Option(
				shortName = "t",
				description = "specify the path to the directory where the trained event model is located",
				defaultValue="/org/apache/ctakes/temporal/ae/timeannotator/")
		public String getTimeModelDirectory();

		@Option(
				shortName = "d",
				description = "specify the path to the directory where the trained event-doctime relation model is located",
				defaultValue="/org/apache/ctakes/temporal/ae/doctimerel")
		public String getDoctimerelModelDirectory();

		@Option(
				shortName = "r",
				description = "Specify the path to the directory where the trained event-time relation model is located",
				defaultValue="target/eval/thyme/train_and_test/event-time/")
		public String getEventTimeRelationModelDirectory();

		@Option(
				shortName = "s",
				description = "Specify the path to the directory where the trained event-event relation model is located",
				defaultValue="target/eval/thyme/train_and_test/event-event/") // add in default value once we have a satisfying trained model
		public String getEventEventRelationModelDirectory();  

		@Option(
				shortName = "c",
				description = "Specify the path to the directory where the trained coreference model is located",
				defaultToNull=true)
		public String getCoreferenceModelDirectory();
	}

	public static void main(String[] args) throws UIMAException, IOException {
		Options options = CliFactory.parseArguments(Options.class, args);
		CollectionReader collectionReader = CollectionReaderFactory.createReaderFromPath(
				"../ctakes/ctakes-core/desc/collection_reader/FilesInDirectoryCollectionReader.xml",
				FilesInDirectoryCollectionReader.PARAM_INPUTDIR,
				options.getInputDirectory());

		AggregateBuilder aggregateBuilder = new AggregateBuilder();
		aggregateBuilder.add(ClinicalPipelineFactory.getFastPipeline());//.getDefaultPipeline()); // core components, dictionary, dependency parser, polarity, uncertainty 

		aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(ClearNLPSemanticRoleLabelerAE.class));
		aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(ConstituencyParser.class));

		// temporal components:
		aggregateBuilder.add(EventAnnotator.createAnnotatorDescription());
		aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(CopyPropertiesToTemporalEventAnnotator.class));
		aggregateBuilder.add(DocTimeRelAnnotator.createAnnotatorDescription("/org/apache/ctakes/temporal/ae/doctimerel/model.jar"));
		aggregateBuilder.add(BackwardsTimeAnnotator.createAnnotatorDescription("/org/apache/ctakes/temporal/ae/timeannotator/model.jar"));
		aggregateBuilder.add(EventTimeRelationAnnotator.createAnnotatorDescription(options.getEventTimeRelationModelDirectory() + File.separator + "model.jar"));
	    if(options.getEventEventRelationModelDirectory()!=null){
	      aggregateBuilder.add(EventEventRelationAnnotator.createAnnotatorDescription(options.getEventEventRelationModelDirectory() + File.separator + "model.jar"));
	    }
		// UMLS relations:
		aggregateBuilder.add(
				AnalysisEngineFactory.createEngineDescription(
						ModifierExtractorAnnotator.class,
						GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH,
						"/org/apache/ctakes/relationextractor/models/modifier_extractor/model.jar"));
		aggregateBuilder.add(
				AnalysisEngineFactory.createEngineDescription(
						DegreeOfRelationExtractorAnnotator.class,
						GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH,
						"/org/apache/ctakes/relationextractor/models/degree_of/model.jar"));
		aggregateBuilder.add(
				AnalysisEngineFactory.createEngineDescription(
						LocationOfRelationExtractorAnnotator.class,
						GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH,
						"/org/apache/ctakes/relationextractor/models/location_of/model.jar"));

		// coreference?
		aggregateBuilder.add(
				AnalysisEngineFactory.createEngineDescriptionFromPath("../ctakes/ctakes-coreference/desc/MipacqSvmCoreferenceResolverAggregate.xml"));

		AnalysisEngine xWriter = getXMIWriter(options.getOutputDirectory());

		SimplePipeline.runPipeline(
				collectionReader,
				aggregateBuilder.createAggregate(),
				xWriter);

	}

	protected static AnalysisEngine getXMIWriter(String outputDirectory) throws ResourceInitializationException{
		return AnalysisEngineFactory.createEngine(
				XmiWriterCasConsumerCtakes.class,
				XmiWriterCasConsumerCtakes.PARAM_OUTPUTDIR,
				outputDirectory
				);
	}

	public static class CopyPropertiesToTemporalEventAnnotator extends JCasAnnotator_ImplBase {

		@Override
		public void process(JCas jcas) throws AnalysisEngineProcessException {
			for(EventMention mention : JCasUtil.select(jcas, EventMention.class)){
				// get temporal event mentions and not dictinoary-derived subclasses
				// find either an exact matching span, or an end-matching span with the smallest overlap
				if(mention.getClass().equals(EventMention.class)){
					EventMention bestCovering = null;
					int smallestSpan = Integer.MAX_VALUE;
					for(EventMention covering : JCasUtil.selectCovering(EventMention.class, mention)){
						if(covering.getClass().equals(EventMention.class)) continue;
						if(covering.getBegin() == mention.getBegin() && covering.getEnd() == mention.getEnd()){
							bestCovering = covering;
							break;
						}else if(covering.getEnd() == mention.getEnd()){
							int span = covering.getEnd() - covering.getBegin();
							if(span < smallestSpan){
								span = smallestSpan;
								bestCovering = covering;
							}
						}
					}
					if(bestCovering != null){
						mention.setPolarity(bestCovering.getPolarity());
						//            mention.getEvent().getProperties().setPolarity(bestCovering.getPolarity());
						mention.setUncertainty(bestCovering.getUncertainty());
					}
				}
			}
		}
	}
}
