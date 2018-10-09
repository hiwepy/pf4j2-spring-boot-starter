/*
 * Copyright (c) 2018, vindell (https://github.com/vindell).
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
package org.pf4j.spring.boot;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Timer;

import org.pf4j.PluginDescriptor;
import org.pf4j.PluginManager;
import org.pf4j.PluginStateEvent;
import org.pf4j.PluginStateListener;
import org.pf4j.RuntimeMode;
import org.pf4j.spring.boot.ext.ExtendedSpringPluginManager;
import org.pf4j.spring.boot.ext.task.PluginLazyTask;
import org.pf4j.spring.boot.ext.task.PluginUpdateTask;
import org.pf4j.spring.boot.ext.task.PluginsLazyTask;
import org.pf4j.spring.boot.ext.update.DefaultUpdateRepositoryProvider;
import org.pf4j.spring.boot.ext.update.UpdateRepositoryProvider;
import org.pf4j.spring.boot.ext.utils.PluginUtils;
import org.pf4j.spring.boot.hooks.Pf4jShutdownHook;
import org.pf4j.update.UpdateManager;
import org.pf4j.update.UpdateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Pf4j 2.x Configuration
 * 
 * @author <a href="https://github.com/vindell">vindell</a>
 */
@Configuration
@AutoConfigureAfter({WebMvcAutoConfiguration.class})
@ConditionalOnClass({ PluginManager.class })
@ConditionalOnProperty(prefix = Pf4jProperties.PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties(Pf4jProperties.class)
public class Pf4jAutoConfiguration {

	private Logger logger = LoggerFactory.getLogger(Pf4jAutoConfiguration.class);
	// 实例化Timer类
	private Timer timer = new Timer(true);

	@Bean
	@ConditionalOnMissingBean(PluginStateListener.class)
	public PluginStateListener pluginStateListener() {

		return new PluginStateListener() {

			@Override
			public void pluginStateChanged(PluginStateEvent event) {

				PluginDescriptor descriptor = event.getPlugin().getDescriptor();

				if (logger.isDebugEnabled()) {
					logger.debug(String.format("Plugin [%s（%s）](%s) %s", descriptor.getPluginId(),
							descriptor.getVersion().toString(), descriptor.getPluginDescription(),
							event.getPluginState().toString()));
				}

			}

		};
	}

	@Bean
	public PluginManager pluginManager(RequestMappingHandlerMapping requestMappingHandlerMapping, Pf4jProperties properties) {

		// 设置运行模式
		RuntimeMode mode = RuntimeMode.byName(properties.getMode());
		System.setProperty("pf4j.mode", mode.toString());
		
		// 设置插件目录
		String pluginsRoot = StringUtils.hasText(properties.getPluginsRoot()) ? properties.getPluginsRoot() : "plugins";
		System.setProperty("pf4j.pluginsDir", pluginsRoot);
		String apphome = System.getProperty("app.home");
		if (RuntimeMode.DEPLOYMENT.compareTo(RuntimeMode.byName(properties.getMode())) == 0
				&& StringUtils.hasText(apphome)) {
			System.setProperty("pf4j.pluginsDir", apphome + File.separator + pluginsRoot);
		}
		
		// final PluginManager pluginManager = new DefaultPluginManager();
		// final PluginManager pluginManager = new JarPluginManager();

		ExtendedSpringPluginManager pluginManager = new ExtendedSpringPluginManager(pluginsRoot,
				requestMappingHandlerMapping);
				
		/*
		 * pluginManager.enablePlugin(pluginId) pluginManager.disablePlugin(pluginId)
		 * pluginManager.deletePlugin(pluginId)
		 * 
		 * pluginManager.loadPlugin(pluginPath) pluginManager.startPlugin(pluginId)
		 * pluginManager.stopPlugin(pluginId) pluginManager.unloadPlugin(pluginId)
		 */

		if (properties.isLazy()) {
			// 延时加载、启动插件目录中的插件
			timer.schedule(new PluginLazyTask(pluginManager), properties.getDelay());
			// 延时加载、启动绝对路径指定的插件
			timer.schedule(new PluginsLazyTask(pluginManager, properties.getPlugins()), properties.getDelay());
		} else {

			// 加载、启动插件目录中的插件
			pluginManager.loadPlugins();
			/*
			 * 调用Plugin实现类的start()方法:
			 */
			pluginManager.startPlugins();

			// 加载、启动绝对路径指定的插件
			PluginUtils.loadAndStartPlugins(pluginManager, properties.getPlugins());
		}

		/**
		 * 应用退出时，要调用shutdown来清理资源，关闭网络连接 注意：我们建议应用在JBOSS、Tomcat等容器的退出钩子里调用shutdown方法
		 */
		Runtime.getRuntime().addShutdownHook(new Pf4jShutdownHook(pluginManager));

		return pluginManager;
	}
	
	@Bean
	@ConditionalOnMissingBean
	public UpdateRepositoryProvider updateRepositoryProvider(Pf4jProperties properties) {
		return new DefaultUpdateRepositoryProvider(properties.getRepos());
	}
	
	@Bean
	public UpdateManager updateManager(PluginManager pluginManager, UpdateRepositoryProvider updateRepositoryProvider,
			Pf4jProperties properties) {
		UpdateManager updateManager = null;
		List<UpdateRepository> repos = updateRepositoryProvider.getRepos();
		if (StringUtils.hasText(properties.getReposJsonPath())) {
			updateManager = new UpdateManager(pluginManager, Paths.get(properties.getReposJsonPath()));
		} else if (!CollectionUtils.isEmpty(repos)) {
			updateManager = new UpdateManager(pluginManager, repos);
		} else {
			updateManager = new UpdateManager(pluginManager);
		}
		// auto update
		if(properties.isAutoUpdate()) {
			timer.schedule(new PluginUpdateTask(pluginManager, updateManager), properties.getPeriod());
		}
		return updateManager;
	}

}