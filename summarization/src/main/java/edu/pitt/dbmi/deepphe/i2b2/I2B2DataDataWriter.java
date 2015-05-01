package edu.pitt.dbmi.deepphe.i2b2;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.TreeSet;

import org.hibernate.SQLQuery;
import org.hibernate.Transaction;

import edu.pitt.dbmi.deepphe.summarization.orm.i2b2data.ConceptDimension;
import edu.pitt.dbmi.deepphe.summarization.orm.i2b2data.I2b2DataDataSourceManager;
import edu.pitt.dbmi.deepphe.summarization.orm.i2b2data.ObservationFact;
import edu.pitt.dbmi.deepphe.summarization.orm.i2b2data.ObservationFactId;
import edu.pitt.dbmi.deepphe.summarization.orm.i2b2data.PatientDimension;

public class I2B2DataDataWriter {

	private Date timeNow = new Date();
	private I2b2DataDataSourceManager dataSourceMgr;
	private int patientNum = -1;
	private TreeSet<PartialPath> partialPaths;
	private int uploadId = 0;
	
	private String sourceSystemCd;

	public static void main(String[] args) {
		I2B2DataDataWriter i2b2MetaDataWriter = new I2B2DataDataWriter();
		try {
			i2b2MetaDataWriter.execute();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public void execute() throws ClassNotFoundException, SQLException {
		cleanOldRecords();
		writePatient();
		writeConcepts();
		writeObservations();
	}

	private void cleanOldRecords() throws SQLException {
		
		String sql = "delete from OBSERVATION_FACT where SOURCESYSTEM_CD = ?";
		SQLQuery sqlUpdate = dataSourceMgr.getSession().createSQLQuery(sql);		
		sqlUpdate.setString(1, getSourceSystemCd());
		sqlUpdate.executeUpdate();
		

		sql = "delete from CONCEPT_DIMENSION where SOURCESYSTEM_CD = ?";
		sqlUpdate = dataSourceMgr.getSession().createSQLQuery(sql);		
		sqlUpdate.setString(1,  getSourceSystemCd());
		sqlUpdate.executeUpdate();
		

		sql = "delete from PATIENT_DIMENSION where SOURCESYSTEM_CD = ?";
		sqlUpdate = dataSourceMgr.getSession().createSQLQuery(sql);		
		sqlUpdate.setString(1,  getSourceSystemCd());
		sqlUpdate.executeUpdate();
		
	}

	private void writePatient() throws SQLException {
		
		PatientDimension patientDimension = new PatientDimension();
	
		Date timeNow = new Date();
		
		patientDimension.setPatientNum(new BigDecimal(patientNum));	
		patientDimension.setVitalStatusCd((String) null);
		patientDimension.setBirthDate(timeNow);
		patientDimension.setDeathDate(timeNow);
		patientDimension.setSexCd("F");
		patientDimension.setAgeInYearsNum(new BigDecimal(78));
		patientDimension.setLanguageCd("Chinese");
		patientDimension.setRaceCd("asian");
		patientDimension.setMaritalStatusCd("single");
		patientDimension.setReligionCd("Budhism");
		patientDimension.setZipCd("15232");
		patientDimension.setStatecityzipPath("Zip codes\\Massachusetts\\Boston\\02115\\");
		patientDimension.setIncomeCd("Medium");
		patientDimension.setPatientBlob(null);
		patientDimension.setUpdateDate(timeNow);
		patientDimension.setDownloadDate(timeNow);
		patientDimension.setImportDate(timeNow);
		patientDimension.setSourcesystemCd(getSourceSystemCd());
		patientDimension.setUploadId(new BigDecimal(patientNum));
		
		
		Transaction tx = dataSourceMgr.getSession().beginTransaction();
		dataSourceMgr.getSession().saveOrUpdate(patientDimension);
		dataSourceMgr.getSession().flush();
		tx.commit();
		
	}

	private void writeConcepts() throws SQLException {		
		for (PartialPath partialPath : partialPaths) {
			if (partialPath.getBaseCode() == null) {
				continue;
			}
			ConceptDimension conceptDimension = new ConceptDimension();
			conceptDimension.setConceptPath(partialPath.getPath() + "\\");
			conceptDimension.setConceptCd(partialPath.getBaseCode());
			conceptDimension.setNameChar(Utilities.extractCname(partialPath));
			conceptDimension.setConceptBlob(null);
			conceptDimension.setUpdateDate(timeNow);
			conceptDimension.setDownloadDate(timeNow);
			conceptDimension.setImportDate(timeNow);
			conceptDimension.setUploadId(new BigDecimal(uploadId++));			
			dataSourceMgr.getSession().saveOrUpdate(conceptDimension);		
		}
		Transaction tx = dataSourceMgr.getSession().beginTransaction();
		dataSourceMgr.getSession().flush();
		tx.commit();
	}

	private void writeObservations() throws SQLException {
		
		for (PartialPath partialPath : getPartialPaths()) {
			if (partialPath.getBaseCode() == null) {
				continue;
			}
			
			ObservationFactId observationFactId = new ObservationFactId();
			observationFactId.setEncounterNum(new BigDecimal(1.0d));
			observationFactId.setPatientNum(new BigDecimal(patientNum));
			observationFactId.setConceptCd(partialPath.getBaseCode());
			observationFactId.setProviderId(getSourceSystemCd());
			observationFactId.setInstanceNum(1L);
			observationFactId.setModifierCd("@");
			observationFactId.setStartDate(timeNow);
			
			ObservationFact observationFact = new ObservationFact();
			observationFact.setId(observationFactId);
			observationFact.setValtypeCd("@");
			observationFact.setTvalChar("@");
			observationFact.setNvalNum(new BigDecimal(-1));
			observationFact.setValueflagCd("@");
			observationFact.setQuantityNum(new BigDecimal(1));
			observationFact.setUnitsCd("@");
			observationFact.setEndDate(timeNow);
			observationFact.setLocationCd("@");
			observationFact.setObservationBlob(null);
			observationFact.setConfidenceNum(new BigDecimal(1.0));
			observationFact.setUpdateDate(timeNow);
			observationFact.setDownloadDate(timeNow);
			observationFact.setImportDate(timeNow);
			observationFact.setSourcesystemCd(getSourceSystemCd());
			observationFact.setUploadId(new BigDecimal(uploadId++));
		
			dataSourceMgr.getSession().saveOrUpdate(observationFact);	

		}

		Transaction tx = dataSourceMgr.getSession().beginTransaction();
		dataSourceMgr.getSession().flush();
		tx.commit();
	}

	private TreeSet<PartialPath> getPartialPaths() {
		return partialPaths;
	}

	public void setPatientNum(int patientNum) {
		this.patientNum = patientNum;
	}


	public String getSourceSystemCd() {
		return sourceSystemCd;
	}

	public void setSourceSystemCd(String sourceSystemCd) {
		this.sourceSystemCd = sourceSystemCd;
	}
	
	public I2b2DataDataSourceManager getDataSourceMgr() {
		return dataSourceMgr;
	}

	public void setDataSourceMgr(I2b2DataDataSourceManager dataSourceMgr) {
		this.dataSourceMgr = dataSourceMgr;
	}


}
