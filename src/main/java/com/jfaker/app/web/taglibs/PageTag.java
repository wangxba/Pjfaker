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

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.jfaker.app.web.taglibs.builder.PageTagBuilder;
import com.jfaker.framework.web.TagDTO;

/**
 * 自定义查询分页标签。查询统计涉及到分页的界面，只要通过page标签，即可显示分页的常用操作
 * @author yuqs
 * @since 0.1
 */
public class PageTag extends TagSupport 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5636798157755500338L;
	//总记录数
	private String totalRecords;
	//总页数
	private String totalPages;
	//当前页数
	private String curPage;
	//导出excel的url
	private String exportUrl;
	//是否打开对话框的查询
	private String lookup;
	//Servlet的上下文
	private ServletContext servletContext = null;

	public int doStartTag() throws JspException {
		//获取ServletContext
		servletContext = pageContext.getServletContext();
		JspWriter writer = pageContext.getOut();
		try {
			TagDTO dto = new TagDTO(servletContext);
			dto.setProperty(PageTagBuilder.TOTAL_RECORDS, totalRecords);
			dto.setProperty(PageTagBuilder.TOTAL_PAGES, totalPages);
			dto.setProperty(PageTagBuilder.CURPAGE, curPage);
			dto.setProperty(PageTagBuilder.EXPORT_URL, exportUrl);
			dto.setProperty(PageTagBuilder.LOOKUP, lookup);
			writer.write(PageTagBuilder.builder.build(dto));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	public String getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(String totalRecords) {
		this.totalRecords = totalRecords;
	}

	public String getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(String totalPages) {
		this.totalPages = totalPages;
	}

	public String getCurPage() {
		return curPage;
	}

	public void setCurPage(String curPage) {
		this.curPage = curPage;
	}

	public String getExportUrl() {
		return exportUrl;
	}

	public void setExportUrl(String exportUrl) {
		this.exportUrl = exportUrl;
	}

	public String getLookup() {
		return lookup;
	}

	public void setLookup(String lookup) {
		this.lookup = lookup;
	}
}
