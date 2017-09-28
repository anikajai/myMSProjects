package com.nagarro.codingcompetition.enums;

public enum SkillLevel {

	BEGINNER("Beginner", 1), INTERMEDIATE("Intermediate", 2), EXPERT("Expert", 3);

	private SkillLevel(String name, int value) {
		this.name = name;
		this.value = value;
	}

	private int value;
	private String name;

	public int getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

	public static SkillLevel getSkillLevelFromString(String currentSkillLevel) {
		SkillLevel skillLevel = null;
		if (null != currentSkillLevel) {
			SkillLevel[] skillLevels = SkillLevel.values();
			for (SkillLevel aSkillLevel : skillLevels) {
				if (currentSkillLevel.equalsIgnoreCase(aSkillLevel.getName())) {
					skillLevel = aSkillLevel;
					break;
				}
			}
		}
		return skillLevel;
	}
}
