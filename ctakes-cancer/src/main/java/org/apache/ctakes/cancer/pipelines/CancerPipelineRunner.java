package org.apache.ctakes.cancer.pipelines;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
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
import org.apache.ctakes.temporal.ae.*;
import org.apache.ctakes.typesystem.type.textsem.*;
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

import javax.annotation.concurrent.Immutable;

import static java.util.regex.Pattern.DOTALL;

@Immutable
final public class CancerPipelineRunner {
   private CancerPipelineRunner() {
   }

   static interface Options extends CancerPipelineOptions {
      @Option(
            shortName = "i",
            description = "specify the path to the directory containing the clinical notes to be processed" )
      public String getInputDirectory();

      @Option(
            shortName = "o",
            description = "specify the path to the directory where the output xmi files are to be saved" )
      public String getOutputDirectory();
   }

   static private final String CTAKES_DIR_PREFIX = "/org/apache/ctakes/";

   public static AnalysisEngineDescription getPipelineDescription()
         throws ResourceInitializationException, InvalidXMLException, IOException {
      return getPipelineDescription( null );
   }

   public static AnalysisEngineDescription getPipelineDescription( final CancerPipelineOptions options )
         throws ResourceInitializationException, InvalidXMLException, IOException {
      final AggregateBuilder aggregateBuilder = new AggregateBuilder();
      aggregateBuilder.add( AnalysisEngineFactory.createEngineDescription( PittHeaderAnnotator.class ) );
      // grab segments using known headers from default + what UPMC has given us.
      aggregateBuilder.add( AnalysisEngineFactory.createEngineDescription( CDASegmentAnnotator.class,
            CDASegmentAnnotator.PARAM_SECTIONS_FILE, "resources/ccda_sections.txt" ) );
//	  aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(UpmcSimpleSegmenter.class));
      // core components, dictionary, dependency parser, polarity, uncertainty
      aggregateBuilder.add( SentenceDetector.createAnnotatorDescription() );
      aggregateBuilder.add( TokenizerAnnotatorPTB.createAnnotatorDescription() );
      aggregateBuilder.add( LvgAnnotator.createAnnotatorDescription() );
      aggregateBuilder.add( ContextDependentTokenizerAnnotator.createAnnotatorDescription() );
      aggregateBuilder.add( POSTagger.createAnnotatorDescription() );
      aggregateBuilder.add( Chunker.createAnnotatorDescription() );
      aggregateBuilder.add( ClinicalPipelineFactory.getStandardChunkAdjusterAnnotator() );

      aggregateBuilder
            .add( AnalysisEngineFactory.createEngineDescription( CopyNPChunksToLookupWindowAnnotations.class ) );
      aggregateBuilder.add( AnalysisEngineFactory.createEngineDescription( RemoveEnclosedLookupWindows.class ) );
      try {
         aggregateBuilder.add( AnalysisEngineFactory.createEngineDescription( DefaultJCasTermAnnotator.class,
               AbstractJCasTermAnnotator.PARAM_WINDOW_ANNOT_PRP,
               "org.apache.ctakes.typesystem.type.textspan.Sentence",
               JCasTermAnnotator.DICTIONARY_DESCRIPTOR_KEY,
               ExternalResourceFactory.createExternalResourceDescription(
                     FileResourceImpl.class,
                     FileLocator.locateFile( "org/apache/ctakes/cancer/dictionary/lookup/fast/cancerHsql.xml" ) )
         ) );
      } catch ( FileNotFoundException e ) {
         e.printStackTrace();
         throw new ResourceInitializationException( e );
      }

      aggregateBuilder.add( ClearNLPDependencyParserAE.createAnnotatorDescription() );
      aggregateBuilder.add( PolarityCleartkAnalysisEngine.createAnnotatorDescription() );
      aggregateBuilder.add( UncertaintyCleartkAnalysisEngine.createAnnotatorDescription() );
      aggregateBuilder.add( AnalysisEngineFactory.createEngineDescription( ClearNLPSemanticRoleLabelerAE.class ) );
      aggregateBuilder.add( AnalysisEngineFactory.createEngineDescription( ConstituencyParser.class ) );

      // Cancer deep phe tnm, hormone receptor status annotator.  Do before temporal (but after polarity?)
      aggregateBuilder.add( TnmAnnotator.createAnnotatorDescription() );

      // temporal components:
      aggregateBuilder.add( EventAnnotator.createAnnotatorDescription() );
      aggregateBuilder
            .add( AnalysisEngineFactory.createEngineDescription( CopyPropertiesToTemporalEventAnnotator.class ) );
      aggregateBuilder.add( DocTimeRelAnnotator.createAnnotatorDescription(
            getStandardModelPath( "temporal/ae/doctimerel" ) ) );
      aggregateBuilder.add( BackwardsTimeAnnotator.createAnnotatorDescription(
            getStandardModelPath( "temporal/ae/timeannotator" ) ) );
      	    aggregateBuilder.add( EventTimeRelationAnnotator.createAnnotatorDescription(
                   getStandardModelPath( "temporal/ae/eventtime" ) ) );
//                   options.getEventTimeRelationModelDirectory() + File.separator + "model.jar" ));
//      	      if( options.getEventEventRelationModelDirectory() != null ) {
      	        aggregateBuilder.add( EventEventRelationAnnotator.createAnnotatorDescription(
                       getStandardModelPath( "temporal/ae/eventevent" ) ) );
//      	      }
      // UMLS relations:
      aggregateBuilder.add(
            AnalysisEngineFactory.createEngineDescription(
                  ModifierExtractorAnnotator.class,
                  GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH,
                  getStandardModelPath( "relationextractor/models/modifier_extractor" ) ) );

      // Kludge to clean out unwanted annotations from the pittsburgh header
      aggregateBuilder.add( AnalysisEngineFactory.createEngineDescription( PittHeaderCleaner.class ) );

      aggregateBuilder.add(
            AnalysisEngineFactory.createEngineDescription(
                  DegreeOfRelationExtractorAnnotator.class,
                  GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH,
                  getStandardModelPath( "relationextractor/models/degree_of" ) ) );
      aggregateBuilder.add(
            AnalysisEngineFactory.createEngineDescription(
                  LocationOfRelationExtractorAnnotator.class,
                  GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH,
                  getStandardModelPath( "relationextractor/models/location_of" ) ) );

      // coreference?
      //	    aggregateBuilder.add(
      //	        AnalysisEngineFactory.createEngineDescriptionFromPath("../ctakes/ctakes-coreference/desc/MipacqSvmCoreferenceResolverAggregate.xml"));

      return aggregateBuilder.createAggregateDescription();
   }

