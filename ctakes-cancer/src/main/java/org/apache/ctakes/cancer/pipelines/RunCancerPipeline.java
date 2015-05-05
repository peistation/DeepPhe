package org.apache.ctakes.cancer.pipelines;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ctakes.assertion.medfacts.cleartk.PolarityCleartkAnalysisEngine;
import org.apache.ctakes.assertion.medfacts.cleartk.UncertaintyCleartkAnalysisEngine;
import org.apache.ctakes.cancer.ae.TnmAnnotator;
import org.apache.ctakes.chunker.ae.Chunker;
import org.apache.ctakes.clinicalpipeline.ClinicalPipelineFactory;
import org.apache.ctakes.clinicalpipeline.ClinicalPipelineFactory.CopyNPChunksToLookupWindowAnnotations;
import org.apache.ctakes.clinicalpipeline.ClinicalPipelineFactory.RemoveEnclosedLookupWindows;
import org.apache.ctakes.constituency.parser.ae.ConstituencyParser;
import org.apache.ctakes.contexttokenizer.ae.ContextDependentTokenizerAnnotator;
import org.apache.ctakes.core.ae.CDASegmentAnnotator;
import org.apache.ctakes.core.ae.SentenceDetector;
import org.apache.ctakes.core.ae.TokenizerAnnotatorPTB;
import org.apache.ctakes.core.cc.XmiWriterCasConsumerCtakes;
import org.apache.ctakes.core.cr.FilesInDirectoryCollectionReader;
import org.apache.ctakes.core.resource.FileLocator;
import org.apache.ctakes.core.resource.FileResourceImpl;
import org.apache.ctakes.dependency.parser.ae.ClearNLPDependencyParserAE;
import org.apache.ctakes.dependency.parser.ae.ClearNLPSemanticRoleLabelerAE;
import org.apache.ctakes.dictionary.lookup2.ae.AbstractJCasTermAnnotator;
import org.apache.ctakes.dictionary.lookup2.ae.DefaultJCasTermAnnotator;
import org.apache.ctakes.dictionary.lookup2.ae.JCasTermAnnotator;
import org.apache.ctakes.lvg.ae.LvgAnnotator;
import org.apache.ctakes.postagger.POSTagger;
import org.apache.ctakes.relationextractor.ae.DegreeOfRelationExtractorAnnotator;
import org.apache.ctakes.relationextractor.ae.LocationOfRelationExtractorAnnotator;
import org.apache.ctakes.relationextractor.ae.ModifierExtractorAnnotator;
import org.apache.ctakes.temporal.ae.BackwardsTimeAnnotator;
import org.apache.ctakes.temporal.ae.DocTimeRelAnnotator;
import org.apache.ctakes.temporal.ae.EventAnnotator;
import org.apache.ctakes.typesystem.type.textsem.EventMention;
import org.apache.ctakes.typesystem.type.textsem.TimeMention;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;
import org.cleartk.ml.jar.GenericJarClassifierFactory;

import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;

public class RunCancerPipeline {
	static interface Options extends CancerPipelineOptions {
		@Option(
				shortName = "i",
				description = "specify the path to the directory containing the clinical notes to be processed")
		public String getInputDirectory();

		@Option(
				shortName = "o",
				description = "specify the path to the directory where the output xmi files are to be saved")
		public String getOutputDirectory();

		
	}

	public static void main(String[] args) throws UIMAException, IOException {
		Options options = CliFactory.parseArguments(Options.class, args);
		CollectionReader collectionReader = CollectionReaderFactory.createReaderFromPath(
				"../ctakes/ctakes-core/desc/collection_reader/FilesInDirectoryCollectionReader.xml",
				FilesInDirectoryCollectionReader.PARAM_INPUTDIR,
				options.getInputDirectory());


    AnalysisEngineDescription aed = getPipelineDescription(options);

    AnalysisEngine xWriter = getXMIWriter(options.getOutputDirectory());

		SimplePipeline.runPipeline(
				collectionReader,
				AnalysisEngineFactory.createEngine(aed),
				xWriter);

	}

	public static AnalysisEngineDescription getPipelineDescription(CancerPipelineOptions options) throws ResourceInitializationException, InvalidXMLException, IOException {
	  AggregateBuilder aggregateBuilder = new AggregateBuilder();
	  aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(PittHeaderAnnotator.class));
	  // grab segments using known headers from default + what UPMC has given us.
	  aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(CDASegmentAnnotator.class, CDASegmentAnnotator.PARAM_SECTIONS_FILE, "resources/ccda_sections.txt"));
