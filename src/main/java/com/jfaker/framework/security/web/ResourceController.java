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
package com.jfaker.framework.security.web;

import com.jfaker.framework.security.model.Resource;
import com.jfaker.framework.security.web.validate.ResourceValidator;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;

/**
 * ResourceController
 * @author yuqs
 * @since 0.1
 */
public class ResourceController extends Controller {
	public void index() {
		keepPara();
		setAttr("page", Resource.dao.paginate(getParaToInt("pageNo", 1), 10, getPara("name")));
		render("resourceList.jsp");
	}
	
	public void add() {
		render("resourceAdd.jsp");
	}
	
	public void edit() {
		setAttr("resource", Resource.dao.get(getParaToInt()));
		render("resourceEdit.jsp");
	}
	
	public void view() {
		setAttr("resource", Resource.dao.get(getParaToInt()));
		render("resourceView.jsp");
	}
	
	@Before(ResourceValidator.class)
	public void save() {
		getModel(Resource.class).save();
		redirect("/security/resource");
	}
	
	@Before(ResourceValidator.class)
	public void update() {
		getModel(Resource.class).update();
		redirect("/security/resource");
	}
	
	public void delete() {
		Resource.dao.deleteById(getParaToInt());
		redirect("/security/resource");
	}
}


