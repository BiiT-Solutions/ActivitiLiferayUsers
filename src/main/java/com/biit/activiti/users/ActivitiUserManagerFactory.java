package com.biit.activiti.users;

import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.persistence.entity.UserEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.biit.activiti.groups.IGroupToActivityRoleConverter;
import com.biit.usermanager.security.IAuthenticationService;
import com.biit.usermanager.security.IAuthorizationService;

@Service
public class ActivitiUserManagerFactory implements SessionFactory {

	@Autowired
	private IAuthorizationService<Long, Long, Long> authorizationService;
	@Autowired
	private IAuthenticationService<Long, Long> authenticationService;
	@Autowired
	private IGroupToActivityRoleConverter groupToActivityConverter;

	@Override
	public Class<?> getSessionType() {
		return UserEntityManager.class;
	}


	public Session openSession() {
		return (Session) new ActivitiUserManager(authorizationService, authenticationService, groupToActivityConverter);
	}

	@Override
	public Session openSession(CommandContext commandContext) {
		return (Session) new ActivitiUserManager(authorizationService, authenticationService, groupToActivityConverter);
	}
}