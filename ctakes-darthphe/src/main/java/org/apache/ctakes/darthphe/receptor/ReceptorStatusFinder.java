package org.apache.ctakes.darthphe.receptor;

import org.apache.log4j.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author SPF , chip-nlp
 * @version %I%
 * @since 4/29/2015
 */
public enum ReceptorStatusFinder {
   INSTANCE;

   public static ReceptorStatusFinder getInstance() {
      return INSTANCE;
   }


   static private final Logger LOGGER = Logger.getLogger( "ReceptorStatusFinder" );

   static public interface ReceptorStatusValue {
      public String getTitle();
   }

   static public final ReceptorStatusValue UNKNOWN_STATUS_VALUE = new ReceptorStatusValue() {
      public String getTitle() {
         return "Unknown Receptor Status Value";
      }
   };

   static public enum DefinedReceptorStatusValue implements ReceptorStatusValue{
      POSITIVE( "Positive", "\\+(pos)?" ),
      NEGATIVE( "Negative", "-(neg)?" );
      final private String __title;
      final private Pattern __pattern;
      private DefinedReceptorStatusValue( final String title, final String regex ) {
         __title = title;
         __pattern = Pattern.compile( regex );
      }
      @Override
      public String getTitle() {
         return __title;
      }
      private Matcher getMatcher( final CharSequence lookupWindow ) {
         return __pattern.matcher( lookupWindow );
      }
   }

   // http://www.breastcancer.org/symptoms/diagnosis/hormone_status
   // http://www.breastcancer.org/symptoms/diagnosis/hormone_status/read_results
   static private enum ReceptorStatusType {
      ER( "Estrogen receptor", "\\bER(\\+(pos)?|-(neg)?)" ),
      PR( "Progesterone receptor", "(\\b|/)PR(\\+(pos)?|-(neg)?)" ),
      HER( "Human epidermal growth factor receptor 2", "\\bHER2(/neu)?(\\+(pos)?|-(neg)?)" );
      final private String __title;
      final private Pattern __pattern;
      private ReceptorStatusType( final String title, final String regex ) {
         __title = title;
         __pattern = Pattern.compile( regex );
      }
      private Matcher getMatcher( final CharSequence lookupWindow ) {
         return __pattern.matcher( lookupWindow );
      }
   }

   static private class ReceptorStatus {
      private final ReceptorStatusType __statusType;
      private final int __startOffset;
      private final int __endOffset;
      private final ReceptorStatusValue __value;
      private ReceptorStatus( final ReceptorStatusType statusType, final int startOffset, final int endOffset,
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

   static private Collection<ReceptorStatus> getReceptorStatuses( final String lookupWindow ) {
      final List<ReceptorStatus> receptorStatuses = new ArrayList<>();
      for ( ReceptorStatusType receptorStatusType : ReceptorStatusType.values() ) {
         final Matcher matcher = receptorStatusType.getMatcher( lookupWindow );
         while ( matcher.find() ) {
            receptorStatuses.add( new ReceptorStatus( receptorStatusType, matcher.start(), matcher.end(),
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


   private enum ReceptorStatusOffsetComparator implements Comparator<ReceptorStatus> {
      INSTANCE;
      public static ReceptorStatusOffsetComparator getInstance() {
         return INSTANCE;
      }
      @Override
      public int compare( final ReceptorStatus status1, final ReceptorStatus status2 ) {
         return status1.__startOffset - status2.__startOffset;
      }
   }


   public Collection<String> getAllReceptorStatuses( final String lookupWindow ) {
      System.out.println( lookupWindow );
      final Collection<ReceptorStatus> receptorStatuses = getReceptorStatuses( lookupWindow );
      final Collection<String> receptors = new ArrayList<>();
      for ( ReceptorStatus receptorStatus : receptorStatuses ) {
         final String receptor = lookupWindow.substring( receptorStatus.__startOffset, receptorStatus.__endOffset );
         System.out.println( "\t" + receptor + "\n\t" + receptorStatus.toString().replace( ',', '\n' ) );
         receptors.add( receptor );
      }
      return receptors;
   }

}