   private static CollectionReader createFilesInDirectoryReader( final String inputDirectory ) throws UIMAException,
                                                                                                      IOException {
      final String descriptorPath = "../ctakes/ctakes-core/desc/collection_reader/FilesInDirectoryCollectionReader.xml";
      return CollectionReaderFactory.createReaderFromPath( descriptorPath,
            FilesInDirectoryCollectionReader.PARAM_INPUTDIR,
            inputDirectory );

   }

   private static AnalysisEngine createXMIWriter( final String outputDirectory )
         throws ResourceInitializationException {
      return AnalysisEngineFactory.createEngine( XmiWriterCasConsumerCtakes.class,
            XmiWriterCasConsumerCtakes.PARAM_OUTPUTDIR,
            outputDirectory );
   }

   public static void runCancerPipeline( final String inputDirectory,
                                         final String outputDirectory ) throws UIMAException, IOException {
      final CollectionReader collectionReader = createFilesInDirectoryReader( inputDirectory );
      final AnalysisEngineDescription analysisEngineDescription = getPipelineDescription();
      final AnalysisEngine xmiWriter = createXMIWriter( outputDirectory );
      runCancerPipeline( collectionReader, analysisEngineDescription, xmiWriter );
   }

   public static void runCancerPipeline( final CollectionReader collectionReader,
                                         final AnalysisEngineDescription analysisEngineDescription,
                                         final AnalysisEngine outputWriter ) throws UIMAException, IOException {
      SimplePipeline.runPipeline( collectionReader,
            AnalysisEngineFactory.createEngine( analysisEngineDescription ),
            outputWriter );
   }

