package com.biit.activiti.security;

import org.springframework.stereotype.Service;

import com.biit.liferay.security.AuthenticationService;

@Service
public class BasicAuthenticationService extends AuthenticationService {

	public BasicAuthenticationService() {
		super();
	}
}
