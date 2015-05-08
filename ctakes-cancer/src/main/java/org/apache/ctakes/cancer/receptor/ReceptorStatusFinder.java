package org.apache.ctakes.cancer.receptor;

import org.apache.ctakes.cancer.type.relation.ReceptorStatusTextRelation;
import org.apache.ctakes.cancer.type.relation.TnmStageTextRelation;
import org.apache.ctakes.cancer.type.textsem.ReceptorStatus;
import org.apache.ctakes.cancer.type.textsem.TnmClassification;
import org.apache.ctakes.cancer.util.FinderUtil;
import org.apache.ctakes.typesystem.type.refsem.UmlsConcept;
import org.apache.ctakes.typesystem.type.relation.RelationArgument;
import org.apache.ctakes.typesystem.type.textsem.DiseaseDisorderMention;
import org.apache.log4j.Logger;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.ctakes.typesystem.type.constants.CONST.NE_TYPE_ID_FINDING;

/**
 * @author SPF , chip-nlp
 * @version %I%
 * @since 4/29/2015
 */
public enum ReceptorStatusFinder {
   // TODO This will probably become a util class
   INSTANCE;

   public static ReceptorStatusFinder getInstance() {
      return INSTANCE;
   }


   static private final Logger LOGGER = Logger.getLogger( "ReceptorStatusFinder" );

   static public interface ReceptorStatusValue {
      public String getTitle();
      public boolean getBooleanValue();
   }

   static public final ReceptorStatusValue UNKNOWN_STATUS_VALUE = new ReceptorStatusValue() {
      public String getTitle() {
         return "Unknown Receptor Status Value";
      }
      public boolean getBooleanValue() {
         return true;
      }
   };

   static public enum DefinedReceptorStatusValue implements ReceptorStatusValue{
      POSITIVE( "Positive", true, "\\+(pos)?" ),
      NEGATIVE( "Negative", false, "-(neg)?" );
      final private String __title;
      final private boolean __booleanValue;
      final private Pattern __pattern;
      private DefinedReceptorStatusValue( final String title, final boolean booleanValue, final String regex ) {
         __title = title;
         __booleanValue = booleanValue;
         __pattern = Pattern.compile( regex );
      }
      @Override
      public String getTitle() {
         return __title;
      }
      private Matcher getMatcher( final CharSequence lookupWindow ) {
         return __pattern.matcher( lookupWindow );
      }
      @Override
      public boolean getBooleanValue() {
         return __booleanValue;
      }
   }

   // http://www.breastcancer.org/symptoms/diagnosis/hormone_status
   // http://www.breastcancer.org/symptoms/diagnosis/hormone_status/read_results
   // I think that the specifications are case-sensitive ...
   static private enum ReceptorStatusType {
      ER( "Estrogen receptor", "\\bER(\\+(pos)?|-(neg)?)", "T034", "C0279754", "C0279756" ),
      PR( "Progesterone receptor", "(\\b|/)PR(\\+(pos)?|-(neg)?)", "T034", "C0279759", "C0279766" ),
      HER2( "Human epidermal growth factor receptor 2", "\\bHER2(/neu)?(\\+(pos)?|-(neg)?)", "T191", "C1960398", "C2316304" );
      final private String __title;
      final private Pattern __pattern;
      final private String __tui;
      final private String __positiveCui;
      final private String __negativeCui;
      private ReceptorStatusType( final String title, final String regex, final String tui,
                                  final String positiveCui, final String negativeCui) {
         __title = title;
         __pattern = Pattern.compile( regex );
         __tui = tui;
         __positiveCui = positiveCui;
         __negativeCui = negativeCui;
      }
      private Matcher getMatcher( final CharSequence lookupWindow ) {
         return __pattern.matcher( lookupWindow );
      }
      private String getCui( final ReceptorStatusValue statusValue ) {
         if ( statusValue == DefinedReceptorStatusValue.POSITIVE ) {
            return __positiveCui;
         }
         return __negativeCui;
      }
   }

   static private class HormoneReceptorStatus {
      private final ReceptorStatusType __statusType;
      private final int __startOffset;
      private final int __endOffset;
      private final ReceptorStatusValue __value;
      private HormoneReceptorStatus( final ReceptorStatusType statusType, final int startOffset, final int endOffset,
                                     final ReceptorStatusValue value ) {
         __statusType = statusType;
         __startOffset = startOffset;
         __endOffset = endOffset;
         __value = value;
      }
      public String toString() {
         return __statusType.__title + ": " + __value.getTitle();
      }
   }

   static private Collection<HormoneReceptorStatus> getReceptorStatuses( final String lookupWindow ) {
      final List<HormoneReceptorStatus> receptorStatuses = new ArrayList<>();
      for ( ReceptorStatusType receptorStatusType : ReceptorStatusType.values() ) {
         final Matcher matcher = receptorStatusType.getMatcher( lookupWindow );
         while ( matcher.find() ) {
            receptorStatuses.add( new HormoneReceptorStatus( receptorStatusType, matcher.start(), matcher.end(),
                  getReceptorStatusValue( lookupWindow, matcher.start(), matcher.end() ) ) );
         }
      }
      Collections.sort( receptorStatuses, ReceptorStatusOffsetComparator.getInstance() );
      return receptorStatuses;
   }


