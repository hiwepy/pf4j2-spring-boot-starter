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
package org.pf4j.spring.boot.ext.task;

import java.util.TimerTask;

import org.pf4j.PluginManager;

public class PluginLazyTask extends TimerTask {

	PluginManager pluginManager = null;
	
	public PluginLazyTask(PluginManager pluginManager) {
		super();
		this.pluginManager = pluginManager;
	}

	@Override
	public void run() {
		
		// 加载插件
		pluginManager.loadPlugins();

		// 启动插件
		pluginManager.startPlugins();
		
		// 执行完成后取消线程
		this.cancel();
	}

}
