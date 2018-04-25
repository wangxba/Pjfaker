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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snaker.engine.access.Page;
import org.snaker.engine.access.QueryFilter;
import org.snaker.engine.entity.HistoryOrder;
import org.snaker.engine.entity.Task;
import org.snaker.engine.entity.WorkItem;
import org.snaker.engine.model.TaskModel.TaskType;

import com.jfaker.framework.security.shiro.ShiroUtils;

/**
 * Snaker流程引擎常用Controller
 * @author yuqs
 * @since 0.1
 */
public class TaskController extends SnakerController {
	private static final Logger log = LoggerFactory.getLogger(TaskController.class);
	
	public void active() {
		List<String> list = ShiroUtils.getGroups();
		list.add(ShiroUtils.getUsername());
		log.info(list.toString());
		String[] assignees = new String[list.size()];
		list.toArray(assignees);
		
		Page<WorkItem> majorPage = new Page<WorkItem>(5);
		Page<WorkItem> aidantPage = new Page<WorkItem>(3);
		Page<HistoryOrder> ccorderPage = new Page<HistoryOrder>(3);
		List<WorkItem> majorWorks = engine
				.query()
				.getWorkItems(majorPage, new QueryFilter()
				.setOperators(assignees)
				.setTaskType(TaskType.Major.ordinal()));
		List<WorkItem> aidantWorks = engine
				.query()
				.getWorkItems(aidantPage, new QueryFilter()
				.setOperators(assignees)
				.setTaskType(TaskType.Aidant.ordinal()));
		List<HistoryOrder> ccWorks = engine
				.query()
				.getCCWorks(ccorderPage, new QueryFilter()
				.setOperators(assignees)
				.setState(1));
		
		setAttr("majorWorks", majorWorks);
		setAttr("majorTotal", majorPage.getTotalCount());
		setAttr("aidantWorks", aidantWorks);
		setAttr("aidantTotal", aidantPage.getTotalCount());
		setAttr("ccorderWorks", ccWorks);
		setAttr("ccorderTotal", ccorderPage.getTotalCount());
		render("activeTask.jsp");
	}
	
	/**
	 * 根据当前用户查询待办任务列表
	 */
	public void user() {
		Page<WorkItem> page = new Page<WorkItem>();
		page.setPageNo(getParaToInt("pageNo", 1));
		engine.query().getWorkItems(page, 
				new QueryFilter().setOperator(ShiroUtils.getUsername()));
		setAttr("page", page);
		render("userTask.jsp");
	}

    public void addActor() {
        keepPara();
        render("actor.jsp");
    }

    public void doAddActor() {
        List<Task> tasks = engine
        		.query()
        		.getActiveTasks(new QueryFilter().setOrderId(getPara(PARA_ORDERID)));
        for(Task task : tasks) {
            if(task.getTaskName().equalsIgnoreCase(getPara(PARA_TASKNAME)) 
            		&& StringUtils.isNotEmpty(getPara("operator"))) {
                engine.task().addTaskActor(task.getId(), getPara("operator"));
            }
        }
        renderJson("success");
    }

    public void tip() {
        List<Task> tasks = engine
        		.query()
        		.getActiveTasks(new QueryFilter().setOrderId(getPara(PARA_ORDERID)));
        StringBuilder builder = new StringBuilder();
        String createTime = "";
        for(Task task : tasks) {
            if(task.getTaskName().equalsIgnoreCase(getPara(PARA_TASKNAME))) {
                String[] actors = engine.query().getTaskActorsByTaskId(task.getId());
                for(String actor : actors) {
                    builder.append(actor).append(",");
                }
                createTime = task.getCreateTime();
            }
        }
        if(builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        Map<String, String> data = new HashMap<String, String>();
        data.put("actors", builder.toString());
        data.put("createTime", createTime);
        renderJson(data);
    }
	
	/**
	 * 活动任务查询列表
	 */
	public void activeMore() {
		Page<WorkItem> page = new Page<WorkItem>();
		page.setPageNo(getParaToInt("pageNo", 1));
		List<String> list = ShiroUtils.getGroups();
		list.add(ShiroUtils.getUsername());
		log.info(list.toString());
		String[] assignees = new String[list.size()];
		list.toArray(assignees);
		int taskType = getParaToInt("taskType");
		engine.query().getWorkItems(page, 
				new QueryFilter().setOperators(assignees).setTaskType(taskType));
		setAttr("page", page);
		setAttr("taskType", taskType);
		render("activeTaskMore.jsp");
	}
	
	/**
	 * 活动任务查询列表
	 */
	public void activeCCMore() {
		Page<HistoryOrder> page = new Page<HistoryOrder>();
		page.setPageNo(getParaToInt("pageNo", 1));
		List<String> list = ShiroUtils.getGroups();
		list.add(ShiroUtils.getUsername());
		log.info(list.toString());
		String[] assignees = new String[list.size()];
		list.toArray(assignees);
		engine
				.query()
				.getCCWorks(page, new QueryFilter()
				.setOperators(assignees)
				.setState(1));
		setAttr("page", page);
		render("activeCCMore.jsp");
	}
	
	/**
	 * 测试任务的执行
	 * @param model
	 * @return
	 */
	public void exec() {
		execute(getPara(PARA_TASKID), ShiroUtils.getUsername(), null);
		redirect("/snaker/task/active");;
	}
	
	/**
	 * 活动任务的驳回
	 * @param model
	 * @param taskId
	 * @return
	 */
	public void reject() {
		String error = "";
		try {
			executeAndJump(getPara(PARA_TASKID), ShiroUtils.getUsername(), null, null);
		} catch(Exception e) {
			error = "?error=1";
		}
		redirect("/snaker/task/active" + error);
	}
	
	/**
	 * 历史完成任务查询列表
	 * @param model
	 * @return
	 */
	public void history() {
		Page<WorkItem> page = new Page<WorkItem>();
		page.setPageNo(getParaToInt("pageNo", 1));
		engine.query().getHistoryWorkItems(page, 
				new QueryFilter().setOperator(ShiroUtils.getUsername()));
		setAttr("page", page);
		render("historyTask.jsp");
	}
	
	/**
	 * 历史任务撤回
	 */
	public void undo() {
		String returnMessage = "";
		try {
			engine.task().withdrawTask(getPara(PARA_TASKID), ShiroUtils.getUsername());
			returnMessage = "任务撤回成功.";
		} catch(Exception e) {
			returnMessage = e.getMessage();
		}
		setAttr("returnMessage", returnMessage);
		redirect("/snaker/task/history");
	}
}
