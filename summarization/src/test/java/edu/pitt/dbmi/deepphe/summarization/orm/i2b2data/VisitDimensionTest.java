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
public class VisitDimensionTest {

    private I2b2DataDataSourceManager i2b2DataDataSourceManager;

    public VisitDimensionTest() {
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
    public void testVisitDimensionCRUD() {
        System.out.println("testCreate");
        Session session = i2b2DataDataSourceManager.getSession();
        for (int patNum = 4002000; patNum < 4002100; patNum++) {
            for (int encounterNum = 0; encounterNum < 5; encounterNum++) {
                Date timeNow = new Date();
                VisitDimension visitDimension = new VisitDimension();
                VisitDimensionId visitId = new VisitDimensionId();
                visitId.setPatientNum(new BigDecimal(patNum));
                visitId.setEncounterNum(new BigDecimal(encounterNum));
                visitDimension.setId(visitId);
                visitDimension.setActiveStatusCd("Active");
                visitDimension.setDownloadDate(timeNow);
                visitDimension.setEndDate(timeNow);              
                visitDimension.setImportDate(timeNow);
                visitDimension.setInoutCd("in");
                visitDimension.setLengthOfStay(new BigDecimal(4.0d));
                visitDimension.setLocationCd("Pennsylvania");
                visitDimension.setLocationPath("Pittsburgh/Pennsylvania");
                visitDimension.setSourcesystemCd("DeepPhe");
                visitDimension.setStartDate(timeNow);
                visitDimension.setUpdateDate(timeNow);
                visitDimension.setVisitBlob(null);
                visitDimension.setUploadId(new BigDecimal(99));
                session.saveOrUpdate(visitDimension);
            }
        }
        session.flush();
        Query query = session.createQuery("from VisitDimension v where locationPath like :locationPath and sourcesystemCd = :sourceSystemCd");
        query.setString("locationPath", "Pitt%");
        query.setString("sourceSystemCd", "DeepPhe");
        List<VisitDimension> visitDimensions = query.list();
        assertEquals(visitDimensions.size(), 500);

        System.out.println("testRead");
        visitDimensions = session.createQuery("from VisitDimension v").setMaxResults(10).list();
        assertEquals(visitDimensions.size(), 10);

        System.out.println("testUpdate");

        query = session.createQuery("from VisitDimension v where locationPath like :locationPath and sourcesystemCd = :sourceSystemCd");
        query.setString("locationPath", "Pitt%");
        query.setString("sourceSystemCd", "DeepPhe");
        visitDimensions = query.list();
        for (VisitDimension visitDimension : visitDimensions) {
            visitDimension.setLocationPath("Seattle/Washington");
            session.saveOrUpdate(visitDimension);
        }
        session.flush();
        query = session.createQuery("from VisitDimension v where locationPath like :locationPath and sourcesystemCd = :sourceSystemCd");
        query.setString("locationPath", "Seattle%");
        query.setString("sourceSystemCd", "DeepPhe");
        visitDimensions = query.list();
        assertEquals(visitDimensions.size(), 500);

        System.out.println("testDelete");

        query = session.createQuery("from VisitDimension v where locationPath like :locationPath and sourcesystemCd like :sourceSystemCd");
        query.setString("locationPath", "Seattle%");
        query.setString("sourceSystemCd", "DeepPhe");
        visitDimensions = query.list();
        for (VisitDimension visitDimension : visitDimensions) {
            session.delete(visitDimension);
        }
        session.flush();
        query = session.createQuery("from VisitDimension v where sourcesystemCd like :sourceSystemCd");
        query.setString("sourceSystemCd", "DeepPhe");
        visitDimensions = query.list();
        assertEquals(visitDimensions.size(), 0);
    }

}
