package com.nagarro.codingcompetition.enums;

public enum Rating {
	APLUS("A+", 0.2), A("A", 0.1), OTHERS("", 0);

	private Rating(String name, double value) {
		this.name = name;
		this.value = value;
	}

	private String name;
	private double value;

	public String getName() {
		return name;
	}

	public double getValue() {
		return value;
	}

	public static Rating getRatingScoreFromString(String rating) {
		Rating ratingEnumValue = null;
		if (null != rating) {
			Rating[] ratings = Rating.values();
			for (Rating aRating : ratings) {
				if (rating.equalsIgnoreCase(aRating.getName())) {
					ratingEnumValue = aRating;
					break;
				}
			}
		}
		if (null == ratingEnumValue) {
			ratingEnumValue = Rating.OTHERS;
		}
		return ratingEnumValue;
	}
}
