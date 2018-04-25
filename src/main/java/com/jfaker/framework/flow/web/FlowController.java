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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.snaker.engine.access.Page;
import org.snaker.engine.access.QueryFilter;
import org.snaker.engine.entity.HistoryOrder;
import org.snaker.engine.entity.Process;
import org.snaker.engine.model.TaskModel;

import com.jfaker.framework.flow.model.Approval;
import com.jfaker.framework.security.shiro.ShiroUtils;
import com.jfaker.framework.utils.DateUtils;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;

/**
 * 通用流程controller类，主要有以下方法：
 * 1、通用的all视图路由
 * 2、通用的流程启动、任务执行方法
 * 3、通用的审批视图路由及审批处理
 * @author yuqs
 * @since 0.1
 */
public class FlowController extends SnakerController {
	/**
	 * 流程实例查询
	 * @param model
	 * @param page
	 * @return
	 */
	public void order() {
		Page<HistoryOrder> page = new Page<HistoryOrder>();
		page.setPageNo(getParaToInt("pageNo", 1));
		engine.query().getHistoryOrders(page, new QueryFilter());
		setAttr("page", page);
		render("order.jsp");
	}
	
	/**
	 * 抄送实例已读
	 */
	public void ccread() {
		List<String> list = ShiroUtils.getGroups();
		list.add(ShiroUtils.getUsername());
		String[] assignees = new String[list.size()];
		list.toArray(assignees);
		engine.order().updateCCStatus(getPara("id"), assignees);
		redirect(getPara("url"));
	}
	
	/**
	 * 处理流程启动或任务执行，并且将表单数据保存至实例、任务变量中
	 * 变量类型根据表单字段的首字母决定，类型分别为:S字符型,I整形,L常整形,B布尔型,D日期型,N浮点型
	 * 执行规则根据method的值决定，值分别为:0执行,-1驳回或跳转,1转主办,2转协办
	 * 适用于演示流程，或者是无业务字段的正式流程
	 */
	@Before(Tx.class)
	public void process() {
		Map<String, Object> params = new HashMap<String, Object>();
		Enumeration<String> paraNames = getRequest().getParameterNames();
		while (paraNames.hasMoreElements()) {
			String element = paraNames.nextElement();
			int index = element.indexOf("_");
			if(index == -1) {
				params.put(element, getPara(element));
			} else {
				char type = element.charAt(0);
				String name = element.substring(index + 1);
				Object value = null;
				switch(type) {
				case 'S':
					value = getPara(element);
					break;
				case 'I':
					value = getParaToInt(element);
					break;
				case 'L':
					value = getParaToLong(element);
					break;
				case 'B':
					value = getParaToBoolean(element);
					break;
				case 'D':
					value = getParaToDate(element);
					break;
				case 'N':
					value = Double.parseDouble(getPara(element));
					break;
				default:
					value = getPara(element);
					break;
				}
				params.put(name, value);
			}
		}
		String processId = getPara(PARA_PROCESSID);
		String orderId = getPara(PARA_ORDERID);
		String taskId = getPara(PARA_TASKID);
		String nextOperator = getPara(PARA_NEXTOPERATOR);
		if (StringUtils.isEmpty(orderId) && StringUtils.isEmpty(taskId)) {
			startAndExecute(processId, ShiroUtils.getUsername(), params);
		} else {
			int method = getParaToInt(PARA_METHOD, 0);
			switch(method) {
			case 0://任务执行
				execute(taskId, ShiroUtils.getUsername(), params);
				break;
			case -1://驳回、任意跳转
				executeAndJump(taskId, ShiroUtils.getUsername(), params, getPara(PARA_NODENAME));
				break;
			case 1://转办
				if(StringUtils.isNotEmpty(nextOperator)) {
					transferMajor(taskId, ShiroUtils.getUsername(), nextOperator.split(","));
				}
				break;
			case 2://协办
				if(StringUtils.isNotEmpty(nextOperator)) {
					transferAidant(taskId, ShiroUtils.getUsername(), nextOperator.split(","));
				}
				break;
			default:
				execute(taskId, ShiroUtils.getUsername(), params);
				break;
			}
		}
		String ccOperator = getPara(PARA_CCOPERATOR);
		if(StringUtils.isNotEmpty(ccOperator)) {
			engine.order().createCCOrder(orderId, ShiroUtils.getUsername(), ccOperator.split(","));
		}
		redirectActiveTask();
	}
	
	/**
	 * 通用的流程展现页面入口
	 * 将流程中的各环节表单以tab+iframe方式展现
	 */
	public void all() {
		keepPara();
		String processId = getPara(PARA_PROCESSID);
		String orderId = getPara(PARA_ORDERID);
		String taskId = getPara(PARA_TASKID);
		if(StringUtils.isNotEmpty(processId)) {
			setAttr("process", engine.process().getProcessById(processId));
		}
		if(StringUtils.isNotEmpty(orderId)) {
			setAttr("order", engine.query().getOrder(orderId));
		}
		if(StringUtils.isNotEmpty(taskId)) {
			setAttr("task", engine.query().getTask(taskId));
		}
		
		render("all.jsp");
	}
	
	/**
	 * 节点信息以json格式返回
	 * all页面以节点信息构造tab及加载iframe
	 */
	public void node() {
		String processId = getPara(PARA_PROCESSID);
		Process process = engine.process().getProcessById(processId);
		List<TaskModel> models = process.getModel().getModels(TaskModel.class);
		renderJson(models);
	}
	
	
	
	/**
	 * 由于审批类流程在各业务系统中经常出现，至此本方法是统一审批的url
	 * 如果审批环节能够统一，建议使用该方法返回统一审批页面
	 */
	public void approval() {
		keepPara();
		String orderId = getPara(PARA_ORDERID);
		String taskId = getPara(PARA_TASKID);
		if(StringUtils.isNotEmpty(taskId)) {
			render("approval.jsp");
		} else {
			setAttr("approvals", Approval.dao.findByFlow(orderId, getPara(PARA_TASKNAME)));
			render("approvalView.jsp");
		}
	}
	
	/**
	 * 审批环节的提交处理
	 * 其中审批表可根据具体审批的业务进行定制，此处仅仅是举例
	 */
	@Before(Tx.class)
	public void doApproval() {
		String orderId = getPara(PARA_ORDERID);
		String taskId = getPara(PARA_TASKID);
		String taskName = getPara(PARA_TASKNAME);
		Approval model = getModel(Approval.class);
		model.set("operateTime", DateUtils.getCurrentTime());
		model.set("operator", ShiroUtils.getUsername());
		model.set("orderId", orderId);
		model.set("taskId", taskId);
		model.set("taskName", taskName);
		model.save();
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("result", model.get("result"));
		execute(taskId, ShiroUtils.getUsername(), params);
		redirectActiveTask();
	}
}
