package edu.pitt.dbmi.deep.phe.model;

import java.util.regex.*;

import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.Condition.ConditionStageComponent;
import org.hl7.fhir.instance.model.Extension;
import org.hl7.fhir.instance.model.StringType;

import edu.pitt.dbmi.nlp.noble.coder.model.Mention;
import edu.pitt.dbmi.nlp.noble.ontology.IOntology;

public class Stage extends ConditionStageComponent{
	public void initialize(Mention st) {
		CodeableConcept c = Utils.getCodeableConcept(st);
		c.setTextSimple(st.getText());
		setSummary(c);
		// extract individual Stage levels if values are conflated
		Matcher m = Pattern.compile(Utils.STAGE_REGEX).matcher(st.getText());
		if(m.matches()){
			IOntology o = ResourceFactory.getInstance().getOntology();
			setStringExtension(""+o.getClass(Utils.T_STAGE).getURI(),m.group(1));
			setStringExtension(""+o.getClass(Utils.N_STAGE).getURI(),m.group(2));
			setStringExtension(""+o.getClass(Utils.M_STAGE).getURI(),m.group(3));
		}
	}
	
	/**
	 * get primary tumor stage
	 * @return
	 */
	public String getPrimaryTumorStage(){
		IOntology o = ResourceFactory.getInstance().getOntology();
		Extension e = getExtension(""+o.getClass(Utils.T_STAGE).getURI());
		return e != null? ((StringType)e.getValue()).getValue():null;
	}
	
	/**
	 * get primary tumor stage
	 * @return
	 */
	public String getDistantMetastasisStage(){
		IOntology o = ResourceFactory.getInstance().getOntology();
		Extension e = getExtension(""+o.getClass(Utils.M_STAGE).getURI());
		return e != null? ((StringType)e.getValue()).getValue():null;
	}
	
	/**
	 * get primary tumor stage
	 * @return
	 */
	public String getRegionalLymphNodeStage(){
		IOntology o = ResourceFactory.getInstance().getOntology();
		Extension e = getExtension(""+o.getClass(Utils.N_STAGE).getURI());
		return e != null? ((StringType)e.getValue()).getValue():null;
	}
}
