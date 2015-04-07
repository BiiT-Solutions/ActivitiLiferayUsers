package com.biit.activiti.security;

import org.springframework.stereotype.Service;

import com.biit.activiti.groups.GroupType;
import com.biit.activiti.groups.ILiferayToActivityRoleConverter;
import com.liferay.portal.model.Role;

@Service
public class LiferayToActivityConverter implements ILiferayToActivityRoleConverter {

	@Override
	public GroupType getActivitiGroup(Role liferayRole) {
		return GroupType.ASSIGNMENT;
	}

}
