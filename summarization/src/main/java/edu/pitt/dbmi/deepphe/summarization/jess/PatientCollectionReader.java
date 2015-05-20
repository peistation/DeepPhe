package edu.pitt.dbmi.deepphe.summarization.jess;

import java.io.IOException;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.UimaContextAdmin;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.collection.CasInitializer;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceManager;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resource.metadata.ProcessingResourceMetaData;
import org.apache.uima.resource.metadata.ResourceMetaData;
import org.apache.uima.util.Logger;
import org.apache.uima.util.Progress;

import edu.pitt.dbmi.deepphe.summarization.jess.kb.Patient;

@SuppressWarnings("deprecation")
public class PatientCollectionReader implements CollectionReader {
	
	private Patient currentPatient;
	
	private int currentEncounterSequence = 0;

	@Override
	public void close() throws IOException {

	}

	@Override
	public ProcessingResourceMetaData getProcessingResourceMetaData() {
		return null;
	}

	@Override
	public Progress[] getProgress() {
		return null;
	}

	@Override
	public boolean hasNext() throws IOException, CollectionException {
		return currentEncounterSequence < currentPatient.getEncounters().size();
	}

	@Override
	public boolean isConsuming() {
		return false;
	}

	@Override
	public void destroy() {
	}

	@Override
	public Logger getLogger() {
		return null;
	}

	@Override
	public ResourceMetaData getMetaData() {
		return null;
	}

	@Override
	public ResourceManager getResourceManager() {
		return null;
	}

	@Override
	public UimaContext getUimaContext() {
		return null;
	}

	@Override
	public UimaContextAdmin getUimaContextAdmin() {
		return null;
	}

	@Override
	public boolean initialize(ResourceSpecifier arg0, Map<String, Object> arg1)
			throws ResourceInitializationException {
		return false;
	}

	@Override
	public void setLogger(Logger arg0) {
	}

	@Override
	public Object getConfigParameterValue(String arg0) {
		return null;
	}

	@Override
	public Object getConfigParameterValue(String arg0, String arg1) {
		return null;
	}

	@Override
	public void reconfigure() throws ResourceConfigurationException {
	}

	@Override
	public void setConfigParameterValue(String arg0, Object arg1) {

	}

	@Override
	public void setConfigParameterValue(String arg0, String arg1, Object arg2) {

	}

	@Override
	public CasInitializer getCasInitializer() {
		return null;
	}

	@Override
	public void getNext(CAS cas) throws IOException, CollectionException {
		cas.setDocumentLanguage("en");
		cas.setDocumentText(currentPatient.getEncounters().get(currentEncounterSequence).getContent());
		currentEncounterSequence++;
	}

	@Override
	public void setCasInitializer(CasInitializer arg0) {

	}

	@Override
	public void typeSystemInit(TypeSystem arg0)
			throws ResourceInitializationException {

	}

	public void setPatient(Patient patient) {
		this.currentPatient = patient;
		
	}

}
