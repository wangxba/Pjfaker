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
package com.jfaker.framework.security.web.validate;

import com.jfaker.framework.security.model.Resource;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

/**
 * ResourceValidator.
 */
public class ResourceValidator extends Validator {
	
	protected void validate(Controller controller) {
		validateRequiredString("resource.name", "nameMsg", "请输入资源名称!");
		validateRequiredString("resource.source", "sourceMsg", "请输入资源值!");
	}
	
	protected void handleError(Controller controller) {
		controller.keepModel(Resource.class);
		
		String actionKey = getActionKey();
		if (actionKey.equals("/security/resource/save"))
			controller.render("resourceAdd.jsp");
		else if (actionKey.equals("/security/resource/update"))
			controller.render("resourceAdd.jsp");
	}
}
