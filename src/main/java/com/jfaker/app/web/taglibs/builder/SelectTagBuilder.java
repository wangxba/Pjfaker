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
package com.jfaker.app.web.taglibs.builder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfaker.framework.config.model.Dict;
import com.jfaker.framework.web.TagBuilder;
import com.jfaker.framework.web.TagDTO;

/**
 * 选择控件构建器（支持select、radio、checkbox选择控件）
 * @author yuqs
 * @since 0.1
 */
public class SelectTagBuilder implements TagBuilder {
	private static final Logger log = LoggerFactory.getLogger(SelectTagBuilder.class);
	public static SelectTagBuilder builder = new SelectTagBuilder();
	private static final String TYPE_SELECT = "select";
	private static final String TYPE_RADIO = "radio";
	private static final String TYPE_CHECKBOX = "checkbox";
	public static final String NAME = "name";
	public static final String TYPE = "type";
	public static final String CONFIGNAME = "configName";
	public static final String STYLE = "style";
	public static final String READONLY = "readonly";
	public static final String CSSCLASS = "cssClass";
	public static final String DISPLAYTYPE = "displayType";
	public static final String VALUE = "value";
	public static final String FROM = "from";
	//控件名称
	private String name;
	//控件类型
	private String type;
	//配置实体名称
	private String configName;
	//对象已选择的值
	private String value;
	//控件style
	private String style;
	//控件是否只读
	private String readonly;
	//控件css
	private String cssClass;
	//显示类型
	private String displayType;
	//值列表
	private List<String> values = new ArrayList<String>();
	//选择列表
	private Map<String, String> items = new TreeMap<String, String>();
	
	/**
	 * 获取DTO传递的参数，并依此构建选择控件的html信息
	 */
	@Override
	public String build(TagDTO dto) {
		dataProcess(dto);
		if(name == null || type == null) {
			log.error("please confirm tag name or tag type is null.");
			return "";
		}
		StringBuffer buffer = new StringBuffer();
		if(type.equalsIgnoreCase(TYPE_CHECKBOX)) {
			buildCheckOrRadio(buffer);
		} else if(type.equalsIgnoreCase(TYPE_RADIO)) {
			buildCheckOrRadio(buffer);
		} else if(type.equalsIgnoreCase(TYPE_SELECT)) {
			if(StringUtils.isNotEmpty(displayType)) {
				if(displayType.equalsIgnoreCase("1")) {
					buildSelectLabel(buffer);
				} else if(displayType.equalsIgnoreCase("0")) {
					buildSelect(buffer);
				}
			} else {
				buildSelect(buffer);
			}
		}
		return buffer.toString();
	}
	
	/**
	 * 根据dto对象，转换为builder的对象属性。并且对于configName，从注入的ConfigManager对象中获取配置数据
	 * @param dto
	 */
	private void dataProcess(TagDTO dto) {
		values.clear();
		items.clear();
		name = dto.getProperty(NAME);
		type = dto.getProperty(TYPE);
		configName = dto.getProperty(CONFIGNAME);
		value = dto.getProperty(VALUE);
		style = dto.getProperty(STYLE);
		readonly = dto.getProperty(READONLY);
		cssClass = dto.getProperty(CSSCLASS);
		displayType = dto.getProperty(DISPLAYTYPE);
		if(StringUtils.isNotEmpty(value)) {
			if(value.indexOf(";") > -1) {
				String[] vs = value.split(";");
				for(String v : vs) {
					values.add(v);
				}
			} else {
				values.add(value);
			}
		}
		items = Dict.dao.getItemsByName(configName);
	}
	
	/**
	 * 选择结果已文本显示
	 * @param buffer
	 */
	private void buildSelectLabel(StringBuffer buffer) {
		Iterator<String> it = items.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			String value = items.get(key);
			if(values.contains(key)) {
				buffer.append(value);
				break;
			}
		}
	}
	
	/**
	 * 构造checkbox、radio类型的控件元素
	 * @param buffer
	 */
	private void buildCheckOrRadio(StringBuffer buffer) {
		Iterator<String> it = items.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			String value = items.get(key);
			String selected = "";
			if (values != null && values.contains(key)) {
				selected = "checked";
			}
			buffer.append("<label>");
			buffer.append("<input type=\"" + type + "\" name=\"" + name + "\" value=\"" + key + "\" ");
			buffer.append(selected).append(" ");
			buffer.append("/>" + value);
			buffer.append("</label>");
		}
	}
	
	/**
	 * 构造select类型的控件元素
	 * @param buffer
	 */
	private void buildSelect(StringBuffer buffer) {
		buffer.append("<select ");
		buffer.append(" name=\"" + name + "\" ");
		if(cssClass != null) {
			buffer.append(" class=\"" + cssClass + "\" ");
		}
		if(style != null) {
			buffer.append(" style=\"" + style + "\" ");
		}
		if(readonly != null && readonly.equalsIgnoreCase("true")) {
			buffer.append(" readonly disabled ");
		}
		buffer.append(">");
		buffer.append("<option value='' selected>------请选择------</option>");
		
		if(items != null && !items.isEmpty()) {
			Iterator<String> it = items.keySet().iterator();
			String selected = "";
			
			while (it.hasNext()) {
				selected = "";
				String key =  it.next();
				String value = (String)items.get(key);
				
				if (values != null && values.contains(key)) {
					selected = "selected";
				}
				
				buffer.append(" <option " + selected + " value=\"" + key + "\">");
				buffer.append(value + "</option>");
			}
		}
		
		buffer.append("</select>");
	}
}
