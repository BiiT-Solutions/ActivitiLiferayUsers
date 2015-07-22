package com.biit.activiti.security;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.biit.liferay.security.AuthorizationService;
import com.biit.usermanager.entity.IRole;
import com.biit.usermanager.security.IActivity;

@Service
public class BasicAuthorizationService extends AuthorizationService {

	@Override
	public Set<IActivity> getRoleActivities(IRole<Long> role) {
		return new HashSet<>();
	}

}
