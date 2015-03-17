package edu.pitt.dbmi.deep.phe.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

import org.apache.uima.jcas.JCas;
import org.hl7.fhir.instance.formats.XmlComposer;
import org.hl7.fhir.instance.model.Address;
import org.hl7.fhir.instance.model.Attachment;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.Contact;
import org.hl7.fhir.instance.model.HumanName;
import org.hl7.fhir.instance.model.Identifier;
import org.hl7.fhir.instance.model.Resource;
import org.hl7.fhir.instance.model.ResourceReference;
import org.hl7.fhir.instance.model.Patient.ContactComponent;
import org.hl7.fhir.instance.model.Patient.PatientLinkComponent;

import edu.pitt.dbmi.nlp.noble.coder.model.Document;

public class Patient extends org.hl7.fhir.instance.model.Patient implements DeepPheModel{
	private Date currentDate;
	
	public Patient(){
		setActiveSimple(true);
		Utils.createIdentifier(addIdentifier(),this);
	}
	
	/**
	 * set patient name
	 * @param name
	 */
	public void setNameSimple(String name){
		HumanName hn = addName();
		if(name.contains(",")){
			String [] p = name.split(",");
			hn.addFamilySimple(p[0].trim());
			if(p.length > 1){
				hn.addGivenSimple(p[p.length-1].trim());
			}
		}else if(name.contains(" ")){
			String [] p = name.split("\\s+");
			hn.addGivenSimple(p[0].trim());
			if(p.length > 1){
				hn.addFamilySimple(p[p.length-1].trim());
			}
		}else{
			hn.addFamilySimple(name);
		}
	}
	/**
	 * get a simple name for a patient
	 * @return
	 */
	public String getNameSimple(){
		for(HumanName n: getName()){
			String f = n.getGiven().get(0).getValue();
			String l = n.getFamily().get(0).getValue();
			return (f+" "+l).trim();
		}
		return null;
	}

	public ResourceReference getReference(){
		return getReference(new ResourceReference());
	}
	public ResourceReference getReference(ResourceReference r){
		r.setDisplaySimple(getNameSimple());
		r.setReferenceSimple(getIdentifierSimple());
		return r;
	}
	
	public Date getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(Date currentDate) {
		currentDate = currentDate;
	}

	public void assertRelatedData(Document doc) {
		// TODO Auto-generated method stub
		
	}

	public void assertRelatedData(JCas cas) {
		// TODO Auto-generated method stub
		
	}

	public void inferRelatedData() {
		// TODO Auto-generated method stub
		
	}

	public String getDisplaySimple() {
		return getNameSimple();
	}

	public String getIdentifierSimple() {
		return Utils.getIdentifier(getIdentifier());
	}

	public String getSummary() {
		StringBuffer st = new StringBuffer();
		st.append("Patient:\t"+getDisplaySimple());
		return st.toString();
	}
	
	public Resource getResource() {
		return this;
	}
	
	/**
	 * copy data from an existing patient object
	 * @param dst
	 */
	public void copy(org.hl7.fhir.instance.model.Patient target) {
		Patient dst = this;
		dst.identifier = new ArrayList();
		for (Identifier i : target.getIdentifier())
			dst.identifier.add(i.copy());
		dst.name = new ArrayList();
		for (HumanName i : target.getName())
			dst.name.add(i.copy());
		dst.telecom = new ArrayList();
		for (Contact i : target.getTelecom())
			dst.telecom.add(i.copy());
		dst.gender = ((target.getGender() == null) ? null : target.getGender().copy());
		dst.birthDate = ((target.getBirthDate() == null) ? null : target.getBirthDate().copy());
		dst.deceased = ((target.getDeceased() == null) ? null : target.getDeceased().copy());
		dst.address = new ArrayList();
		for (Address i : target.getAddress())
			dst.address.add(i.copy());
		dst.maritalStatus = ((target.getMaritalStatus() == null) ? null : target.getMaritalStatus().copy());
		dst.multipleBirth = ((target.getMultipleBirth() == null) ? null : target.getMultipleBirth().copy());
		dst.photo = new ArrayList();
		for (Attachment i : target.getPhoto())
			dst.photo.add(i.copy());
		dst.contact = new ArrayList();
		for (ContactComponent i : target.getContact())
			dst.contact.add(i.copy());
		dst.animal = ((target.getAnimal() == null) ? null : target.getAnimal().copy());
		dst.communication = new ArrayList();
		for (CodeableConcept i : target.getCommunication())
			dst.communication.add(i.copy());
		dst.careProvider = new ArrayList();
		for (ResourceReference i : target.getCareProvider())
			dst.careProvider.add(i.copy());
		dst.managingOrganization = ((target.getManagingOrganization() == null) ? null : target.getManagingOrganization().copy());
		dst.link = new ArrayList();
		for (PatientLinkComponent i : target.getLink())
			dst.link.add(i.copy());
		dst.active = ((target.getActive() == null) ? null : target.getActive().copy());
	}

	public void saveFHIR(File dir) throws FileNotFoundException, Exception {
		Utils.saveFHIR(this,getIdentifierSimple(),dir);
	}

}