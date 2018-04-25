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

import com.jfaker.framework.security.model.Menu;
import com.jfaker.framework.security.web.validate.MenuValidator;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;

/**
 * MenuController
 * @author yuqs
 * @since 0.1
 */
public class MenuController extends Controller {
	public void index() {
		keepPara();
		setAttr("page", Menu.dao.paginate(getParaToInt("pageNo", 1), 10, getPara("name")));
		render("menuList.jsp");
	}
	
	public void add() {
		render("menuAdd.jsp");
	}
	
	public void view() {
		setAttr("menu", Menu.dao.get(getParaToInt()));
		render("menuView.jsp");
	}
	
	public void edit() {
		setAttr("menu", Menu.dao.get(getParaToInt()));
		render("menuEdit.jsp");
	}
	
	@Before(MenuValidator.class)
	public void save() {
		getModel(Menu.class).save();
		redirect("/security/menu");
	}
	
	@Before(MenuValidator.class)
	public void update() {
		getModel(Menu.class).update();
		redirect("/security/menu");
	}
	
	public void delete() {
		Menu.dao.deleteById(getParaToInt());
		redirect("/security/menu");
	}
}


