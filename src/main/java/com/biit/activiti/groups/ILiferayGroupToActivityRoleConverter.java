package com.biit.activiti.groups;

import java.util.Set;

import com.liferay.portal.model.Role;

public interface ILiferayGroupToActivityRoleConverter {

	GroupType getActivitiGroup(Role liferayRole);

	Set<Role> getAllRoles();

	Set<Role> getRoles(GroupType type);

}
