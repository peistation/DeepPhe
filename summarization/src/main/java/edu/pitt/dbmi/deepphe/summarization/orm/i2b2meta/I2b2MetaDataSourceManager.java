/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.dbmi.deepphe.summarization.orm.i2b2meta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class I2b2MetaDataSourceManager {

    private static final Logger logger = LoggerFactory
            .getLogger(I2b2MetaDataSourceManager.class);

    private String hibernateDialect = org.hibernate.dialect.Oracle10gDialect.class
            .getName();
    private String hibernateDriver = oracle.jdbc.OracleDriver.class.getName();
    private String hibernateConnectionUrl = "jdbc:oracle:thin:@dbmi-i2b2-dev05.dbmi.pitt.edu:1521:XE";
    private String hibernateUserName = "i2b2metadata";
    private String hibernateUserPassword = "demouser";

    //Very dangerous to have this set to create or update. Only available for 
    //explicit use by the installer or other utility methods. Must always 
    //default to null to avoid accidental database modifications.
    private String hibernateHbm2ddlAuto = "none";

    private String hibernateShowSql = "true";
    private String hibernateCacheUseSecondLevelCache = "false";

    private Configuration configuration;

    private SessionFactory sessionFactory;

    private Session session;

    public I2b2MetaDataSourceManager() {
    }

    private void buildConfiguration() {
        configuration = new Configuration();
        configuration.setProperty("hibernate.dialect", getHibernateDialect());
        configuration.setProperty("hibernate.connection.driver_class",
                getHibernateDriver());
        configuration.setProperty("hibernate.show_sql", getHibernateShowSql());
        configuration.setProperty("hibernate.connection.url",
                getHibernateConnectionUrl());
        configuration.setProperty("hibernate.connection.username",
                getHibernateUserName());
        configuration.setProperty("hibernate.connection.password",
                getHibernateUserPassword());
        configuration.setProperty("hibernate.cache.use_second_level_cache", getHibernateCacheUseSecondLevelCache());
        if (getHibernateHbm2ddlAuto() != null) {
            configuration.setProperty("hibernate.hbm2ddl.auto",
                    getHibernateHbm2ddlAuto());
        }
        addAnnotatedClasses();
    }

    /**
     * get or create configuration object to further customize it before calling
     * getSession()
     *
     * @return
     */
    public Configuration getConfiguration() {
        if (configuration == null) {
            buildConfiguration();
        }
        return configuration;
    }

    private void addAnnotatedClasses() {
        configuration.addAnnotatedClass(DeeppheOntology.class);
        configuration.addAnnotatedClass(OntProcessStatus.class);
        configuration.addAnnotatedClass(TableAccess.class);
    }

    public void addAnnotatedClsesForPackage() {
        logger.debug("Entered addAnnotatedClsesForPackage for " + getClass().getName());
        try {
            String pkgName = getClass().getPackage().getName();
            ClassPath classPath = ClassPath.from(getClass().getClassLoader());
            final ArrayList<Class<?>> clses = new ArrayList<>();
            Set<ClassInfo> clsInfos = classPath
            .getTopLevelClassesRecursive(pkgName);
            for (ClassInfo clsInfo : clsInfos) {
            	if (!clsInfo.getName().equals(getClass().getName())) {
            		clses.add(clsInfo.load());
            	}
            }
            if (clses.isEmpty()) {
                logger.error("Failed to load hibernate clses");
            } else {
            	for (Class<?> cls : clses) {
            		 System.err.println("Configuring " + cls.getName() + " into Hibernate");
                     logger.info("Configuring " + cls.getName() + " into Hibernate");
                     configuration.addAnnotatedClass(cls);
            	}
            }
        } catch (IOException e) {
        }
        logger.debug("Exited addAnnotatedClsesForPackage for " + getClass().getName());
    }

    /*
     * SessionFactory
     */
    @SuppressWarnings("deprecation")
	private boolean buildSessionFactory(Configuration configuration) {
        sessionFactory = configuration.buildSessionFactory();
        return !sessionFactory.isClosed();
    }

    /*
     * Session
     */
    public Session getSession() {
        if (session == null) {
            if (configuration == null) {
                buildConfiguration();
            }
            if (sessionFactory == null) {
                buildSessionFactory(configuration);
            }

            session = sessionFactory.openSession();
        }
        return session;
    }

    /*
     * Clean up
     */
    private void closeSession() throws HibernateException {
        if (session != null && session.isOpen()) {
            session.close();
            session = null;
        }
    }

    private void closeSessionFactory() throws HibernateException {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
        sessionFactory = null;
    }

    public void destroy() {
        closeSession();
        closeSessionFactory();
        logger.debug("destroyed " + getClass().getName() + " : session and sessionFactory removed.");
    }

    @Override
    protected void finalize() throws Throwable {
        destroy();
        super.finalize();
    }

    public String getHibernateDialect() {
        return hibernateDialect;
    }

    public void setHibernateDialect(String hibernateDialect) {
        this.hibernateDialect = hibernateDialect;
    }

    public String getHibernateDriver() {
        return hibernateDriver;
    }

    public void setHibernateDriver(String hibernateDriver) {
        this.hibernateDriver = hibernateDriver;
    }

    public String getHibernateConnectionUrl() {
        return hibernateConnectionUrl;
    }

    public void setHibernateConnectionUrl(String hibernateConnectionUrl) {
        this.hibernateConnectionUrl = hibernateConnectionUrl;
    }

    public String getHibernateUserName() {
        return hibernateUserName;
    }

    public void setHibernateUserName(String hibernateUserName) {
        this.hibernateUserName = hibernateUserName;
    }

    public String getHibernateUserPassword() {
        return hibernateUserPassword;
    }

    public void setHibernateUserPassword(String hibernateUserPassword) {
        this.hibernateUserPassword = hibernateUserPassword;
    }

    public String getHibernateHbm2ddlAuto() {
        return hibernateHbm2ddlAuto;
    }

    public void setHibernateHbm2ddlAuto(String hibernateHbm2ddlAuto) {
        this.hibernateHbm2ddlAuto = hibernateHbm2ddlAuto;
    }

    public String getHibernateShowSql() {
        return hibernateShowSql;
    }

    public void setHibernateShowSql(String hibernateShowSql) {
        this.hibernateShowSql = hibernateShowSql;
    }

    public String getHibernateCacheUseSecondLevelCache() {
        return hibernateCacheUseSecondLevelCache;
    }

    public void setHibernateCacheUseSecondLevelCache(
            String hibernateCacheUseSecondLevelCache) {
        this.hibernateCacheUseSecondLevelCache = hibernateCacheUseSecondLevelCache;
    }

}
