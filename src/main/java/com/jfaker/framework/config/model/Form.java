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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfaker.framework.utils.DateUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * 表单模型
 * @author yuqs
 * @since 0.1
 */
public class Form extends Model<Form> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8945469242429605208L;
	private static final Logger log = LoggerFactory.getLogger(Form.class);
	private static final String TABLE_PREFIX = "TBL_";
	public static final Form dao = new Form();
	
	public Page<Form> paginate (int pageNumber, int pageSize, String name) {
		String from = " from df_form ";
		if(name != null && name.length() > 0) {
			from += " where name like '%" + name + "%' ";
		}
		from += " order by id desc ";
		return paginate(pageNumber, pageSize, "select *", from);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, String> process(Form model, Map<String, Object> datas) {
		Map<String, String> nameMap = new HashMap<String, String>();
		if(datas == null) {
			throw new NullPointerException();
		}
		String tableName = getTableName(model);
		List<Field> fields = new ArrayList<Field>();
		for(Map.Entry<String, Object> entry : datas.entrySet()) {
			Map<String, String> fieldInfo = (Map<String, String>)entry.getValue();
			Field field = new Field();
			field.set("name", fieldInfo.get("fieldname"));
			field.set("title", fieldInfo.get("title"));
			field.set("plugins", fieldInfo.get("plugins"));
			field.set("flow", fieldInfo.get("fieldflow"));
			field.set("tableName", tableName);
			field.set("formId", model.get("id"));
			field.set("type", fieldInfo.get("orgtype"));
			fields.add(field);
			nameMap.put(entry.getKey(), fieldInfo.get("fieldname"));
		}
		model.set("fieldNum", model.getInt("fieldNum") + fields.size());
		String check = "select count(*) from " + tableName + " where id = 1";
		boolean isExists = true;
		try {
			Db.queryLong(check);
		} catch(Exception e) {
			isExists = false;
		}
		StringBuilder sql = new StringBuilder();
		try {
			List<String> fieldNames = Db.query("select name from df_field where tableName=?", tableName);
			if(!isExists) {
				sql.append("CREATE TABLE ").append(tableName).append(" (");
				sql.append("ID INT NOT NULL AUTO_INCREMENT,");
				for(Field field : fields) {
					sql.append(field.getStr("name"));
					sql.append(" ").append(fieldSQL(field)).append(",");
				}

				sql.append("FORMID INT NOT NULL,");
				sql.append("UPDATETIME VARCHAR(20),");
				sql.append("ORDERID VARCHAR(50),");
				sql.append("TASKID  VARCHAR(50),");
				sql.append("PRIMARY KEY (ID)");
				sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
				Db.update(sql.toString());
			} else {
				if(fields.size() > 0) {
					for(Field field : fields) {
						if(StringUtils.isNotEmpty(field.getStr("name")) && 
								!fieldNames.contains(field.getStr("name"))) {
							Db.update("ALTER TABLE " + tableName + " ADD COLUMN " + field.getStr("name") + fieldSQL(field));
						}
					}
				}
			}
			
			for(Field field : fields) {
				if(!fieldNames.contains(field.getStr("name"))) {
					field.save();
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return nameMap;
	}
	
	public Record getDataByOrderId(Form model, String orderId) {
		String tableName = getTableName(model);
		List<String> fieldNames = Db.query("select name from df_field where tableName=?", tableName);
		StringBuilder sql = new StringBuilder("select FORMID, UPDATETIME, ORDERID, TASKID ");
		if(fieldNames != null && fieldNames.size() > 0) {
			for(String fieldName : fieldNames) {
				sql.append(",").append(fieldName);
			}
		}
		sql.append(" from ");
		sql.append(tableName);
		sql.append(" where orderId = ? order by UPDATETIME");
		return Db.findFirst(sql.toString(), orderId);
	}
	
	public List<Record> getDatasByOrderId(Form model, String orderId) {
		String tableName = getTableName(model);
		List<String> fieldNames = Db.query("select name from df_field where tableName=?", tableName);
		StringBuilder sql = new StringBuilder("select FORMID, UPDATETIME, ORDERID, TASKID ");
		if(fieldNames != null && fieldNames.size() > 0) {
			for(String fieldName : fieldNames) {
				sql.append(",").append(fieldName);
			}
		}
		sql.append(" from ");
		sql.append(tableName);
		sql.append(" where orderId = ?");
		return Db.find(sql.toString(), orderId);
	}
	
	public void submit(Form model, List<Field> fields, Map<String, String[]> paraMap, String orderId, String taskId) {
		String tableName = getTableName(model);
		StringBuilder beforeSql = new StringBuilder();
		StringBuilder afterSql = new StringBuilder();
		beforeSql.append("INSERT INTO ").append(tableName);
		beforeSql.append(" (FORMID, UPDATETIME, ORDERID, TASKID ");
		afterSql.append(") values (?,?,?,?");
		List<Object> datas = new ArrayList<Object>();
		datas.add(model.getInt("id"));
		datas.add(DateUtils.getCurrentTime());
		datas.add(orderId);
		datas.add(taskId);
		if(fields != null) {
			StringBuilder fieldSql = new StringBuilder();
			StringBuilder valueSql = new StringBuilder();
			for(Field field : fields) {
				String[] data = paraMap.get(field.get("name"));
				if(data == null) {
					continue;
				}
				fieldSql.append(",").append(field.get("name")+"");
				valueSql.append(",?");
				if(data.length == 1) {
					datas.add(data[0]);
				} else {
					String dataArr = ArrayUtils.toString(data);
					if(dataArr.length() > 1) {
						datas.add(dataArr.substring(1, dataArr.length() - 1));
					}
				}
			}
			if(fieldSql.length() > 0) {
				beforeSql.append(fieldSql.toString());
				afterSql.append(valueSql.toString());
			}
		}
		afterSql.append(")");
		beforeSql.append(afterSql.toString());
		String sql = beforeSql.toString();
		log.info("dynamic sql is:" + sql);
		log.info(datas.toString());
		Db.update(sql, datas.toArray());
	}
	
	private String getTableName(Form model) {
		return TABLE_PREFIX + model.getStr("name");
	}
	
	private String fieldSQL(Field field) {
		String plugins = field.getStr("plugins");
        if(plugins.equalsIgnoreCase("textarea") 
        		|| plugins.equalsIgnoreCase("listctrl")) {
            return " TEXT";
        } else if(plugins.equalsIgnoreCase("text")) {
        	String type = field.getStr("type");
        	if("text".equals(type)) {
        		return " VARCHAR(255) NOT NULL DEFAULT ''";
        	} else if("int".equals(type)) {
        		return " INT NOT NULL DEFAULT 0";
        	} else if("float".equals(type)) {
        		return " FLOAT ";
        	} else {
        		return " VARCHAR(255) NOT NULL DEFAULT ''";
        	}
        } else if(plugins.equalsIgnoreCase("radios")) {
            return " VARCHAR(255) NOT NULL DEFAULT ''";
        } else {
            return " VARCHAR(255) NOT NULL DEFAULT ''";
        }
	}
}
