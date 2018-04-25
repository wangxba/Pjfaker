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
package com.jfaker.framework.security.shiro;

import org.apache.shiro.web.env.IniWebEnvironment;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;

/**
 * 自定义shiro的初始化web环境
 * @author yuqs
 * @since 0.1
 */
public class ShiroIniWebEnvironment extends IniWebEnvironment {
	public void init() {
		super.init();
		FilterChainResolver resolver = getFilterChainResolver();
		if(resolver != null && resolver instanceof PathMatchingFilterChainResolver) {
			PathMatchingFilterChainResolver pathResolver = (PathMatchingFilterChainResolver)resolver;
			ShiroPlugin.setFilterChainManager(pathResolver.getFilterChainManager());
		}
	}
}
