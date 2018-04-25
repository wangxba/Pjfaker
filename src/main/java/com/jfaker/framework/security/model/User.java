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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.jfaker.framework.utils.Digests;
import com.jfaker.framework.utils.EncodeUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

/**
 * 用户模型
 * @author yuqs
 * @since 0.1
 */
public class User extends Model<User> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8781209142247805658L;
	public static final String HASH_ALGORITHM = "SHA-1";
	public static final int HASH_INTERATIONS = 1024;
	private static final int SALT_SIZE = 8;
	public static final User dao = new User();
	
	public Page<User> paginate (int pageNumber, int pageSize, User user) {
		StringBuilder from = new StringBuilder("from sec_user u left join sec_org o on u.org=o.id where 1=1 ");
		List<String> params = new ArrayList<String>();
		String username = user.getStr("username");
		String fullname = user.getStr("fullname");
		if(StringUtils.isNotEmpty(username)) {
			from.append(" and u.username=? ");
			params.add(username);
		}
		if(StringUtils.isNotEmpty(fullname)) {
			from.append(" and u.fullname=? ");
			params.add(fullname);
		}
		from.append(" order by id desc");
		return paginate(pageNumber, pageSize, "select u.*,o.name as orgName", from.toString(), params.toArray());
	}
	
	public User getByName(String name) {
		return User.dao.findFirst("select u.*,o.name as orgName from sec_user u left join sec_org o on u.org=o.id where u.username=?", name);
	}
	
	public User get(Integer id) {
		return User.dao.findFirst("select u.*,o.name as orgName from sec_user u left join sec_org o on u.org=o.id where u.id=?", id);
	}
	
	public List<User> getByOrg(Integer orgId) {
		String sql = "select u.*,o.name as orgName from sec_user u left join sec_org o on u.org=o.id ";
		if(orgId != null && orgId > 0) {
			sql += " where u.org=" + orgId;
		}
		return User.dao.find(sql);
	}
	
	public List<Role> getRoles(Integer id) {
		return Role.dao.find("select r.* from sec_role r "
				+ "LEFT JOIN sec_role_user ru ON r.id=ru.role_id "
				+ "LEFT JOIN sec_user u ON u.id=ru.user_id "
				+ "WHERE u.id=?", id);
	}
	
	public void insertCascade(Integer id, Integer roleId) {
		Db.update("insert into sec_role_user (user_id, role_id) values (?,?)", id, roleId);
	}
	
	public void deleteCascade(Integer id) {
		Db.update("delete from sec_role_user where user_id = ?", id);
	}
	
	/**
	 * 根据用户ID查询该用户所拥有的权限列表
	 * @param userId
	 * @return
	 */
	public List<String> getAuthoritiesName(Integer userId) {
		String sql = "select a.name from sec_user u " + 
					" left outer join sec_role_user ru on u.id=ru.user_id " + 
					" left outer join sec_role r on ru.role_id=r.id " + 
					" left outer join sec_role_authority ra on r.id = ra.role_id " + 
					" left outer join sec_authority a on ra.authority_id = a.id " +                     
					" where u.id=? ";
		return Db.query(sql, userId);
	}
	
	/**
	 * 根据用户ID查询该用户所拥有的角色列表
	 * @param userId
	 * @return
	 */
	public List<String> getRolesName(Integer userId) {
		String sql = "select r.name from sec_user u " + 
					" left outer join sec_role_user ru on u.id=ru.user_id " + 
					" left outer join sec_role r on ru.role_id=r.id " + 
					" where u.id=? ";
		return Db.query(sql, userId);
	}
	
	/**
	 * 设定安全的密码，生成随机的salt并经过1024次 sha-1 hash
	 */
	public void entryptPassword(User user) {
		byte[] salt = Digests.generateSalt(SALT_SIZE);
		user.set("salt", EncodeUtils.hexEncode(salt));

		byte[] hashPassword = Digests.sha1(user.getStr("plainPassword").getBytes(), salt, HASH_INTERATIONS);
		user.set("password", EncodeUtils.hexEncode(hashPassword));
	}
}
