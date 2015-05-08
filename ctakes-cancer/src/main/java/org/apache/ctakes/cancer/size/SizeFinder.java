package org.apache.ctakes.cancer.size;

import org.apache.ctakes.cancer.type.relation.CancerSizeTextRelation;
import org.apache.ctakes.cancer.type.textsem.CancerSize;
import org.apache.ctakes.cancer.type.textsem.SizeMeasurement;
import org.apache.ctakes.cancer.util.FinderUtil;
import org.apache.ctakes.typesystem.type.relation.RelationArgument;
import org.apache.ctakes.typesystem.type.textsem.DiseaseDisorderMention;
import org.apache.ctakes.typesystem.type.textsem.EventMention;
import org.apache.ctakes.typesystem.type.textsem.SignSymptomMention;
import org.apache.log4j.Logger;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author SPF , chip-nlp
 * @version %I%
 * @since 5/7/2015
 */
public enum SizeFinder {
   // TODO This will probably become a util class
   INSTANCE;

   public static SizeFinder getInstance() {
      return INSTANCE;
   }

   static private final Logger LOGGER = Logger.getLogger( "SizeFinder" );

   // look for sizes applicable to DD 191 (cancer) and SS 33 (finding), 34 (lab/test result), 184 (SS)


   static private class Measurement {
      private final float __value;
      private final String __unit;
      private Measurement( final float value, final String unit ) {
         __value = value;
         __unit = unit;
      }
      public String toString() {
         return __value + " " + __unit;
      }
   }

   static private class Size {
      private final int __startOffset;
      private final int __endOffset;
      private final Collection<Measurement> __measurements;
      private Size( final int startOffset, final int endOffset, final Collection<Measurement> measurements ) {
         __startOffset = startOffset;
         __endOffset = endOffset;
         __measurements = new ArrayList<>( measurements );
      }
      public String toString() {
         if ( __measurements.isEmpty() ) {
            return "No size";
         }
         final StringBuilder sb = new StringBuilder();
         for ( Measurement measurement : __measurements ) {
            sb.append( measurement.toString() ).append( " x " );
         }
         sb.setLength( sb.length()-3 );
         return "Size: " + sb.toString();
      }
   }

   static private final String VALUE_REGEX = "\\d+(\\.\\d+)?";
   static private final Pattern VALUE_PATTERN = Pattern.compile( VALUE_REGEX );

   static private enum DimensionUnit {
      CENTIMETER( "centimeter", "cm" ),
      MILLIMETER( "millimeter", "mm" );

      static private final String VALUES_REGEX = "\\d+(\\.\\d+)?( ?(x|\\*) ?\\d+(\\.\\d+)?){0,2} ?";
      static private final String END_REGEX = "(\\b|,|\\.|\\?|!|\\)|\\]|\\})";
      private final String __title;
      final private Pattern __pattern;
      private DimensionUnit( final String title, final String regex ) {
         __title = title;
         __pattern = Pattern.compile( VALUES_REGEX + regex + END_REGEX, Pattern.CASE_INSENSITIVE );
      }
      private Matcher getMatcher( final CharSequence lookupWindow ) {
         return __pattern.matcher( lookupWindow );
      }
   }

   static private Collection<Size> getSizes( final String lookupWindow ) {
      final Collection<Size> sizes = new ArrayList<>();
      for ( DimensionUnit dimensionUnit : DimensionUnit.values() ) {
         final Matcher matcher = dimensionUnit.getMatcher( lookupWindow );
         while ( matcher.find() ) {
            sizes.add( new Size( matcher.start(), matcher.end(),
                  getMeasurements( lookupWindow, dimensionUnit, matcher.start(), matcher.end() ) ) );
         }
      }
      return sizes;
   }

   static private Collection<Measurement> getMeasurements( final String text, final DimensionUnit dimensionUnit,
                                                           final int startOffset, final int endOffset ) {
      return getMeasurements( text.substring( startOffset, endOffset ), dimensionUnit );
   }

