package com.biit.activiti.groups;

import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.persistence.entity.GroupIdentityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.biit.usermanager.security.IAuthenticationService;
import com.biit.usermanager.security.IAuthorizationService;

@Service
public class ActivitiGroupManagerFactory implements SessionFactory {

	@Autowired
	private IAuthorizationService<Long, Long, Long> authorizationService;
	@Autowired
	private IAuthenticationService<Long, Long> authenticationService;
	@Autowired
	private ILiferayGroupToActivityRoleConverter liferayToActivityConverter;

	@Override
	public Class<?> getSessionType() {
		return GroupIdentityManager.class;
	}

	@Override
	public Session openSession() {
		return new ActivitiGroupManager(authorizationService, authenticationService, liferayToActivityConverter);
	}

}
