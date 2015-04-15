package edu.pitt.dbmi.deep.phe.model;

import java.io.File;
import java.io.IOException;

import org.hl7.fhir.instance.model.Resource;


/**
 * DeepPhe model interface that 
 * @author tseytlin
 */
public interface Element {
	public String getDisplaySimple();
	public String getIdentifierSimple();
	public String getSummary();
	public Resource getResource();
	public void setReport(Report r);
	public void save(File e) throws Exception;
}
