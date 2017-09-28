package com.nagarro.codingcompetition.pojo;

public class Dummy implements Comparable<Dummy> {
	private double x;
	private String y;
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public String getY() {
		return y;
	}
	public void setY(String y) {
		this.y = y;
	}
	
	@Override
	public int compareTo(Dummy dummy) {
		double otherMatchScore= dummy.getX();
		if (x > otherMatchScore) {
			return 1;
		} else if (x < otherMatchScore) {
			return -1;
		} else {
			return 0;
		}
	}
}
