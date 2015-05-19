package edu.pitt.dbmi.deep.phe.util;

/*
 Copyright (c) 2011-2013, HL7, Inc.
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this 
 list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, 
 this list of conditions and the following disclaimer in the documentation 
 and/or other materials provided with the distribution.
 * Neither the name of HL7 nor the names of its contributors may be used to 
 endorse or promote products derived from this software without specific 
 prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
 PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 POSSIBILITY OF SUCH DAMAGE.

 */

// Generated on Tue, Sep 30, 2014 18:08+1000 for FHIR v0.0.82

import org.hl7.fhir.instance.formats.XmlComposer;
import org.hl7.fhir.instance.model.*;
import org.hl7.fhir.utilities.Utilities;

import edu.pitt.dbmi.deep.phe.model.Cancer;

/**
 * a copy of XML Composer to take care of CancerPhenotype
 * @author tseytlin
 *
 */

public class FHIRComposer extends XmlComposer {

	private void composeElementElements(Element element) throws Exception {
		for (Extension e : element.getExtensions()) {
			composeExtension("extension", e);
		}
	}

	private void composeBackboneElements(BackboneElement element) throws Exception {
		composeElementElements(element);
		for (Extension e : element.getModifierExtensions()) {
			composeExtension("modifierExtension", e);
		}
	}

	private <E extends Enum<E>> void composeEnumeration(String name, Enumeration<E> value, EnumFactory e)
			throws Exception {
		if (value != null
				&& (!Utilities.noString(value.getXmlId()) || value.hasExtensions() || value.getValue() != null)) {
			composeElementAttributes(value);
			if (value.getValue() != null)
				xml.attribute("value", e.toCode(value.getValue()));

			xml.open(FHIR_NS, name);
			composeElementElements(value);
			xml.close(FHIR_NS, name);
		}
	}

	private void composeInteger(String name, IntegerType value) throws Exception {
		if (value != null) {
			composeElementAttributes(value);
			xml.attribute("value", toString(value.getValue()));

			xml.open(FHIR_NS, name);
			composeElementElements(value);
			xml.close(FHIR_NS, name);
		}
	}

	private void composeDateTime(String name, DateTimeType value) throws Exception {
		if (value != null
				&& (!Utilities.noString(value.getXmlId()) || value.hasExtensions() || value.getValue() != null)) {
			composeElementAttributes(value);
			if (value.getValue() != null)
				xml.attribute("value", toString(value.getValue()));

			xml.open(FHIR_NS, name);
			composeElementElements(value);
			xml.close(FHIR_NS, name);
		}
	}

	private void composeCode(String name, CodeType value) throws Exception {
		if (value != null
				&& (!Utilities.noString(value.getXmlId()) || value.hasExtensions() || !Utilities.noString(value
						.getValue()))) {
			composeElementAttributes(value);
			if (value.getValue() != null)
				xml.attribute("value", toString(value.getValue()));

			xml.open(FHIR_NS, name);
			composeElementElements(value);
			xml.close(FHIR_NS, name);
		}
	}

	private void composeDate(String name, DateType value) throws Exception {
		if (value != null
				&& (!Utilities.noString(value.getXmlId()) || value.hasExtensions() || value.getValue() != null)) {
			composeElementAttributes(value);
			if (value.getValue() != null)
				xml.attribute("value", toString(value.getValue()));

			xml.open(FHIR_NS, name);
			composeElementElements(value);
			xml.close(FHIR_NS, name);
		}
	}

	private void composeDecimal(String name, DecimalType value) throws Exception {
		if (value != null
				&& (!Utilities.noString(value.getXmlId()) || value.hasExtensions() || value.getValue() != null)) {
			composeElementAttributes(value);
			if (value.getValue() != null)
				xml.attribute("value", toString(value.getValue()));

			xml.open(FHIR_NS, name);
			composeElementElements(value);
			xml.close(FHIR_NS, name);
		}
	}

	private void composeUri(String name, UriType value) throws Exception {
		if (value != null
				&& (!Utilities.noString(value.getXmlId()) || value.hasExtensions() || value.getValue() != null)) {
			composeElementAttributes(value);
			if (value.getValue() != null)
				xml.attribute("value", toString(value.getValue()));

			xml.open(FHIR_NS, name);
			composeElementElements(value);
			xml.close(FHIR_NS, name);
		}
	}

	private void composeId(String name, IdType value) throws Exception {
		if (value != null
				&& (!Utilities.noString(value.getXmlId()) || value.hasExtensions() || !Utilities.noString(value
						.getValue()))) {
			composeElementAttributes(value);
			if (value.getValue() != null)
				xml.attribute("value", toString(value.getValue()));

			xml.open(FHIR_NS, name);
			composeElementElements(value);
			xml.close(FHIR_NS, name);
		}
	}

