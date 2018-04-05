/*
 * Copyright (c) 2010-2020, vindell (https://github.com/vindell).
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
import java.util.Timer;

import org.pf4j.PluginClasspath;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginManager;
import org.pf4j.PluginStateEvent;
import org.pf4j.PluginStateListener;
import org.pf4j.RuntimeMode;
import org.pf4j.spring.boot.ext.Pf4jJarPluginManager;
import org.pf4j.spring.boot.ext.Pf4jJarPluginWhitSpringManager;
import org.pf4j.spring.boot.ext.Pf4jPluginClasspath;
import org.pf4j.spring.boot.ext.Pf4jPluginManager;
import org.pf4j.spring.boot.ext.PluginLazyTask;
import org.pf4j.spring.boot.ext.PluginUtils;
import org.pf4j.spring.boot.ext.PluginsLazyTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * Pf4j 2.x Configuration
 * @author <a href="https://github.com/vindell">vindell</a>
 */
@Configuration
@ConditionalOnClass({ PluginManager.class })
@ConditionalOnProperty(prefix = Pf4jProperties.PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties(Pf4jProperties.class)
public class Pf4jAutoConfiguration implements DisposableBean {

	private PluginManager pluginManager;
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
	public PluginManager pluginManager(Pf4jProperties properties) {

		// 设置运行模式
		RuntimeMode mode = RuntimeMode.byName(properties.getMode());
		System.setProperty("pf4j.mode", mode.toString());

		// 设置插件目录
		String pluginsDir = StringUtils.hasText(properties.getPluginsDir()) ? properties.getPluginsDir() : "plugins";
		System.setProperty("pf4j.pluginsDir", pluginsDir);
		String apphome = System.getProperty("app.home");
		if (RuntimeMode.DEPLOYMENT.compareTo(RuntimeMode.byName(properties.getMode())) == 0
				&& StringUtils.hasText(apphome)) {
			System.setProperty("pf4j.pluginsDir", apphome + File.separator + pluginsDir);
		}

		// final PluginManager pluginManager = new DefaultPluginManager();
		// final PluginManager pluginManager = new JarPluginManager();

		PluginManager pluginManager = null;
		if (properties.isJarPackages()) {

			PluginClasspath pluginClasspath = new Pf4jPluginClasspath(properties.getClassesDirectories(),
					properties.getLibDirectories());

			if (properties.isSpring()) {

				/**
				 * 使用Spring时需编写如下的初始化逻辑
				 * 
				 * @Configuration public class Pf4jConfig {
				 * @Bean public ExtensionsInjector extensionsInjector() { return new
				 *       ExtensionsInjector(); } }
				 * 
				 */

				pluginManager = new Pf4jJarPluginWhitSpringManager(pluginClasspath);
			} else {
				pluginManager = new Pf4jJarPluginManager(pluginClasspath);
			}
		} else {
			pluginManager = new Pf4jPluginManager(pluginsDir);
		}

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

		this.pluginManager = pluginManager;
		return pluginManager;
	}

	@Override
	public void destroy() throws Exception {
		// 销毁插件
		if (pluginManager != null) {
			/*
			 * 调用Plugin实现类的stop()方法
			 */
			pluginManager.stopPlugins();
		}

	}

}