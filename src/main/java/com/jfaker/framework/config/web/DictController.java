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
package com.jfaker.framework.config.web;

import com.jfaker.framework.config.model.Dict;
import com.jfaker.framework.config.model.DictItem;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.tx.Tx;

/**
 * DictController
 * @author yuqs
 * @since 0.1
 */
public class DictController extends Controller {
	public void index() {
		String name = getPara("name");
		setAttr("page", Dict.dao.paginate(getParaToInt("pageNo", 1), 10, name));
		setAttr("name", name);
		render("dictList.jsp");
	}
	
	public void add() {
		render("dictAdd.jsp");
	}
	
	public void view() {
		int dictId = getParaToInt();
		setAttr("dict", Dict.dao.findById(dictId));
		setAttr("dictItems", DictItem.dao.getAll(dictId));
		render("dictView.jsp");
	}
	
	public void edit() {
		int dictId = getParaToInt();
		setAttr("dict", Dict.dao.findById(dictId));
		setAttr("dictItems", DictItem.dao.getAll(dictId));
		render("dictEdit.jsp");
	}
	
	@Before(Tx.class)
	public void save() {
		Dict model = getModel(Dict.class);
		model.save();
		String[] itemNames = getParaValues("itemNames");
		Integer[] orderbys = getParaValuesToInt("orderbys");
		String[] codes = getParaValues("codes");
		for(int i = 0; i < itemNames.length; i++) {
			DictItem ci = new DictItem();
			ci.set("name", itemNames[i]);
			ci.set("orderby", orderbys[i]);
			ci.set("code", codes[i]);
			ci.set("dictionary", model.get("id"));
			ci.save();
		}
		redirect("/security/dict");
	}
	
	@Before(Tx.class)
	public void update() {
		Dict model = getModel(Dict.class);
		model.update();
		
		DictItem.dao.deleteByDictId(model.getInt("id"));
		String[] itemNames = getParaValues("itemNames");
		Integer[] orderbys = getParaValuesToInt("orderbys");
		String[] codes = getParaValues("codes");
		for(int i = 0; i < itemNames.length; i++) {
			DictItem ci = new DictItem();
			ci.set("name", itemNames[i]);
			ci.set("orderby", orderbys[i]);
			ci.set("code", codes[i]);
			ci.set("dictionary", model.get("id"));
			ci.save();
		}
		redirect("/security/dict");
	}
	
	public void delete() {
		Dict.dao.deleteById(getParaToInt());
		redirect("/security/dict");
	}
}


