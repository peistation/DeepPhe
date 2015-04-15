package edu.pitt.dbmi.deep.phe.model;

import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.Condition.ConditionStageComponent;

import edu.pitt.dbmi.nlp.noble.coder.model.Mention;

public class Stage extends ConditionStageComponent{

	public void initialize(Mention st) {
		CodeableConcept c = Utils.getCodeableConcept(st);
		c.setTextSimple(st.getText());
		setSummary(c);
	}

}
