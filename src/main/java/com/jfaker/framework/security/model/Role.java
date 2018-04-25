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

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

/**
 * 角色模型
 * @author yuqs
 * @since 0.1
 */
public class Role extends Model<Role> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8781209142247805658L;
	public static final Role dao = new Role();
	
	public Page<Role> paginate (int pageNumber, int pageSize, String name) {
		String sql = "from sec_role ";
		if(StringUtils.isNotEmpty(name)) {
			sql += " where name like '%" + name + "%' ";
		}
		sql += " order by id desc ";
		return paginate(pageNumber, pageSize, "select *", sql);
	}
	
	public List<Authority> getAuthorities(Integer id) {
		return Authority.dao.find("select a.* from sec_authority a "
				+ "LEFT JOIN sec_role_authority ra ON a.id=ra.authority_id "
				+ "LEFT JOIN sec_role r ON r.id=ra.role_id "
				+ "WHERE r.id=?", id);
	}
	
	public List<Role> getAll() {
		return Role.dao.find("select * from sec_role");
	}
	
	public void insertCascade(Integer id, Integer authorityId) {
		Db.update("insert into sec_role_authority (role_id, authority_id) values (?,?)", id, authorityId);
	}
	
	public void deleteCascade(Integer id) {
		Db.update("delete from sec_role_authority where role_id = ?", id);
	}
}
