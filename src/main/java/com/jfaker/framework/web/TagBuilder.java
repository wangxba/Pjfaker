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
package com.jfaker.framework.web;

/**
 * 标签构建接口
 * @author yuqs
 * @since 0.1
 */
public interface TagBuilder {
	/**
	 * 根据标签变量构建控件元素
	 * @param dto 标签之间数据传输对象，包括标签属性、值以及常用的上下文
	 * @return
	 */
	public String build(TagDTO dto);
}
