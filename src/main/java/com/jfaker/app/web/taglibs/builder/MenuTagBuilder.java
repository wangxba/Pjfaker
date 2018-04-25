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
package com.jfaker.app.web.taglibs.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;

import com.jfaker.framework.security.model.Menu;
import com.jfaker.framework.security.shiro.ShiroUtils;
import com.jfaker.framework.web.TagBuilder;
import com.jfaker.framework.web.TagDTO;

/**
 * 自定义菜单标签处理类。 根据当前认证实体获取允许访问的所有菜单，并输出特定导航菜单的html
 * @author yuqs
 * @since 0.1
 */
public class MenuTagBuilder implements TagBuilder {
	public static MenuTagBuilder builder = new MenuTagBuilder();
	// Servlet的上下文
	private ServletContext servletContext = null;

	@Override
	public String build(TagDTO dto) {
		this.servletContext = dto.getServletContext();
		StringBuffer buffer = new StringBuffer();
		// 获取所有可允许访问的菜单列表
		List<Menu> menus = getAllowedAccessMenu();
		// 循环迭代菜单列表，构成ID、List结构的Map
		Map<Integer, List<Menu>> menuMaps = buildMenuTreeMap(menus);
		// 根据Map构造符合左栏菜单显示的html
		buildMenuTreeFolder(buffer, menuMaps, Menu.ROOT_MENU);
		return buffer.toString();
	}

	/**
	 * 循环迭代菜单列表，构成ID、List结构的Map
	 * 
	 * @param menus
	 * @return
	 */
	private Map<Integer, List<Menu>> buildMenuTreeMap(List<Menu> menus) {
		Map<Integer, List<Menu>> menuMap = new TreeMap<Integer, List<Menu>>();
		for (Menu menu : menus) {
			/**
			 * 判断是否有上一级菜单，如果有，则添加到上一级菜单的Map中去 如果没有上一级菜单，把该菜单作为根节点
			 */
			Integer parentMenuId = menu.getInt("parent_menu") == null ? Menu.ROOT_MENU
					: menu.getInt("parent_menu");
			if (!menuMap.containsKey(parentMenuId)) {
				List<Menu> subMenus = new ArrayList<Menu>();
				subMenus.add(menu);
				menuMap.put(parentMenuId, subMenus);
			} else {
				List<Menu> subMenus = menuMap.get(parentMenuId);
				subMenus.add(menu);
				menuMap.put(parentMenuId, subMenus);
			}
		}
		return menuMap;
	}

	/**
	 * 获取当前登录账号所有允许访问的菜单列表
	 * 
	 * @return
	 */
	private List<Menu> getAllowedAccessMenu() {
		return Menu.dao.getAllowedAccessMenus(ShiroUtils.getUserId());
	}

	/**
	 * 构建菜单目录
	 * 
	 * @param buffer
	 *            html信息
	 * @param menuMap
	 * @param menuId
	 */
	private void buildMenuTreeFolder(StringBuffer buffer,
			Map<Integer, List<Menu>> menuMap, Integer menuId) {
		List<Menu> treeFolders = menuMap.get(menuId);
		if (treeFolders == null) {
			return;
		}
		for (Menu menu : treeFolders) {
			List<Menu> treeNodes = menuMap.get(menu.getInt("id"));
			if((treeNodes == null || treeNodes.isEmpty()) && StringUtils.isEmpty(menu.getStr("description"))) {
				continue;
			}
			buffer.append("<dl>");
			buffer.append("<dt id='sidebar_goods_manage' class='hou'><i class='pngFix'></i>");
			buffer.append(menu.get("name")+"");
			buffer.append("</dt>");
			buffer.append("<dd>");
			buffer.append("<ul>");
			/**
			 * 有子菜单时，将子菜单添加到当前节点上
			 */
			buildMenuTreeNode(buffer, treeNodes);
			buffer.append("</ul>");
			buffer.append("</dd>");
			buffer.append("</dl>");
		}
	}

	/**
	 * 循环子菜单资源，并构造tree型html语句
	 * 
	 * @param buffer
	 * @param treeNodes
	 */
	private void buildMenuTreeNode(StringBuffer buffer, List<Menu> treeNodes) {
		if (treeNodes == null) {
			return;
		}
		for (Menu menu : treeNodes) {
			buffer.append("<li>");
			buffer.append("<a href='");
			buffer.append(servletContext.getContextPath());
			buffer.append(menu.getStr("description"));
			buffer.append("' target='mainFrame' ");
			buffer.append(">");
			buffer.append(menu.getStr("name"));
			buffer.append("</a>");
			buffer.append("</li>");
		}
	}
}
