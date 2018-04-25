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

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import com.jfaker.framework.security.model.Role;
import com.jfaker.framework.security.model.User;
import com.jfaker.framework.security.web.validate.UserValidator;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;

/**
 * UserController
 * @author yuqs
 * @since 0.1
 */
public class UserController extends Controller {
	public void index() {
		User user = getModel(User.class);
		Page<User> page = User.dao.paginate(getParaToInt("pageNo", 1), 10, user);
		setAttr("page", page);
		keepModel(User.class);
		render("userList.jsp");
	}
	
	public void add() {
		setAttr("roles", Role.dao.getAll());
		render("userAdd.jsp");
	}
	
	public void edit() {
		setAttr("user", User.dao.get(getParaToInt()));
		List<Role> roles = Role.dao.getAll();
		List<Role> rs = User.dao.getRoles(getParaToInt());
		for(Role role : roles) {
			for(Role r : rs) {
				if(role.getInt("id").intValue() == r.getInt("id").intValue())
				{
					role.put("selected", 1);
				}
				if(role.get("selected") == null)
				{
					role.put("selected", 0);
				}
			}
		}
		setAttr("roles", roles);
		render("userEdit.jsp");
	}
	
	public void view() {
		setAttr("user", User.dao.get(getParaToInt()));
		setAttr("roles", User.dao.getRoles(getParaToInt()));
		render("userView.jsp");
	}
	
	@Before({UserValidator.class, Tx.class})
	public void save() {
		Integer[] orderIndexs = getParaValuesToInt("orderIndexs");
		User model = getModel(User.class);
		if (StringUtils.isNotBlank(model.getStr("plainPassword"))) {
			model.entryptPassword(model);
		}
		model.save();
		if(orderIndexs != null) {
			for(Integer orderIndex : orderIndexs) {
				User.dao.insertCascade(model.getInt("id"), orderIndex);
			}
		}
		redirect("/security/user");
	}
	
	@Before({UserValidator.class, Tx.class})
	public void update() {
		Integer[] orderIndexs = getParaValuesToInt("orderIndexs");
		User model = getModel(User.class);
		if (StringUtils.isNotBlank(model.getStr("plainPassword"))) {
			model.entryptPassword(model);
		}
		model.update();
		User.dao.deleteCascade(model.getInt("id"));
		if(orderIndexs != null) {
			for(Integer orderIndex : orderIndexs) {
				User.dao.insertCascade(model.getInt("id"), orderIndex);
			}
		}
		redirect("/security/user");
	}
	
	@Before(Tx.class)
	public void delete() {
		User.dao.deleteCascade(getParaToInt());
		User.dao.deleteById(getParaToInt());
		redirect("/security/user");
	}
	
	@ActionKey("/login")
	public void login() {
		render("login.jsp");
	}
	
	public void dologin() {
		String error = "";
		String username = getPara("user.username");
		String password = getPara("user.password");
		if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
			error = "账号或密码不能为空";
		}
		if(StringUtils.isEmpty(error)) {
			Subject subject = SecurityUtils.getSubject();
			UsernamePasswordToken token = new UsernamePasswordToken(username, password);
			try {
				subject.login(token);
			} catch(UnknownAccountException ue) {
				token.clear();
				error = "登录失败，您输入的账号不存在";
			} catch(IncorrectCredentialsException ie) {
				ie.printStackTrace();
				token.clear();
				error = "登录失败，密码不匹配";
			} catch(RuntimeException re) {
				re.printStackTrace();
				token.clear();
				error = "登录失败";
			}
		}
		if(StringUtils.isEmpty(error)) {
			redirect("/");
		} else {
			keepModel(User.class);
			setAttr("error", error);
			render("login.jsp");
		}
	}
}


