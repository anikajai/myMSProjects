package com.nagarro.codingcompetition.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.nagarro.codingcompetition.enums.SkillLevel;
import com.nagarro.codingcompetition.pojo.MatchedResource;
import com.nagarro.codingcompetition.pojo.Opening;
import com.nagarro.codingcompetition.pojo.OpeningResourcePair;
import com.nagarro.codingcompetition.pojo.ProspectiveOpening;
import com.nagarro.codingcompetition.pojo.Resource;
import com.nagarro.codingcompetition.pojo.TreeLevel;
import com.nagarro.codingcompetition.pojo.TreeNode;

public class AllotmentService {

	// Step A
	public static void matchResourceOpening(List<Opening> openings, List<Resource> resources) {
		for (Opening opening : openings) {
			for (int index = 0; index < resources.size(); index++) {
				Map<String, SkillLevel> requiredSkills = opening.getMandatorySkillsMap();
				Resource resource = resources.get(index);
				Map<String, SkillLevel> resourceSkills = resource.getSkillMap();
				Set<String> requiredSkillNames = requiredSkills.keySet();
				int skillMatchCount = 0;
				for (String requiredSkillName : requiredSkillNames) {
					SkillLevel level = resourceSkills.get(requiredSkillName);
					if (null == level) {
						break;
					} else {
						SkillLevel requiredLevel = requiredSkills.get(requiredSkillName);
						if (level.getValue() >= requiredLevel.getValue()) {
							skillMatchCount++;
						} else {
							break;
						}
					}
				}
				if (skillMatchCount == requiredSkills.size()) {
					Date resourceDate = resource.getAvailableFromDate();
					if (null != resourceDate) {
						int resourceComparedToRequiredDate = resourceDate.compareTo(opening.getRequestStartDate());
						if (resourceComparedToRequiredDate == -1 || resourceComparedToRequiredDate == 0) {
							MatchedResource matchedResource = new MatchedResource();
							String employeeID = resource.getEmployeeID();
							matchedResource.setEmpID(employeeID);
							matchedResource.setScore(1);
							matchedResource.setResource(resource);
							matchedResource.setResourceNumber(index);
							opening.getMatchedResources().add(matchedResource);
						}
					}
				}
			}
		}
	}

	// Step B
	public static void changeMatchScore(List<Opening> openings) {
		for (Opening opening : openings) {
			List<MatchedResource> matchedResources = opening.getMatchedResources();
			for (MatchedResource matchedResource : matchedResources) {
				Resource resource = matchedResource.getResource();

				// NAGP effect.
				if (resource.isNAGP()) {
					matchedResource.setScore(matchedResource.getScore() + 0.3);
				}

				// Rating effect
				matchedResource.setScore(matchedResource.getScore() + resource.getRatingEnum().getValue());

				// Domain match
				String projectDomain = opening.getProjectDomain();
				if (null != projectDomain) {
					Set<String> resourceDomainExperienceSet = resource.getDomainExperienceSet();
					if (null != resourceDomainExperienceSet && resourceDomainExperienceSet.contains(projectDomain)) {
						matchedResource.setScore(matchedResource.getScore() + 0.2);
					}
				}

				// customer match
				String customerName = opening.getCustomerName();
				if (null != customerName) {
					Set<String> previousCustomerExp = resource.getPreviouscustomerExperienceList();
					if (null != previousCustomerExp && previousCustomerExp.contains(customerName)) {
						matchedResource.setScore(matchedResource.getScore() + 0.3);
					}
				}

				// Experience match.
				double requiredYearsOfExp = opening.getYearsOfExperience();
				double resourceYearsOfExp = resource.getYearsOfExperience();
				if (resourceYearsOfExp > requiredYearsOfExp) {
					double diff = (resourceYearsOfExp - requiredYearsOfExp) * 0.05;
					double reductionValue = Math.round(diff * 100.0) / 100.0;
					double finalScore = matchedResource.getScore() - reductionValue;
					matchedResource.setScore(finalScore);
				}
			}
		}
	}

	public static void rejectBasedOnKeyConstraints(List<Opening> openings) {
		for (Opening opening : openings) {
			if (opening.isKeyProject()) {
				if (opening.isKeyPosition()) {
					maximumLimitBasedRejectionOfResources(opening, 2);
				} else {
					maximumLimitBasedRejectionOfResources(opening, 1.3);
				}
			} else {
				if (opening.isKeyPosition()) {
					maximumLimitBasedRejectionOfResources(opening, 1.5);
				} else {
					maximumLimitBasedRejectionOfResources(opening, 1);
				}

			}
			System.out.println(opening.getRequestID() + " " + opening.getMatchedResources().size());
		}
	}

