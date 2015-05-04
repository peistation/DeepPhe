package org.apache.ctakes.cancer.ae;

import java.io.*;
import java.util.Collection;

import org.apache.ctakes.assertion.medfacts.cleartk.PolarityCleartkAnalysisEngine;
import org.apache.ctakes.clinicalpipeline.ClinicalPipelineFactory;
import org.apache.ctakes.clinicalpipeline.ClinicalPipelineFactory.CopyNPChunksToLookupWindowAnnotations;
import org.apache.ctakes.clinicalpipeline.ClinicalPipelineFactory.RemoveEnclosedLookupWindows;
import org.apache.ctakes.clinicalpipeline.ae.ExtractionPrepAnnotator;
import org.apache.ctakes.core.resource.FileLocator;
import org.apache.ctakes.core.resource.FileResourceImpl;
import org.apache.ctakes.dependency.parser.ae.ClearNLPDependencyParserAE;
import org.apache.ctakes.dictionary.lookup.ae.UmlsDictionaryLookupAnnotator;
import org.apache.ctakes.dictionary.lookup2.ae.AbstractJCasTermAnnotator;
import org.apache.ctakes.dictionary.lookup2.ae.DefaultJCasTermAnnotator;
import org.apache.ctakes.dictionary.lookup2.ae.JCasTermAnnotator;
import org.apache.ctakes.temporal.ae.BackwardsTimeAnnotator;
import org.apache.ctakes.temporal.ae.DocTimeRelAnnotator;
import org.apache.ctakes.temporal.ae.EventAnnotator;
import org.apache.ctakes.typesystem.type.refsem.Event;
import org.apache.ctakes.typesystem.type.refsem.EventProperties;
import org.apache.ctakes.typesystem.type.textsem.EventMention;
import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;


public class CancerPipelineTest {

   // LOG4J logger based on class name
   static private final Logger LOGGER = Logger.getLogger( "CancerPipelineTest" );

   // TODO  ---> this data directory path must be changed based upon the local system
//   static private final String DATA_DIRECTORY_PATH = "../apache-ctakes/trunk/ctakes-examples/data/";
   static private final String DATA_DIRECTORY_PATH = "C:\\Spiffy\\prj_darth_phenome\\dev\\github\\data\\sample\\docs";

   static private final String TIME_ANNOTATOR_MODEL = "/org/apache/ctakes/temporal/ae/timeannotator/model.jar";
   static private final String EVENT_ANNOTATOR_MODEL = "/org/apache/ctakes/temporal/ae/eventannotator/model.jar";
   static private final String DOCTIMEREL_ANNOTATOR_MODEL = "/org/apache/ctakes/temporal/ae/doctimerel/model.jar";



   static private AnalysisEngine buildAnalysisEngine() throws ResourceInitializationException {
      final AggregateBuilder builder = new AggregateBuilder();
      builder.add( ClinicalPipelineFactory.getTokenProcessingPipeline() );
      builder.add( AnalysisEngineFactory.createEngineDescription( CopyNPChunksToLookupWindowAnnotations.class ) );
      builder.add( AnalysisEngineFactory.createEngineDescription( RemoveEnclosedLookupWindows.class ) );

//      builder.add( UmlsDictionaryLookupAnnotator.createAnnotatorDescription() );
      try {
         builder.add(AnalysisEngineFactory.createEngineDescription(DefaultJCasTermAnnotator.class,
               AbstractJCasTermAnnotator.PARAM_WINDOW_ANNOT_PRP,
               "org.apache.ctakes.typesystem.type.textspan.Sentence",
               JCasTermAnnotator.DICTIONARY_DESCRIPTOR_KEY,
               ExternalResourceFactory.createExternalResourceDescription(
                     FileResourceImpl.class,
                     FileLocator.locateFile( "org/apache/ctakes/dictionary/lookup/fast/cTakesHsql.xml" ) )
         ));
      } catch (FileNotFoundException e) {
         e.printStackTrace();
         throw new ResourceInitializationException(e);
      }

      builder.add( ClearNLPDependencyParserAE.createAnnotatorDescription() );

      // Add BackwardsTimeAnnotator
      builder.add( BackwardsTimeAnnotator.createAnnotatorDescription( TIME_ANNOTATOR_MODEL ) );
      // Add EventAnnotator
      builder.add( EventAnnotator.createAnnotatorDescription( EVENT_ANNOTATOR_MODEL ) );
      //link event to eventMention
      builder.add( AnalysisEngineFactory.createEngineDescription( AddEvent.class ) );
      // Add Document Time Relative Annotator
      builder.add( DocTimeRelAnnotator.createAnnotatorDescription( DOCTIMEREL_ANNOTATOR_MODEL ) );

      builder.add( PolarityCleartkAnalysisEngine.createAnnotatorDescription() );
      builder.add( AnalysisEngineFactory.createEngineDescription( ExtractionPrepAnnotator.class,
            "AnnotationVersion", 2,
            "AnnotationVersionPropKey", "ANNOTATION_VERSION" ) );
      // Add the Cancer Stage and Receptor Status Annotator
      builder.add( AnalysisEngineFactory.createEngineDescription( TnmAnnotator.class ) );

      //ADD XMI CAS CONSUMER HERE?

      return builder.createAggregate();
   }


   @Test
   public void test() throws ResourceInitializationException, AnalysisEngineProcessException {
      // String note = "patient took 50mg of aspirin for pain in right knee.";
      final File dir = new File( DATA_DIRECTORY_PATH );
      final File[] files = dir.listFiles();
      if ( files == null || files.length == 0 ) {
         LOGGER.error( "No Files in directory: " + dir.getPath() );
         System.exit( 1 );
      }
      // Get the default pipeline
      final AnalysisEngine aggregateAE = buildAnalysisEngine();

      final StringBuilder sb = new StringBuilder();
      final JCas jcas = aggregateAE.newJCas();
      for ( File file : files ) {
         LOGGER.info( "Processing: " + file.getName() );
         try ( BufferedReader br = new BufferedReader( new FileReader( file ) ) ) {
            String line = br.readLine();
            while ( line != null ) {
               sb.append( line ).append( '\n' );
               line = br.readLine();
            }
         } catch ( IOException ioE ) {
            throw new AnalysisEngineProcessException( ioE );
         }
         jcas.setDocumentText( sb.toString() );
         sb.setLength( 0 );
         aggregateAE.process( jcas );
         jcas.reset();
      }

   }


   public static class AddEvent extends JCasAnnotator_ImplBase {
      @Override
      public void process( final JCas jCas ) throws AnalysisEngineProcessException {
         final Collection<EventMention> eventMentions = JCasUtil.select( jCas, EventMention.class );
         if ( eventMentions == null ) {
            LOGGER.error( "No Event Mentions in JCAS" );
            return;
         }
         for ( EventMention emention : eventMentions ) {
            final EventProperties eventProperties = new EventProperties( jCas );
            // create the event object
            final Event event = new Event( jCas );

            // add the links between event, mention and properties
            event.setProperties( eventProperties );
            emention.setEvent( event );

            // add the annotations to the indexes
            eventProperties.addToIndexes();
            event.addToIndexes();
         }
      }
   }

}
