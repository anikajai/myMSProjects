package com.nagarro.codingcompetition.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.nagarro.codingcompetition.enums.Rating;
import com.nagarro.codingcompetition.enums.SkillLevel;
import com.nagarro.codingcompetition.openings.ResourceData;
import com.nagarro.codingcompetition.openings.ResourceData.Record;
import com.nagarro.codingcompetition.pojo.Opening;
import com.nagarro.codingcompetition.pojo.Resource;
import com.nagarro.codingcompetition.util.StringUtility;

public class OpeningsResourcePreprocessor {

	public static void mapper(List<Opening> openings, List<Resource> resources, ResourceData openingSet,
			com.nagarro.codingcompetition.resources.ResourceData resourceSet) {
		List<Record> openingRecords = openingSet.getRecord();
		for (Record record : openingRecords) {
			Opening opening = new Opening();
			opening.setRequestID(record.getRequestID());
			opening.setClientKey(record.getClientKey());
			opening.setProjectKey(record.getProjectKey());
			opening.setProjectName(record.getProjectName());
			opening.setProjectStartDate(record.getProjectStartDate());
			opening.setProjectEndDate(record.getProjectEndDate());
			opening.setCustomerName(record.getCustomerName());
			String isKeyProject = record.getIsKeyProject();
			boolean keyProject = StringUtility.checkTrueFalse(isKeyProject);
			opening.setKeyProject(keyProject);
			opening.setProjectDomain(record.getProjectDomain());
			opening.setRole(record.getRole());
			String isKeyPosition = record.getIsKeyPosition();
			boolean keyPosition = StringUtility.checkTrueFalse(isKeyPosition);
			opening.setKeyPosition(keyPosition);
			opening.setYearsOfExperience(record.getYearsOfExperience().doubleValue());
			opening.setMandatorySkills(record.getMandatorySkills());
			opening.setClientCommunication(StringUtility.checkTrueFalse(record.getClientCommunication()));
			opening.setRequestStartDate(record.getRequestStartDate());
			opening.setAllocationEndDate(record.getAllocationEndDate());
			openings.add(opening);
		}
		List<com.nagarro.codingcompetition.resources.ResourceData.Record> resourceRecords = resourceSet.getRecord();
		for (com.nagarro.codingcompetition.resources.ResourceData.Record record : resourceRecords) {
			Resource resource = new Resource();
			resource.setAvailableFromDate(record.getAvailableFromDate());
			resource.setEmployeeID(record.getEmployeeID());
			resource.setDoj(record.getDOJ());
			resource.setDomainExperience(record.getDomainExperience());
			resource.setSkills(record.getSkills());
			resource.setRating(record.getRating());
			resource.setCommunicationRating(record.getCommunicationsRating());
			resource.setNAGP(StringUtility.checkTrueFalse(record.getNAGP()));
			resource.setYearsOfExperience(record.getYearsOfExperience().doubleValue());
			resource.setCurrentRole(record.getCurrentRole());
			resource.setPerviousCustomerExperience(record.getPreviousCustomerExperience());
			resources.add(resource);
		}
	}

	public static void openingsProcessor(List<Opening> openings) {
		for (Opening aOpening : openings) {

			// Opening's mandatory skills
			openingsSkillProcessor(aOpening);
		}
	}

	public static void resourcesProcessor(List<Resource> resources) {
		for (Resource aResource : resources) {

			// Resource skills
			resourcesSkillProcessor(aResource);

			// Resource domain experience
			String domain = aResource.getDomainExperience();
			Set<String> domainSet = domainProcessor(domain);
			aResource.setDomainExperienceSet(domainSet);

			// Resource customer experience
			String previousCustomerExperience = aResource.getPerviousCustomerExperience();
			Set<String> previousCustomerExperienceSet = domainProcessor(previousCustomerExperience);
			aResource.setPreviouscustomerExperienceSet(previousCustomerExperienceSet);

			// Rating Enum
			Rating ratingEnum = Rating.getRatingScoreFromString(aResource.getRating());
			aResource.setRatingEnum(ratingEnum);

		}
	}

	public static Set<String> domainProcessor(String domain) {
		List<String> domainList = StringUtility.tokeniseString(domain);
		Set<String> domainSet = new HashSet<String>();
		domainSet.addAll(domainList);
		return domainSet;
	}

	public static void openingsSkillProcessor(Opening aOpening) {
		String mandatorySkills = aOpening.getMandatorySkills();
		Map<String, SkillLevel> mandatorySkillsMap = aOpening.getMandatorySkillsMap();
		skillSetProcessor(mandatorySkills, mandatorySkillsMap);
	}

	public static void resourcesSkillProcessor(Resource aResource) {
		String skills = aResource.getSkills();
		Map<String, SkillLevel> skillMap = aResource.getSkillMap();
		skillSetProcessor(skills, skillMap);
	}

	public static void skillSetProcessor(final String mandatorySkills, Map<String, SkillLevel> skillMap) {
		List<String> tokenizedMandatorySkills = StringUtility.tokeniseString(mandatorySkills);
		for (String aSkill : tokenizedMandatorySkills) {
			List<String> tokenizedSkill = StringUtility.tokeniseString(aSkill, "-");
			String currentSkillLevel = tokenizedSkill.get(1);
			SkillLevel level = SkillLevel.getSkillLevelFromString(currentSkillLevel);
			skillMap.put(tokenizedSkill.get(0), level);
		}
	}
}
