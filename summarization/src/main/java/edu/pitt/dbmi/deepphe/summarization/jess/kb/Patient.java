package edu.pitt.dbmi.deepphe.summarization.jess.kb;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class Patient extends Summarizable {
	
	protected final List<Encounter> encounters = new ArrayList<Encounter>();

	public void addEncounter(Encounter encounter) {
		encounters.add(encounter);
	}
	
	public List<Encounter> getEncounters() {
		return encounters;
	}
	
	public void clearEncounters() {
		encounters.clear();
	}

	public Object getGender() {
		return "Female";
	}
	
	public String fetchInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("Patient" + StringUtils.leftPad(getId() + "", 4, "0") + "\n");
		if (getSummaries().size() == 0) {
			sb.append("\n\n\nYet to be summarized");
		}
		else {
			sb.append("\n\n\nPatient Summary Information: \n\n");
			for (Summary summary : getSummaries()) {
				sb.append(summary.toString() + "\n");
			}
		}
		return sb.toString();
	}
	
	public String toString() {
		return "Patient" + StringUtils.leftPad(getId() + "", 4, "0");
	}

}
