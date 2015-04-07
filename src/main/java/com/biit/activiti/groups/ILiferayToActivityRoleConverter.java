package com.biit.activiti.groups;

import com.liferay.portal.model.Role;

public interface ILiferayToActivityRoleConverter {

	GroupType getActivitiGroup(Role liferayRole);

}
