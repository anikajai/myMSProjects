package com.nagarro.codingcompetition.pojo;

public class ProspectiveOpening implements Comparable<ProspectiveOpening> {
	private int openingIndex;
	private double matchScore;
	public int getOpeningIndex() {
		return openingIndex;
	}
	public void setOpeningIndex(int openingIndex) {
		this.openingIndex = openingIndex;
	}
	public double getMatchScore() {
		return matchScore;
	}
	public void setMatchScore(double matchScore) {
		this.matchScore = matchScore;
	}
	
	@Override
	public int compareTo(ProspectiveOpening prospectiveOpening) {
		double otherMatchScore= prospectiveOpening.getMatchScore();
		if (matchScore < otherMatchScore) {
			return 1;
		} else if (matchScore > otherMatchScore) {
			return -1;
		} else {
			return 0;
		}
	}

}
