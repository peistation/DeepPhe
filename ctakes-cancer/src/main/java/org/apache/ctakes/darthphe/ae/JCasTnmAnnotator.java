package org.apache.ctakes.darthphe.ae;

import org.apache.ctakes.cancer.type.relation.ReceptorStatusTextRelation;
import org.apache.ctakes.cancer.type.relation.TnmStageTextRelation;
import org.apache.ctakes.core.util.JCasUtil;
import org.apache.ctakes.darthphe.receptor.ReceptorStatusFinder;
import org.apache.ctakes.darthphe.tnm.TnmStageFinder;
import org.apache.ctakes.typesystem.type.refsem.UmlsConcept;
import org.apache.ctakes.typesystem.type.textsem.DiseaseDisorderMention;
import org.apache.ctakes.typesystem.type.textsem.IdentifiedAnnotation;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JFSIndexRepository;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

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

   // type of lookup window to use, typically "Sentence"
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
         final Collection<DiseaseDisorderMention> disorderMentions = new ArrayList<>();
         final Collection<DiseaseDisorderMention> lookupWindowT191s = new HashSet<>();
         for ( Annotation lookupWindow : lookupWindows ) {
            disorderMentions.addAll(
                  org.apache.uima.fit.util.JCasUtil.selectCovered( jcas, DiseaseDisorderMention.class, lookupWindow ) );
            if ( disorderMentions.isEmpty() ) {
               continue;
            }
            for ( DiseaseDisorderMention disorderMention : disorderMentions ) {
               if ( isTuiT191( disorderMention ) ) {
                  lookupWindowT191s.add( disorderMention );
               }
            }
            disorderMentions.clear();
            if ( lookupWindowT191s.isEmpty() ) {
               continue;
            }
            TnmStageFinder.getInstance().addMalignantTumorClasses( jcas, lookupWindow, lookupWindowT191s );
            ReceptorStatusFinder.getInstance().addReceptorStatuses( jcas, lookupWindow, lookupWindowT191s );
            lookupWindowT191s.clear();

            printCancerFindings( jcas );
         }
      } catch ( ArrayIndexOutOfBoundsException iobE ) {
         // JCasHashMap will throw this every once in a while.  Assume the windows are done and move on
         LOGGER.warn( iobE.getMessage() );
      }

      LOGGER.info( "Finished processing" );
   }

   static private boolean isTuiT191( final DiseaseDisorderMention disorderMention ) {
      final FeatureStructure[] featureStructures = getFeatureStructures( disorderMention );
      for ( FeatureStructure featureStructure : featureStructures ) {
         if ( featureStructure instanceof UmlsConcept ) {
            final String tui = ((UmlsConcept)featureStructure).getTui();
            // T191 (Neoplastic Process) works for cancer.
            // T033 is good for mass and lesion, but we are interested in DD not finding
            if ( tui != null && tui.equals( "T191" ) ) {
               return true;
            }
         }
      }
      return false;
   }

   static private final FeatureStructure[] NO_FEATURES = new FeatureStructure[0];

   static private FeatureStructure[] getFeatureStructures( final IdentifiedAnnotation annotation ) {
      final FSArray fsArray = annotation.getOntologyConceptArr();
      if ( fsArray == null ) {
         return NO_FEATURES;
      }
      return fsArray.toArray();
   }


   static private void printCancerFindings( final JCas jcas ) {
      final Collection<TnmStageTextRelation> tnmStageRelations
            = org.apache.uima.fit.util.JCasUtil.select( jcas, TnmStageTextRelation.class );
      for ( TnmStageTextRelation relation : tnmStageRelations ) {
         System.out.println( "TNM Relation " + relation.getArg1().getArgument().toString()
                             + "\n\tRelated To: " + relation.getArg2().getArgument().getCoveredText() );
      }

      final Collection<ReceptorStatusTextRelation> receptorStatusRelations
            = org.apache.uima.fit.util.JCasUtil.select( jcas, ReceptorStatusTextRelation.class );
      for ( ReceptorStatusTextRelation relation : receptorStatusRelations ) {
         System.out.println( "Receptor Status Relation " + relation.getArg1().getArgument().toString()
                             + "\n\tRelated To: " + relation.getArg2().getArgument().getCoveredText() );
      }
   }


}