   static private ReceptorStatusValue getReceptorStatusValue( final String text, final int startOffset, final int endOffset ) {
      return getReceptorStatusValue( text.substring( startOffset, endOffset ) );
   }

   static private ReceptorStatusValue getReceptorStatusValue( final CharSequence receptorText ) {
      for ( DefinedReceptorStatusValue receptorStatusValue : DefinedReceptorStatusValue.values() ) {
         final Matcher matcher = receptorStatusValue.getMatcher( receptorText );
         if ( matcher.find() ) {
            return receptorStatusValue;
         }
      }
      return UNKNOWN_STATUS_VALUE;
   }


   private enum ReceptorStatusOffsetComparator implements Comparator<HormoneReceptorStatus> {
      INSTANCE;
      public static ReceptorStatusOffsetComparator getInstance() {
         return INSTANCE;
      }
      @Override
      public int compare( final HormoneReceptorStatus status1, final HormoneReceptorStatus status2 ) {
         return status1.__startOffset - status2.__startOffset;
      }
   }

   public void addReceptorStatuses( final JCas jcas, final AnnotationFS lookupWindow,
                                    final Iterable<DiseaseDisorderMention> lookupWindowT191s ) {
      final Collection<HormoneReceptorStatus> receptorStatuses = getReceptorStatuses( lookupWindow.getCoveredText() );
      if ( receptorStatuses.isEmpty() ) {
         return;
      }
      final int windowStartOffset = lookupWindow.getBegin();
      for ( HormoneReceptorStatus receptorStatus : receptorStatuses ) {
         final DiseaseDisorderMention closestDiseaseMention
               = FinderUtil.getClosestEventMention( windowStartOffset + receptorStatus.__startOffset,
               windowStartOffset + receptorStatus.__endOffset, lookupWindowT191s );
         final ReceptorStatus receptorStatusAnnotation = createReceptorStatusAnnotation( jcas, lookupWindow, receptorStatus );
         addReceptorRelationToCas( jcas, receptorStatusAnnotation, closestDiseaseMention );
      }
   }

   static private ReceptorStatus createReceptorStatusAnnotation( final JCas jcas,
                                                                 final AnnotationFS lookupWindow,
                                                                 final HormoneReceptorStatus receptorStatus ) {
      final ReceptorStatus receptorStatusAnnotation
            = new ReceptorStatus( jcas,
            lookupWindow.getBegin()+receptorStatus.__startOffset,
            lookupWindow.getBegin()+receptorStatus.__endOffset );
      receptorStatusAnnotation.setCode( receptorStatus.__statusType.name() );
      receptorStatusAnnotation.setDescription( receptorStatus.__statusType.__title );
      receptorStatusAnnotation.setValue( receptorStatus.__value.getBooleanValue() );
      // Sets the receptor status annotation to match the umls concept.  I'm not sure that we want/need this
      receptorStatusAnnotation.setTypeID( NE_TYPE_ID_FINDING );
      final UmlsConcept umlsConcept = new UmlsConcept( jcas );
      umlsConcept.setCui( receptorStatus.__statusType.getCui( receptorStatus.__value ) );
      umlsConcept.setTui( receptorStatus.__statusType.__tui );
//      umlsConcept.setCodingScheme( "SNOMED" ); ---> These are more NCI than SNOMED
//      umlsConcept.setCode( "385379008" );
      umlsConcept.setPreferredText( receptorStatus.__value.getTitle() + " " + receptorStatus.__statusType.__title );
      final FSArray ontologyConcepts = new FSArray( jcas, 1 );
      ontologyConcepts.set( 0, umlsConcept );
      receptorStatusAnnotation.setOntologyConceptArr( ontologyConcepts );



      receptorStatusAnnotation.addToIndexes();
      return receptorStatusAnnotation;
   }


   /**
    * Create a UIMA relation type based on arguments and the relation label. This
    * allows subclasses to create/define their own types: e.g. coreference can
    * create CoreferenceRelation instead of BinaryTextRelation
    *
    * @param jCas - JCas object, needed to create new UIMA types
    * @param receptorStatus - First argument to relation
    * @param disorderMention - Second argument to relation
    */
   static private void addReceptorRelationToCas( final JCas jCas,
                                            final ReceptorStatus receptorStatus,
                                            final DiseaseDisorderMention disorderMention ) {
      // add the relation to the CAS
      final RelationArgument receptorStatusArgument = new RelationArgument( jCas );
      receptorStatusArgument.setArgument( receptorStatus );
      receptorStatusArgument.setRole( "Argument" );
      receptorStatusArgument.addToIndexes();
      final RelationArgument disorderMentionArgument = new RelationArgument( jCas );
      disorderMentionArgument.setArgument( disorderMention );
      disorderMentionArgument.setRole( "Related_to" );
      disorderMentionArgument.addToIndexes();
      final ReceptorStatusTextRelation receptorStatusRelation = new ReceptorStatusTextRelation( jCas );
      receptorStatusRelation.setArg1( receptorStatusArgument );
      receptorStatusRelation.setArg2( disorderMentionArgument );
      receptorStatusRelation.setCategory( "Receptor_status" );
      receptorStatusRelation.addToIndexes();
   }

}
