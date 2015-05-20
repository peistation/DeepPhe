/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.dbmi.deepphe.summarization.orm.i2b2meta;

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
public class DeeppheOntologyTest {
    
    private I2b2MetaDataSourceManager i2b2MetaDataSourceManager;
    
    public DeeppheOntologyTest() {
    }
    
      @Before
    public void setUp() {
        i2b2MetaDataSourceManager = new I2b2MetaDataSourceManager();
    }

    @After
    public void tearDown() {
        i2b2MetaDataSourceManager.destroy();
        i2b2MetaDataSourceManager = null;
    }
    
    @SuppressWarnings("unchecked")
	@Test
    public void testDeeppheOntologyCRUD() {
        System.out.println("testCreate");
        Session session = i2b2MetaDataSourceManager.getSession();
        for (int deepPheNum = 4002000; deepPheNum < 4003000; deepPheNum++) {
            Date timeNow = new Date();
            String pathPrefix = "/some/arbitrary/path1/";
            if (deepPheNum % 2 == 1) {
                pathPrefix = "/some/arbitrary/path2/";
            }
            DeeppheOntology deeppheOntology = new DeeppheOntology();
            deeppheOntology.setCBasecode("NA");
            deeppheOntology.setCColumndatatype("NA");
            deeppheOntology.setCColumnname(pathPrefix);
            deeppheOntology.setCComment(null);
            deeppheOntology.setCDimcode("NA");
            deeppheOntology.setCFacttablecolumn("NA");
            deeppheOntology.setCFullname(pathPrefix + "DeepPhe" + deepPheNum);
            deeppheOntology.setCHlevel(BigDecimal.ZERO);
            deeppheOntology.setCMetadataxml(null);
            deeppheOntology.setCName("NA");
            deeppheOntology.setCOperator("NA");
            deeppheOntology.setCPath("NA");
            deeppheOntology.setCSymbol("NA");
            deeppheOntology.setCSynonymCd("NA");
            deeppheOntology.setCTablename("NA");
            deeppheOntology.setCTooltip("NA");
            deeppheOntology.setCTotalnum(BigDecimal.TEN);
            deeppheOntology.setCVisualattributes("NA");
            deeppheOntology.setDownloadDate(timeNow);
            deeppheOntology.setImportDate(timeNow);
            deeppheOntology.setMAppliedPath("NA");
            deeppheOntology.setMExclusionCd("NA");
            deeppheOntology.setSourcesystemCd("DeepPhe");
            deeppheOntology.setUpdateDate(timeNow);
            deeppheOntology.setValuetypeCd("NA");          
            session.saveOrUpdate(deeppheOntology);
        }
        session.flush();
        
        System.out.println("After creation flush....");
        
        Query query;
        query = session.createQuery("from DeeppheOntology o where CFullname like :cFullName AND sourcesystemCd like :sourceSystemCd");
        query.setString("cFullName", "%path2%");
        query.setString("sourceSystemCd", "DeepPhe");
        List<DeeppheOntology> deeppheOntologys = query.list();
        assertEquals(deeppheOntologys.size(), 500);

        System.out.println("testRead");
        deeppheOntologys = session.createQuery("from DeeppheOntology o").setMaxResults(10).list();
        assertEquals(deeppheOntologys.size(), 10);

        System.out.println("testUpdate");

        query = session.createQuery("from DeeppheOntology o where CFullname like :cFullName and sourcesystemCd like :sourceSystemCd");
        query.setString("cFullName", "%path2%");
        query.setString("sourceSystemCd", "DeepPhe");
        deeppheOntologys = query.list();
        for (DeeppheOntology deeppheOntology : deeppheOntologys) {
            deeppheOntology.setCColumnname(deeppheOntology.getCColumnname().replaceAll("path2", "path1"));
            session.saveOrUpdate(deeppheOntology);
        }
        session.flush();
        query = session.createQuery("from DeeppheOntology c where CColumnname like :cColumnName and sourcesystemCd like :sourceSystemCd");
        query.setString("cColumnName", "%path1%");
        query.setString("sourceSystemCd", "DeepPhe");
        deeppheOntologys = query.list();
        assertEquals(deeppheOntologys.size(), 1000);

        System.out.println("testDelete");

        query = session.createQuery("from DeeppheOntology o where sourcesystemCd like :sourceSystemCd");
        query.setString("sourceSystemCd", "DeepPhe");
        deeppheOntologys = query.list();
        for (DeeppheOntology deeppheOntology : deeppheOntologys) {
            session.delete(deeppheOntology);
        }
        session.flush();
        query = session.createQuery("from DeeppheOntology o where sourcesystemCd like :sourceSystemCd");
        query.setString("sourceSystemCd", "DeepPhe");
        deeppheOntologys = query.list();
        assertEquals(deeppheOntologys.size(), 0);

    }

}