	private void composeString(String name, StringType value) throws Exception {
		if (value != null
				&& (!Utilities.noString(value.getXmlId()) || value.hasExtensions() || !Utilities.noString(value
						.getValue()))) {
			composeElementAttributes(value);
			if (value.getValue() != null)
				xml.attribute("value", toString(value.getValue()));

			xml.open(FHIR_NS, name);
			composeElementElements(value);
			xml.close(FHIR_NS, name);
		}
	}

	private void composeBoolean(String name, BooleanType value) throws Exception {
		if (value != null) {
			composeElementAttributes(value);
			xml.attribute("value", toString(value.getValue()));

			xml.open(FHIR_NS, name);
			composeElementElements(value);
			xml.close(FHIR_NS, name);
		}
	}

	private void composeExtension(String name, Extension element) throws Exception {
		if (element != null) {
			composeElementAttributes(element);
			if (element.getUrl() != null)
				xml.attribute("url", element.getUrl().getValue());
			xml.open(FHIR_NS, name);
			composeElementElements(element);
			composeType("value", element.getValue());
			xml.close(FHIR_NS, name);
		}
	}

	private void composeNarrative(String name, Narrative element) throws Exception {
		if (element != null) {
			composeElementAttributes(element);
			xml.open(FHIR_NS, name);
			composeElementElements(element);
			if (element.getStatus() != null)
				composeEnumeration("status", element.getStatus(), new Narrative.NarrativeStatusEnumFactory());
			composeXhtml("div", element.getDiv());
			xml.close(FHIR_NS, name);
		}
	}

	private void composePeriod(String name, Period element) throws Exception {
		if (element != null) {
			composeTypeAttributes(element);
			xml.open(FHIR_NS, name);
			composeElementElements(element);
			composeDateTime("start", element.getStart());
			composeDateTime("end", element.getEnd());
			xml.close(FHIR_NS, name);
		}
	}

	private void composeCoding(String name, Coding element) throws Exception {
		if (element != null) {
			composeTypeAttributes(element);
			xml.open(FHIR_NS, name);
			composeElementElements(element);
			composeUri("system", element.getSystem());
			composeString("version", element.getVersion());
			composeCode("code", element.getCode());
			composeString("display", element.getDisplay());
			composeBoolean("primary", element.getPrimary());
			composeResourceReference("valueSet", element.getValueSet());
			xml.close(FHIR_NS, name);
		}
	}

	
	private void composeQuantity(String name, Quantity element) throws Exception {
		if (element != null) {
			composeTypeAttributes(element);
			xml.open(FHIR_NS, name);
			composeElementElements(element);
			composeDecimal("value", element.getValue());
			if (element.getComparator() != null)
				composeEnumeration("comparator", element.getComparator(), new Quantity.QuantityComparatorEnumFactory());
			composeString("units", element.getUnits());
			composeUri("system", element.getSystem());
			composeCode("code", element.getCode());
			xml.close(FHIR_NS, name);
		}
	}

	
	private void composeResourceReference(String name, ResourceReference element) throws Exception {
		if (element != null) {
			composeTypeAttributes(element);
			xml.open(FHIR_NS, name);
			composeElementElements(element);
			composeString("reference", element.getReference());
			composeString("display", element.getDisplay());
			xml.close(FHIR_NS, name);
		}
	}

	private void composeCodeableConcept(String name, CodeableConcept element) throws Exception {
		if (element != null) {
			composeTypeAttributes(element);
			xml.open(FHIR_NS, name);
			composeElementElements(element);
			for (Coding e : element.getCoding())
				composeCoding("coding", e);
			composeString("text", element.getText());
			xml.close(FHIR_NS, name);
		}
	}

	private void composeIdentifier(String name, Identifier element) throws Exception {
		if (element != null) {
			composeTypeAttributes(element);
			xml.open(FHIR_NS, name);
			composeElementElements(element);
			if (element.getUse() != null)
				composeEnumeration("use", element.getUse(), new Identifier.IdentifierUseEnumFactory());
			composeString("label", element.getLabel());
			composeUri("system", element.getSystem());
			composeString("value", element.getValue());
			composePeriod("period", element.getPeriod());
			composeResourceReference("assigner", element.getAssigner());
			xml.close(FHIR_NS, name);
		}
	}
	
	
	private void composeResourceAttributes(Resource element) throws Exception {
		composeElementAttributes(element);
	}

	private void composeResourceElements(Resource element) throws Exception {
		composeBackboneElements(element);
		composeCode("language", element.getLanguage());
		composeNarrative("text", element.getText());
		for (Resource r : element.getContained()) {
			if (r.getXmlId() == null)
				throw new Exception("Contained Resource has no id - one must be assigned"); // we
																							// can't
																							// assign
																							// one
																							// here
																							// -
																							// what
																							// points
																							// to
																							// it?
			xml.open(FHIR_NS, "contained");
			composeResource(r);
			xml.close(FHIR_NS, "contained");
		}
	}

