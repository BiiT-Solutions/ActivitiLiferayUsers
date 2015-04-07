package com.biit.activiti.groups;

public enum GroupType {

	SECURITY_ROLE("security-role"),

	ASSIGNMENT("assignment");

	private String type;

	GroupType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
