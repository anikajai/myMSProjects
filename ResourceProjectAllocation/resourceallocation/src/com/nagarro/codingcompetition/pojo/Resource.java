package com.nagarro.codingcompetition.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.nagarro.codingcompetition.enums.Rating;
import com.nagarro.codingcompetition.enums.SkillLevel;

public class Resource {
	private int sno;
	private String employeeID;
	private String employeeName;
	private Date doj;
	private String skills;
	private String domainExperience;
	private String rating;
	private String communicationRating;
	private boolean isNAGP;
	private String certifications;
	private double yearsOfExperience;
	private String currentRole;
	private String perviousCustomerExperience;
	private double costPerHour;
	private Date availableFromDate;

	private Map<String, SkillLevel> skillMap = new HashMap<String, SkillLevel>();
	private Set<String> previouscustomerExperienceSet = new HashSet<String>();
	private Set<String> domainExperienceSet;
	private Rating ratingEnum;

	List<ProspectiveOpening> matchedOpeningList = new ArrayList<ProspectiveOpening>();

	public List<ProspectiveOpening> getMatchedOpeningList() {
		return matchedOpeningList;
	}
	
	public void addToMatchedOpeningList(ProspectiveOpening prospectiveOpening) {
		matchedOpeningList.add(prospectiveOpening);
	}

	public void setMatchedOpeningList(List<ProspectiveOpening> matchedOpeningList) {
		this.matchedOpeningList = matchedOpeningList;
	}

	public Set<String> getPreviouscustomerExperienceSet() {
		return previouscustomerExperienceSet;
	}

	public int getSno() {
		return sno;
	}

	public String getEmployeeID() {
		return employeeID;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public Date getDoj() {
		return doj;
	}

	public String getSkills() {
		return skills;
	}

	public String getDomainExperience() {
		return domainExperience;
	}

	public String getRating() {
		return rating;
	}

	public String getCommunicationRating() {
		return communicationRating;
	}

	public boolean isNAGP() {
		return isNAGP;
	}

	public String getCertifications() {
		return certifications;
	}

	public double getYearsOfExperience() {
		return yearsOfExperience;
	}

	public String getCurrentRole() {
		return currentRole;
	}

	public String getPerviousCustomerExperience() {
		return perviousCustomerExperience;
	}

	public double getCostPerHour() {
		return costPerHour;
	}

	public Date getAvailableFromDate() {
		return availableFromDate;
	}

	public void setSno(int sno) {
		this.sno = sno;
	}

	public void setEmployeeID(String employeeID) {
		this.employeeID = employeeID;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public void setDoj(Date doj) {
		this.doj = doj;
	}

	public void setSkills(String skills) {
		this.skills = skills;
	}

	public void setDomainExperience(String domainExperience) {
		this.domainExperience = domainExperience;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public void setCommunicationRating(String communicationRating) {
		this.communicationRating = communicationRating;
	}

	public void setNAGP(boolean isNAGP) {
		this.isNAGP = isNAGP;
	}

	public void setCertifications(String certifications) {
		this.certifications = certifications;
	}

	public void setYearsOfExperience(double yearsOfExperience) {
		this.yearsOfExperience = Math.round(yearsOfExperience * 100.0) / 100.0;
	}

	public void setCurrentRole(String currentRole) {
		this.currentRole = currentRole;
	}

	public void setPerviousCustomerExperience(String perviousCustomerExperience) {
		this.perviousCustomerExperience = perviousCustomerExperience;
	}

	public void setCostPerHour(double costPerHour) {
		this.costPerHour = costPerHour;
	}

	public void setAvailableFromDate(Date availableFromDate) {
		this.availableFromDate = availableFromDate;
	}

	public Rating getRatingEnum() {
		return ratingEnum;
	}

	public void setRatingEnum(Rating ratingEnum) {
		this.ratingEnum = ratingEnum;
	}

	public Map<String, SkillLevel> getSkillMap() {
		return skillMap;
	}

	public Set<String> getPreviouscustomerExperienceList() {
		return previouscustomerExperienceSet;
	}

	public Set<String> getDomainExperienceSet() {
		return domainExperienceSet;
	}

	public void setSkillMap(Map<String, SkillLevel> skillMap) {
		this.skillMap = skillMap;
	}

	public void setPreviouscustomerExperienceSet(Set<String> previouscustomerExperienceList) {
		this.previouscustomerExperienceSet = previouscustomerExperienceList;
	}

	public void setDomainExperienceSet(Set<String> domainExperienceSet) {
		this.domainExperienceSet = domainExperienceSet;
	}
}
