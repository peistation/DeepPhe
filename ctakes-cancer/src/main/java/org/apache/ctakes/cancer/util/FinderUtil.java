package org.apache.ctakes.cancer.util;

import org.apache.ctakes.typesystem.type.refsem.UmlsConcept;
import org.apache.ctakes.typesystem.type.textsem.DiseaseDisorderMention;
import org.apache.ctakes.typesystem.type.textsem.EventMention;
import org.apache.ctakes.typesystem.type.textsem.IdentifiedAnnotation;
import org.apache.log4j.Logger;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;

import javax.annotation.concurrent.Immutable;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author SPF , chip-nlp
 * @version %I%
 * @since 5/7/2015
 */
@Immutable
final public class FinderUtil {

   static private final Logger LOGGER = Logger.getLogger( "FinderUtil" );

   private FinderUtil() {}


   static private final FeatureStructure[] NO_FEATURES = new FeatureStructure[0];

   static private FeatureStructure[] getFeatureStructures( final IdentifiedAnnotation annotation ) {
      final FSArray fsArray = annotation.getOntologyConceptArr();
      if ( fsArray == null ) {
         return NO_FEATURES;
      }
      return fsArray.toArray();
   }

   static public boolean hasWantedTui( final IdentifiedAnnotation annotation, final String ... wantedTuis ) {
      final FeatureStructure[] featureStructures = getFeatureStructures( annotation );
      for ( FeatureStructure featureStructure : featureStructures ) {
         if ( featureStructure instanceof UmlsConcept ) {
            final String tui = ((UmlsConcept)featureStructure).getTui();
            // T191 (Neoplastic Process) works for cancer.
            // T033 is good for mass and lesion, but we are interested in DD not finding
            if ( tui == null ) {
               continue;
            }
            for ( String wantedTui : wantedTuis ) {
               if ( tui.equals( wantedTui ) ) {
                  return true;
               }
            }
         }
      }
      return false;
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

   /**
    *
    * @param testStartOffset start offset of the test text span
    * @param testEndOffset end offset of the test text span
    * @param eventMentions mentions to test for proximity to test text span
    * @param <T> extension of EventMention
    * @return the closest T to the test text span
    */
   static public <T extends EventMention> T getClosestEventMention( final int testStartOffset, final int testEndOffset,
                                                                    final Iterable<T> eventMentions ) {
      T closestMention = null;
      int smallestGap = Integer.MAX_VALUE;
      for ( T eventMention : eventMentions ) {
         final int gap = Math.max( eventMention.getBegin() - testEndOffset,
               testStartOffset - eventMention.getEnd() );
         if ( gap < smallestGap ) {
            closestMention = eventMention;
            smallestGap = gap;
         }
      }
      return closestMention;
   }

   /**
    * TODO kludgy at this point.
    * @param testStartOffset start offset of the test text span
    * @param testEndOffset end offset of the test text span
    * @param eventMention1 -
    * @param eventMention2 -
    * @return true if the diseaseDisorder is closer to the test text span than the signSymptom
    */
   static public EventMention getCloserEventMention( final int testStartOffset, final int testEndOffset,
                                                     final EventMention eventMention1,
                                                     final EventMention eventMention2 ) {
      if ( eventMention1 == null ) {
         return eventMention2;
      } else if ( eventMention2 == null ) {
         return eventMention1;
      }
      return getClosestEventMention( testStartOffset, testEndOffset, Arrays.asList( eventMention1, eventMention2 ) );
   }


}
