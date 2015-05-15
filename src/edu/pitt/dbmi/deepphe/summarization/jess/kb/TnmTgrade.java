package edu.pitt.dbmi.deepphe.summarization.jess.kb;

public class TnmTgrade extends Summary {
	
	private String providingDepartment;
	private int groupIndex = -1;

	public String getProvidingDepartment() {
		return providingDepartment;
	}

	public void setProvidingDepartment(String providingDepartment) {
		this.providingDepartment = providingDepartment;
	}

	public int getGroupIndex() {
		return groupIndex;
	}

	public void setGroupIndex(int groupIndex) {
		this.groupIndex = groupIndex;
	}

}
