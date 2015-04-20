package edu.pitt.dbmi.deep.phe.data.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.pitt.dbmi.deep.phe.util.TextUtils;

/**
 * The reperesents a patient report 
 * @author tseytlin
 */
public class Report {
	private Patient patient;
	private String text,documentType,recordId,mrn,race,gender,name,body,ssn,recordStatus;
	private Date eventDate,birthDate;
	

	public String getRecordStatus() {
		return recordStatus;
	}
	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}
	public String getMedicalRecordNumber() {
		return mrn;
	}
	public void setMedicalRecordNumber(String mrn) {
		this.mrn = mrn;
	}
	public String getRace() {
		return race;
	}
	public void setRace(String race) {
		this.race = race;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getSocialSecurityNumber() {
		return ssn;
	}
	public void setSocialSecurityNumber(String ssn) {
		this.ssn = ssn;
	}
	public Date getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}
	public Patient getPatient() {
		if(patient == null){
			patient = new Patient();
			patient.setName(getName());
			patient.setMedicalRecordNumber(getMedicalRecordNumber());
			patient.setGender(getGender());
			patient.setRace(getRace());
			patient.setSocialSecurityNumber(getSocialSecurityNumber());
			patient.setBirthDate(getBirthDate());
		}
		return patient;
	}
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getDocumentType() {
		return documentType;
	}
	public void setDocumentType(String type) {
		this.documentType = type;
	}
	public String getRecordId() {
		return recordId;
	}
	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}
	public Date getEventDate() {
		return eventDate;
	}
	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}
	
	/**
	 * read MARS summary line
	 * MRN|Event Date|Doc Type|Report ID
	 * @param text
	 * @return
	 * @throws IOException
	 */
	public static Report readSummaryformat(String text) throws IOException{
		String [] parts  = text.trim().split("\\|");
		if(parts.length > 3){
			Report doc = new Report();
			doc.setDocumentType(parts[2].trim());
			doc.setEventDate(TextUtils.parseDateString(parts[1].trim()));
			doc.setRecordId(parts[3].trim());
			doc.setMedicalRecordNumber(parts[0].trim());
			return doc;
		}
		return null;
	}
	
	/**
	 * read MARS BAR format
	 * @param text
	 * @return
	 * @throws IOException
	 */
	public static Report readBARformat(String text) throws IOException{
		Report doc = null;
		
		BufferedReader reader = new BufferedReader(new StringReader(text));
		boolean inHeader = false;
		boolean inBody = false;
		StringBuffer body = null;
		StringBuffer xml = null;
		StringBuffer header = null;
		
		for(String l = reader.readLine();l != null; l = reader.readLine()){
			l = l.trim();
			if("S_O_H".equals(l)){
				inHeader = true;
				inBody = false;
				doc = new Report();
				xml = new StringBuffer();
				xml.append(l+"\n");
				header = new StringBuffer();
			}else if("E_O_H".equals(l) && inHeader){
				inHeader = false;
				inBody = true;
				
				// read header
				xml.append(l+"\n");
				String [] hd = header.toString().split("\\|");
				
				// set header values constants
				final int RECORD_TYPE = 4;
				final int RECORD_STATUS = 70;
				final int DATE = 3;
				final int RECORD_ID = 10;
				final int ID = 1;
				final int RACE = 46;
				final int DOB = 24;
				final int SEX = 53;
				final int NAME = 2;
				final int SSN = 61;
				final int PQNO = 5;
				
				// issues with parsing header
				if(hd.length > RECORD_STATUS){
					// set relevant fields					
					doc.setDocumentType(hd[RECORD_TYPE].trim());
					doc.setRecordStatus(hd[RECORD_STATUS].trim());
					doc.setEventDate(TextUtils.parseDateString(hd[DATE].trim()));
					doc.setRecordId(hd[RECORD_ID].trim());
					String mrn =hd[ID].trim();
					String[] parts = mrn.split(" ");
					if(parts.length > 1)
						doc.setMedicalRecordNumber(parts[0] + parts[1]);
					else
						doc.setMedicalRecordNumber(mrn);
					doc.setRace(hd[RACE].trim());
					doc.setBirthDate(TextUtils.parseDateString(hd[DOB].trim()));
					doc.setGender(hd[SEX].trim());
					doc.setName(hd[NAME].trim());
					doc.setSocialSecurityNumber(hd[SSN].replaceAll("\\D", "").trim());
				}else{
					inHeader = false;
				}
				
				header = null;
				body = new StringBuffer();
			}else if("E_O_R".equals(l) && inBody){
				inHeader = false;
				inBody = false;
				xml.append(l+"\n");
				// set body and xml
				doc.setBody(body.toString());
				doc.setText(xml.toString());
				
			}else if(inHeader){
				xml.append(l+"\n");
				header.append(l+" ");
			}else if(inBody){
				xml.append(l+"\n");
				body.append(l+"\n");
			}
		}
		return doc;
	}
	
	
	/**
	 * read MARS BAR format
	 * @param text
	 * @return
	 * @throws IOException
	 */
	public static Report readMAPformat(Map<String,String> map) throws IOException{
		Report doc = new Report();
		
		// set relevant fields					
		doc.setDocumentType("NOTE");
		doc.setRecordStatus("FINAL");
		doc.setEventDate(TextUtils.parseDateString(map.get("CONTACT_DATE")));
		doc.setRecordId(map.get("NOTE_CSN_ID"));
		doc.setMedicalRecordNumber(map.get("MEDIPAC_MRN"));
		//doc.setRace(hd[RACE].trim());
		//doc.setBirthDate(TextUtils.parseDateString(hd[DOB].trim()));
		//doc.setGender(hd[SEX].trim());
		doc.setName(map.get("PAT_NAME"));
		//doc.setSocialSecurityNumber(hd[SSN].replaceAll("\\D", "").trim());
	
		// set body and xml
		//doc.setBody(body.toString());
		String txt = map.get("NOTE_TEXT").replaceAll(":(\\s{2,})",":__");
		txt = txt.replaceAll("  ","\n").replaceAll(":__",":  ");
		//txt = txt.replaceAll("\n{2,}(\\w{1,20}:)","\n$1");
		doc.setBody(txt);
		// create a BAR looking text 
		
		final int RECORD_TYPE = 4;
		final int RECORD_STATUS = 70;
		final int DATE = 3;
		final int RECORD_ID = 10;
		final int ID = 1;
		final int NAME = 2;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		StringBuffer b = new StringBuffer();
		b.append("S_O_H\n");
		for(int i=0;i<=RECORD_STATUS;i++){
			switch(i){
			case RECORD_TYPE: 	b.append(doc.getDocumentType()); break;
			case RECORD_STATUS:	b.append(doc.getRecordStatus()); break;
			case RECORD_ID:	 	b.append(doc.getRecordId()); break;
			case ID: 			b.append(doc.getMedicalRecordNumber()); break;
			case DATE: 			b.append(sdf.format(doc.getEventDate())); break;
			case NAME: 			b.append(doc.getName()); break;
			}
			b.append("|");
		}
		b.append("\nE_O_H\n");
		b.append(doc.getBody()+"\n");
		b.append("E_O_R\n");
		doc.setText(b.toString());
		
		return doc;
	}
}
