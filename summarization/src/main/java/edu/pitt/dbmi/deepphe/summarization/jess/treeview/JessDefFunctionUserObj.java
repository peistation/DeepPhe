package edu.pitt.dbmi.deepphe.summarization.jess.treeview;

public class JessDefFunctionUserObj {
	private jess.Deffunction function;
	
	public JessDefFunctionUserObj() {
	}
	
	public String getContent() {
		return function.getDocstring();
	}
	
	public String toString() {
		return function.getName();
	}

	public jess.Deffunction getFunction() {
		return function;
	}

	public void setFunction(jess.Deffunction function) {
		this.function = function;
	}
}