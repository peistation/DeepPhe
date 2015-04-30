package org.apache.ctakes.darthphe.ae;

import org.apache.ctakes.core.util.JCasUtil;
import org.apache.ctakes.darthphe.receptor.ReceptorStatusFinder;
import org.apache.ctakes.darthphe.tnm.TnmStageFinder;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JFSIndexRepository;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import org.apache.log4j.Logger;

/**
 * @author SPF , chip-nlp
 * @version %I%
 * @since 4/28/2015
 */
public class JCasTnmAnnotator extends JCasAnnotator_ImplBase {

   static private final Logger LOGGER = Logger.getLogger( "JCasTnmAnnotator" );

   /**
    * specifies the type of window to use for lookup
    */
   public static final String PARAM_WINDOW_ANNOT_PRP = "tnmWindowAnnotations";

   static private final String DEFAULT_LOOKUP_WINDOW = "org.apache.ctakes.typesystem.type.textspan.Sentence";

   // type of lookup window to use, typically "LookupWindowAnnotation" or "Sentence"
   private int _lookupWindowType;

   /**
    * {@inheritDoc}
    */
   @Override
   public void initialize( final UimaContext uimaContext ) throws ResourceInitializationException {
      super.initialize( uimaContext );
//      try {
         String windowClassName = (String)uimaContext.getConfigParameterValue( PARAM_WINDOW_ANNOT_PRP );
         if ( windowClassName == null || windowClassName.isEmpty() ) {
            windowClassName = DEFAULT_LOOKUP_WINDOW;
         }
         LOGGER.info( "Using Malignant Tumor Classification (TNM) annotator lookup window type: " + windowClassName );
         _lookupWindowType = JCasUtil.getType( windowClassName );
//      } catch ( ResourceAccessException | AnnotatorContextException multE ) {
//         throw new ResourceInitializationException( multE );
//      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void process( final JCas jcas ) throws AnalysisEngineProcessException {
      LOGGER.info( "Starting processing" );

      final JFSIndexRepository indexes = jcas.getJFSIndexRepository();
      final AnnotationIndex<Annotation> lookupWindows = indexes.getAnnotationIndex( _lookupWindowType );
      if ( lookupWindows == null ) {  // I don't trust AnnotationIndex.size(), so don't check
         return;
      }

      try {
         for ( Object window : lookupWindows ) {
            TnmStageFinder.getInstance().getMalignantTumorClasses( ((Annotation)window).getCoveredText() );
            ReceptorStatusFinder.getInstance().getAllReceptorStatuses( ((Annotation)window).getCoveredText() );
         }
      } catch ( ArrayIndexOutOfBoundsException iobE ) {
         // JCasHashMap will throw this every once in a while.  Assume the windows are done and move on
         LOGGER.warn( iobE.getMessage() );
      }

      LOGGER.info( "Finished processing" );
   }


}
