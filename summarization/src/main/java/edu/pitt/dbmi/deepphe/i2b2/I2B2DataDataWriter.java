package edu.pitt.dbmi.deepphe.i2b2;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TreeSet;

import org.hibernate.SQLQuery;
import org.hibernate.Transaction;

import edu.pitt.dbmi.deepphe.summarization.jess.kb.Patient;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.Encounter;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.Summary;
import edu.pitt.dbmi.deepphe.summarization.orm.i2b2data.ConceptDimension;
import edu.pitt.dbmi.deepphe.summarization.orm.i2b2data.I2b2DataDataSourceManager;
import edu.pitt.dbmi.deepphe.summarization.orm.i2b2data.ObservationFact;
import edu.pitt.dbmi.deepphe.summarization.orm.i2b2data.ObservationFactId;
import edu.pitt.dbmi.deepphe.summarization.orm.i2b2data.PatientDimension;
import edu.pitt.dbmi.deepphe.summarization.orm.i2b2data.VisitDimension;
import edu.pitt.dbmi.deepphe.summarization.orm.i2b2data.VisitDimensionId;

public class I2B2DataDataWriter {

	private Date timeNow = new Date();
	private I2b2DataDataSourceManager dataSourceMgr;
	private int patientNum = -1;
	private List<Patient> patients;

	private int uploadId = 0;

	private String sourceSystemCd;

	private final TreeSet<Summary> activeSummaries = new TreeSet<Summary>(
			new Comparator<Summary>() {
				@Override
				public int compare(Summary o1, Summary o2) {
					return o1.getPath().compareTo(o2.getPath());
				}
			});

