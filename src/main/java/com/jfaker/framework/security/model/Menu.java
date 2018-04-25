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
package com.jfaker.framework.security.model;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

/**
 * 菜单模型
 * @author yuqs
 * @since 0.1
 */
public class Menu extends Model<Menu> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8781209142247805658L;
	public static final Menu dao = new Menu();
	//菜单资源的根菜单标识为0
	public static final Integer ROOT_MENU = 0;
	public Page<Menu> paginate (int pageNumber, int pageSize, String name) {
		String sql = "from sec_menu m left join sec_menu pm on m.parent_menu=pm.id";
		if(StringUtils.isNotEmpty(name)) {
			sql += " where m.name like '%" + name + "%' ";
		}
		sql += " order by id desc";
		return paginate(pageNumber, pageSize, "select m.*,pm.name as parentName", sql);
	}
	
	public Menu getParent() {
		return Menu.dao.findById(get("parent_menu"));
	}
	
	public Menu get(int id) {
		return Menu.dao.findFirst("select m.*,pm.id as parentId, pm.name as parentName from sec_menu m inner join sec_menu pm on m.parent_menu=pm.id where m.id=?", id);
	}
	
	public List<Menu> getAllowedAccessMenus(Integer userId) {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select * from (");
		//获取Sec_Menu表中定义且未关联资源表Sec_Resource的所有菜单列表
		sqlBuffer.append(" select m.id,m.name,m.parent_menu,m.description,m.orderby from sec_menu m ");
		sqlBuffer.append(" where not exists (select re.id from sec_resource re where re.menu = m.id)" );
		sqlBuffer.append(" union ");
		//获取Sec_Resource表中已关联且未设置权限的菜单列表
		sqlBuffer.append(" select m.id,m.name,m.parent_menu,re.source as description,m.orderby from sec_resource re ");
		sqlBuffer.append(" left outer join sec_menu m on re.menu = m.id  ");
		sqlBuffer.append(" where re.menu is not null and not exists (select ar.authority_id from sec_authority_resource ar where ar.resource_id = re.id)");
		sqlBuffer.append(" union ");
		//获取Sec_Resource表中已关联且设置权限，并根据当前登录账号拥有相应权限的菜单列表
		sqlBuffer.append(" select m.id,m.name,m.parent_menu,re.source as description,m.orderby from sec_user u ");
		sqlBuffer.append(" left outer join sec_role_user ru on u.id=ru.user_id ");
		sqlBuffer.append(" left outer join sec_role r on ru.role_id=r.id ");
		sqlBuffer.append(" left outer join sec_role_authority ra on r.id = ra.role_id ");
		sqlBuffer.append(" left outer join sec_authority a on ra.authority_id = a.id ");
		sqlBuffer.append(" left outer join sec_authority_resource ar on a.id = ar.authority_id ");
		sqlBuffer.append(" left outer join sec_resource re on ar.resource_id = re.id ");
		sqlBuffer.append(" left outer join sec_menu m on re.menu = m.id ");
		sqlBuffer.append(" where u.id=? and re.menu is not null ");
		sqlBuffer.append(") tbl order by orderby");
		
		return find(sqlBuffer.toString(), userId);
	}
}
