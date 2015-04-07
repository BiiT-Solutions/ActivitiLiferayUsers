package com.biit.activiti.tests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:applicationContext.xml" })
@Test(groups = "activitiGroupTasks")
public class GroupProcessTest extends AbstractTransactionalTestNGSpringContextTests {
	private static final String ONE_TASK_PROCESS_NAME = "oneTaskProcess";
	private static final String HOLIDAY_PROCESS_NAME = "HolidayRequest";
	private static final String HOLIDAY_PROCESS_APPROVE_TASK_NAME = "Approve Request";
	private static final String HOLIDAY_PROCESS_APPROVE_TASK_2_NAME = "Send Request Approved";
	private static final String HOLIDAY_PROCESS_APPROVE_TASK_USER = "kermit";
	private static final String HOLIDAY_PROCESS_APPROVE_TASK_GROUP = "management";
	private static final String HOLIDAY_PROCESS_APPROVE_TASK_USER2 = "fozzie";

	@Autowired
	private ProcessEngine processEngine;

	private String holidayRequestId;

	/**
	 * Holidays with more than 2 days, needs human approval.
	 */
	@Test
	@Deployment(resources = { "process/holidayRequest.bpmn20.xml" })
	@Rollback(value = false)
	public void holidayRequestsManualTask() {
		Assert.assertNotNull(processEngine);
		RuntimeService runtimeService = processEngine.getRuntimeService();
		Assert.assertNotNull(runtimeService);
		TaskService taskService = processEngine.getTaskService();
		Assert.assertNotNull(taskService);

		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("days", 4);
		variables.put("startDate", "01-01-2020");
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(HOLIDAY_PROCESS_NAME, variables);
		Assert.assertNotNull(processInstance);

		holidayRequestId = processInstance.getId();

		// Check if a task is available for the given process
		Assert.assertEquals(1, taskService.createTaskQuery().processInstanceId(holidayRequestId).count());
	}

	/**
	 * This task has two user actions. Assign and resolve both of them.
	 */
	@Test(dependsOnMethods = { "holidayRequestsManualTask" })
	@Rollback(value = false)
	public void assignFirstTaskToGroup() {
		TaskService taskService = processEngine.getTaskService();
		Assert.assertNotNull(taskService);

		// Check if a task is available for the given process
		Assert.assertEquals(1, taskService.createTaskQuery().processInstanceId(holidayRequestId).count());

		Task requestApproval = taskService.createTaskQuery().processInstanceId(holidayRequestId).list().get(0);
		Assert.assertEquals(0, taskService.createTaskQuery().taskAssignee(HOLIDAY_PROCESS_APPROVE_TASK_USER).count());
		taskService.addCandidateGroup(requestApproval.getId(), HOLIDAY_PROCESS_APPROVE_TASK_GROUP);
		Assert.assertEquals(1, taskService.createTaskQuery().taskCandidateGroup(HOLIDAY_PROCESS_APPROVE_TASK_GROUP)
				.count());
	}

}
