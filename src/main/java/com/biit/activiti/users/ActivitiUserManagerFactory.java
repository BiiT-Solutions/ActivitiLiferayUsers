package com.biit.activiti.users;

import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.persistence.entity.UserIdentityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.biit.activiti.groups.ILiferayGroupToActivityRoleConverter;
import com.biit.liferay.security.IAuthenticationService;
import com.biit.liferay.security.IAuthorizationService;

@Service
public class ActivitiUserManagerFactory implements SessionFactory {

	@Autowired
	private IAuthorizationService authorizationService;
	@Autowired
	private IAuthenticationService authenticationService;
	@Autowired
	private ILiferayGroupToActivityRoleConverter liferayToActivityConverter;

	@Override
	public Class<?> getSessionType() {
		return UserIdentityManager.class;
	}

	@Override
	public Session openSession() {
		return new ActivitiUserManager(authorizationService, authenticationService, liferayToActivityConverter);
	}
}