package org.apache.ctakes.cancer.ae;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.apache.ctakes.cancer.receptor.ReceptorStatusFinder;
import org.apache.ctakes.cancer.tnm.TnmStageFinder;
import org.apache.ctakes.cancer.type.relation.ReceptorStatusTextRelation;
import org.apache.ctakes.cancer.type.relation.TnmStageTextRelation;
import org.apache.ctakes.typesystem.type.refsem.UmlsConcept;
import org.apache.ctakes.typesystem.type.textsem.DiseaseDisorderMention;
import org.apache.ctakes.typesystem.type.textsem.IdentifiedAnnotation;
import org.apache.ctakes.typesystem.type.textspan.Sentence;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

/**
 * @author SPF , chip-nlp
 * @version %I%
 * @since 4/28/2015
 */
public class TnmAnnotator extends JCasAnnotator_ImplBase {

   static private final Logger LOGGER = Logger.getLogger( "TnmAnnotator" );

   /**
    * specifies the type of window to use for lookup
    */
   public static final String PARAM_WINDOW_ANNOT_PRP = "tnmWindowAnnotations";
   @ConfigurationParameter( name=PARAM_WINDOW_ANNOT_PRP, mandatory=false )
   private Class<? extends Annotation> _lookupWindowType = Sentence.class;
   

   /**
    * {@inheritDoc}
    */
   @Override
   public void process( final JCas jcas ) throws AnalysisEngineProcessException {
      LOGGER.info( "Starting processing" );

      final Collection<DiseaseDisorderMention> disorderMentions = new ArrayList<>();
      final Collection<DiseaseDisorderMention> lookupWindowT191s = new HashSet<>();
      final Collection<? extends Annotation> lookupWindows = JCasUtil.select(jcas, _lookupWindowType );
      for ( Annotation lookupWindow : lookupWindows ) {
        disorderMentions.addAll(
            JCasUtil.selectCovered( jcas, DiseaseDisorderMention.class, lookupWindow ) );
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

         if ( LOGGER.isDebugEnabled() ) {
            printCancerFindings( jcas );
         }
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
            = JCasUtil.select( jcas, TnmStageTextRelation.class );
      for ( TnmStageTextRelation relation : tnmStageRelations ) {
         LOGGER.debug( "TNM Relation " + relation.getArg1().getArgument().toString()
                      + "\n\tRelated To: " + relation.getArg2().getArgument().getCoveredText() );
      }

      final Collection<ReceptorStatusTextRelation> receptorStatusRelations
            = JCasUtil.select( jcas, ReceptorStatusTextRelation.class );
      for ( ReceptorStatusTextRelation relation : receptorStatusRelations ) {
         LOGGER.debug( "Receptor Status Relation " + relation.getArg1().getArgument().toString()
                      + "\n\tRelated To: " + relation.getArg2().getArgument().getCoveredText() );
      }
   }

   public static AnalysisEngineDescription createAnnotatorDescription() throws ResourceInitializationException{
     return AnalysisEngineFactory.createEngineDescription( TnmAnnotator.class );
   }
}
