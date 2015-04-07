package com.biit.activiti.groups;

import com.liferay.portal.model.Role;

public interface ILiferayGroupToActivityRoleConverter {

	GroupType getActivitiGroup(Role liferayRole);

}
