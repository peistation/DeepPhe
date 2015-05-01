/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.dbmi.deepphe.summarization.orm;

import edu.pitt.dbmi.deepphe.summarization.orm.i2b2data.OrmDataTestSuite;
import edu.pitt.dbmi.deepphe.summarization.orm.i2b2meta.OrmMetaTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author kjm84
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({OrmDataTestSuite.class,OrmMetaTestSuite.class})
public class OrmTestSuite {
    
}
