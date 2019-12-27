/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.pf4j.spring.boot.ext.utils;

import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;

import org.pf4j.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class PluginUtils {

	private static Logger logger = LoggerFactory.getLogger(PluginUtils.class);
	
	public static void loadAndStartPlugins(PluginManager pluginManager, List<String> plugins) {
		
		if(!CollectionUtils.isEmpty(plugins)) {
			
			List<String> pluginIds = new ArrayList<String>();
			
			for (String path : plugins) {
				try {
					//加载指定路径的插件
					String pluginId = pluginManager.loadPlugin(FileSystems.getDefault().getPath(path));
					//如果插件存在则，启动该插件
					if(StringUtils.hasText(pluginId)) {
						pluginIds.add(pluginId);
						pluginManager.startPlugin(pluginId);
					}
				} catch (Exception e) {
					logger.error(e.getMessage());;
				}
			}
			
			try {
				for (String pluginId : pluginIds) {
					//如果插件存在则，启动该插件
					if(StringUtils.hasText(pluginId)) {
						pluginManager.startPlugin(pluginId);
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());;
			}
		}
		
	}
	
}