	public static void main(String[] args) {
		I2B2DataDataWriter i2b2MetaDataWriter = new I2B2DataDataWriter();
		try {
			i2b2MetaDataWriter.execute();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public void execute() throws ClassNotFoundException, SQLException {
		writePatients();
		writeEncounters();
		writeConcepts();
		writeObservations();
	}

	public void cleanOldRecords() throws SQLException {

		String sql = "delete from OBSERVATION_FACT where SOURCESYSTEM_CD = :sourceSystemCd";
		SQLQuery sqlUpdate = dataSourceMgr.getSession().createSQLQuery(sql);
		sqlUpdate.setString("sourceSystemCd", getSourceSystemCd());
		sqlUpdate.executeUpdate();

		sql = "delete from CONCEPT_DIMENSION where SOURCESYSTEM_CD = :sourceSystemCd";
		sqlUpdate = dataSourceMgr.getSession().createSQLQuery(sql);
		sqlUpdate.setString("sourceSystemCd", getSourceSystemCd());
		sqlUpdate.executeUpdate();

		sql = "delete from PATIENT_DIMENSION where SOURCESYSTEM_CD = :sourceSystemCd";
		sqlUpdate = dataSourceMgr.getSession().createSQLQuery(sql);
		sqlUpdate.setString("sourceSystemCd", getSourceSystemCd());
		sqlUpdate.executeUpdate();

	}

	private void writePatients() throws SQLException {

		for (Patient kbPatient : patients) {
			PatientDimension patientDimension = new PatientDimension();

			Date timeNow = new Date();

			patientDimension.setPatientNum(new BigDecimal(kbPatient.getId()));
			patientDimension.setVitalStatusCd((String) null);
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.set(1967, 3, 20);
			patientDimension.setBirthDate(calendar.getTime());
			patientDimension.setDeathDate(null);
			patientDimension.setSexCd("F");
			patientDimension.setAgeInYearsNum(new BigDecimal(78));
			patientDimension.setLanguageCd("Chinese");
			patientDimension.setRaceCd("asian");
			patientDimension.setMaritalStatusCd("single");
			patientDimension.setReligionCd("Budhism");
			patientDimension.setZipCd("15232");
			patientDimension
					.setStatecityzipPath("Zip codes\\Massachusetts\\Boston\\02115\\");
			patientDimension.setIncomeCd("Medium");
			patientDimension.setPatientBlob(null);
			patientDimension.setUpdateDate(timeNow);
			patientDimension.setDownloadDate(timeNow);
			patientDimension.setImportDate(timeNow);
			patientDimension.setSourcesystemCd(getSourceSystemCd());
			patientDimension.setUploadId(new BigDecimal(patientNum));

			dataSourceMgr.getSession().saveOrUpdate(patientDimension);
		}

		Transaction tx = dataSourceMgr.getSession().beginTransaction();
		dataSourceMgr.getSession().flush();
		tx.commit();

	}

	private void writeEncounters() {
		for (Patient kbPatient : patients) {
			for (Encounter kbEncounter : kbPatient.getEncounters()) {
				Date timeNow = new Date();
				VisitDimension visitDimension = new VisitDimension();
				VisitDimensionId visitId = new VisitDimensionId();
				visitId.setPatientNum(new BigDecimal(kbPatient.getId()));
				visitId.setEncounterNum(new BigDecimal(kbEncounter.getId()));
				visitDimension.setId(visitId);
				visitDimension.setActiveStatusCd("Active");
				visitDimension.setDownloadDate(timeNow);
				visitDimension.setEndDate(timeNow);
				visitDimension.setImportDate(timeNow);
				visitDimension.setInoutCd("in");
				visitDimension.setLengthOfStay(new BigDecimal(4.0d));
				visitDimension.setLocationCd("Pennsylvania");
				visitDimension.setLocationPath("Pittsburgh/Pennsylvania");
				visitDimension.setSourcesystemCd(getSourceSystemCd());
				visitDimension.setStartDate(timeNow);
				visitDimension.setUpdateDate(timeNow);
				visitDimension.setVisitBlob(null);
				visitDimension.setUploadId(new BigDecimal(99));
				dataSourceMgr.getSession().saveOrUpdate(visitDimension);
			}
		}

		Transaction tx = dataSourceMgr.getSession().beginTransaction();
		dataSourceMgr.getSession().flush();
		tx.commit();
	}

	private void writeConcepts() throws SQLException {
		activeSummaries.clear();
		for (Patient kbPatient : patients) {
			for (Summary kbSummary : kbPatient.getSummaries()) {
				if (kbSummary.getIsActive() == 1) {
					activeSummaries.add(kbSummary);
				}
			}
			for (Encounter kbEncounter : kbPatient.getEncounters()) {
				for (Summary kbSummary : kbEncounter.getSummaries()) {
					if (kbSummary.getIsActive() == 1) {
						activeSummaries.add(kbSummary);
					}
				}
			}
		}
		for (Summary kbSummary : activeSummaries) {
			ConceptDimension conceptDimension = new ConceptDimension();
			conceptDimension.setConceptPath(kbSummary.getPath() + "\\");
			conceptDimension.setConceptCd(kbSummary.getBaseCode());
			conceptDimension.setNameChar(kbSummary.getNameChar());
			conceptDimension.setConceptBlob(null);
			conceptDimension.setUpdateDate(timeNow);
			conceptDimension.setDownloadDate(timeNow);
			conceptDimension.setImportDate(timeNow);
			conceptDimension.setSourcesystemCd(getSourceSystemCd());
			conceptDimension.setUploadId(new BigDecimal(uploadId++));
			dataSourceMgr.getSession().saveOrUpdate(conceptDimension);
		}
		Transaction tx = dataSourceMgr.getSession().beginTransaction();
		dataSourceMgr.getSession().flush();
		tx.commit();
	}

	private void writeObservations() throws SQLException {
		for (Patient kbPatient : patients) {
			for (Summary kbSummary : kbPatient.getSummaries()) {
				if (kbSummary.getIsActive() == 1) {
					writeObservation(kbPatient, null, kbSummary);
				}
			}
			for (Encounter kbEncounter : kbPatient.getEncounters()) {
				for (Summary kbSummary : kbEncounter.getSummaries()) {
					if (kbSummary.getIsActive() == 1) {
						writeObservation(kbPatient, kbEncounter, kbSummary);
					}
				}
			}
		}

		Transaction tx = dataSourceMgr.getSession().beginTransaction();
		dataSourceMgr.getSession().flush();
		tx.commit();
	}

	private void writeObservation(Patient kbPatient, Encounter kbEncounter,
			Summary kbSummary) {

		ObservationFactId observationFactId = new ObservationFactId();
		if (kbEncounter != null) {
			observationFactId.setEncounterNum(new BigDecimal(kbEncounter
					.getId()));
		}
		else {
			observationFactId.setEncounterNum(new BigDecimal(kbPatient
					.getId()));
		}

		observationFactId.setPatientNum(new BigDecimal(kbPatient.getId()));
		observationFactId.setConceptCd(kbSummary.getBaseCode());
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

	public List<Patient> getPatients() {
		return patients;
	}

	public void setPatients(List<Patient> patients) {
		this.patients = patients;
	}

}
