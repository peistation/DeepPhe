package edu.pitt.dbmi.deepphe.summarization.jess;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeepPheSize extends DeepPheTop {

	private double sizeInMaxDimension = -1.0d;
	private double xSize= -1.0d;
	private double ySize= -1.0d;
	private double zSize= -1.0d;
	private String uom = "NA";
	
	public double getSizeInMaxDimension() {
		return sizeInMaxDimension;
	}
	public void setSizeInMaxDimension(double sizeInMaxDimension) {
		this.sizeInMaxDimension = sizeInMaxDimension;
	}
	public double getxSize() {
		return xSize;
	}
	public void setxSize(double xSize) {
		this.xSize = xSize;
	}
	public double getySize() {
		return ySize;
	}
	public void setySize(double ySize) {
		this.ySize = ySize;
	}
	public double getzSize() {
		return zSize;
	}
	public void setzSize(double zSize) {
		this.zSize = zSize;
	}
	public String getUom() {
		return uom;
	}
	public void setUom(String uom) {
		this.uom = uom;
	}
	public void assignMeasure(String measurementMention) {
		Pattern p = Pattern.compile("[0-9.]+");
		Matcher matcher = p.matcher(measurementMention);
		if (matcher.find()) {
			setSizeInMaxDimension(Double.parseDouble(matcher.group()));
		}
		p = Pattern.compile("cm|mm|nm|in|inches", Pattern.CASE_INSENSITIVE);
		matcher = p.matcher(measurementMention);
		if (matcher.find()) {
			setUom(matcher.group().toLowerCase());
		}
	}
	
	
}
