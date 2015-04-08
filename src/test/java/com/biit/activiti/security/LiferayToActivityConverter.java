package com.biit.activiti.security;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.biit.activiti.groups.GroupType;
import com.biit.activiti.groups.ILiferayGroupToActivityRoleConverter;
import com.biit.activiti.logger.ActivitiUsersLogger;
import com.biit.liferay.access.exceptions.AuthenticationRequired;
import com.biit.liferay.security.IAuthorizationService;
import com.liferay.portal.model.Role;

@Service
public class LiferayToActivityConverter implements ILiferayGroupToActivityRoleConverter {

	@Autowired
	private IAuthorizationService authorizationService;

	@Override
	public GroupType getActivitiGroup(Role liferayRole) {
		return TestRole.get(liferayRole.getName()).getActivitiGroup();
	}

	@Override
	public Set<Role> getRoles(GroupType type) {
		Set<Role> roles = new HashSet<>();
		for (TestRole testRole : TestRole.get(type)) {
			try {
				roles.add(authorizationService.getRole(testRole.getLiferayName()));
			} catch (IOException | AuthenticationRequired e) {
				ActivitiUsersLogger.errorMessage(this.getClass().getName(), e);
			}
		}
		return roles;
	}

	@Override
	public Set<Role> getAllRoles() {
		Set<Role> roles = new HashSet<>();
		for (TestRole role : TestRole.values()) {
			try {
				roles.add(authorizationService.getRole(role.getLiferayName()));
			} catch (IOException | AuthenticationRequired e) {
				ActivitiUsersLogger.errorMessage(this.getClass().getName(), e);
			}
		}
		return roles;
	}

}
