package com.biit.activiti.security;

import java.util.List;
import java.util.Set;

import org.activiti.engine.identity.User;

import com.liferay.portal.model.Role;

public interface ActivitiLiferayAuthorizationService {

	Set<Role> getLiferayUserRoles(User liferayUser);

	List<User> getAllLiferayUsers();

}
