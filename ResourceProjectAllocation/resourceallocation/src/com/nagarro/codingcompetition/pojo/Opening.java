package com.nagarro.codingcompetition.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nagarro.codingcompetition.enums.SkillLevel;

public class Opening {
	private int requestID;
	private String clientKey;
	private String projectKey;
	private String customerName;
	private String projectName;
	private boolean isKeyProject;
	private String projectDomain;
	private Date projectStartDate;
	private Date projectEndDate;
	private String role;
	private boolean isKeyPosition;
	private double yearsOfExperience;
	private String mandatorySkills;
	private String optionalSkills;
	private String domainExperience;
	private boolean clientCommunication;
	private String certificationRequirement;
	private Date requestStartDate;
	private Date allocationStartDate;
	private Date allocationEndDate;
	private double billingRate;
	private double billingAllocation;

	private Map<String, SkillLevel> mandatorySkillsMap = new HashMap<String, SkillLevel>();
	private List<MatchedResource> matchedResources = new ArrayList<MatchedResource>();
	private Project myProject;

	public int getRequestID() {
		return requestID;
	}

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

	public boolean isKeyProject() {
		return isKeyProject;
	}

	public String getProjectDomain() {
		return projectDomain;
	}

	public Date getProjectStartDate() {
		return projectStartDate;
	}

	public Date getProjectEndDate() {
		return projectEndDate;
	}

	public String getRole() {
		return role;
	}

	public boolean isKeyPosition() {
		return isKeyPosition;
	}

	public double getYearsOfExperience() {
		return yearsOfExperience;
	}

	public String getMandatorySkills() {
		return mandatorySkills;
	}

	public String getOptionalSkills() {
		return optionalSkills;
	}

	public String getDomainExperience() {
		return domainExperience;
	}

	public boolean isClientCommunication() {
		return clientCommunication;
	}

	public String getCertificationRequirement() {
		return certificationRequirement;
	}

	public Date getRequestStartDate() {
		return requestStartDate;
	}

	public Date getAllocationStartDate() {
		return allocationStartDate;
	}

	public Date getAllocationEndDate() {
		return allocationEndDate;
	}

	public double getBillingRate() {
		return billingRate;
	}

	public double getBillingAllocation() {
		return billingAllocation;
	}

	public void setRequestID(int requestID) {
		this.requestID = requestID;
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

	public void setKeyProject(boolean isKeyProject) {
		this.isKeyProject = isKeyProject;
	}

	public void setProjectDomain(String projectDomain) {
		this.projectDomain = projectDomain;
	}

	public void setProjectStartDate(Date projectStardDate) {
		this.projectStartDate = projectStardDate;
	}

	public void setProjectEndDate(Date projectEndDate) {
		this.projectEndDate = projectEndDate;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void setKeyPosition(boolean isKeyPosition) {
		this.isKeyPosition = isKeyPosition;
	}

	public void setYearsOfExperience(double yearsOfExperience) {
		this.yearsOfExperience = Math.round(yearsOfExperience * 100.0) / 100.0;
	}

	public void setMandatorySkills(String mandatorySkills) {
		this.mandatorySkills = mandatorySkills;
	}

	public void setOptionalSkills(String optionalSkills) {
		this.optionalSkills = optionalSkills;
	}

	public void setDomainExperience(String domainExperience) {
		this.domainExperience = domainExperience;
	}

	public void setClientCommunication(boolean clientCommunication) {
		this.clientCommunication = clientCommunication;
	}

	public void setCertificationRequirement(String certificationRequirement) {
		this.certificationRequirement = certificationRequirement;
	}

	public void setRequestStartDate(Date requestStartDate) {
		this.requestStartDate = requestStartDate;
	}

	public void setAllocationStartDate(Date allocationStartDate) {
		this.allocationStartDate = allocationStartDate;
	}

	public void setAllocationEndDate(Date allocationEndDate) {
		this.allocationEndDate = allocationEndDate;
	}

	public void setBillingRate(double billingRate) {
		this.billingRate = billingRate;
	}

	public void setBillingAllocation(double billingAllocation) {
		this.billingAllocation = billingAllocation;
	}

	public Map<String, SkillLevel> getMandatorySkillsMap() {
		return mandatorySkillsMap;
	}

	public void setMandatorySkillsMap(Map<String, SkillLevel> mandatorySkillsMap) {
		this.mandatorySkillsMap = mandatorySkillsMap;
	}

	public List<MatchedResource> getMatchedResources() {
		return matchedResources;
	}

	public void setMatchedResources(List<MatchedResource> matchedResources) {
		this.matchedResources = matchedResources;
	}

	public Project getMyProject() {
		return myProject;
	}

	public void setMyProject(Project myProject) {
		this.myProject = myProject;
	}

}
