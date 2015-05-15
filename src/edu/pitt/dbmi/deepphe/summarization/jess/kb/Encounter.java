package edu.pitt.dbmi.deepphe.summarization.jess.kb;

import org.apache.commons.lang.StringUtils;

public class Encounter extends Summarizable {
	
	private int patientId = -1;
	private int sequence = -1;
	private String kind = "NA";
	private String uri;
	private String content;
	
	
	public Encounter() {
	}

	public int getPatientId() {
		return patientId;
	}

	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public String fetchInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("Encounter" + StringUtils.leftPad(getId() + "", 4, "0") + "\n");
		sb.append("\n\n\n" + getContent() + "\n\n\n");
		sb.append("\n\n=====================================================================\n\n");
		if (getSummaries().size() == 0) {
			sb.append("\n\n\nYet to be processed by cTAKES and FHIR");
		}
		else {
			sb.append("\n\n\nEncounter Summary Information: \n\n");
			for (Summary summary : getSummaries()) {
				sb.append(summary.toString() + "\n");
			}
		}
		return sb.toString();
	}
	
	public String toString() {
		return "Encounter" + StringUtils.leftPad(getId() + "", 4, "0");
	}

}
