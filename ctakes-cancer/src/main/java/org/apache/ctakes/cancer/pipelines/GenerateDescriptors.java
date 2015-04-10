package org.apache.ctakes.cancer.pipelines;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;
import org.xml.sax.SAXException;

import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;

public class GenerateDescriptors {

  static interface DescriptorOptions extends CancerPipelineOptions {
    @Option(
        shortName = "x",
        description = "File to write xml descriptor to",
        defaultToNull=true)
    public String getXmlOutput();

  }
  
  public static void main(String[] args) throws ResourceInitializationException, InvalidXMLException, IOException, SAXException {
    DescriptorOptions options = CliFactory.parseArguments(DescriptorOptions.class, args);
    AnalysisEngineDescription aed = RunCancerPipeline.getPipelineDescription(options);
    if(options.getXmlOutput() != null){
      aed.toXML(new FileOutputStream(options.getXmlOutput()));
    }

  }

}
