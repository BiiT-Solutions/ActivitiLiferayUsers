package com.biit.activiti.services;

import org.activiti.engine.IdentityService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.User;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractService {

	@Autowired
	protected TaskService taskService;
	@Autowired
	protected IdentityService identityService;

	protected Task findTaskByDefinitionKey(ProcessInstance processInstance, String definitionKey) {
		return taskService.createTaskQuery().processInstanceId(processInstance.getId())
				.taskDefinitionKey(definitionKey).singleResult();
	}

	protected String getFullNameOfUser(String userId) {
		User user = identityService.createUserQuery().userId(userId).singleResult();
		if (user == null) {
			return null;
		} else {
			return String.format("%s %s", user.getFirstName(), user.getLastName());
		}
	}

}