	private static void maximumLimitBasedRejectionOfResources(Opening opening, double limitValue) {
		List<MatchedResource> matchedResources = opening.getMatchedResources();
		for (int index = 0; index < matchedResources.size(); index++) {
			MatchedResource matchedResource = matchedResources.get(index);
			if (matchedResource.getScore() > limitValue) {
				matchedResources.remove(index);
			}
		}
	}

	public static void createMatrix(List<Resource> resources, List<Opening> openings, int numOfOpenings) {

		for (int openingIndex = 0; openingIndex < numOfOpenings; openingIndex++) {
			List<MatchedResource> matchedResources = openings.get(openingIndex).getMatchedResources();
			for (MatchedResource matchedResource : matchedResources) {
				ProspectiveOpening propectiveOpening = new ProspectiveOpening();
				propectiveOpening.setMatchScore(matchedResource.getScore());
				propectiveOpening.setOpeningIndex(openingIndex);
				int resourceIndex = matchedResource.getResourceNumber();
				resources.get(resourceIndex).addToMatchedOpeningList(propectiveOpening);
			}
		}
		// double[][] matrix = new double[20][20];
		// for (int i = 0; i < 20; i++) {
		// for (int j = 0; j< 20;j++) {
		// matrix[i][j] = 0;
		// }
		// }
		// for (int resIndex =0; resIndex < 20; resIndex++) {
		// List<ProspectiveOpening> matchedOpeningList =
		// resources.get(resIndex).getMatchedOpeningList();
		// for (ProspectiveOpening prospectiveOpening : matchedOpeningList) {
		// int index = prospectiveOpening.getOpeningIndex();
		// matrix[resIndex][index] = prospectiveOpening.getMatchScore();
		// }
		// Collections.sort(resources.get(resIndex).getMatchedOpeningList());
		// }

		// for (int resIndex =0; resIndex < 20; resIndex++) {
		// for (int index =0; index < 20;index++) {
		// System.out.print(matrix[resIndex][index] + " ");
		// }
		// System.out.println();
		for (Resource resource : resources) {
			Collections.sort(resource.getMatchedOpeningList());
		}
		// }
	}

