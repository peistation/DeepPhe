package edu.pitt.dbmi.deepphe.summarization.jess;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import edu.pitt.dbmi.deepphe.summarization.jess.kb.Encounter;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.Patient;

public class PatientListReader {
	
	private String inputDirectoryPath;
	private List<Patient> patients;

	public static void main(String[] args) {
		//String file  = "/home/tseytlin/Work/DeepPhe/summarization/src/main/resources/summarization/raw";//"src\\main\\resources\\summarization\\raw";
		PatientListReader reader = new PatientListReader();
		File reportsDirectory = new File(SummarizationGui.PROJECT_LOCATION+"/data/sample/docs");
		final List<Patient> patients = new ArrayList<>();
		reader.setInputDirectoryPath(reportsDirectory.getAbsolutePath());
		reader.setPatients(patients);
		reader.execute();
		
		for (Patient patient : patients) {
			System.out.println("got patient #" + patient.getId());
		}

	}
	
	public void execute() {
		try {
			tryExecute();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	private void tryExecute() throws IOException {
		File reportsDirectory = new File(getInputDirectoryPath());
		File[] rawFiles = reportsDirectory.listFiles();
		for (File rawFile : rawFiles) {
			if (rawFile.isDirectory()) {
				createPatient(rawFile);
			}
		}
	}

	private void createPatient(File patientDirectory) throws IOException {
		Patient patient = new Patient();
		patients.add(patient);
		File[] rawFiles = patientDirectory.listFiles();
		int encounterSequence = 0;
		for (File rawFile : rawFiles) {
			if (rawFile.isFile()) {
				Encounter encounter = new Encounter();
				encounter.setUri(rawFile.getAbsolutePath());
				encounter.setContent(FileUtils.readFileToString(rawFile));
				encounter.setSequence(encounterSequence++);
				patient.addEncounter(encounter);
			}
		}
		
	}

	public String getInputDirectoryPath() {
		return inputDirectoryPath;
	}

	public void setInputDirectoryPath(String inputDirectoryPath) {
		this.inputDirectoryPath = inputDirectoryPath;
	}

	public List<Patient> getPatients() {
		return patients;
	}

	public void setPatients(List<Patient> patients) {
		this.patients = patients;
	}
	
	

}
