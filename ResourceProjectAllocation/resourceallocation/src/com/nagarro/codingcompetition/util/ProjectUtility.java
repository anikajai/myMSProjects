package com.nagarro.codingcompetition.util;

import com.nagarro.codingcompetition.pojo.Opening;
import com.nagarro.codingcompetition.pojo.Project;

public final class ProjectUtility {
	public static void copyProjectDetails(Project projectData, Opening opening) {
		if (null != projectData) {
			projectData.setClientKey(opening.getClientKey());
			projectData.setProjectName(opening.getProjectName());
			projectData.setProjectKey(opening.getProjectKey());
			projectData.setCustomerName(opening.getCustomerName());
		}
	}
}
