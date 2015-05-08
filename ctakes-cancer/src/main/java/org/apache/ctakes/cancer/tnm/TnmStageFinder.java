package org.apache.ctakes.cancer.tnm;

import org.apache.ctakes.cancer.type.relation.TnmStageTextRelation;
import org.apache.ctakes.cancer.type.textsem.*;
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
 * @since 4/28/2015
 */
public enum TnmStageFinder {
   // TODO This will probably become a util class
   INSTANCE;

   public static TnmStageFinder getInstance() {
      return INSTANCE;
   }


   static private final Logger LOGGER = Logger.getLogger( "TnmStageFinder" );

   static private final Pattern WHITESPACE_PATTERN = Pattern.compile( "\\s+" );

   static private class MalignantTumorClassification {
      private final Map<MalignantClassType,MalignantClass> __malignantClassMap;
      private final Collection<MalignantClassificationOption> __classificationOptions;
      private final int __startOffset;
      private final int __endOffset;
      private MalignantTumorClassification( final Map<MalignantClassType, MalignantClass> malignantClassMap,
                                            final Collection<MalignantClassificationOption> classificationOptions ) {
         __malignantClassMap = malignantClassMap;
         __classificationOptions = new ArrayList<>( classificationOptions );
         int startOffset = Integer.MAX_VALUE;
         int endOffset = Integer.MIN_VALUE;
         for ( MalignantClass stage : malignantClassMap.values() ) {
            startOffset = Math.min( stage.__startOffset, startOffset );
            endOffset = Math.max( stage.__endOffset, endOffset );
         }
         for ( MalignantClassificationOption option : classificationOptions ) {
            endOffset = Math.max( option.__endOffset, endOffset );
         }
         __startOffset = startOffset;
         __endOffset = endOffset;
      }
      public String toString() {
         final StringBuilder sb = new StringBuilder();
         for ( MalignantClassType classType : MalignantClassType.values() ) {
            final MalignantClass stage = __malignantClassMap.get( classType );
            if ( stage != null ) {
               sb.append( stage.toString() ).append( ", " );
            }
         }
         for ( MalignantClassificationOption option : __classificationOptions ) {
            sb.append( option.toString() ).append( ", " );
         }
         sb.setLength( sb.length()-1 );
         return sb.toString();
      }
   }

   static private class MalignantClass {
      private final MaligantClassPrefixType __classPrefixType;
      private final MalignantClassType __classType;
      private final int __startOffset;
      private final int __endOffset;
      private final String __value;

      private MalignantClass( final MaligantClassPrefixType classPrefixType,
                              final MalignantClassType classType,
                              final int startOffset,
                              final int endOffset,
                              final String value ) {
         __classPrefixType = classPrefixType;
         __classType = classType;
         __startOffset = startOffset;
         __endOffset = endOffset;
         __value = value;
      }
      public String toString() {
         return __classType.__title + ": " + __value + " ; " + __classPrefixType.__title;
      }
   }

   static private class MalignantClassificationOption {
      static private final String CERTAINTY_TITLE = "Certainty of last mentioned parameter";
      private final MalignantOptionType __optionType;
      private final int __startOffset;
      private final int __endOffset;
      private final int __value;
      private final int __certainty;
      private MalignantClassificationOption( final MalignantOptionType optionType,
                                             final int startOffset, final int endOffset,
                                             final int value, final int certainty ) {
         __optionType = optionType;
         __startOffset = startOffset;
         __endOffset = endOffset;
         __value = value;
         __certainty = certainty;
      }
      public String toString() {
         return __optionType.__title +  ": " + __value
                + ( __certainty > 0 ? " , " + CERTAINTY_TITLE + ": " + __certainty : "" );
      }
   }

   // http://en.wikipedia.org/wiki/TNM_staging_system
   // http://www.cancer.gov/cancertopics/diagnosis-staging/staging/staging-fact-sheet
   // http://cancerstaging.blogspot.it
   // I think that the specifications are case-sensitive ...
   static private enum MalignantClassType {
      T( 0, "Size or direct extent of the primary tumor", "T(x|is|[0-4][a-z]?)(\\((m|\\d+)?,?(is)?\\))?" ),
      N( 1, "Degree of spread to regional lymph nodes", "N(x|[0-3][a-z]?)" ),
      M( 2, "Presence of distant metastasis", "M(x|[0-1][a-z]?)" );
      final private int __order;
      final private String __title;
      final private Pattern __pattern;
      private MalignantClassType( final int order, final String title, final String regex ) {
         __order = order;
         __title = title;
         __pattern = Pattern.compile( regex );
      }
      private Matcher getMatcher( final CharSequence lookupWindow ) {
         return __pattern.matcher( lookupWindow );
      }
   }

