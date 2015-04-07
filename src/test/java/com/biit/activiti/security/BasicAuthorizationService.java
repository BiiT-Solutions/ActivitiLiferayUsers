package com.biit.activiti.security;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.biit.liferay.security.AuthorizationService;
import com.biit.liferay.security.IActivity;
import com.liferay.portal.model.Role;

@Service
public class BasicAuthorizationService extends AuthorizationService {

	@Override
	public Set<IActivity> getRoleActivities(Role role) {
		return new HashSet<>();
	}

}
