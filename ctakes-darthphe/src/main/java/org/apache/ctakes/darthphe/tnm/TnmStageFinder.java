package org.apache.ctakes.darthphe.tnm;

import org.apache.log4j.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author SPF , chip-nlp
 * @version %I%
 * @since 4/28/2015
 */
public enum TnmStageFinder {
   INSTANCE;

   public static TnmStageFinder getInstance() {
      return INSTANCE;
   }


   static private final Logger LOGGER = Logger.getLogger( "TnmRegexFinder" );

   static private final Pattern WHITESPACE_PATTERN = Pattern.compile( "\\s+" );

   static private class FullTnmStage {
      private final Collection<TnmStage> __stages;
      private final Collection<TnmOption> __optionalParameters;
      private final int __startOffset;
      private final int __endOffset;
      private FullTnmStage( final Collection<TnmStage> stages, final Collection<TnmOption> optionalParameters ) {
         __stages = new ArrayList<>( stages );
         __optionalParameters = new ArrayList<>( optionalParameters );
         int startOffset = Integer.MAX_VALUE;
         int endOffset = Integer.MIN_VALUE;
         for ( TnmStage stage : stages ) {
            startOffset = Math.min( stage.__startOffset, startOffset );
            endOffset = Math.max( stage.__endOffset, endOffset );
         }
         for ( TnmOption option : optionalParameters ) {
            endOffset = Math.max( option.__endOffset, endOffset );
         }
         __startOffset = startOffset;
         __endOffset = endOffset;
      }
      public String toString() {
         final StringBuilder sb = new StringBuilder();
         for ( TnmStage stage : __stages ) {
            sb.append( stage.toString() ).append( ", " );
         }
         for ( TnmOption option : __optionalParameters ) {
            sb.append( option.toString() ).append( ", " );
         }
         sb.setLength( sb.length()-1 );
         return sb.toString();
      }
   }

   static private class TnmStage {
      private final TnmPrefix __prefix;
      private final TnmMandatory __mandatory;
      private final int __startOffset;
      private final int __endOffset;
      private final String __value;

      private TnmStage( final TnmPrefix prefix, final TnmMandatory mandatory, final int startOffset, final int endOffset,
                        final String value ) {
         __prefix = prefix;
         __mandatory = mandatory;
         __startOffset = startOffset;
         __endOffset = endOffset;
         __value = value;
      }
      public String toString() {
         return __mandatory.__title + ": " + __value + " ; " + __prefix.__title;
      }
   }

   static private class TnmOption {
      static private final String CERTAINTY_TITLE = "Certainty of last mentioned parameter";
      private final OptionalParameter __optionalParameter;
      private final int __startOffset;
      private final int __endOffset;
      private final int __value;
      private final int __certainty;
      private TnmOption( final OptionalParameter optionalParameter, final int startOffset, final int endOffset,
                         final int value, final int certainty ) {
         __optionalParameter = optionalParameter;
         __startOffset = startOffset;
         __endOffset = endOffset;
         __value = value;
         __certainty = certainty;
      }
      public String toString() {
         return __optionalParameter.__title +  ": " + __value
                + ( __certainty > 0 ? " , " + CERTAINTY_TITLE + ": " + __certainty : "" );
      }
   }

   // http://en.wikipedia.org/wiki/TNM_staging_system
   // http://www.cancer.gov/cancertopics/diagnosis-staging/staging/staging-fact-sheet
   // http://cancerstaging.blogspot.it
   static private enum TnmMandatory {
      T( 0, "Size or direct extent of the primary tumor", "T(x|is|[0-4][a-z]?)(\\((m|\\d+)?,?(is)?\\))?" ),
      N( 1, "Degree of spread to regional lymph nodes", "N(x|[0-3][a-z]?)" ),
      M( 2, "Presence of distant metastasis", "M(x|[0-1][a-z]?)" );
      final private int __order;
      final private String __title;
      final private Pattern __pattern;
      private TnmMandatory( final int order, final String title, final String regex ) {
         __order = order;
         __title = title;
         __pattern = Pattern.compile( regex );
      }
      private Matcher getMatcher( final CharSequence lookupWindow ) {
         return __pattern.matcher( lookupWindow );
      }
   }

   static private enum TnmPrefix {
      C( "Stage given by clinical examination", 'c' ),
      P( "Stage given by pathologic examination", 'p' ),
      Y( "Stage assessed after chemotherapy and/or radiation", 'y' ),
      R( "Stage for a recurrent tumor", 'r' ),
      A( "Stage determined at autopsy", 'a' ),
      U( "Stage determined by ultrasound or endosonography", 'u' ),
      UNSPECIFIED( "Stage determination unspecified; assume clinical examination", '-' );
      final private String __title;
      final private char __characterCode;
      private TnmPrefix( final String title, final char characterCode ) {
         __title = title;
         __characterCode = characterCode;
      }
      static private TnmPrefix getPrefix( final CharSequence lookupWindow, final int stageStartOffset ) {
         if ( stageStartOffset <= 0 ) {
            return UNSPECIFIED;
         }
         final char c = lookupWindow.charAt( stageStartOffset-1 );
         for ( TnmPrefix prefix : values() ) {
            if ( c == prefix.__characterCode ) {
               return prefix;
            }
         }
         return UNSPECIFIED;
      }
   }



