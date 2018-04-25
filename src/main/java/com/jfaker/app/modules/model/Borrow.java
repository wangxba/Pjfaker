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
package com.jfaker.app.modules.model;

import com.jfinal.plugin.activerecord.Model;

/**
 * 借款模型
 * @author yuqs
 * @since 0.1
 */
public class Borrow extends Model<Borrow> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3366223825642786110L;
	public static final Borrow dao = new Borrow();
	
	public Borrow findByOrderId(String orderId) {
		return Borrow.dao.findFirst("select * from flow_borrow where orderId = ? ", orderId);
	}
}
