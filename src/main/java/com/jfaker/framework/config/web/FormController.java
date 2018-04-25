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
package com.jfaker.framework.config.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.snaker.engine.helper.JsonHelper;

import com.jfaker.framework.flow.web.SnakerController;
import com.jfaker.framework.config.model.Field;
import com.jfaker.framework.config.model.Form;
import com.jfaker.framework.security.shiro.ShiroUtils;
import com.jfaker.framework.utils.DateUtils;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;

/**
 * 表单管理controller
 * @author yuqs
 * @since 0.1
 */
public class FormController extends SnakerController {
	public void index() {
		String name = getPara("name");
		setAttr("page", Form.dao.paginate(getParaToInt("pageNo", 1), 10, name));
		keepPara();
		render("formList.jsp");
	}
	
	public void add() {
		render("formAdd.jsp");
	}
	
	public void view() {
		setAttr("form", Form.dao.findById(getParaToInt()));
		render("formView.jsp");
	}
	
	public void edit() {
		setAttr("form", Form.dao.findById(getParaToInt()));
		render("formEdit.jsp");
	}
	
	public void designer() {
		setAttr("form", Form.dao.findById(getParaToInt()));
		render("formDesigner.jsp");
	}
	
	public void save() {
		Form model = getModel(Form.class);
		model.set("creator", ShiroUtils.getUsername());
		model.set("createTime", DateUtils.getCurrentTime());
		model.set("fieldNum", 0);
		model.save();
		redirect("/config/form");
	}
	
	public void update() {
		getModel(Form.class).update();
		redirect("/config/form");
	}
	
	@SuppressWarnings("unchecked")
	public void processor() {
		Form model = null;
		try {
			model = Form.dao.findById(getParaToInt("formid"));
			Map<String, Object> map = JsonHelper.fromJson(getPara("parse_form"), Map.class);
			Map<String, Object> datas = (Map<String, Object>)map.get("add_fields");
			Map<String, String> nameMap = Form.dao.process(model, datas);
			String template = (String)map.get("template");
			String parseHtml = (String)map.get("parse");
			if(!nameMap.isEmpty()) {
				for(Map.Entry<String, String> entry : nameMap.entrySet()) {
					template = template.replaceAll(entry.getKey(), entry.getValue());
					parseHtml = parseHtml.replaceAll(entry.getKey(), entry.getValue());
				}
			}
			model.set("originalHtml", template);
			model.set("parseHtml", parseHtml);
			model.update();
			renderJson(Boolean.TRUE);
		} catch(Exception e) {
			e.printStackTrace();
			renderJson(Boolean.FALSE);
		}
	}
	
	public void delete() {
		Form.dao.deleteById(getParaToInt());
		redirect("/config/form");
	}
	
	public void use() {
		Form model = Form.dao.findById(getParaToInt());
		setAttr("form", model);
		keepPara();
		String orderId = getPara(PARA_ORDERID);
		String taskId = getPara(PARA_TASKID);
		if(StringUtils.isEmpty(orderId) || StringUtils.isNotEmpty(taskId)) {
			render("formUse.jsp");
		} else {
			//setAttr("result", Form.dao.getDataByOrderId(model, orderId));
			render("formUseView.jsp");
		}
	}
	
	public void formData() {
		String orderId = getPara(PARA_ORDERID);
		Form model = Form.dao.findById(getParaToInt("formId"));
		renderJson(Form.dao.getDataByOrderId(model, orderId));
	}
	
	@Before(Tx.class)
	public void submit() {
		String processId = getPara(PARA_PROCESSID);
		String orderId = getPara(PARA_ORDERID);
		String taskId = getPara(PARA_TASKID);
		int formId = getParaToInt("formId");
		List<Field> fields = Field.dao.find("select * from df_field where formId=?", formId);
		Map<String, Object> params = new HashMap<String, Object>();
		for(Field field : fields) {
			if(Field.FLOW.equals(field.getStr("flow"))) {
				String name = field.getStr("name");
				String type = field.getStr("type");
				String paraValue = getPara(name);
				Object value = null;
				if("text".equalsIgnoreCase(type)) {
					value = paraValue;
				} else if("int".equalsIgnoreCase(type)) {
					value = getParaToInt(name, 0);
				} else if("float".equalsIgnoreCase(type)) {
					if(paraValue == null || "".equals(paraValue)) {
						value = 0.0;
					} else {
						try {
							value = Double.parseDouble(getPara(name));
						} catch(Exception e) {
							value = 0.0;
						}
					}
				} else {
					value = paraValue;
				}
				params.put(name, value);
			}
		}

		if(StringUtils.isNotEmpty(processId)) {
			if (StringUtils.isEmpty(orderId) && StringUtils.isEmpty(taskId)) {
				orderId = startAndExecute(processId, ShiroUtils.getUsername(), params).getId();
			} else {
				int method = getParaToInt(PARA_METHOD, 0);
				String nextOperator = getPara(PARA_NEXTOPERATOR);
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
		}

		Form model = Form.dao.findById(formId);
		Map<String, String[]> paraMap = getParaMap();
		Form.dao.submit(model, fields, paraMap, orderId, taskId);
		//redirect(getPara("url"));
		if(StringUtils.isNotEmpty(processId)) {
			redirectActiveTask();
		} else {
			redirect("/config/form");
		}
	}
}
