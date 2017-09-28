package com.nagarro.codingcompetition.pojo;

import java.util.Map;

public class Project {
	private String clientKey;
	private String projectKey;
	private String customerName;
	private String projectName;
	private int totalNumOfOpenings = 0;
	private double maxProjectScoreLimit = 0;
	private Map<Integer, Opening> projectOpenings;

	public String getClientKey() {
		return clientKey;
	}

	public String getProjectKey() {
		return projectKey;
	}

	public String getCustomerName() {
		return customerName;
	}

	public String getProjectName() {
		return projectName;
	}

	public int getTotalNumOfOpenings() {
		return totalNumOfOpenings;
	}

	public void setClientKey(String clientKey) {
		this.clientKey = clientKey;
	}

	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public void setTotalNumOfOpenings(int totalNumOfOpenings) {
		this.totalNumOfOpenings = totalNumOfOpenings;
	}

	public void increaseTotalNumOfOpenings() {
		totalNumOfOpenings += 1;
	}

	public double getMaxProjectScoreLimit() {
		return maxProjectScoreLimit;
	}

	public void setMaxProjectScoreLimit(double maxProjectScoreLimit) {
		this.maxProjectScoreLimit = maxProjectScoreLimit;
	}

	public Map<Integer, Opening> getProjectOpenings() {
		return projectOpenings;
	}

	public void setProjectOpenings(Map<Integer, Opening> projectOpenings) {
		this.projectOpenings = projectOpenings;
	}
}