//	  aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(UpmcSimpleSegmenter.class));
    // core components, dictionary, dependency parser, polarity, uncertainty
	  aggregateBuilder.add(SentenceDetector.createAnnotatorDescription());
	  aggregateBuilder.add(TokenizerAnnotatorPTB.createAnnotatorDescription());
	  aggregateBuilder.add(LvgAnnotator.createAnnotatorDescription());
	  aggregateBuilder.add(ContextDependentTokenizerAnnotator.createAnnotatorDescription());
	  aggregateBuilder.add(POSTagger.createAnnotatorDescription());
	  aggregateBuilder.add(Chunker.createAnnotatorDescription());
	  aggregateBuilder.add(ClinicalPipelineFactory.getStandardChunkAdjusterAnnotator());	   

	  aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(CopyNPChunksToLookupWindowAnnotations.class));
	  aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(RemoveEnclosedLookupWindows.class));
	  try {
	    aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(DefaultJCasTermAnnotator.class,
	        AbstractJCasTermAnnotator.PARAM_WINDOW_ANNOT_PRP,
	        "org.apache.ctakes.typesystem.type.textspan.Sentence",
	        JCasTermAnnotator.DICTIONARY_DESCRIPTOR_KEY,
	        ExternalResourceFactory.createExternalResourceDescription(
	            FileResourceImpl.class,
	            FileLocator.locateFile("org/apache/ctakes/cancer/dictionary/lookup/fast/cancerHsql.xml"))
	        ));
	  } catch (FileNotFoundException e) {
	    e.printStackTrace();
	    throw new ResourceInitializationException(e);
	  }
	  aggregateBuilder.add(ClearNLPDependencyParserAE.createAnnotatorDescription());
	  aggregateBuilder.add(PolarityCleartkAnalysisEngine.createAnnotatorDescription());
	  aggregateBuilder.add(UncertaintyCleartkAnalysisEngine.createAnnotatorDescription());
	  aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(ClearNLPSemanticRoleLabelerAE.class));
	  aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(ConstituencyParser.class));

      // Cancer deep phe tnm, hormone receptor status annotator.  Do before temporal (but after polarity?)
      aggregateBuilder.add( TnmAnnotator.createAnnotatorDescription() );

      // temporal components:
	  aggregateBuilder.add(EventAnnotator.createAnnotatorDescription());
	  aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(CopyPropertiesToTemporalEventAnnotator.class));
	  aggregateBuilder.add(DocTimeRelAnnotator.createAnnotatorDescription("/org/apache/ctakes/temporal/ae/doctimerel/model.jar"));
	  aggregateBuilder.add(BackwardsTimeAnnotator.createAnnotatorDescription("/org/apache/ctakes/temporal/ae/timeannotator/model.jar"));
	  //	    aggregateBuilder.add(EventTimeRelationAnnotator.createAnnotatorDescription(options.getEventTimeRelationModelDirectory() + File.separator + "model.jar"));
	  //	      if(options.getEventEventRelationModelDirectory()!=null){
	  //	        aggregateBuilder.add(EventEventRelationAnnotator.createAnnotatorDescription(options.getEventEventRelationModelDirectory() + File.separator + "model.jar"));
	  //	      }
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
	  //	    aggregateBuilder.add(
	  //	        AnalysisEngineFactory.createEngineDescriptionFromPath("../ctakes/ctakes-coreference/desc/MipacqSvmCoreferenceResolverAggregate.xml"));

	  return aggregateBuilder.createAggregateDescription();
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
	
	public static class PittHeaderAnnotator extends JCasAnnotator_ImplBase {

	  static Pattern datePatt = Pattern.compile(".*Principal Date\\D+(\\d+) (\\d+).*", Pattern.DOTALL);
    @Override
    public void process(JCas jcas) throws AnalysisEngineProcessException {
      // TODO -- use a document creation time type?
      String docText = jcas.getDocumentText();
      Matcher m = datePatt.matcher(docText);
      if(m.matches()){
        TimeMention docTime = new TimeMention(jcas);
        docTime.setBegin(m.start(1));
        docTime.setEnd(m.end(1));
        docTime.setId(0);
        docTime.addToIndexes();
      }
      
    }
	}
}
