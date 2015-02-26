package edu.pitt.dbmi.deep.phe.data.model;

import java.util.*;

public class Patient {
	private List<Report> reports;
	private String name,mrn,ssn,race,gender;
	private Date birthDate;
	private Date cancerDate;
	private Map<String,Date> dates;
	private Map<String,Integer> countMap;
	public String getMedicalRecordNumber() {
		return mrn;
	}
	public void setMedicalRecordNumber(String mrn) {
		this.mrn = mrn;
	}
	public String getSocialSecurityNumber() {
		return ssn;
	}
	public void setSocialSecurityNumber(String ssn) {
		this.ssn = ssn;
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
	public Date getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}
	public List<Report> getReports() {
		if(reports == null){
			reports = new ArrayList<Report>();
		}
		return reports;
	}
	public void setReports(List<Report> reports) {
		this.reports = reports;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getCancerDate() {
		return cancerDate;
	}
	public void setCancerDate(Date cancerDate) {
		this.cancerDate = cancerDate;
	}
	public void addReport(Report r) {
		getReports().add(r);
		r.setPatient(this);
	}
	public int getReportCount(){
		return getReports().size();
	}
	public Map<String, Date> getDates() {
		if(dates == null)
			dates = new LinkedHashMap<String, Date>();
		return dates;
	}
	
	public Date getDate(String type){
		return getDates().get(type);
	}
	
	public void addDate(Date dt, String type){
		getDates().put(type,dt);
	}
	
	public Map<String,Integer> getReportCounts(){
		if(countMap == null){
			countMap = new HashMap<String, Integer>();
			for(Report r: getReports()){
				Integer x = countMap.get(r.getDocumentType());
				countMap.put(r.getDocumentType(),(x == null)?1:x.intValue()+1);
			}
		}
		return countMap;
	}
	
	public SortedSet<Date> getTreatmentDates(){
		SortedSet<Date> d = new TreeSet<Date>();
		for(String type: getDates().keySet()){
			if(!Arrays.asList("Date 1st Recurrence","Date of 1st Contact","Date of Death","Date of Initial Diagnosis","Date of Last Contact").contains(type)){
				d.add(getDate(type));
			}
		}
		return d;
	}
	
	public Date getInitialDiagnosisDate(){
		return getDate("Date of Initial Diagnosis");
	}

	public Date getRecurrenceDate(){
		return getDate("Date 1st Recurrence");
	}

	public Date getDeathDate(){
		return getDate("Date of Death");
	}
	
	public int  getReportCounts(List<String> types){
		int n = 0;
		for(String tp: types){
			Integer x = getReportCounts().get(tp);
			if(x != null)
				n += x.intValue();
		}
		return n;
	}
	
	
	public void sortReports(){
		Collections.sort(reports,new Comparator<Report>() {
			public int compare(Report o1, Report o2) {
				return o1.getEventDate().compareTo(o2.getEventDate());
			}
		});
	}
}


/*
 * Example of BAR format 
  S_O_H
  1    CKSM CkSum Value of Report
  2    ID patientMRN
  3    NA patientName
  4    DAT Main Date of Report
  5    TYP Type of Report
  6    PQ Parsing Queue Number
  7    DOC Attending Physician
  8    ADM Administrative Code
  9    EXAM Exam Type
 10    STYP SubType
 11    SPNO Unique Document ID
 12    ACCT Account Number
 13    CTYP Case Type
 14    IDS ?
 15    LOC Location
 16    UNIQ ?
 17    ADDR Address
 18    ADT ?
 19    AGE PatientElement Age
 20    ALG PatientElement allegies
 21    ASP Secondary Document ID
 22    CHRG ?
 23    DNO Doctor Number
 24    DOA Date of Admission
 25    DOB Date of Birth
 26    DOCS ?
 27    DOD Date of Discharge
 28    DOO Date of Order
 29    DOP Date of Procedure
 30    DOS Date of Signout
 31    DOT Date of Transcription
 32    DX  Diagnosis
 33    FC  Financial Class
 34    HOSP Hospital
 35    MNEM Mnemonic
 36    MNO Domain (used for Epicare reports)
 37    MPI ?
 38    MSG Pharmacy Message
 39    ODOC Ordering Doc
 40    OPD ?
 41    PDOC Procedure Doc
 42    PDX Pathology DX
 43    PMSG PatientElement Message
 44    PNOT PatientElement Note
 45    PROC Procedure Code
 46    PTYP PatientElement Type
 47    RACE Race
 48    RDOC Referring Doc
 49    RES  Resident
 50    ROOM Room
 51    RX   Drug Order
 52    SDOC Signing Doc
 53    SERV Service
 54    SEX  Sex
 55    SPEC Speciality
 56    SPLT Split 
 57    TRC  Transport Code 
 58    TRX  Transcriptionist
 59    DACC Date of Ascension
 60    INST Institution
 61    TEL Telephone Number
 62    SSN Social Security Number
 63    BED Med
 64    ADOC Admitting Doc
 65    EDOC External Referring Doc
 66    PCP PCP
 67    IDOC Internal Referring Doc
 68    FDOC Family Physician
 69    LOS Hospital LOS
E_O_H
<BODY>
E_O_R
 */