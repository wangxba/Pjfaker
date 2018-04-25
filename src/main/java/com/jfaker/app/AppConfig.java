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
package com.jfaker.app;

import org.snaker.jfinal.plugin.SnakerPlugin;

import com.jfaker.app.modules.model.Borrow;
import com.jfaker.app.modules.web.BorrowController;
import com.jfaker.app.modules.web.LeaveController;
import com.jfaker.app.web.CommonController;
import com.jfaker.framework.flow.model.Approval;
import com.jfaker.framework.flow.web.FlowController;
import com.jfaker.framework.flow.web.ProcessController;
import com.jfaker.framework.flow.web.TaskController;
import com.jfaker.framework.flow.web.SurrogateController;
import com.jfaker.framework.config.model.Dict;
import com.jfaker.framework.config.model.DictItem;
import com.jfaker.framework.config.web.DictController;
import com.jfaker.framework.config.model.Field;
import com.jfaker.framework.config.model.Form;
import com.jfaker.framework.config.web.FieldController;
import com.jfaker.framework.config.web.FormController;
import com.jfaker.framework.security.model.Authority;
import com.jfaker.framework.security.model.Menu;
import com.jfaker.framework.security.model.Org;
import com.jfaker.framework.security.model.Resource;
import com.jfaker.framework.security.model.Role;
import com.jfaker.framework.security.model.User;
import com.jfaker.framework.security.shiro.ShiroPlugin;
import com.jfaker.framework.security.web.AuthorityController;
import com.jfaker.framework.security.web.MenuController;
import com.jfaker.framework.security.web.OrgController;
import com.jfaker.framework.security.web.ResourceController;
import com.jfaker.framework.security.web.RoleController;
import com.jfaker.framework.security.web.SecurityTreeController;
import com.jfaker.framework.security.web.UserController;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.render.ViewType;

/**
 * 应用总配置
 * @author yuqs
 * @since 0.1
 */
public class AppConfig extends JFinalConfig {
	/**
	 * 配置常量
	 */
	public void configConstant(Constants me) {
		loadPropertyFile("jfinal.properties");
		me.setDevMode(getPropertyToBoolean("devMode", false));
//	    me.setError404View("/common/404.jsp");
//	    me.setError500View("/common/500.jsp");
	    
	    me.setViewType(ViewType.JSP);
	    me.setBaseViewPath("WEB-INF/content/");
	}
	
	/**
	 * 配置路由
	 */
	public void configRoute(Routes me) {
		me.add("/", CommonController.class, "/system");
		me.add("/security/user", UserController.class, "/security");
		me.add("/security/org", OrgController.class, "/security");
		me.add("/security/authority", AuthorityController.class, "/security");
		me.add("/security/role", RoleController.class, "/security");
		me.add("/security/resource", ResourceController.class, "/security");
		me.add("/security/menu", MenuController.class, "/security");
		me.add("/security/tree", SecurityTreeController.class);
		
		me.add("/config/form", FormController.class, "/config");
		me.add("/config/field", FieldController.class, "/config");
		
		me.add("/config/dictionary", DictController.class, "/config");
		
		me.add("/snaker/process", ProcessController.class, "/snaker");
		me.add("/snaker/surrogate", SurrogateController.class, "/snaker");
		me.add("/snaker/task", TaskController.class, "/snaker");
		me.add("/snaker/flow", FlowController.class, "/snaker");
		
		me.add("/flow/leave", LeaveController.class, "/flow/leave");
		me.add("/flow/borrow", BorrowController.class, "/flow/borrow");
	}
	
	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {
		// 配置C3p0数据库连接池插件
		C3p0Plugin c3p0Plugin = new C3p0Plugin(getProperty("jdbcUrl"), getProperty("user"), getProperty("password").trim());
		me.add(c3p0Plugin);
		
		ScriptsPlugin scriptsPlugin = new ScriptsPlugin(c3p0Plugin);
		me.add(scriptsPlugin);
		
		// 配置ActiveRecord插件
		ActiveRecordPlugin arp = new ActiveRecordPlugin(c3p0Plugin);
		me.add(arp);
		arp.addMapping("sec_user", User.class);
		arp.addMapping("sec_org", Org.class);
		arp.addMapping("sec_role", Role.class);
		arp.addMapping("sec_authority", Authority.class);
		arp.addMapping("sec_resource", Resource.class);
		arp.addMapping("sec_menu", Menu.class);
		
		arp.addMapping("conf_dictionary", Dict.class);
		arp.addMapping("conf_dictitem", DictItem.class);
		
		arp.addMapping("df_form", Form.class);
		arp.addMapping("df_field", Field.class);
		
		arp.addMapping("flow_approval", Approval.class);
		arp.addMapping("flow_borrow", Borrow.class);
		
		// 配置Snaker插件
		SnakerPlugin snakerPlugin = new SnakerPlugin(c3p0Plugin, this.prop.getProperties());
		me.add(snakerPlugin);
		
		// 配置shiro插件
		ShiroPlugin shiroPlugin = new ShiroPlugin();
		me.add(shiroPlugin);
	}
	
	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {
		
	}
	
	/**
	 * 配置处理器
	 */
	public void configHandler(Handlers me) {
		me.add(new ContextPathHandler("BASE_PATH"));
	}
	
	public static void main(String[] args) {
		JFinal.start("WebContent", 8889, "/jfaker", 5);
	}
}