	private void composeConditionConditionStageComponent(String name, Condition.ConditionStageComponent element)
			throws Exception {
		if (element != null) {
			composeElementAttributes(element);
			xml.open(FHIR_NS, name);
			composeBackboneElements(element);
			composeCodeableConcept("summary", element.getSummary());
			for (ResourceReference e : element.getAssessment())
				composeResourceReference("assessment", e);
			xml.close(FHIR_NS, name);
		}
	}

	private void composeConditionConditionEvidenceComponent(String name, Condition.ConditionEvidenceComponent element)
			throws Exception {
		if (element != null) {
			composeElementAttributes(element);
			xml.open(FHIR_NS, name);
			composeBackboneElements(element);
			composeCodeableConcept("code", element.getCode());
			for (ResourceReference e : element.getDetail())
				composeResourceReference("detail", e);
			xml.close(FHIR_NS, name);
		}
	}

	private void composeConditionConditionLocationComponent(String name, Condition.ConditionLocationComponent element)
			throws Exception {
		if (element != null) {
			composeElementAttributes(element);
			xml.open(FHIR_NS, name);
			composeBackboneElements(element);
			composeCodeableConcept("code", element.getCode());
			composeString("detail", element.getDetail());
			xml.close(FHIR_NS, name);
		}
	}

	private void composeConditionConditionRelatedItemComponent(String name,
			Condition.ConditionRelatedItemComponent element) throws Exception {
		if (element != null) {
			composeElementAttributes(element);
			xml.open(FHIR_NS, name);
			composeBackboneElements(element);
			if (element.getType() != null)
				composeEnumeration("type", element.getType(), new Condition.ConditionRelationshipTypeEnumFactory());
			composeCodeableConcept("code", element.getCode());
			composeResourceReference("target", element.getTarget());
			xml.close(FHIR_NS, name);
		}
	}
	

	private void composeTumor(String name, Cancer.Tumor element) throws Exception {
		if (element != null) {
			composeElementAttributes(element);
			xml.open(FHIR_NS, name);
			composeBackboneElements(element);
			composeCodeableConcept("type", element.getType());
			for (Condition.ConditionLocationComponent e : element.getLocation())
				composeConditionConditionLocationComponent("location", e);
			for (Condition.ConditionEvidenceComponent e : element.getPhenotypicFactors())
				composeConditionConditionEvidenceComponent("phenotypicFactor", e);
			for (Condition.ConditionEvidenceComponent e : element.getTreatmentFactors())
				composeConditionConditionEvidenceComponent("treatmentFactor", e);
			for (Condition.ConditionEvidenceComponent e : element.getGenomicFactors())
				composeConditionConditionEvidenceComponent("genomicFactor", e);
			for (Condition.ConditionEvidenceComponent e : element.getRelatedFactors())
				composeConditionConditionEvidenceComponent("relatedFactor", e);
			
			xml.close(FHIR_NS, name);
		}
	}

	private void composeCancer(String name, Cancer element) throws Exception {
		if (element != null) {
			composeResourceAttributes(element);
			xml.open(FHIR_NS, name);
			composeResourceElements(element);
			for (Identifier e : element.getIdentifier())
				composeIdentifier("identifier", e);
			composeResourceReference("subject", element.getSubject());
			composeResourceReference("encounter", element.getEncounter());
			composeResourceReference("asserter", element.getAsserter());
			composeDate("dateAsserted", element.getDateAsserted());
			composeCodeableConcept("code", element.getCode());
			composeCodeableConcept("category", element.getCategory());
			if (element.getStatus() != null)
				composeEnumeration("status", element.getStatus(), new Condition.ConditionStatusEnumFactory());
			composeCodeableConcept("certainty", element.getCertainty());
			composeCodeableConcept("severity", element.getSeverity());
			composeType("onset", element.getOnset());
			composeType("abatement", element.getAbatement());
			composeConditionConditionStageComponent("stage", element.getStage());
			for (Condition.ConditionEvidenceComponent e : element.getEvidence())
				composeConditionConditionEvidenceComponent("evidence", e);
			for (Condition.ConditionLocationComponent e : element.getLocation())
				composeConditionConditionLocationComponent("location", e);
			for (Condition.ConditionRelatedItemComponent e : element.getRelatedItem())
				composeConditionConditionRelatedItemComponent("relatedItem", e);
			composeString("notes", element.getNotes());
			
			// now lets add the tumor stuff
			for(Cancer.Tumor t : element.getTumors()){
				composeTumor("tumor", t);
			}
			
			
			xml.close(FHIR_NS, name);
		}
	}



	protected void composeResource(Resource resource) throws Exception {
		if (resource instanceof Cancer)
			composeCancer("CancerPhenotype", (Cancer) resource);
		else 
			super.composeResource(resource);
	}

}