   // http://en.wikipedia.org/wiki/TNM_staging_system
   static private enum OptionalParameter {
      G( "Grade of cancer cells", "G[1-4](C[1-5])?" ),
      S( "Elevation of serum tumor markers", "S[0-3](C[1-5])?" ),
      R( "Completeness of the operation", "R[0-2](C[1-5])?" ),
      L( "Invasion into lymphatic vessels", "L[0-1](C[1-5])?" ),
      V( "Invasion into vein", "V[0-2](C[1-5])?" );
      final private String __title;
      final private Pattern __pattern;
      private OptionalParameter( final String title, final String regex ) {
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

   static private List<TnmStage> getTnmStages( final String lookupWindow ) {
      final List<TnmStage> stages = new ArrayList<>();
      for ( TnmMandatory tnmMandatory : TnmMandatory.values() ) {
         final Matcher matcher = tnmMandatory.getMatcher( lookupWindow );
         while ( matcher.find() ) {
            int startOffset = matcher.start();
            final TnmPrefix prefix = TnmPrefix.getPrefix( lookupWindow, startOffset );
            if ( prefix != TnmPrefix.UNSPECIFIED ) {
               startOffset -= 1;
            }
            stages.add( new TnmStage( prefix, tnmMandatory, startOffset, matcher.end(),
//                  getTnmValue( lookupWindow, matcher.start(), matcher.end() ) ) );
                  lookupWindow.substring( matcher.start()+1, matcher.end() ) ) );
         }
      }
      return stages;
   }


   static private Collection<TnmOption> getTnmOptions( final CharSequence lookupWindow, final int stageEndIndex ) {
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
      final Collection<TnmOption> options = new ArrayList<>();
      for ( OptionalParameter parameter : OptionalParameter.values() ) {
         final Matcher matcher = parameter.getMatcher( splits[0] );
         while ( matcher.find() ) {
            options.add( new TnmOption( parameter,
                  stageEndIndex+matcher.start(), stageEndIndex+matcher.end(),
                  getTnmValue( splits[0], matcher.start(), matcher.end() ),
                  getOptionCertainty( splits[0], matcher.start(), matcher.end() ) ) );
         }
      }
      return options;
   }

   static private int getOptionCertainty( final String text, final int startOffset, final int endOffset ) {
      if ( endOffset - startOffset != 4 ) {
         return -1;
      }
      return getTnmValue( text, endOffset-2, endOffset );
   }


   static private Collection<FullTnmStage> getFullTnmStages( final String lookupWindow ) {
      final List<TnmStage> stages = getTnmStages( lookupWindow );
      if ( stages.isEmpty() ) {
         return Collections.emptyList();
      }
      Collections.sort( stages, TnmStageOffsetComparator.getInstance() );
      final Collection<FullTnmStage> fullStages = new ArrayList<>();
      Collection<TnmStage> currentStages = new ArrayList<>( 3 );
      int currentOrder = -1;
      int currentStart = -1;
      int currentEnd = -1;
      for ( TnmStage stage : stages ) {
         if ( currentStart < 0 ) {
            currentOrder = stage.__mandatory.__order;
            currentStart = stage.__startOffset;
         } else if ( stage.__mandatory.__order <= currentOrder || stage.__startOffset > currentEnd + 1 ) {
            // Ordering of TNM is set, so if things occur out of order start a new one
            // Or if the next start is more than one space away then start a new one
            fullStages.add( new FullTnmStage( currentStages, getTnmOptions( lookupWindow, currentEnd ) ) );
            currentStages.clear();
            currentStart = stage.__startOffset;
         }
         currentStages.add( stage );
         currentEnd = stage.__endOffset;
      }
      fullStages.add( new FullTnmStage( currentStages, getTnmOptions( lookupWindow, currentEnd ) ) );
      return fullStages;
   }

   private enum TnmStageOffsetComparator implements Comparator<TnmStage> {
      INSTANCE;
      public static TnmStageOffsetComparator getInstance() {
         return INSTANCE;
      }
      @Override
      public int compare( final TnmStage stage1, final TnmStage stage2 ) {
         return stage1.__startOffset - stage2.__startOffset;
      }
   }

   static private int getTnmValue( final String text, final int startOffset, final int endOffset ) {
      return getTnmValue( text.substring( startOffset, endOffset ) );
   }

   static private int getTnmValue( final String tnmItem ) {
      final String tnmNum = tnmItem.substring( 1, 2 );
      try {
         return Integer.parseInt( tnmNum );
      } catch ( NumberFormatException nfE ) {
         LOGGER.error( "Could not parse value for " + tnmItem );
      }
      return -1;
   }




   public Collection<String> getMalignantTumorClasses( final String lookupWindow ) {
      System.out.println( lookupWindow );
      final Collection<FullTnmStage> fullTnmStages = getFullTnmStages( lookupWindow );
      final Collection<String> tumorClasses = new ArrayList<>();
      for ( FullTnmStage fullTnmStage : fullTnmStages ) {
         final String tumorClass = lookupWindow.substring( fullTnmStage.__startOffset, fullTnmStage.__endOffset );
         System.out.println( "\t" + tumorClass + "\n\t" + fullTnmStage.toString().replace( ',', '\n' ) );
         tumorClasses.add( tumorClass );
      }
      return tumorClasses;
   }


}
