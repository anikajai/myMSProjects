package com.nagarro.codingcompetition.util;

import java.util.Comparator;

import com.nagarro.codingcompetition.pojo.Project;

public class ProjectComparator implements Comparator<Object> {

	@Override
	public int compare(Object arg0, Object arg1) {
		int returnValue = 0;
		Project project1 = (Project) arg0;
		Project project2 = (Project) arg1;
		double projectScoreMinimumLimit1 = project1.getProjectScoreMinimumLimit();
		double projectScoreMinimumLimit2 = project2.getProjectScoreMinimumLimit();
		if (projectScoreMinimumLimit1 > projectScoreMinimumLimit2) {
			returnValue = 1;
		} else if (projectScoreMinimumLimit1 < projectScoreMinimumLimit2) {
			returnValue = -1;
		}
		return returnValue;
	}

}
