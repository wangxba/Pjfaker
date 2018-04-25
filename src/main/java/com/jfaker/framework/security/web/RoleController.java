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

import java.util.List;

import com.jfaker.framework.security.model.Authority;
import com.jfaker.framework.security.model.Role;
import com.jfaker.framework.security.web.validate.RoleValidator;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.tx.Tx;

/**
 * RoleController
 * @author yuqs
 * @since 0.1
 */
public class RoleController extends Controller {
	public void index() {
		keepPara();
		setAttr("page", Role.dao.paginate(getParaToInt("pageNo", 1), 10, getPara("name")));
		render("roleList.jsp");
	}
	
	public void add() {
		setAttr("authorities", Authority.dao.getAll());
		render("roleAdd.jsp");
	}
	
	public void edit() {
		setAttr("role", Role.dao.findById(getParaToInt()));
		List<Authority> authorities = Authority.dao.getAll();
		List<Authority> auths = Role.dao.getAuthorities(getParaToInt());
		for(Authority auth : authorities) {
			for(Authority sels : auths) {
				if(auth.getInt("id").intValue() == sels.getInt("id").intValue())
				{
					auth.put("selected", 1);
				}
				if(auth.get("selected") == null)
				{
					auth.put("selected", 0);
				}
			}
		}
		setAttr("authorities", authorities);
		render("roleEdit.jsp");
	}
	
	public void view() {
		setAttr("role", Role.dao.findById(getParaToInt()));
		setAttr("authorities", Role.dao.getAuthorities(getParaToInt()));
		render("roleView.jsp");
	}
	
	@Before({RoleValidator.class, Tx.class})
	public void save() {
		Integer[] orderIndexs = getParaValuesToInt("orderIndexs");
		Role model = getModel(Role.class);
		model.save();
		if(orderIndexs != null) {
			for(Integer orderIndex : orderIndexs) {
				Role.dao.insertCascade(model.getInt("id"), orderIndex);
			}
		}
		redirect("/security/role");
	}
	
	@Before({RoleValidator.class, Tx.class})
	public void update() {
		Integer[] orderIndexs = getParaValuesToInt("orderIndexs");
		Role model = getModel(Role.class);
		model.update();
		Role.dao.deleteCascade(model.getInt("id"));
		if(orderIndexs != null) {
			for(Integer orderIndex : orderIndexs) {
				Role.dao.insertCascade(model.getInt("id"), orderIndex);
			}
		}
		redirect("/security/role");
	}
	
	@Before(Tx.class)
	public void delete() {
		Role.dao.deleteCascade(getParaToInt());
		Role.dao.deleteById(getParaToInt());
		redirect("/security/role");
	}
}


