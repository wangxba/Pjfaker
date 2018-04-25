/*
 *  Copyright 2014-2015 snakerflow.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package com.jfaker.framework.flow.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.snaker.engine.SnakerEngine;
import org.snaker.engine.access.Page;
import org.snaker.engine.access.QueryFilter;
import org.snaker.engine.entity.HistoryTask;
import org.snaker.engine.entity.Order;
import org.snaker.engine.entity.Process;
import org.snaker.engine.entity.Surrogate;
import org.snaker.engine.entity.Task;
import org.snaker.engine.helper.StreamHelper;
import org.snaker.engine.model.TaskModel.TaskType;
import org.snaker.jfinal.plugin.SnakerPlugin;

import com.jfinal.core.Controller;

/**
 * 流程控制器父类，主要提供常用的流程api
 * @author yuqs
 * @since 0.1
 */
public class SnakerController extends Controller {
	public static final String PARA_PROCESSID = "processId";
	public static final String PARA_ORDERID = "orderId";
	public static final String PARA_TASKID = "taskId";
	public static final String PARA_TASKNAME = "taskName";
	public static final String PARA_METHOD = "method";
	public static final String PARA_NEXTOPERATOR = "nextOperator";
	public static final String PARA_NODENAME = "nodeName";
	public static final String PARA_CCOPERATOR = "ccOperator";
	public static final String URL_ACTIVETASK = "/snaker/task/active";
	/**
	 * 通过插件获取流程引擎入口
	 */
	protected SnakerEngine engine = SnakerPlugin.getEngine();
	
	/**
	 * 初始化请假、借款流程示例
	 */
	public void initFlows() {
		engine.process().deploy(StreamHelper.getStreamFromClasspath("flows/leave.snaker"));
		engine.process().deploy(StreamHelper.getStreamFromClasspath("flows/borrow.snaker"));
	}
	
	/**
	 * 重定向至待办任务列表
	 */
	public void redirectActiveTask() {
		redirect(URL_ACTIVETASK);
	}
	
	/**
	 * 根据orderId、taskName获取其任务的变量数据
	 * @param orderId
	 * @param taskName
	 */
	protected void flowData(String orderId, String taskName) {
		if (StringUtils.isNotEmpty(orderId) && StringUtils.isNotEmpty(taskName)) {
			List<HistoryTask> histTasks = engine.query()
					.getHistoryTasks(
							new QueryFilter().setOrderId(orderId).setName(
									taskName));
			List<Map<String, Object>> vars = new ArrayList<Map<String,Object>>();
			for(HistoryTask hist : histTasks) {
				vars.add(hist.getVariableMap());
			}
			setAttr("vars", vars);
			setAttr("histTasks", histTasks);
		}
	}
	
	/**
	 * 获取引擎
	 * @return
	 */
	public SnakerEngine getEngine() {
		return engine;
	}
	
	/**
	 * 获取所有流程定义的名称
	 * @return
	 */
	public List<String> getAllProcessNames() {
		List<Process> list = engine.process().getProcesss(new QueryFilter());
		List<String> names = new ArrayList<String>();
		for(Process entity : list) {
			if(names.contains(entity.getName())) {
				continue;
			} else {
				names.add(entity.getName());
			}
		}
		return names;
	}
	
	public Order startInstanceById(String processId, String operator, Map<String, Object> args) {
		return engine.startInstanceById(processId, operator, args);
	}
	
	public Order startInstanceByName(String name, Integer version, String operator, Map<String, Object> args) {
		return engine.startInstanceByName(name, version, operator, args);
	}
	
	public Order startAndExecute(String name, Integer version, String operator, Map<String, Object> args) {
		Order order = engine.startInstanceByName(name, version, operator, args);
		List<Task> tasks = engine.query().getActiveTasks(new QueryFilter().setOrderId(order.getId()));
		List<Task> newTasks = new ArrayList<Task>();
		if(tasks != null && tasks.size() > 0) {
			Task task = tasks.get(0);
			newTasks.addAll(engine.executeTask(task.getId(), operator, args));
		}
		return order;
	}
	
	public Order startAndExecute(String processId, String operator, Map<String, Object> args) {
		Order order = engine.startInstanceById(processId, operator, args);
		List<Task> tasks = engine.query().getActiveTasks(new QueryFilter().setOrderId(order.getId()));
		List<Task> newTasks = new ArrayList<Task>();
		if(tasks != null && tasks.size() > 0) {
			Task task = tasks.get(0);
			newTasks.addAll(engine.executeTask(task.getId(), operator, args));
		}
		return order;
	}
	
	public List<Task> execute(String taskId, String operator, Map<String, Object> args) {
		return engine.executeTask(taskId, operator, args);
	}
	
	public List<Task> executeAndJump(String taskId, String operator, Map<String, Object> args, String nodeName) {
		return engine.executeAndJumpTask(taskId, operator, args, nodeName);
	}
	
	public List<Task> transferMajor(String taskId, String operator, String... actors) {
		List<Task> tasks = engine.task().createNewTask(taskId, TaskType.Major.ordinal(), actors);
		engine.task().complete(taskId, operator);
		return tasks;
	}
	
	public List<Task> transferAidant(String taskId, String operator, String... actors) {
		List<Task> tasks = engine.task().createNewTask(taskId, TaskType.Aidant.ordinal(), actors);
		engine.task().complete(taskId, operator);
		return tasks;
	}
	
	public void addSurrogate(Surrogate entity) {
		if(entity.getState() == null) {
			entity.setState(1);
		}
		engine.manager().saveOrUpdate(entity);
	}
	
	public void deleteSurrogate(String id) {
		engine.manager().deleteSurrogate(id);
	}
	
	public Surrogate getSurrogate(String id) {
		return engine.manager().getSurrogate(id);
	}
	
	public List<Surrogate> searchSurrogate(Page<Surrogate> page, QueryFilter filter) {
		return engine.manager().getSurrogate(page, filter);
	}
}
