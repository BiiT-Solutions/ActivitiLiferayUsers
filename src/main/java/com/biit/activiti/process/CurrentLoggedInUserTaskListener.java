package com.biit.activiti.process;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.identity.Authentication;

public class CurrentLoggedInUserTaskListener implements TaskListener {
	private static final long serialVersionUID = 814372315504639168L;
	private Expression variableName;

	@Override
	public void notify(DelegateTask delegateTask) {
		String currentLoggedInUser = Authentication.getAuthenticatedUserId();
		String targetVariable = (String) variableName.getValue(delegateTask);

		delegateTask.setVariable(targetVariable, currentLoggedInUser);
	}

	public void setVariableName(Expression variableName) {
		this.variableName = variableName;
	}
}