   static private String getStandardModelPath( final String moduleDirectory ) {
      return CTAKES_DIR_PREFIX + moduleDirectory + "/model.jar";
   }


   public static void main( final String... args ) throws UIMAException, IOException {
      final Options options = CliFactory.parseArguments( Options.class, args );
      runCancerPipeline( options.getInputDirectory(), options.getOutputDirectory() );
   }


   public static class CopyPropertiesToTemporalEventAnnotator extends JCasAnnotator_ImplBase {

      @Override
      public void process( final JCas jcas ) throws AnalysisEngineProcessException {
         for ( EventMention mention : JCasUtil.select( jcas, EventMention.class ) ) {
            // get temporal event mentions and not dictinoary-derived subclasses
            // find either an exact matching span, or an end-matching span with the smallest overlap
            if ( mention.getClass().equals( EventMention.class ) ) {
               EventMention bestCovering = null;
               int smallestSpan = Integer.MAX_VALUE;
               for ( EventMention covering : JCasUtil.selectCovering( EventMention.class, mention ) ) {
                  if ( covering.getClass().equals( EventMention.class ) ) {
                     continue;
                  }
                  if ( covering.getBegin() == mention.getBegin() && covering.getEnd() == mention.getEnd() ) {
                     bestCovering = covering;
                     break;
                  } else if ( covering.getEnd() == mention.getEnd() ) {
                     int span = covering.getEnd() - covering.getBegin();
                     if ( span < smallestSpan ) {
                        span = smallestSpan;
                        bestCovering = covering;
                     }
                  }
               }
               if ( bestCovering != null ) {
                  mention.setPolarity( bestCovering.getPolarity() );
                  //            mention.getEvent().getProperties().setPolarity(bestCovering.getPolarity());
                  mention.setUncertainty( bestCovering.getUncertainty() );
               }
            }
         }
      }
   }

   public static class PittHeaderAnnotator extends JCasAnnotator_ImplBase {

      static private final Pattern DATE_PATTERN = Pattern.compile( ".*Principal Date\\D+(\\d+) (\\d+).*", DOTALL );

      @Override
      public void process( final JCas jcas ) throws AnalysisEngineProcessException {
         // TODO -- use a document creation time type?
         final String docText = jcas.getDocumentText();
         final Matcher m = DATE_PATTERN.matcher( docText );
         if ( m.matches() ) {
            final TimeMention docTime = new TimeMention( jcas );
            docTime.setBegin( m.start( 1 ) );
            docTime.setEnd( m.end( 1 ) );
            docTime.setId( 0 );
            docTime.addToIndexes();
         }

      }
   }

   /**
    * Cleans EventMentions out of Pitt Header area
    */
   public static class PittHeaderCleaner extends JCasAnnotator_ImplBase {

      static private final Pattern DIVIDER_PATTERN
            = Pattern.compile( "===================================================================" );

      static private final Collection<Class<? extends IdentifiedAnnotation>> UNWANTED_ANNOTATION_CLASSES
            = Arrays.asList( EventMention.class, EntityMention.class, Modifier.class, FractionAnnotation.class );

      @Override
      public void process( final JCas jcas ) throws AnalysisEngineProcessException {
         final String docText = jcas.getDocumentText();
         if ( docText.length() <= 80 ) {
            return;
         }
         final Matcher matcher = DIVIDER_PATTERN.matcher( docText );
         if ( !matcher.find( 80 ) ) {
            return;
         }
         final int endIndex = matcher.end();
         final Collection<IdentifiedAnnotation> unwantedAnnotations = new HashSet<>();
         for ( Class<? extends IdentifiedAnnotation> unwantedClass : UNWANTED_ANNOTATION_CLASSES ) {
            unwantedAnnotations.addAll( JCasUtil.selectCovered( jcas, unwantedClass, 0, endIndex ) );
         }
         for ( IdentifiedAnnotation unwantedAnnotation : unwantedAnnotations ) {
            unwantedAnnotation.removeFromIndexes();
         }
      }
   }


}
