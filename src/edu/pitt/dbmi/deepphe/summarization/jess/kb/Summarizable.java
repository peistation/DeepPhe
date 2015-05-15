package edu.pitt.dbmi.deepphe.summarization.jess.kb;

import java.util.ArrayList;
import java.util.List;

public class Summarizable extends Identified {
	
	protected final List<Summary> summaries = new ArrayList<Summary>();

	public void addSummary(Summary summary) {
		summaries.add(summary);
	}
	
	public List<Summary> getSummaries() {
		return summaries;
	}
	
	public void clearSummaries() {
		summaries.clear();
	}


}
