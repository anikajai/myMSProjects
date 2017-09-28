package com.nagarro.codingcompetition.pojo;

public class MatchedResource {

	private String empID;
	private double score;
	private Resource resource;
	private int resourceIndex;

	public String getEmpID() {
		return empID;
	}

	public void setEmpID(String empID) {
		this.empID = empID;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = Math.round(score * 100.0) / 100.0;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public int getResourceNumber() {
		return resourceIndex;
	}

	public void setResourceNumber(int resourceNumber) {
		this.resourceIndex = resourceNumber;
	}

}
