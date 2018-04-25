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

import com.jfaker.framework.security.model.Org;
import com.jfaker.framework.security.web.validate.OrgValidator;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;

/**
 * OrgController
 * @author yuqs
 * @since 0.1
 */
public class OrgController extends Controller {
	public void index() {
		keepPara();
		setAttr("page", Org.dao.paginate(getParaToInt("pageNo", 1), 10, getPara("name")));
		render("orgList.jsp");
	}
	
	public void add() {
		render("orgAdd.jsp");
	}
	
	public void view() {
		setAttr("org", Org.dao.get(getParaToInt()));
		render("orgView.jsp");
	}
	
	public void edit() {
		setAttr("org", Org.dao.get(getParaToInt()));
		render("orgEdit.jsp");
	}
	
	@Before(OrgValidator.class)
	public void save() {
		getModel(Org.class).save();
		redirect("/security/org");
	}
	
	@Before(OrgValidator.class)
	public void update() {
		getModel(Org.class).update();
		redirect("/security/org");
	}
	
	public void delete() {
		Org.dao.deleteById(getParaToInt());
		redirect("/security/org");
	}
}


