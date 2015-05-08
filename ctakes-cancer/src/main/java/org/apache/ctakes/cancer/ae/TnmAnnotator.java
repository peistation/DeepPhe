package org.apache.ctakes.cancer.ae;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.apache.ctakes.cancer.receptor.ReceptorStatusFinder;
import org.apache.ctakes.cancer.size.SizeFinder;
import org.apache.ctakes.cancer.tnm.TnmStageFinder;
import org.apache.ctakes.cancer.type.relation.CancerSizeTextRelation;
import org.apache.ctakes.cancer.type.relation.ReceptorStatusTextRelation;
import org.apache.ctakes.cancer.type.relation.TnmStageTextRelation;
import org.apache.ctakes.cancer.util.FinderUtil;
import org.apache.ctakes.typesystem.type.refsem.UmlsConcept;
import org.apache.ctakes.typesystem.type.textsem.DiseaseDisorderMention;
import org.apache.ctakes.typesystem.type.textsem.IdentifiedAnnotation;
import org.apache.ctakes.typesystem.type.textsem.SignSymptomMention;
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

   /**
    * specifies the type of window to use for lookup
    */
   public static final String PARAM_WINDOW_ANNOT_PRP = "tnmWindowAnnotations";
   static private final Logger LOGGER = Logger.getLogger( "TnmAnnotator" );
   @ConfigurationParameter(name = PARAM_WINDOW_ANNOT_PRP, mandatory = false)
   private Class<? extends Annotation> _lookupWindowType = Sentence.class;


   /**
    * {@inheritDoc}
    */
   @Override
   public void process( final JCas jcas ) throws AnalysisEngineProcessException {
      LOGGER.info( "Starting processing" );

      final Collection<DiseaseDisorderMention> disorderMentions = new ArrayList<>();
      final Collection<DiseaseDisorderMention> lookupDisorders = new HashSet<>();
      final Collection<SignSymptomMention> symptomMentions = new ArrayList<>();
      final Collection<SignSymptomMention> lookupSymptoms = new HashSet<>();
      final Collection<? extends Annotation> lookupWindows = JCasUtil.select( jcas, _lookupWindowType );
      for ( Annotation lookupWindow : lookupWindows ) {
         disorderMentions.addAll( JCasUtil.selectCovered( jcas, DiseaseDisorderMention.class, lookupWindow ) );
         symptomMentions.addAll( JCasUtil.selectCovered( jcas, SignSymptomMention.class, lookupWindow ) );
         if ( disorderMentions.isEmpty() && symptomMentions.isEmpty() ) {
            continue;
         }
         for ( DiseaseDisorderMention disorderMention : disorderMentions ) {
            if ( FinderUtil.hasWantedTui( disorderMention, "T191" ) ) {
               lookupDisorders.add( disorderMention );
            }
         }
         disorderMentions.clear();
         for ( SignSymptomMention symptomMention : symptomMentions ) {
            if ( FinderUtil.hasWantedTui( symptomMention, "T033", "T034", "T184" ) ) {
               lookupSymptoms.add( symptomMention );
            }
         }
         symptomMentions.clear();
         if ( lookupDisorders.isEmpty() && lookupSymptoms.isEmpty() ) {
            continue;
         }
         TnmStageFinder.getInstance().addMalignantTumorClasses( jcas, lookupWindow, lookupDisorders );
         ReceptorStatusFinder.getInstance().addReceptorStatuses( jcas, lookupWindow, lookupDisorders );
         SizeFinder.getInstance().addSizes( jcas, lookupWindow, lookupDisorders, lookupSymptoms );
         lookupDisorders.clear();
         lookupSymptoms.clear();

         if ( LOGGER.isDebugEnabled() ) {
            printCancerFindings( jcas );
         }
      }

      LOGGER.info( "Finished processing" );
   }

   /**
    * Only run for debug
    *
    * @param jcas -
    */
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
      final Collection<CancerSizeTextRelation> cancerSizeRelations
            = JCasUtil.select( jcas, CancerSizeTextRelation.class );
      for ( CancerSizeTextRelation relation : cancerSizeRelations ) {
         LOGGER.debug( "Cancer Size Relation " + relation.getArg1().getArgument().toString()
                       + "\n\tRelated To: " + relation.getArg2().getArgument().getCoveredText() );
      }
   }

   public static AnalysisEngineDescription createAnnotatorDescription() throws ResourceInitializationException {
      return AnalysisEngineFactory.createEngineDescription( TnmAnnotator.class );
   }
}
