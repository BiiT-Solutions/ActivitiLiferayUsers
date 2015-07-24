package com.biit.activiti.groups;

import java.util.Set;

import com.biit.usermanager.entity.IRole;

/**
 * This class must translate from Liferay Roles to Activiti groups.
 */
public interface IGroupToActivityRoleConverter {

	/**
	 * Gets the Activiti GroupType equivalence from a Liferay role.
	 * 
	 * @param liferayRole
	 * @return
	 */
	GroupType getActivitiGroup(IRole<Long> liferayRole);

	/**
	 * Creates a unique group name from a Liferay role. This can be the role name, or in the case of multitenancy, a
	 * composition of the role name and the organization name.
	 * 
	 * @param liferayRole
	 * @return
	 */
	String getGroupName(IRole<Long> liferayRole);

	/**
	 * Gets the Liferay equivalence role from an Activiti group. Must exactly do the inverse process of
	 * {@link #getGroupName(Role liferayRole)}.
	 * 
	 * @param activitiGroupName
	 * @return
	 */
	String getRoleName(String activitiGroupName);

	/**
	 * Gets all available roles from Liferay. The list only must include all roles that can be use with the application.
	 * 
	 * @return
	 */
	Set<IRole<Long>> getAllRoles();

	/**
	 * Gets all available roles from Liferay that are related to an Activiti GroupType. The list only must include all
	 * roles that can be use with the application.
	 * 
	 * @param type
	 * @return
	 */
	Set<IRole<Long>> getRoles(GroupType type);

}
