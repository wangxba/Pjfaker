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
package com.jfaker.framework.security.web;

import java.util.ArrayList;
import java.util.List;

import com.jfaker.framework.security.model.Org;
import com.jfaker.framework.security.model.TreeNode;
import com.jfaker.framework.security.model.User;
import com.jfinal.core.Controller;

/**
 * 
 * @author yuqs
 * @since 0.1
 */
public class SecurityTreeController extends Controller {
	public void orgTree() {
		List<Org> orgs = Org.dao.getByParent(getParaToInt("parentId"));
		List<TreeNode> trees = new ArrayList<TreeNode>();
		TreeNode node = null;
		for(Org org : orgs) {
			node = new TreeNode();
			node.setId(org.getInt("id"));
			Integer parentOrg = org.getInt("parent_org");
			node.setpId(parentOrg == null ? Org.ROOT_ORG_ID : parentOrg);
			node.setName(org.getStr("name"));
			if(parentOrg == null) {
				node.setOpen(true);
			}
			trees.add(node);
		}
		renderJson(trees);
	}
	
	public void orgUserTree() {
		List<Org> orgs = Org.dao.getByParent(getParaToInt("parentId"));
		List<TreeNode> trees = new ArrayList<TreeNode>();
		TreeNode node = null;
		for(Org org : orgs) {
			node = new TreeNode();
			node.setId(org.getInt("id"));
			Integer parentOrg = org.getInt("parent_org");
			node.setpId(parentOrg == null ? Org.ROOT_ORG_ID : parentOrg);
			node.setName(org.getStr("name"));
			if(parentOrg == null) {
				node.setOpen(true);
			}
			trees.add(node);
		}
		
		List<User> users = User.dao.getByOrg(getParaToInt("parentId"));
		for(User user : users) {
			node = new TreeNode();
			node.setId(user.getInt("id"));
			Integer parentOrg = user.getInt("org");
			node.setpId(parentOrg == null ? Org.ROOT_ORG_ID : parentOrg);
			node.setName(user.getStr("fullname"));
			trees.add(node);
		}
		renderJson(trees);
	}
}
