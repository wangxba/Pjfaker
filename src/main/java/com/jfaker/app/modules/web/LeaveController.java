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
package com.jfaker.app.modules.web;

import org.apache.commons.lang.StringUtils;

import com.jfaker.framework.flow.web.SnakerController;
import com.jfaker.framework.security.shiro.ShiroUtils;

/**
 * 请假流程Controller
 * 流程特点:多级审批流程、decision节点的表达式写法->${day > 2 ? 'transition5' : 'transition4'}
 * 业务数据:
 *     所有节点的业务数据均以json格式保存在order、task表的variable字段中
 *     建议业务数据以独立的表保存，通过orderId来关联
 * @author yuqs
 * @since 0.1
 */
public class LeaveController extends SnakerController {
	/**
	 * 请假申请路由方法
	 */
	public void apply() {
		//将请求参数继续传递给视图页面
		keepPara();
		//设置操作人为当前登录用户，请假流程演示时，将申请人、部门经理审批人、总经理审批人都设置为当前用户
		//可通过修改申请页面的部门经理、总经理输入框来改变下一步的处理人
		setAttr("operator", ShiroUtils.getUsername());
		String orderId = getPara(PARA_ORDERID);
		String taskId = getPara(PARA_TASKID);
		//根据taskId是否为空来标识当前请求的页面是否为活动任务的节点页面
		if(StringUtils.isEmpty(orderId) || StringUtils.isNotEmpty(taskId)) {
			//如果实例id为空或者驳回情况下，返回apply.jsp
			render("apply.jsp");
		} else {
			//如果orderId非空、taskId为空，则表示申请步骤已提交，此时可获取申请数据
			//由于请假流程中的业务数据，是保存在任务表的variable字段中，所以通过flowData方法获取
			//如果业务数据保存在业务表中，需要业务表的orderId字段来关联流程，进而根据orderId查询出业务数据
			flowData(orderId, getPara(PARA_TASKNAME));
			//返回申请的查看页面
			render("applyView.jsp");
		}
	}
	
	/**
	 * 部门经理审批路由方法
	 */
	public void approveDept() {
		keepPara();
		String orderId = getPara(PARA_ORDERID);
		String taskId = getPara(PARA_TASKID);
		if(StringUtils.isNotEmpty(taskId)) {
			render("approveDept.jsp");
		} else {
			flowData(orderId, getPara(PARA_TASKNAME));
			render("approveDeptView.jsp");
		}
	}
	
	/**
	 * 总经理审批路由方法
	 */
	public void approveBoss() {
		keepPara();
		String orderId = getPara(PARA_ORDERID);
		String taskId = getPara(PARA_TASKID);
		if(StringUtils.isNotEmpty(taskId)) {
			render("approveBoss.jsp");
		} else {
			flowData(orderId, getPara(PARA_TASKNAME));
			render("approveBossView.jsp");
		}
	}
}
