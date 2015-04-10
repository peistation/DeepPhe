package edu.pitt.dbmi.deepphe.summarization.jess;

public class DeepPheLink {
	
	private int uuid;
	private int docUuid;
	private int activationLevel;
	private String name;
	private int srcUuid = -1;
	private int tgtUuid = -1;
	private int sequence = -1;

	public int getUuid() {
		return uuid;
	}
	public void setUuid(int uuid) {
		this.uuid = uuid;
	}
	public int getDocUuid() {
		return docUuid;
	}
	public void setDocUuid(int docUuid) {
		this.docUuid = docUuid;
	}
	public int getActivationLevel() {
		return activationLevel;
	}
	public void setActivationLevel(int activationLevel) {
		this.activationLevel = activationLevel;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSrcUuid() {
		return srcUuid;
	}
	public void setSrcUuid(int srcUuid) {
		this.srcUuid = srcUuid;
	}
	public int getTgtUuid() {
		return tgtUuid;
	}
	public void setTgtUuid(int tgtUuid) {
		this.tgtUuid = tgtUuid;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

}
