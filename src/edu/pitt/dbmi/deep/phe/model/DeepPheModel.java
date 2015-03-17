package edu.pitt.dbmi.deep.phe.model;

import org.hl7.fhir.instance.model.Resource;

/**
 * DeepPhe model interface that 
 * @author tseytlin
 */
public interface DeepPheModel {
	public String getDisplaySimple();
	public String getIdentifierSimple();
	public String getSummary();
	public Resource getResource();
}
