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
package com.jfaker.framework.flow.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

/**
 * 审批表单模型
 * @author yuqs
 * @since 0.1
 */
public class Approval extends Model<Approval> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2386449387832849587L;
	public static final Approval dao = new Approval();
	
	public List<Approval> findByFlow(String orderId, String taskName) {
		return Approval.dao.find("select * from flow_approval where orderId = ? and taskName = ?", orderId, taskName);
	}
}
