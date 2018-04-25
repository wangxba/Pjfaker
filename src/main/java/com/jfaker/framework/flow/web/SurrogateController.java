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

import org.snaker.engine.access.Page;
import org.snaker.engine.access.QueryFilter;
import org.snaker.engine.entity.Surrogate;

/**
 * 委托授权
 * @author yuqs
 * @since 0.1
 */
public class SurrogateController extends SnakerController {
	public void index() {
		Page<Surrogate> page = new Page<Surrogate>();
		page.setPageNo(getParaToInt("pageNo", 1));
		searchSurrogate(page, new QueryFilter());
		setAttr("page", page);
		render("surrogateList.jsp");
	}
	
	public void add() {
		setAttr("processNames", getAllProcessNames());
		render("surrogateAdd.jsp");
	}

	public void edit() {
		setAttr("surrogate", getSurrogate(getPara()));
		setAttr("processNames", getAllProcessNames());
		render("surrogateEdit.jsp");
	}
	
	public void view() {
		setAttr("surrogate", getSurrogate(getPara()));
		render("surrogateView.jsp");
	}
	
	public void save() {
		Surrogate model = getModel(Surrogate.class);
		addSurrogate(model);
		redirect("/snaker/surrogate");
	}
	
	public void update() {
		Surrogate model = getModel(Surrogate.class);
		addSurrogate(model);
		redirect("/snaker/surrogate");
	}
	
	public void delete() {
		deleteSurrogate(getPara());
		redirect("/snaker/surrogate");
	}
}