   static private enum MaligantClassPrefixType {
      C( "Stage given by clinical examination", 'c' ),
      P( "Stage given by pathologic examination", 'p' ),
      Y( "Stage assessed after chemotherapy and/or radiation", 'y' ),
      R( "Stage for a recurrent tumor", 'r' ),
      A( "Stage determined at autopsy", 'a' ),
      U( "Stage determined by ultrasound or endosonography", 'u' ),
      UNSPECIFIED( "Stage determination unspecified; assume clinical examination", '-' );
      final private String __title;
      final private char __characterCode;
      private MaligantClassPrefixType( final String title, final char characterCode ) {
         __title = title;
         __characterCode = characterCode;
      }
      static private MaligantClassPrefixType getPrefix( final CharSequence lookupWindow, final int stageStartOffset ) {
         if ( stageStartOffset <= 0 ) {
            return UNSPECIFIED;
         }
         final char c = lookupWindow.charAt( stageStartOffset-1 );
         for ( MaligantClassPrefixType prefix : values() ) {
            if ( c == prefix.__characterCode ) {
               return prefix;
            }
         }
         return UNSPECIFIED;
      }
   }



   // http://en.wikipedia.org/wiki/TNM_staging_system
   static private enum MalignantOptionType {
      G( "Grade of cancer cells", "G[1-4](C[1-5])?" ),
      S( "Elevation of serum tumor markers", "S[0-3](C[1-5])?" ),
      R( "Completeness of the operation", "R[0-2](C[1-5])?" ),
      L( "Invasion into lymphatic vessels", "L[0-1](C[1-5])?" ),
      V( "Invasion into vein", "V[0-2](C[1-5])?" );
      final private String __title;
      final private Pattern __pattern;
      private MalignantOptionType( final String title, final String regex ) {
         __title = title;
         __pattern = Pattern.compile( regex );
      }
      public String getTitle() {
         return __title;
      }
      private Matcher getMatcher( final CharSequence lookupWindow ) {
         return __pattern.matcher( lookupWindow );
      }
   }

   static private List<MalignantClass> getMalignantClasses( final String lookupWindow ) {
      final List<MalignantClass> stages = new ArrayList<>();
      for ( MalignantClassType classType : MalignantClassType.values() ) {
         final Matcher matcher = classType.getMatcher( lookupWindow );
         while ( matcher.find() ) {
            int startOffset = matcher.start();
            final MaligantClassPrefixType prefix = MaligantClassPrefixType.getPrefix( lookupWindow, startOffset );
            if ( prefix != MaligantClassPrefixType.UNSPECIFIED ) {
               startOffset -= 1;
            }
            stages.add( new MalignantClass( prefix, classType, startOffset, matcher.end(),
                  lookupWindow.substring( matcher.start()+1, matcher.end() ) ) );
         }
      }
      return stages;
   }


   static private Collection<MalignantClassificationOption> getMalignantOptions( final CharSequence lookupWindow,
                                                                                 final int stageEndIndex ) {
      if ( stageEndIndex >= lookupWindow.length() ) {
         return Collections.emptyList();
      }
      final char firstChar = lookupWindow.charAt( stageEndIndex );
      if ( ( firstChar == ' ' ) || firstChar == '\t' || firstChar == '\r' || firstChar == '\n' ) {
         return Collections.emptyList();
      }
      final String[] splits
            = WHITESPACE_PATTERN.split( lookupWindow.subSequence( stageEndIndex, lookupWindow.length() ) );
      if ( splits.length < 1 ) {
         return Collections.emptyList();
      }
      final Collection<MalignantClassificationOption> options = new ArrayList<>();
      for ( MalignantOptionType parameter : MalignantOptionType.values() ) {
         final Matcher matcher = parameter.getMatcher( splits[0] );
         while ( matcher.find() ) {
            options.add( new MalignantClassificationOption( parameter,
                  stageEndIndex+matcher.start(), stageEndIndex+matcher.end(),
                  getIntValue( splits[ 0 ], matcher.start(), matcher.end() ),
                  getOptionCertainty( splits[ 0 ], matcher.start(), matcher.end() ) ) );
         }
      }
      return options;
   }

