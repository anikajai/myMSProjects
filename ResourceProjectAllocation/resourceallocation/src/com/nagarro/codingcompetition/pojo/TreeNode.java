package com.nagarro.codingcompetition.pojo;

public class TreeNode implements Comparable<TreeNode> {

	private double currentBestScore;
	private OpeningResourcePair openingResourcePair;

	@Override
	public int compareTo(TreeNode anotherTreeNode) {
		double otherTreeNodeScore = anotherTreeNode.getCurrentBestScore();
		if (otherTreeNodeScore < currentBestScore) {
			return 1;
		} else if (otherTreeNodeScore > currentBestScore) {
			return -1;
		} else {
			return 0;
		}
	}

	public double getCurrentBestScore() {
		return currentBestScore;
	}

	public void setCurrentBestScore(double currentBestScore) {
		this.currentBestScore = currentBestScore;
	}

	public OpeningResourcePair getOpeningResourcePair() {
		return openingResourcePair;
	}

	public void setOpeningResourcePair(OpeningResourcePair openingResourcePair) {
		this.openingResourcePair = openingResourcePair;
	}
}
