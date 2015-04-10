package edu.pitt.dbmi.deepphe.summarization.jess;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeepPheTnmResult {

	private int id;
	private String tnmAggregate = "NA";
	private String tumor = "NA";
	private String node = "NA";
	private String metastasis = "NA";

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTnmAggregate() {
		return tnmAggregate;
	}
	public void setTnmAggregate(String tnmAggregate) {
		this.tnmAggregate = tnmAggregate;
	}
	public String getTumor() {
		return tumor;
	}
	public void setTumor(String tumor) {
		this.tumor = tumor;
	}
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	public String getMetastasis() {
		return metastasis;
	}
	public void setMetastasis(String metastasis) {
		this.metastasis = metastasis;
	}
	public void assignMeasureToTumor(String measurementMention) {
		Pattern p = Pattern.compile("([0-9.]+).*CM");
		Matcher matcher = p.matcher(measurementMention);
		if (matcher.find()) {
			double centimeters = Double.parseDouble(matcher.group(1));
			if (centimeters > 2.0d) {
				setTumor("T1");
			}
		}
	}	
	public void assignmentMeasureToLymph(String measurementMention) {
		Pattern p = Pattern.compile("([0-9.]+).*CM");
		Matcher matcher = p.matcher(measurementMention);
		if (matcher.find()) {
			double centimeters = Double.parseDouble(matcher.group(1));
			if (centimeters > 2.0d) {
				setNode("N1");
			}
		}
	}	
}