	public static void optimisationHeuristic(List<Resource> resources, List<Opening> openings) {
		int numOfResources = resources.size();

		// Level 0
		// double bestScore = bestPossibleScore(resources, 0, null);

		// levels begin
		List<TreeLevel> treeLevels = new ArrayList<TreeLevel>(numOfResources);
		int resourceIndex = 0;
		for (; resourceIndex < numOfResources; resourceIndex++) {
			TreeLevel resourceTreeLevel = new TreeLevel();
			treeLevels.add(resourceTreeLevel);
			int lastTreeLevelIndex = resourceIndex - 1;

			// set previous assignments.
			// indices of consumed openings
			Set<Integer> indicesOfAssignedOpenings = new HashSet<Integer>();
			double inheritedScore = 0;
			if (lastTreeLevelIndex >= 0) {
				TreeLevel lastResourceTreeLevel = treeLevels.get(lastTreeLevelIndex);
				inheritedScore += lastResourceTreeLevel.getPreviousAssignmentsScore();
				for (OpeningResourcePair openingResourcePair : lastResourceTreeLevel.getPreviousAssignments()) {
					resourceTreeLevel.addToPreviousAssignments(openingResourcePair);
					indicesOfAssignedOpenings.add(openingResourcePair.getOpeningIndex());
				}
				List<TreeNode> lastTreeLevelTreeNodes = lastResourceTreeLevel.getTreenodes();
				if (!(lastTreeLevelTreeNodes == null || lastTreeLevelTreeNodes.isEmpty())) {
					TreeNode lastBestScoringTreeNode = lastTreeLevelTreeNodes.get(0); // sorted
					OpeningResourcePair lastOpeningResourcePair = lastBestScoringTreeNode.getOpeningResourcePair();
					resourceTreeLevel.addToPreviousAssignments(lastOpeningResourcePair);
					inheritedScore += lastOpeningResourcePair.getScore();
				}
			}
			resourceTreeLevel.setPreviousAssignmentsScore(inheritedScore);

			List<ProspectiveOpening> matchedOpeningList = resources.get(resourceIndex).getMatchedOpeningList();
			for (ProspectiveOpening prospectiveOpening : matchedOpeningList) {
				int openingIndex = prospectiveOpening.getOpeningIndex();
				if (!indicesOfAssignedOpenings.contains(openingIndex)) {
					TreeNode treeNode = new TreeNode();
					resourceTreeLevel.addTreenode(treeNode);
					OpeningResourcePair proposedOpeningResourcePair = new OpeningResourcePair();
					proposedOpeningResourcePair.setOpeningIndex(openingIndex);
					proposedOpeningResourcePair.setResourceIndex(resourceIndex);
					double proposedOpeningResourceScore = prospectiveOpening.getMatchScore();
					proposedOpeningResourcePair.setScore(proposedOpeningResourceScore);
					treeNode.setOpeningResourcePair(proposedOpeningResourcePair);
					indicesOfAssignedOpenings.add(openingIndex);
					double futureScoreMix = bestPossibleScore(resources, resourceIndex + 1, indicesOfAssignedOpenings);
					treeNode.setCurrentBestScore(futureScoreMix + proposedOpeningResourceScore + inheritedScore);
					indicesOfAssignedOpenings.remove(openingIndex);
				}
			}
			List<TreeNode> treenodes = resourceTreeLevel.getTreenodes();
			if (treenodes != null && !treenodes.isEmpty()) {
				Collections.sort(treenodes);
				// System.out.println("Score:" +
				// treenodes.get(0).getCurrentBestScore());
				// TreeNode treeNode = treenodes.get(0);
				// double currentBestScore = treeNode.getCurrentBestScore();
				// Project project =
				// openings.get(treeNode.getProposedOpeningResourcePair().getOpeningIndex())
				// .getMyProject();
				// Map<String, Double> projectCurrentScoreMap =
				// resourceTreeLevel.getProjectCurrentScoreMap();
				// String projectKey = project.getProjectKey();
				// Double tillLastProjectScore =
				// projectCurrentScoreMap.get(projectKey);
				// Double projectScoreLimit = project.getMaxProjectScoreLimit();
				// double newScore = projectScoreLimit + tillLastProjectScore;
				// if (projectScoreLimit >= newScore) {
				// if (tillLastProjectScore == null) {
				// projectCurrentScoreMap.put(projectKey, currentBestScore);
				// } else {
				// projectCurrentScoreMap.put(projectKey, newScore);
				// }
				// } else {
				// // go to next
				// }
			}
		}
		// TreeNode bestTreeNode = null;
		double displayScore = 0;
		for (int treeLevelIndex = numOfResources - 1; treeLevelIndex >= 0; treeLevelIndex--) {
			TreeLevel treeLevel = treeLevels.get(treeLevelIndex);
			List<TreeNode> treenodes = treeLevel.getTreeNodes();
			if (treenodes == null || treenodes.isEmpty()) {
				continue;
			} else {
				displayScore += treeLevel.getPreviousAssignmentsScore();
				displayScore += treenodes.get(0).getOpeningResourcePair().getScore();
				break;
			}
		}
		// if (null != bestTreeNode) {
		System.out.println("Score:" + displayScore);
		// }

	}

	private static double bestPossibleScore(List<Resource> resources, int resourceStartIndex,
			Set<Integer> indicesOfAssignedOpenings) {
		double totalMaxScore = 0;
		double maximumResourceValue;
		for (Resource resource : resources) {
			maximumResourceValue = 0;
			List<ProspectiveOpening> matchedOpeningList = resource.getMatchedOpeningList();
			for (int prospectiveOpeningIndex = 0; prospectiveOpeningIndex < matchedOpeningList
					.size(); prospectiveOpeningIndex++) {
				ProspectiveOpening prospectiveOpening = matchedOpeningList.get(prospectiveOpeningIndex);
				if (null == indicesOfAssignedOpenings
						|| !indicesOfAssignedOpenings.contains(prospectiveOpening.getOpeningIndex())) {
					maximumResourceValue = prospectiveOpening.getMatchScore();
					break;
				}
			}
			// for (ProspectiveOpening prospectiveOpening : matchedOpeningList)
			// {
			// int openingIndex = prospectiveOpening.getOpeningIndex();
			// if (null == indicesOfAssignedOpenings ||
			// !indicesOfAssignedOpenings.contains(openingIndex)) {
			// double currentValue = prospectiveOpening.getMatchScore();
			// if (currentValue > maximumResourceValue) {
			// maximumResourceValue = currentValue;
			// }
			// }
			// }
			totalMaxScore += maximumResourceValue;
		}
		return Math.round(totalMaxScore * 100.0) / 100.0;
	}
}
