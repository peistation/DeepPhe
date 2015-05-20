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

import static org.junit.Assert.*;

/**
 *
 * @author kjm84
 */
public class ConceptDimensionTest {

    private I2b2DataDataSourceManager i2b2DataDataSourceManager;

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
	@org.junit.Test
    public void testConceptDimensionCRUD() {
        System.out.println("testCreate");
        Session session = i2b2DataDataSourceManager.getSession();
        for (int deepPheNum = 4002000; deepPheNum < 4003000; deepPheNum++) {
            String cui = "deepPhe:" + deepPheNum;
            Date timeNow = new Date();
            String pathPrefix = "/some/arbitrary/path1/";
            if (deepPheNum % 2 == 1) {
                pathPrefix = "/some/arbitrary/path2/";
            }
            ConceptDimension conceptDimension = new ConceptDimension();
            conceptDimension.setConceptCd(cui);
            conceptDimension.setConceptPath(pathPrefix + cui);
            conceptDimension.setDownloadDate(timeNow);
            conceptDimension.setImportDate(timeNow);
            conceptDimension.setNameChar(pathPrefix + cui);
            conceptDimension.setSourcesystemCd("DeepPhe");
            conceptDimension.setUpdateDate(timeNow);
            conceptDimension.setUploadId(BigDecimal.ZERO);
            session.saveOrUpdate(conceptDimension);
        }
        session.flush();
        Query query = session.createQuery("from ConceptDimension c where conceptCd like :conceptCd");
        query.setString("conceptCd", "deepPhe:%");
        List<ConceptDimension> conceptDimensions = query.list();
        assertEquals(conceptDimensions.size(), 1000);

        query = session.createQuery("from ConceptDimension c where conceptCd like :conceptCd and conceptPath like :conceptPath");
        query.setString("conceptCd", "deepPhe:%");
        query.setString("conceptPath", "%path1%");
        conceptDimensions = query.list();
        assertEquals(conceptDimensions.size(), 500);

        System.out.println("testRead");
        conceptDimensions = session.createQuery("from ConceptDimension c").setMaxResults(10).list();
        assertEquals(conceptDimensions.size(), 10);

        System.out.println("testUpdate");

        query = session.createQuery("from ConceptDimension c where conceptPath like :conceptPath");
        query.setString("conceptPath", "%path2%");
        conceptDimensions = query.list();
        for (ConceptDimension conceptDimension : conceptDimensions) {
            conceptDimension.setConceptPath(conceptDimension.getConceptPath().replaceAll("path2", "path1"));
            session.saveOrUpdate(conceptDimension);
        }
        session.flush();
        query = session.createQuery("from ConceptDimension c where conceptPath like :conceptPath");
        query.setString("conceptPath", "%path1%");
        conceptDimensions = query.list();
        assertEquals(conceptDimensions.size(), 1000);

        System.out.println("testDelete");

        query = session.createQuery("from ConceptDimension c where conceptCd like :conceptCd");
        query.setString("conceptCd", "deepPhe:%");
        conceptDimensions = query.list();
        for (ConceptDimension conceptDimension : conceptDimensions) {
            session.delete(conceptDimension);
        }
        session.flush();
        query = session.createQuery("from ConceptDimension c where conceptCd like :conceptCd");
        query.setString("conceptCd", "deepPhe:%");
        conceptDimensions = query.list();
        assertEquals(conceptDimensions.size(), 0);

    }

}
