package org.apache.ctakes.cancer.pipelines;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;
import org.xml.sax.SAXException;

import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;

import javax.annotation.concurrent.Immutable;

@Immutable
final public class DescriptorGenerator {
   private DescriptorGenerator() {}

   static interface DescriptorOptions extends CancerPipelineOptions {
      @Option(
            shortName = "x",
            description = "File to write xml descriptor to",
            defaultToNull = true )
      public String getXmlOutput();

   }

   public static void writeDescriptor( final String descriptorPath )
         throws ResourceInitializationException, InvalidXMLException, IOException, SAXException {
      final AnalysisEngineDescription aed = CancerPipelineRunner.getPipelineDescription();
         aed.toXML( new FileOutputStream( descriptorPath ) );
   }

   public static void main( final String... args )
         throws ResourceInitializationException, InvalidXMLException, IOException, SAXException {
//      final DescriptorOptions options = CliFactory.parseArguments( DescriptorOptions.class, args );
      final DescriptorOptions options = CliFactory.parseArguments( DescriptorOptions.class, "-x", "C:\\Spiffy\\ctakes_trunk_intellij\\dev\\apache\\ctakes-cancer\\desc\\CancerPipeline.xml" );
      final AnalysisEngineDescription aed = CancerPipelineRunner.getPipelineDescription( options );
      if ( options.getXmlOutput() != null ) {
         aed.toXML( new FileOutputStream( options.getXmlOutput() ) );
      }
   }


}
