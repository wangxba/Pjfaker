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
package com.jfaker.app.web.taglibs;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.jfaker.app.web.taglibs.builder.MenuTagBuilder;
import com.jfaker.framework.web.TagDTO;

/**
 * 系统首界面左栏导航菜单自定义标签
 * @author yuqs
 * @since 0.1
 */
public class MenuTag extends TagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3041636263647268721L;
	//Servlet的上下文
	private ServletContext servletContext = null;

	public int doStartTag() throws JspException {
		//获取ServletContext
		servletContext = pageContext.getServletContext();
		JspWriter writer = pageContext.getOut();
		TagDTO dto = new TagDTO(servletContext);
		try {
			writer.write(MenuTagBuilder.builder.build(dto));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
