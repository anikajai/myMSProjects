package com.nagarro.codingcompetition.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TreeLevel {

	private List<OpeningResourcePair> previousAssignments = new ArrayList<OpeningResourcePair>(1);
	private double previousAssignmentsScore = 0;
//	private double currentChosenScore = 0;
	private List<TreeNode> treeNodes = new LinkedList<TreeNode>();

	public double getPreviousAssignmentsScore() {
		return previousAssignmentsScore;
	}

	public void setPreviousAssignmentsScore(double previousAssignmentsScore) {
		this.previousAssignmentsScore = previousAssignmentsScore;
	}

//	public double getCurrentChosenScore() {
//		return currentChosenScore;
//	}
//
//	public void setCurrentChosenScore(double currentChosenScore) {
//		this.currentChosenScore = currentChosenScore;
//	}

	public List<TreeNode> getTreeNodes() {
		return treeNodes;
	}

	public void setTreeNodes(List<TreeNode> treeNodes) {
		this.treeNodes = treeNodes;
	}

	Map<String, Double> projectCurrentScoreMap = new HashMap<String, Double>();

	public Map<String, Double> getProjectCurrentScoreMap() {
		return projectCurrentScoreMap;
	}

	public void setProjectCurrentScoreMap(Map<String, Double> projectCurrentScore) {
		this.projectCurrentScoreMap = projectCurrentScore;
	}

	public List<OpeningResourcePair> getPreviousAssignments() {
		return previousAssignments;
	}

	public void setPreviousAssignments(List<OpeningResourcePair> previousAssignments) {
		this.previousAssignments = previousAssignments;
	}

	public List<TreeNode> getTreenodes() {
		return treeNodes;
	}

	public void addTreenode(TreeNode treeNode) {
		treeNodes.add(treeNode);
	}

	public void setTreenodes(List<TreeNode> treenodes) {
		this.treeNodes = treenodes;
	}

	public void addToPreviousAssignments(OpeningResourcePair pair) {
		previousAssignments.add(pair);
	}

	public void addAllToPreviousAssignments(List<OpeningResourcePair> pairs) {
		previousAssignments.addAll(pairs);
	}
}
