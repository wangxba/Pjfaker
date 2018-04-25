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
package com.jfaker.framework.web;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

/**
 * 自定义标签的数据传输对象（这里的传输并非网络、分页之间的传输，而是自定义标签与具体标签处理类的对象传输）
 * @author yuqs
 * @since 0.1
 */
public class TagDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2439992485911479461L;
	//Servlet的上下文
	private ServletContext servletContext = null;
	//自定义标签的属性集合
	private Map<String, String> properties = new HashMap<String, String>();
	
	/**
	 * 构造函数，接收ServletContext上下文参数。该参数经常在标签处理类中需要用到
	 * @param servletContext
	 */
	public TagDTO(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	/**
	 * 获取ServletContext上下文
	 * @return
	 */
	public ServletContext getServletContext() {
		return servletContext;
	}
	
	/**
	 * 根据属性名称，获取属性的值
	 * @param name
	 * @return
	 */
	public String getProperty(String name) {
		return properties.get(name);
	}
	
	/**
	 * 设置属性名称、值的键值对
	 * @param name
	 * @param value
	 */
	public void setProperty(String name, String value) {
		properties.put(name, value);
	}
}
