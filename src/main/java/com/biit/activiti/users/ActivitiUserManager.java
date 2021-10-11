package com.biit.activiti.users;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.identity.Picture;
import org.activiti.engine.identity.UserQuery;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.UserQueryImpl;
import org.activiti.engine.impl.persistence.entity.IdentityInfoEntity;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.impl.persistence.entity.UserEntityImpl;
import org.activiti.engine.impl.persistence.entity.UserEntityManager;
import org.springframework.util.StringUtils;

import com.biit.activiti.groups.ActivitiGroupManager;
import com.biit.activiti.groups.IGroupToActivityRoleConverter;
import com.biit.activiti.logger.ActivitiUsersLogger;
import com.biit.usermanager.entity.IRole;
import com.biit.usermanager.entity.IUser;
import com.biit.usermanager.security.IAuthenticationService;
import com.biit.usermanager.security.IAuthorizationService;
import com.biit.usermanager.security.exceptions.AuthenticationRequired;
import com.biit.usermanager.security.exceptions.InvalidCredentialsException;
import com.biit.usermanager.security.exceptions.UserDoesNotExistException;
import com.biit.usermanager.security.exceptions.UserManagementException;
import com.liferay.portal.model.User;

/**
 * Allows the use of Liferay User in Activiti.
 */
public class ActivitiUserManager implements UserEntityManager {
	private IAuthorizationService<Long, Long, Long> authorizationService;
	private IAuthenticationService<Long, Long> authenticationService;
	private IGroupToActivityRoleConverter groupToActivityConverter;

	public ActivitiUserManager(IAuthorizationService<Long, Long, Long> authorizationService, IAuthenticationService<Long, Long> authenticationService,
			IGroupToActivityRoleConverter groupToActivityConverter) {
		this.authorizationService = authorizationService;
		this.authenticationService = authenticationService;
		this.groupToActivityConverter = groupToActivityConverter;
	}

	public static UserEntity getActivitiUser(IUser<Long> liferayUser) {
		if (liferayUser instanceof User) {
			return getActivitiUser((User) liferayUser);
		}
		return null;
	}

	public static UserEntity getActivitiUser(User liferayUser) {
		if (liferayUser == null) {
			return null;
		}
		UserEntity activitiUser = new UserEntityImpl();
		activitiUser.setEmail(liferayUser.getEmailAddress());
		activitiUser.setFirstName(liferayUser.getFirstName());
		activitiUser.setId(liferayUser.getUniqueId() + "");
		activitiUser.setLastName(liferayUser.getLastName());
		activitiUser.setPassword(liferayUser.getPassword());
		activitiUser.setPicture(new Picture(null, null));
		activitiUser.setRevision(0);

		return activitiUser;
	}


	public UserEntity findUserById(String userId) {
		try {
			IUser<Long> liferayUser = authenticationService.getUserById(Long.parseLong(userId));
			return getActivitiUser(liferayUser);
		} catch (NumberFormatException | UserManagementException e) {
			e.printStackTrace();
			ActivitiUsersLogger.errorMessage(this.getClass().getName(), e);
		}
		return null;
	}

	private UserEntity findUserByEmail(String userEmail) {
		try {
			IUser<Long> liferayUser = authenticationService.getUserByEmail(userEmail);
			return getActivitiUser(liferayUser);
		} catch (NumberFormatException | UserManagementException | UserDoesNotExistException e) {
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

		IUser<Long> liferayUser;
		try {
			liferayUser = authenticationService.getUserById(Long.parseLong(userId));
			Set<IRole<Long>> liferayRoles = authorizationService.getUserRoles(liferayUser);
			for (IRole<Long> liferayRole : liferayRoles) {
				activitiGroups.add(ActivitiGroupManager.getActivitiGroup(liferayRole, groupToActivityConverter));
			}
		} catch (NumberFormatException | UserManagementException e) {
			ActivitiUsersLogger.errorMessage(this.getClass().getName(), e);
		}
		return activitiGroups;
	}

	@Override
	public UserQuery createNewUserQuery() {
		return null;
	}

	@Override
	public Boolean checkPassword(String userId, String password) {
		IUser<Long> liferayUser;
		try {
			liferayUser = authenticationService.getUserById(Long.parseLong(userId));
			return authenticationService.authenticate(liferayUser.getEmailAddress(), password) != null;
		} catch (NumberFormatException | InvalidCredentialsException | UserManagementException | AuthenticationRequired | UserDoesNotExistException e) {
			ActivitiUsersLogger.errorMessage(this.getClass().getName(), e);
		}
		return false;
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
	public List<org.activiti.engine.identity.User> findUserByQueryCriteria(UserQueryImpl query, Page page) {
		List<org.activiti.engine.identity.User> userList = new ArrayList<>();
		UserQueryImpl userQuery = query;
		if (!StringUtils.isEmpty(userQuery.getId())) {
			userList.add(findUserById(userQuery.getId()));
			return userList;
		} else if (!StringUtils.isEmpty(userQuery.getEmail())) {
			userList.add(findUserByEmail(userQuery.getEmail()));
			return userList;
		} else {
			Set<IUser<Long>> liferayUsers;
			try {
				liferayUsers = authorizationService.getAllUsers();
				for (IUser<Long> liferayUser : liferayUsers) {
					userList.add(getActivitiUser(liferayUser));
				}
				return userList;
			} catch (UserManagementException e) {
				ActivitiUsersLogger.errorMessage(this.getClass().getName(), e);
			}
		}
		return null;
	}

	@Override
	public long findUserCountByQueryCriteria(UserQueryImpl query) {
		return findUserByQueryCriteria(query, null).size();
	}


	@Override
	public List<org.activiti.engine.identity.User> findUsersByNativeQuery(Map<String, Object> parameterMap, int firstResult, int maxResults) {
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

	@Override
	public void deletePicture(org.activiti.engine.identity.User user) {
		throw new UnsupportedOperationException();
	}

	public IAuthorizationService<Long, Long, Long> getAuthorizationService() {
		return authorizationService;
	}

	public void setAuthorizationService(IAuthorizationService<Long, Long, Long> authorizationService) {
		this.authorizationService = authorizationService;
	}

	@Override
	public UserEntity create() {
		throw new UnsupportedOperationException();
	}

	@Override
	public UserEntity findById(String userId) {
		try {
			IUser<Long> liferayUser = authenticationService.getUserById(Long.parseLong(userId));
			return getActivitiUser(liferayUser);
		} catch (NumberFormatException | UserManagementException e) {
			e.printStackTrace();
			ActivitiUsersLogger.errorMessage(this.getClass().getName(), e);
		}
		return null;
	}

	@Override
	public void insert(UserEntity entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void insert(UserEntity entity, boolean b) {
		throw new UnsupportedOperationException();
	}

	@Override
	public UserEntity update(UserEntity entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public UserEntity update(UserEntity entity, boolean b) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(String s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(UserEntity entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(UserEntity entity, boolean b) {
		throw new UnsupportedOperationException();
	}
}
