package edu.pitt.dbmi.deepphe.summarization.jess;

public class DeepPheDocument {
	
	private int uuid = -1;
	private int activationLevel = -1;
	private String name;
	private int sequence = -1;

	public int getUuid() {
		return uuid;
	}
	public void setUuid(int uuid) {
		this.uuid = uuid;
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
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

}
