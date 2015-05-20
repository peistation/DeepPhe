package edu.pitt.dbmi.deepphe.summarization.jess.treeview;

public class JessDefRuleUserObj {
	private jess.Defrule rule;
	public JessDefRuleUserObj() {
	}
	public String toString() {
		return rule.getName();
	}

	public jess.Defrule getRule() {
		return rule;
	}

	public void setRule(jess.Defrule rule) {
		this.rule = rule;
	}
	public String getContent() {
		return rule.getDocstring();
	}
}