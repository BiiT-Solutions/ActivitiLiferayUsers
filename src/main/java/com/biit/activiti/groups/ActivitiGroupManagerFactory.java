package com.biit.activiti.groups;

import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.persistence.entity.GroupEntityManager;
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
	private IGroupToActivityRoleConverter groupToActivityConverter;

	@Override
	public Class<?> getSessionType() {
		return GroupEntityManager.class;
	}


	public Session openSession() {
		return (Session) new ActivitiGroupManager(authorizationService, authenticationService, groupToActivityConverter);
	}

	@Override
	public Session openSession(CommandContext commandContext) {
		return (Session) new ActivitiGroupManager(authorizationService, authenticationService, groupToActivityConverter);
	}
}