   static private int getOptionCertainty( final String text, final int startOffset, final int endOffset ) {
      if ( endOffset - startOffset != 4 ) {
         return -1;
      }
      return getIntValue( text, endOffset - 2, endOffset );
   }


   static private Collection<MalignantTumorClassification> getMalignantTumorClassifications( final String lookupWindow ) {
      final List<MalignantClass> stages = getMalignantClasses( lookupWindow );
      if ( stages.isEmpty() ) {
         return Collections.emptyList();
      }
      Collections.sort( stages, MalignantClassOffsetComparator.getInstance() );
      final Collection<MalignantTumorClassification> classifications = new ArrayList<>();
      final EnumMap<MalignantClassType,MalignantClass> malignantClassMap = new EnumMap<>( MalignantClassType.class );
      int currentOrder = -1;
      int currentStart = -1;
      int currentEnd = -1;
      for ( MalignantClass stage : stages ) {
         if ( currentStart < 0 ) {
            currentOrder = stage.__classType.__order;
            currentStart = stage.__startOffset;
         } else if ( stage.__classType.__order <= currentOrder || stage.__startOffset > currentEnd + 1 ) {
            // Ordering of TNM is set, so if things occur out of order start a new one
            // Or if the next start is more than one space away then start a new one
            classifications.add( new MalignantTumorClassification( malignantClassMap,
                  getMalignantOptions( lookupWindow, currentEnd ) ) );
            malignantClassMap.clear();
            currentStart = stage.__startOffset;
         }
         malignantClassMap.put( stage.__classType, stage );
         currentEnd = stage.__endOffset;
      }
      classifications.add( new MalignantTumorClassification( malignantClassMap,
            getMalignantOptions( lookupWindow, currentEnd ) ) );
      return classifications;
   }

   private enum MalignantClassOffsetComparator implements Comparator<MalignantClass> {
      INSTANCE;
      public static MalignantClassOffsetComparator getInstance() {
         return INSTANCE;
      }
      @Override
      public int compare( final MalignantClass stage1, final MalignantClass stage2 ) {
         return stage1.__startOffset - stage2.__startOffset;
      }
   }

   static private int getIntValue( final String text, final int startOffset, final int endOffset ) {
      return getIntValue( text.substring( startOffset, endOffset ) );
   }

   static private int getIntValue( final String tnmItem ) {
      final String tnmNum = tnmItem.substring( 1, 2 );
      try {
         return Integer.parseInt( tnmNum );
      } catch ( NumberFormatException nfE ) {
         LOGGER.error( "Could not parse value for " + tnmItem );
      }
      return -1;
   }

   public void addMalignantTumorClasses( final JCas jcas, final AnnotationFS lookupWindow,
                                         final Iterable<DiseaseDisorderMention> lookupWindowT191s ) {
      final Collection<MalignantTumorClassification> malignantTumorClassifications
            = getMalignantTumorClassifications( lookupWindow.getCoveredText() );
      if ( malignantTumorClassifications.isEmpty() ) {
         return;
      }
      final int windowStartOffset = lookupWindow.getBegin();
      for ( MalignantTumorClassification classification : malignantTumorClassifications ) {
         final DiseaseDisorderMention closestDiseaseMention
               = FinderUtil.getClosestEventMention( windowStartOffset + classification.__startOffset,
               windowStartOffset + classification.__endOffset, lookupWindowT191s );
         final TnmClassification tnmAnnotation = createTnmAnnotation( jcas, lookupWindow, classification );
         addTnmRelationToCas( jcas, tnmAnnotation, closestDiseaseMention );
      }
   }

