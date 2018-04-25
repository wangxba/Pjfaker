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

import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfaker.framework.security.model.Resource;
import com.jfinal.plugin.IPlugin;

/**
 * 支持Shiro的插件
 * @author yuqs
 * @since 0.1
 */
public class ShiroPlugin implements IPlugin {
	private static Logger log = LoggerFactory.getLogger(ShiroPlugin.class);
	public static final String PREMISSION_FORMAT = "perms[\"{0}\"]";
	private static FilterChainManager manager = null;
	@Override
	public boolean start() {
		if(manager == null) return false;
		List<Resource> resources = Resource.dao.getWithAuthAll();
		for(Resource resource : resources) {
			String source = resource.getStr("source");
			String authority = resource.getStr("authorityName");
			if(StringUtils.isEmpty(source)) {
				continue;
			}
			if(source.indexOf(";") != -1) {
				String[] sources = source.split(";");
				for(String singleSource : sources) {
					createChain(manager, singleSource, authority);
				}
			} else {
				createChain(manager, source, authority);
			}
		}
		manager.createChain("/**", "user");
		return true;
	}

	@Override
	public boolean stop() {
		return true;
	}
	
	private void createChain(FilterChainManager manager, String key, String value) {
	    log.info("add authority url[url=" + key + "\tvalue=" + value + "]");
	    manager.createChain(key, MessageFormat.format(PREMISSION_FORMAT, value));
	}

	public static void setFilterChainManager(FilterChainManager manager) {
		ShiroPlugin.manager = manager;
	}
}
