package com.biit.activiti.groups;

import java.util.Set;

import com.liferay.portal.model.Role;

/**
 * This class must translate from Liferay Roles to Activiti groups.
 */
public interface ILiferayGroupToActivityRoleConverter {

	/**
	 * Gets the Activiti GroupType equivalence from a Liferay role.
	 * 
	 * @param liferayRole
	 * @return
	 */
	GroupType getActivitiGroup(Role liferayRole);

	/**
	 * Creates a unique group name from a Liferay role. This can be the role name, or in the case of multitenancy, a
	 * composition of the role name and the organization name.
	 * 
	 * @param liferayRole
	 * @return
	 */
	String getGroupName(Role liferayRole);

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
	Set<Role> getAllRoles();

	/**
	 * Gets all available roles from Liferay that are related to an Activiti GroupType. The list only must include all
	 * roles that can be use with the application.
	 * 
	 * @param type
	 * @return
	 */
	Set<Role> getRoles(GroupType type);

}
