package com.biit.activiti.groups;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.activiti.engine.identity.GroupQuery;
import org.activiti.engine.impl.GroupQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.GroupEntityManager;

import com.biit.activiti.logger.ActivitiUsersLogger;
import com.biit.liferay.access.exceptions.AuthenticationRequired;
import com.biit.liferay.access.exceptions.NotConnectedToWebServiceException;
import com.biit.liferay.access.exceptions.UserDoesNotExistException;
import com.biit.liferay.access.exceptions.WebServiceAccessError;
import com.biit.liferay.security.IAuthenticationService;
import com.biit.liferay.security.IAuthorizationService;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;

/**
 * Allows the use of Liferay Roles as Activiti groups.
 */
public class ActivitiGroupManager extends GroupEntityManager {

	private IAuthorizationService authorizationService;
	private IAuthenticationService authenticationService;
	private ILiferayGroupToActivityRoleConverter liferayToActivityConverter;

	public ActivitiGroupManager(IAuthorizationService authorizationService,
			IAuthenticationService authenticationService, ILiferayGroupToActivityRoleConverter liferayToActivityConverter) {
		this.authorizationService = authorizationService;
		this.authenticationService = authenticationService;
		this.liferayToActivityConverter = liferayToActivityConverter;
	}

	public static GroupEntity getActivitiGroup(Role liferayRole, ILiferayGroupToActivityRoleConverter liferayToActivity) {
		GroupEntity activitiGroup = new GroupEntity();
		activitiGroup.setName(liferayRole.getName());
		activitiGroup.setType(liferayToActivity.getActivitiGroup(liferayRole).getType());
		activitiGroup.setId(liferayRole.getRoleId() + "");
		activitiGroup.setRevision(0);

		return activitiGroup;
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
	public org.activiti.engine.identity.Group createNewGroup(String groupId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void insertGroup(org.activiti.engine.identity.Group group) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateGroup(org.activiti.engine.identity.Group updatedGroup) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteGroup(String groupId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public GroupQuery createNewGroupQuery() {
		return super.createNewGroupQuery();
	}

	@Override
	public List<org.activiti.engine.identity.Group> findGroupByQueryCriteria(GroupQueryImpl query, Page page) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long findGroupCountByQueryCriteria(GroupQueryImpl query) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isNewGroup(org.activiti.engine.identity.Group activitiGroup) {
		return false;
	}

	public IAuthorizationService getAuthorizationService() {
		return authorizationService;
	}

	public void setAuthorizationService(IAuthorizationService authorizationService) {
		this.authorizationService = authorizationService;
	}

}
