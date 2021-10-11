package com.biit.activiti.groups;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.activiti.engine.impl.GroupQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.GroupEntityImpl;
import org.activiti.engine.impl.persistence.entity.GroupEntityManager;
import org.activiti.engine.impl.persistence.entity.GroupEntityManagerImpl;
import org.springframework.util.StringUtils;

import com.biit.activiti.logger.ActivitiUsersLogger;
import com.biit.usermanager.entity.IRole;
import com.biit.usermanager.entity.IUser;
import com.biit.usermanager.security.IAuthenticationService;
import com.biit.usermanager.security.IAuthorizationService;
import com.biit.usermanager.security.exceptions.RoleDoesNotExistsException;
import com.biit.usermanager.security.exceptions.UserManagementException;

/**
 * Allows the use of Liferay Roles as Activiti groups.
 */
public class ActivitiGroupManager implements GroupEntityManager {

	private IAuthorizationService<Long, Long, Long> authorizationService;
	private IAuthenticationService<Long, Long> authenticationService;
	private IGroupToActivityRoleConverter groupToActivityConverter;

	public ActivitiGroupManager(IAuthorizationService<Long, Long, Long> authorizationService, IAuthenticationService<Long, Long> authenticationService,
			IGroupToActivityRoleConverter groupToActivityConverter) {
		this.authorizationService = authorizationService;
		this.authenticationService = authenticationService;
		this.groupToActivityConverter = groupToActivityConverter;
	}

	public static GroupEntity getActivitiGroup(IRole<Long> liferayRole, IGroupToActivityRoleConverter liferayToActivity) {
		GroupEntity activitiGroup = new GroupEntityImpl();
		activitiGroup.setName(liferayToActivity.getGroupName(liferayRole));
		activitiGroup.setType(liferayToActivity.getActivitiGroup(liferayRole).getType());
		activitiGroup.setId(liferayRole.getUniqueId() + "");
		activitiGroup.setRevision(0);

		return activitiGroup;
	}

	public GroupEntity findGroupById(String roleId) {
		try {
			IRole<Long> liferayUser = authorizationService.getRole(Long.parseLong(roleId));
			return getActivitiGroup(liferayUser, groupToActivityConverter);
		} catch (NumberFormatException | UserManagementException | RoleDoesNotExistsException e) {
			ActivitiUsersLogger.errorMessage(this.getClass().getName(), e);
		}
		return null;
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
	public List<Group> findGroupsByNativeQuery(Map<String, Object> map, int i, int i1) {
		return null;
	}

	@Override
	public long findGroupCountByNativeQuery(Map<String, Object> map) {
		return 0;
	}

	@Override
	public org.activiti.engine.identity.Group createNewGroup(String groupId) {
		throw new UnsupportedOperationException();
	}


	@Override
	public GroupQuery createNewGroupQuery() {
		return null;
		//return GroupEntityManagerImpl.createNewGroupQuery();
	}

	@Override
	public List<org.activiti.engine.identity.Group> findGroupByQueryCriteria(GroupQueryImpl query, Page page) {
		List<org.activiti.engine.identity.Group> groupList = new ArrayList<org.activiti.engine.identity.Group>();
		GroupQueryImpl groupQuery = (GroupQueryImpl) query;
		if (!StringUtils.isEmpty(groupQuery.getId())) {
			groupList.add(findGroupById(groupQuery.getId()));
			return groupList;
		}
		if (!StringUtils.isEmpty(groupQuery.getName())) {
			try {
				groupList.add(getActivitiGroup(authorizationService.getRole(groupToActivityConverter.getRoleName(groupQuery.getName())),
						groupToActivityConverter));
			} catch (UserManagementException | RoleDoesNotExistsException e) {
				ActivitiUsersLogger.errorMessage(this.getClass().getName(), e);
			}
			return groupList;
		} else if (!StringUtils.isEmpty(groupQuery.getUserId())) {
			groupList.addAll(findGroupsByUser(groupQuery.getUserId()));
			return groupList;
		} else if (!StringUtils.isEmpty(groupQuery.getType())) {
			Set<IRole<Long>> roles = groupToActivityConverter.getRoles(GroupType.getGroupType(groupQuery.getType()));
			for (IRole<Long> role : roles) {
				groupList.add(getActivitiGroup(role, groupToActivityConverter));
			}
			return groupList;
		} else {
			Set<IRole<Long>> liferayRoles = groupToActivityConverter.getAllRoles();
			for (IRole<Long> liferayRole : liferayRoles) {
				groupList.add(getActivitiGroup(liferayRole, groupToActivityConverter));
			}
			return groupList;
		}
	}

	@Override
	public long findGroupCountByQueryCriteria(GroupQueryImpl query) {
		return findGroupByQueryCriteria(query, null).size();
	}

	@Override
	public boolean isNewGroup(org.activiti.engine.identity.Group activitiGroup) {
		return false;
	}

	public IAuthorizationService<Long, Long, Long> getAuthorizationService() {
		return authorizationService;
	}

	public void setAuthorizationService(IAuthorizationService<Long, Long, Long> authorizationService) {
		this.authorizationService = authorizationService;
	}

	@Override
	public GroupEntity create() {
		throw new UnsupportedOperationException();
	}

	@Override
	public GroupEntity findById(String roleId) {
		try {
			IRole<Long> liferayUser = authorizationService.getRole(Long.parseLong(roleId));
			return getActivitiGroup(liferayUser, groupToActivityConverter);
		} catch (NumberFormatException | UserManagementException | RoleDoesNotExistsException e) {
			ActivitiUsersLogger.errorMessage(this.getClass().getName(), e);
		}
		return null;
	}

	@Override
	public void insert(GroupEntity entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void insert(GroupEntity entity, boolean b) {
		throw new UnsupportedOperationException();
	}

	@Override
	public GroupEntity update(GroupEntity entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public GroupEntity update(GroupEntity entity, boolean b) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(String s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(GroupEntity entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(GroupEntity entity, boolean b) {
		throw new UnsupportedOperationException();
	}
}