   static private Collection<Measurement> getMeasurements( final CharSequence fullMeasurementText,
                                                           final DimensionUnit dimensionUnit ) {
      final Collection<Measurement> measurements = new ArrayList<>();
      final Matcher matcher = VALUE_PATTERN.matcher( fullMeasurementText );
      while ( matcher.find() ) {
         final CharSequence matchText = fullMeasurementText.subSequence( matcher.start(), matcher.end() );
         if ( matchText != null && matchText.length() > 0 ) {
            float value = 0f;
            try {
               value = Float.parseFloat( "" + matchText );
            } catch ( NumberFormatException nfE ) {
               LOGGER.error( nfE.getMessage() );
            }
            measurements.add( new Measurement( value, dimensionUnit.__title ) );
         }
      }
      return measurements;
   }


   public void addSizes( final JCas jcas, final AnnotationFS lookupWindow,
                         final Iterable<DiseaseDisorderMention> lookupDisorders,
                         final Iterable<SignSymptomMention> lookupSymptoms ) {
      final Collection<Size> sizes = getSizes( lookupWindow.getCoveredText() );
      if ( sizes.isEmpty() ) {
         return;
      }
      final int windowStartOffset = lookupWindow.getBegin();
      for ( Size size : sizes ) {
         final CancerSize cancerSize = createCancerSize( jcas, windowStartOffset, size );
         final int startOffset = windowStartOffset + size.__startOffset;
         final int endOffset = windowStartOffset + size.__endOffset;
         final DiseaseDisorderMention closestDiseaseMention
               = FinderUtil.getClosestEventMention( startOffset, endOffset, lookupDisorders );
         final SignSymptomMention closestFindingMention
               = FinderUtil.getClosestEventMention( startOffset, endOffset, lookupSymptoms );
         final EventMention closerEventMention
               = FinderUtil.getCloserEventMention( startOffset, endOffset, closestDiseaseMention, closestFindingMention );
         addCancerSizeRelationToCas( jcas, cancerSize, closerEventMention );
      }
   }

   static private final CancerSize createCancerSize( final JCas jcas, final int windowStartOffset, final Size size ) {
      final int startOffset = windowStartOffset + size.__startOffset;
      final int endOffset = windowStartOffset + size.__endOffset;
      final CancerSize cancerSize = new CancerSize( jcas, startOffset, endOffset );
      if ( !size.__measurements.isEmpty() ) {
         final FSArray measurementFeatures = new FSArray( jcas, size.__measurements.size() );
         int measurementIndex = 0;
         for ( Measurement measurement : size.__measurements ) {
            final SizeMeasurement measurementFeature = new SizeMeasurement( jcas );
            measurementFeature.setValue( measurement.__value );
            measurementFeature.setUnit( measurement.__unit );
            measurementFeatures.set( measurementIndex, measurementFeature );
            measurementIndex++;
         }
         cancerSize.setMeasurements( measurementFeatures );
      }
      cancerSize.addToIndexes();
      return cancerSize;
   }

   /**
    * Create a UIMA relation type based on arguments and the relation label. This
    * allows subclasses to create/define their own types: e.g. coreference can
    * create CoreferenceRelation instead of BinaryTextRelation
    *
    * @param jCas - JCas object, needed to create new UIMA types
    * @param cancerSize - First argument to relation
    * @param eventMention - Second argument to relation
    */
   static private <T extends EventMention> void addCancerSizeRelationToCas( final JCas jCas,
                                                                            final CancerSize cancerSize,
                                                                            final EventMention eventMention ) {
      // add the relation to the CAS
      final RelationArgument cancerSizeArgument = new RelationArgument( jCas );
      cancerSizeArgument.setArgument( cancerSize );
      cancerSizeArgument.setRole( "Argument" );
      cancerSizeArgument.addToIndexes();
      final RelationArgument eventMentionArgument = new RelationArgument( jCas );
      eventMentionArgument.setArgument( eventMention );
      eventMentionArgument.setRole( "Related_to" );
      eventMentionArgument.addToIndexes();
      final CancerSizeTextRelation cancerSizeTextRelation = new CancerSizeTextRelation( jCas );
      cancerSizeTextRelation.setArg1( cancerSizeArgument );
      cancerSizeTextRelation.setArg2( eventMentionArgument );
      cancerSizeTextRelation.setCategory( "Cancer_Size" );
      cancerSizeTextRelation.addToIndexes();
   }



}
