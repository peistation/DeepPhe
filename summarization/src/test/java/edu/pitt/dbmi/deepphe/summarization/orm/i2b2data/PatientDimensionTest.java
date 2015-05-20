/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.dbmi.deepphe.summarization.orm.i2b2data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author kjm84
 */
public class PatientDimensionTest {

    private I2b2DataDataSourceManager i2b2DataDataSourceManager;

    public PatientDimensionTest() {
    }

    @Before
    public void setUp() {
        i2b2DataDataSourceManager = new I2b2DataDataSourceManager();
    }

    @After
    public void tearDown() {
        i2b2DataDataSourceManager.destroy();
        i2b2DataDataSourceManager = null;
    }

    @SuppressWarnings("unchecked")
	@Test
    public void testPatientDimensionCRUD() {
        System.out.println("testCreate");
        Session session = i2b2DataDataSourceManager.getSession();
        for (int patNum = 4002000; patNum < 4003000; patNum++) {
            Date timeNow = new Date();
            PatientDimension patientDimension = new PatientDimension();
            patientDimension.setPatientNum(new BigDecimal(patNum));
            patientDimension.setAgeInYearsNum(new BigDecimal(78));
            patientDimension.setBirthDate(timeNow);
            patientDimension.setDeathDate(timeNow);
            patientDimension.setDownloadDate(timeNow);
            patientDimension.setImportDate(timeNow);
            patientDimension.setIncomeCd(null);
            patientDimension.setLanguageCd(null);
            patientDimension.setMaritalStatusCd(null);
            patientDimension.setPatientBlob(null);
            patientDimension.setRaceCd(null);
            patientDimension.setSourcesystemCd("DeepPhe");
            session.saveOrUpdate(patientDimension);
        }
        session.flush();
        Query query = session.createQuery("from PatientDimension p where ageInYearsNum like :ageInYearsNum and sourcesystemCd = :sourceSystemCd");
        query.setBigDecimal("ageInYearsNum", new BigDecimal(78));
        query.setString("sourceSystemCd", "DeepPhe");
        List<PatientDimension> patientDimensions = query.list();
        assertEquals(patientDimensions.size(), 1000);

        System.out.println("testRead");
        patientDimensions = session.createQuery("from PatientDimension p").setMaxResults(10).list();
        assertEquals(patientDimensions.size(), 10);

        System.out.println("testUpdate");

        query = session.createQuery("from PatientDimension p where ageInYearsNum like :ageInYearsNum and sourcesystemCd = :sourceSystemCd");
        query.setBigDecimal("ageInYearsNum", new BigDecimal(78));
        query.setString("sourceSystemCd", "DeepPhe");
        patientDimensions = query.list();
        for (PatientDimension patientDimension : patientDimensions) {
            patientDimension.setAgeInYearsNum(new BigDecimal(patientDimension.getAgeInYearsNum().doubleValue() + 1.0d));
            session.saveOrUpdate(patientDimension);
        }
        session.flush();
        query = session.createQuery("from PatientDimension p where ageInYearsNum like :ageInYearsNum and sourcesystemCd = :sourceSystemCd");
        query.setBigDecimal("ageInYearsNum", new BigDecimal(79));
        query.setString("sourceSystemCd", "DeepPhe");
        patientDimensions = query.list();
        assertEquals(patientDimensions.size(), 1000);

        System.out.println("testDelete");

        query = session.createQuery("from PatientDimension p where ageInYearsNum like :ageInYearsNum and sourcesystemCd like :sourceSystemCd");
        query.setBigDecimal("ageInYearsNum", new BigDecimal(79));
        query.setString("sourceSystemCd", "DeepPhe");
        patientDimensions = query.list();
        for (PatientDimension patientDimension : patientDimensions) {
            session.delete(patientDimension);
        }
        session.flush();
        query = session.createQuery("from PatientDimension p where sourcesystemCd like :sourceSystemCd");
        query.setString("sourceSystemCd", "DeepPhe");
        patientDimensions = query.list();
        assertEquals(patientDimensions.size(), 0);
    }

}
