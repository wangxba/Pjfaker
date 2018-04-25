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

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

/**
 * 字典项模型
 * @author yuqs
 * @since 0.1
 */
public class DictItem extends Model<DictItem> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2706867741882170405L;
	public static final DictItem dao = new DictItem();
	
	public List<DictItem> getAll(int dictId) {
		return DictItem.dao.find("select * from conf_dictitem where dictionary = ? order by orderby", dictId);
	}
	
	public List<DictItem> getAll(String dictName) {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select ci.id,ci.name,ci.code,ci.orderby,ci.dictionary,ci.description from conf_dictitem ci ");
		sqlBuffer.append(" left outer join conf_dictionary cd on cd.id = ci.dictionary ");
		sqlBuffer.append(" where cd.name = ? order by ci.orderby");
		return DictItem.dao.find(sqlBuffer.toString(), dictName);
	}
	
	public void deleteByDictId(int dictId) {
		Db.update("delete from conf_dictitem where dictionary = ?", dictId);
	}
}
