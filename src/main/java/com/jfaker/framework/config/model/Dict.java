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
package com.jfaker.framework.config.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

/**
 * 字典模型
 * @author yuqs
 * @since 0.1
 */
public class Dict extends Model<Dict> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3290069328905665226L;
	public static final Dict dao = new Dict();
	
	public Page<Dict> paginate (int pageNumber, int pageSize, String name) {
		String from = " from conf_dictionary ";
		if(name != null && name.length() > 0) {
			from += " where name like '%" + name + "%' ";
		}
		from += " order by id desc ";
		return paginate(pageNumber, pageSize, "select *", from);
	}
	
	public Map<String, String> getItemsByName(String name) { 
		List<DictItem> items = DictItem.dao.getAll(name);
		if(items == null || items.isEmpty()) return Collections.emptyMap();
        Map<String, String> dicts = new TreeMap<String, String>();
        for(DictItem item : items) {
            dicts.put(item.getStr("code"), item.getStr("name"));
        }
        return dicts;
	}
}
