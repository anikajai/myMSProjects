package com.nagarro.codingcompetition.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.nagarro.codingcompetition.pojo.Opening;
import com.nagarro.codingcompetition.pojo.Project;
import com.nagarro.codingcompetition.util.ProjectUtility;

public class ProjectOpeningsDataIntegrator {

	public static void projectOpeningsIntegration(List<Opening> openings, Map<String, Project> projectOpeningsMap) {
		for (Opening opening : openings) {
			String projectKey = opening.getProjectKey();
			Project projectData;
			if (null == projectOpeningsMap.get(projectKey)) {
				projectData = new Project();
				ProjectUtility.copyProjectDetails(projectData, opening);
				Map<Integer, Opening> projectOpenings = new HashMap<Integer, Opening>();
				projectOpenings.put(opening.getRequestID(), opening);
				projectData.increaseTotalNumOfOpenings();
				projectData.setProjectOpenings(projectOpenings);
				projectOpeningsMap.put(projectKey, projectData);
			} else {
				projectData = projectOpeningsMap.get(projectKey);
				projectData.increaseTotalNumOfOpenings();
				Map<Integer, Opening> projectOpenings = projectData.getProjectOpenings();
				projectOpenings.put(opening.getRequestID(), opening);
				projectData.setProjectOpenings(projectOpenings); // i feel its
																	// useless.
			}
		}

		// set project limit score
		Set<String> projectKeySet = projectOpeningsMap.keySet();
		for (String projectKey : projectKeySet) {
			Project project = projectOpeningsMap.get(projectKey);
			double maxLimitScore = project.getTotalNumOfOpenings() * 1.5;
			project.setMaxProjectScoreLimit(maxLimitScore);
		}
	}

	// private void calculateKeyBasedGeneratedScore(final ProjectAllocationData
	// projectAllocationData) {
	// if (projectAllocationData.isKeyProject()) {
	// double keyPositionScore = projectAllocationData.getNumOfKeyOpenings()
	// * ProjectConstants.KEY_PROJECT_KEY_POSITION_SCORE;
	// double nonKeyPositionScore =
	// projectAllocationData.getNumOfNonKeyOpenings()
	// * ProjectConstants.KEY_PROJECT_NON_KEY_POSITION_SCORE;
	// projectAllocationData.setKeyBasedGeneratedScore(keyPositionScore +
	// nonKeyPositionScore);
	// } else {
	// double keyPositionScore = projectAllocationData.getNumOfKeyOpenings()
	// * ProjectConstants.NON_KEY_PROJECT_KEY_POSITION_SCORE;
	// double nonKeyPositionScore =
	// projectAllocationData.getNumOfNonKeyOpenings()
	// * ProjectConstants.NON_KEY_PROJECT_NON_KEY_POSITION_SCORE;
	// projectAllocationData.setKeyBasedGeneratedScore(keyPositionScore +
	// nonKeyPositionScore);
	// }
	// }
}
