package edu.pitt.dbmi.deepphe.summarization.jess.kb;

public class TumorSize extends Summary {
	
	private double greatestDimension;
	private double dimensionOne;
	private double dimensionTwo;
	private double dimensionThree;
	
	public double getGreatestDimension() {
		return greatestDimension;
	}
	public void setGreatestDimension(double greatestDimension) {
		this.greatestDimension = greatestDimension;
	}
	public double getDimensionOne() {
		return dimensionOne;
	}
	public void setDimensionOne(double dimensionOne) {
		this.dimensionOne = dimensionOne;
	}
	public double getDimensionTwo() {
		return dimensionTwo;
	}
	public void setDimensionTwo(double dimensionTwo) {
		this.dimensionTwo = dimensionTwo;
	}
	public double getDimensionThree() {
		return dimensionThree;
	}
	public void setDimensionThree(double dimensionThree) {
		this.dimensionThree = dimensionThree;
	}


}
