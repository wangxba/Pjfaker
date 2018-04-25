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
 * 部门模型
 * @author yuqs
 * @since 0.1
 */
public class Org extends Model<Org> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5018575558755643041L;
	//根部门ID号默认为0
	public static final Integer ROOT_ORG_ID = 0;
	public static final Org dao = new Org();
	
	public Page<Org> paginate (int pageNumber, int pageSize, String name) {
		String sql = "from sec_org o left join sec_org op on o.parent_org=op.id ";
		if(StringUtils.isNotEmpty(name)) {
			sql += " where o.name like '%" + name + "%' ";
		}
		sql += " order by id desc";
		return paginate(pageNumber, pageSize, "select o.*,op.name as parentName", sql);
	}
	
	public Org get(Integer id) {
		return Org.dao.findFirst("select o.*,po.id as parentId, po.name as parentName from sec_org o inner join sec_org po on o.parent_org=po.id where o.id=?", id);
	}
	
	public List<Org> getByParent(Integer parentId) {
		String sql = "select o.*,po.id as parentId, po.name as parentName from sec_org o inner join sec_org po on o.parent_org=po.id ";
		if(parentId != null && parentId > 0) {
			sql += " where o.parent_org=" + parentId;
		}
		return find(sql);
	}
}
