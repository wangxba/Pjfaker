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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.snaker.engine.access.Page;
import org.snaker.engine.access.QueryFilter;
import org.snaker.engine.entity.HistoryOrder;
import org.snaker.engine.entity.HistoryTask;
import org.snaker.engine.entity.Process;
import org.snaker.engine.entity.Task;
import org.snaker.engine.helper.AssertHelper;
import org.snaker.engine.helper.StreamHelper;
import org.snaker.engine.helper.StringHelper;
import org.snaker.engine.model.ProcessModel;

import com.jfaker.framework.flow.SnakerHelper;
import com.jfaker.framework.security.shiro.ShiroUtils;
import com.jfinal.upload.UploadFile;

/**
 * 流程定义
 * @author yuqs
 * @since 0.1
 */
public class ProcessController extends SnakerController {
	/**
	 * 流程定义查询列表
	 */
	public void index() {
		QueryFilter filter = new QueryFilter();
		String displayName = getPara("displayName");
		if(StringHelper.isNotEmpty(displayName)) {
			filter.setDisplayName(displayName);
		}
		Page<Process> page = new Page<Process>();
		page.setPageNo(getParaToInt("pageNo", 1));
		engine.process().getProcesss(page, filter);
		setAttr("page", page);
		render("processList.jsp");
	}
	
	/**
	 * 初始化流程定义
	 */
	public void init() {
		initFlows();
		redirect("/snaker/process");
	}
	
	/**
	 * 根据流程定义部署
	 */
	public void deploy() {
		render("processDeploy.jsp");
	}
	
	/**
	 * 新建流程定义
	 */
	public void add() {
		render("processAdd.jsp");
	}
	
	/**
	 * 新建流程定义[web流程设计器]
	 */
	public void designer() {
		String processId = getPara(PARA_PROCESSID);
		if(StringUtils.isNotEmpty(processId)) {
			Process process = engine.process().getProcessById(processId);
			AssertHelper.notNull(process);
			ProcessModel processModel = process.getModel();
			if(processModel != null) {
				String json = SnakerHelper.getModelJson(processModel);
				setAttr("process", json);
			}
			setAttr("processId", processId);
		}
		render("processDesigner.jsp");
	}
	
	/**
	 * 编辑流程定义
	 */
	public void edit() {
		Process process = engine.process().getProcessById(getPara());
		setAttr("process", process);
		if(process.getDBContent() != null) {
            try {
            	setAttr("content", StringHelper.textXML(new String(process.getDBContent(), "UTF-8")));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
		render("processEdit.jsp");
	}
	
	/**
	 * 根据流程定义ID，删除流程定义
	 */
	public void delete() {
		engine.process().undeploy(getPara());
		redirect("/snaker/process");
	}
	
	/**
	 * 添加流程定义后的部署
	 */
	public void doFileDeploy() {
		InputStream input = null;
		try {
			String id = getPara("id");
			UploadFile file = getFile("snakerFile");
			input = new FileInputStream(file.getFile());
			if(StringUtils.isNotEmpty(id)) {
				engine.process().redeploy(id, input);
			} else {
				engine.process().deploy(input);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		redirect("/snaker/process");
	}
	
	/**
	 * 保存流程定义[web流程设计器]
	 * @param model
	 * @return
	 */
	public void doStringDeploy() {
		InputStream input = null;
		try {
			String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" + SnakerHelper.convertXml(getPara("model"));
			System.out.println("model xml=\n" + xml);
			String id = getPara("id");
			input = StreamHelper.getStreamFromString(xml);
			if(StringUtils.isNotEmpty(id)) {
				engine.process().redeploy(id, input);
			} else {
				engine.process().deploy(input);
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(false);
		} finally {
			if(input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		renderJson(true);
	}
	
	/**
	 * 通用的流程启动
	 */
	public void processStart() {
		startInstanceByName(getPara("processName"), null, ShiroUtils.getUsername(), null);
		redirect("/snaker/process");
	}
	
	/**
	 * 流程图展现需要的json数据
	 */
	public void json() {
		String processId = getPara(PARA_PROCESSID);
		String orderId = getPara(PARA_ORDERID);
		Process process = engine.process().getProcessById(processId);
		AssertHelper.notNull(process);
		ProcessModel model = process.getModel();
		Map<String, String> jsonMap = new HashMap<String, String>();
		if(model != null) {
			jsonMap.put("process", SnakerHelper.getModelJson(model));
		}
		if(StringUtils.isNotEmpty(orderId)) {
			List<Task> tasks = engine.query().getActiveTasks(new QueryFilter().setOrderId(orderId));
			List<HistoryTask> historyTasks = engine.query().getHistoryTasks(new QueryFilter().setOrderId(orderId));
			jsonMap.put("state", SnakerHelper.getStateJson(model, tasks, historyTasks));
		}
		//{"historyRects":{"rects":[{"paths":["TO 任务1"],"name":"开始"},{"paths":["TO 分支"],"name":"任务1"},{"paths":["TO 任务3","TO 任务4","TO 任务2"],"name":"分支"}]}}
		renderJson(jsonMap);
	}
	
	/**
	 * 显示流程图与历史任务记录
	 */
	public void display() {
		keepPara();
		String orderId = getPara(PARA_ORDERID);
		HistoryOrder order = engine.query().getHistOrder(orderId);
		setAttr("order", order);
		List<HistoryTask> tasks = engine.query().getHistoryTasks(new QueryFilter().setOrderId(orderId));
		setAttr("tasks", tasks);
		render("processView.jsp");
	}
	
	/**
	 * 显示独立的流程图
	 */
	public void diagram() {
		keepPara();
		render("diagram.jsp");
	}
	
	public void validate() {
		render("processValidate.jsp");
	}
}
