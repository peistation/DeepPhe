package edu.pitt.dbmi.deep.phe.model;

import java.util.ArrayList;
import java.util.regex.*;

import org.apache.ctakes.cancer.type.textsem.TnmClassification;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.Condition.ConditionStageComponent;
import org.hl7.fhir.instance.model.Extension;
import org.hl7.fhir.instance.model.ResourceReference;
import org.hl7.fhir.instance.model.StringType;

import edu.pitt.dbmi.nlp.noble.coder.model.Mention;
import edu.pitt.dbmi.nlp.noble.ontology.IClass;
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
	
	public void initialize(TnmClassification st) {
		CodeableConcept c = Utils.getCodeableConcept(st);
		c.setTextSimple(st.getCoveredText());
		setSummary(c);
		// extract individual Stage levels if values are conflated
		IOntology o = ResourceFactory.getInstance().getOntology();
		if(st.getSize() != null)
			setStringExtension(""+o.getClass(Utils.T_STAGE).getURI(),st.getSize().getCode()+st.getSize().getValue());
		if(st.getNodeSpread() != null)
			setStringExtension(""+o.getClass(Utils.N_STAGE).getURI(),st.getNodeSpread().getCode()+st.getNodeSpread().getValue());
		if(st.getMetastasis() != null)
			setStringExtension(""+o.getClass(Utils.M_STAGE).getURI(),st.getMetastasis().getCode()+st.getMetastasis().getValue());
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
	public CodeableConcept getPrimaryTumorStageCode(){
		return getStageValue(Utils.T_STAGE);
	}
	
	
	/**
	 * get primary tumor stage
	 * @return
	 */
	public CodeableConcept getDistantMetastasisStageCode(){
		return getStageValue(Utils.M_STAGE);
	}
	
	/**
	 * get primary tumor stage
	 * @return
	 */
	public CodeableConcept getRegionalLymphNodeStageCode(){
		return getStageValue(Utils.N_STAGE);
	}
	
	
	private CodeableConcept getStageValue(String stage){
		IOntology o = ResourceFactory.getInstance().getOntology();
		IClass cls = o.getClass(stage);
		if(cls == null)
			return null;
		Extension e = getExtension(""+cls.getURI());
		if(e == null)
			return null;
		
		String val = ((StringType)e.getValue()).getValue();
		for(IClass c : cls.getSubClasses()){
			for(String s: c.getConcept().getSynonyms()){
				if(s.matches("\\b"+val+"\\b")){
					return Utils.getCodeableConcept(c);
				}
			}
		}
		return null;
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
	
	public Stage copy() {
		Stage dst = new Stage();
		dst.summary = ((this.summary == null) ? null : this.summary.copy());
		dst.assessment = new ArrayList();
		for (ResourceReference i : this.assessment)
			dst.assessment.add(i.copy());
		return dst;
	}
}