   static private TnmClassification createTnmAnnotation( final JCas jcas,
                                                         final AnnotationFS lookupWindow,
                                                         final MalignantTumorClassification classification ) {
      final TnmClassification tnmClassificationType = new TnmClassification( jcas,
            lookupWindow.getBegin()+classification.__startOffset,
            lookupWindow.getBegin()+classification.__endOffset );
      final MalignantClass sizeClass = classification.__malignantClassMap.get( MalignantClassType.T );
      if ( sizeClass != null ) {
         tnmClassificationType.setSize( createTnmStageFeature( jcas, sizeClass ) );
      }
      final MalignantClass nodeSpreadClass = classification.__malignantClassMap.get( MalignantClassType.N );
      if ( nodeSpreadClass != null ) {
         tnmClassificationType.setNodeSpread( createTnmStageFeature( jcas, nodeSpreadClass ) );
      }
      final MalignantClass metastasisClass = classification.__malignantClassMap.get( MalignantClassType.M );
      if ( metastasisClass != null ) {
         tnmClassificationType.setMetastasis( createTnmStageFeature( jcas, metastasisClass ) );
      }
      if ( !classification.__classificationOptions.isEmpty() ) {
         final FSArray optionFeatures = new FSArray( jcas, classification.__classificationOptions.size() );
         int optionIndex = 0;
         for ( MalignantClassificationOption option : classification.__classificationOptions ) {
            final TnmOption tnmOptionFeature = new TnmOption( jcas );
            tnmOptionFeature.setCode( option.__optionType.name() );
            tnmOptionFeature.setDescription( option.__optionType.getTitle() );
            tnmOptionFeature.setValue( option.__value );
            tnmOptionFeature.setCertainty( option.__certainty );
            optionFeatures.set( optionIndex, tnmOptionFeature );
            optionIndex++;
         }
         tnmClassificationType.setOptions( optionFeatures );
      }
      // Sets the tnm annotation to match the umls concept.  I'm not sure that we want/need this
      tnmClassificationType.setTypeID( NE_TYPE_ID_FINDING );
      final UmlsConcept umlsConcept = new UmlsConcept( jcas );
      umlsConcept.setCui( "C1300492" );
      umlsConcept.setTui( "T034" );
      umlsConcept.setCodingScheme( "SNOMED" );
      umlsConcept.setCode( "385379008" );
      umlsConcept.setPreferredText( "TNM tumor staging finding" );
      final FSArray ontologyConcepts = new FSArray( jcas, 1 );
      ontologyConcepts.set( 0, umlsConcept );
      tnmClassificationType.setOntologyConceptArr( ontologyConcepts );
      tnmClassificationType.addToIndexes();
      return tnmClassificationType;
   }

   static private TnmStagePrefix createTnmStagePrefix( final JCas jcas, final MalignantClass malignantClass ) {
      final MaligantClassPrefixType prefixType = malignantClass.__classPrefixType;
      final TnmStagePrefix tnmStagePrefix = new TnmStagePrefix( jcas );
      tnmStagePrefix.setCode( prefixType.__characterCode + "" );
      tnmStagePrefix.setDescription( prefixType.__title );
      return tnmStagePrefix;
   }

   static private TnmStage createTnmStageFeature( final JCas jcas, final MalignantClass malignantClass ) {
      final TnmStage tnmStageFeature = new TnmStage( jcas );
      tnmStageFeature.setPrefix( createTnmStagePrefix( jcas, malignantClass ) );
      tnmStageFeature.setCode( malignantClass.__classType.name() );
      tnmStageFeature.setDescription( malignantClass.__classType.__title );
      tnmStageFeature.setValue( malignantClass.__value );
      return tnmStageFeature;
   }

   /**
    * Create a UIMA relation type based on arguments and the relation label. This
    * allows subclasses to create/define their own types: e.g. coreference can
    * create CoreferenceRelation instead of BinaryTextRelation
    *
    * @param jCas - JCas object, needed to create new UIMA types
    * @param tnmClassification - First argument to relation
    * @param disorderMention - Second argument to relation
    */
   static private void addTnmRelationToCas( final JCas jCas,
                                            final TnmClassification tnmClassification,
                                            final DiseaseDisorderMention disorderMention ) {
      // add the relation to the CAS
      final RelationArgument tnmClassificationArgument = new RelationArgument( jCas );
      tnmClassificationArgument.setArgument( tnmClassification );
      tnmClassificationArgument.setRole( "Argument" );
      tnmClassificationArgument.addToIndexes();
      final RelationArgument disorderMentionArgument = new RelationArgument( jCas );
      disorderMentionArgument.setArgument( disorderMention );
      disorderMentionArgument.setRole( "Related_to" );
      disorderMentionArgument.addToIndexes();
      final TnmStageTextRelation tnmStageTextRelation = new TnmStageTextRelation( jCas );
      tnmStageTextRelation.setArg1( tnmClassificationArgument );
      tnmStageTextRelation.setArg2( disorderMentionArgument );
      tnmStageTextRelation.setCategory( "TNM_Stage" );
      tnmStageTextRelation.addToIndexes();
   }

}
