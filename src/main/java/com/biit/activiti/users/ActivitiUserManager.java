package com.biit.activiti.users;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.identity.Picture;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.UserQueryImpl;
import org.activiti.engine.impl.persistence.entity.IdentityInfoEntity;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.impl.persistence.entity.UserEntityManager;
import org.springframework.util.StringUtils;

import com.biit.activiti.groups.ActivitiGroupManager;
import com.biit.activiti.groups.ILiferayToActivityRoleConverter;
import com.biit.activiti.logger.ActivitiUsersLogger;
import com.biit.liferay.access.exceptions.AuthenticationRequired;
import com.biit.liferay.access.exceptions.NotConnectedToWebServiceException;
import com.biit.liferay.access.exceptions.UserDoesNotExistException;
import com.biit.liferay.access.exceptions.WebServiceAccessError;
import com.biit.liferay.security.IAuthenticationService;
import com.biit.liferay.security.IAuthorizationService;
import com.biit.liferay.security.exceptions.InvalidCredentialsException;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;

/**
 * Allows the use of Liferay User in Activiti.
 */
public class ActivitiUserManager extends UserEntityManager {
	private IAuthorizationService authorizationService;
	private IAuthenticationService authenticationService;
	private ILiferayToActivityRoleConverter liferayToActivityConverter;

	public ActivitiUserManager(IAuthorizationService authorizationService,
			IAuthenticationService authenticationService, ILiferayToActivityRoleConverter liferayToActivityConverter) {
		this.authorizationService = authorizationService;
		this.authenticationService = authenticationService;
		this.liferayToActivityConverter = liferayToActivityConverter;
	}

	public static UserEntity getActivitiUser(User liferayUser) {
		if (liferayUser == null) {
			return null;
		}
		UserEntity activitiUser = new UserEntity();
		activitiUser.setEmail(liferayUser.getEmailAddress());
		activitiUser.setFirstName(liferayUser.getFirstName());
		activitiUser.setId(liferayUser.getUserId() + "");
		activitiUser.setLastName(liferayUser.getLastName());
		activitiUser.setPassword(liferayUser.getPassword());
		activitiUser.setPicture(new Picture(null, null));
		activitiUser.setRevision(0);

		return activitiUser;
	}

	@Override
	public UserEntity findUserById(String userId) {
		try {
			User liferayUser = authenticationService.getUserById(Long.parseLong(userId));
			return getActivitiUser(liferayUser);
		} catch (NumberFormatException | NotConnectedToWebServiceException | UserDoesNotExistException | IOException
				| AuthenticationRequired | WebServiceAccessError e) {
			e.printStackTrace();
			ActivitiUsersLogger.errorMessage(this.getClass().getName(), e);
		}
		return null;
	}

	private UserEntity findUserByEmail(String userEmail) {
		try {
			User liferayUser = authenticationService.getUserByEmail(userEmail);
			return getActivitiUser(liferayUser);
		} catch (NumberFormatException | NotConnectedToWebServiceException | UserDoesNotExistException | IOException
				| AuthenticationRequired | WebServiceAccessError e) {
			ActivitiUsersLogger.errorMessage(this.getClass().getName(), e);
		}
		return null;
	}

	@Override
	public boolean isNewUser(org.activiti.engine.identity.User user) {
		return false;
	}

	@Override
	public List<org.activiti.engine.identity.Group> findGroupsByUser(String userId) {
		List<org.activiti.engine.identity.Group> activitiGroups = new ArrayList<>();

		User liferayUser;
		try {
			liferayUser = authenticationService.getUserById(Long.parseLong(userId));
			Set<Role> liferayRoles = authorizationService.getUserRoles(liferayUser);
			for (Role liferayRole : liferayRoles) {
				activitiGroups.add(ActivitiGroupManager.getActivitiGroup(liferayRole, liferayToActivityConverter));
			}
		} catch (NumberFormatException | NotConnectedToWebServiceException | UserDoesNotExistException | IOException
				| AuthenticationRequired | WebServiceAccessError e) {
			ActivitiUsersLogger.errorMessage(this.getClass().getName(), e);
		}
		return activitiGroups;
	}

	@Override
	public Boolean checkPassword(String userId, String password) {
		User liferayUser;
		try {
			liferayUser = authenticationService.getUserById(Long.parseLong(userId));
			return authenticationService.authenticate(liferayUser.getEmailAddress(), password) != null;
		} catch (NumberFormatException | NotConnectedToWebServiceException | UserDoesNotExistException | IOException
				| AuthenticationRequired | WebServiceAccessError | InvalidCredentialsException e) {
			ActivitiUsersLogger.errorMessage(this.getClass().getName(), e);
		}
		return false;
	}

	@Override
	public void insertUser(org.activiti.engine.identity.User user) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateUser(org.activiti.engine.identity.User updatedUser) {
		throw new UnsupportedOperationException();
	}

	@Override
	public UserEntity createNewUser(String userId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteUser(String userId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IdentityInfoEntity findUserInfoByUserIdAndKey(String userId, String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<String> findUserInfoKeysByUserIdAndType(String userId, String type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<org.activiti.engine.identity.User> findUserByQueryCriteria(UserQueryImpl query, Page page) {
		List<org.activiti.engine.identity.User> userList = new ArrayList<org.activiti.engine.identity.User>();
		UserQueryImpl userQuery = (UserQueryImpl) query;
		if (!StringUtils.isEmpty(userQuery.getId())) {
			userList.add(findUserById(userQuery.getId()));
			return userList;
		} else if (!StringUtils.isEmpty(userQuery.getEmail())) {
			userList.add(findUserByEmail(userQuery.getEmail()));
			return userList;
		} else {
			List<User> liferayUsers = authorizationService.getAllUsers();
			for (User liferayUser : liferayUsers) {
				userList.add(getActivitiUser(liferayUser));
			}
			return userList;
		}
	}

	@Override
	public long findUserCountByQueryCriteria(UserQueryImpl query) {
		return findUserByQueryCriteria(query, null).size();
	}

	@Override
	public List<org.activiti.engine.identity.User> findPotentialStarterUsers(String proceDefId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<org.activiti.engine.identity.User> findUsersByNativeQuery(Map<String, Object> parameterMap,
			int firstResult, int maxResults) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long findUserCountByNativeQuery(Map<String, Object> parameterMap) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Picture getUserPicture(String userId) {
		return null;

	}

	@Override
	public void setUserPicture(String userId, Picture picture) {
		throw new UnsupportedOperationException();
	}

	public IAuthorizationService getAuthorizationService() {
		return authorizationService;
	}

	public void setAuthorizationService(IAuthorizationService authorizationService) {
		this.authorizationService = authorizationService;
	}

}
