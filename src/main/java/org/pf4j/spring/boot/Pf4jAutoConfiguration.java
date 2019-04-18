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

import org.apache.maven.spring.boot.ext.MavenClientTemplate;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginManager;
import org.pf4j.PluginStateEvent;
import org.pf4j.PluginStateListener;
import org.pf4j.RuntimeMode;
import org.pf4j.spring.SpringPluginManager;
import org.pf4j.spring.boot.ext.ExtendedSpringPluginManager;
import org.pf4j.spring.boot.ext.registry.Pf4jDynamicControllerRegistry;
import org.pf4j.spring.boot.ext.task.PluginUpdateTask;
import org.pf4j.spring.boot.ext.update.MavenUpdateRepository;
import org.pf4j.spring.boot.ext.utils.PluginUtils;
import org.pf4j.spring.boot.hooks.Pf4jShutdownHook;
import org.pf4j.update.DefaultUpdateRepository;
import org.pf4j.update.UpdateManager;
import org.pf4j.update.UpdateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * Pf4j 2.x Configuration
 * 
 * @author <a href="https://github.com/vindell">vindell</a>
 */
@Configuration
@ConditionalOnClass({ PluginManager.class, UpdateManager.class, SpringPluginManager.class })
@ConditionalOnProperty(prefix = Pf4jProperties.PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties({Pf4jProperties.class, Pf4jUpdateMavenProperties.class})
public class Pf4jAutoConfiguration {

	private Logger logger = LoggerFactory.getLogger(Pf4jAutoConfiguration.class);
	// 实例化Timer类
	private Timer timer = new Timer(true);

	@Bean
	@ConditionalOnMissingBean(Pf4jDynamicControllerRegistry.class)
	public Pf4jDynamicControllerRegistry pf4jDynamicControllerRegistry() {
		return new Pf4jDynamicControllerRegistry();
	}
	
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
				properties.isAutowire(), properties.isSingleton(), properties.isInjectable());
		
		/*
		 * pluginManager.enablePlugin(pluginId) pluginManager.disablePlugin(pluginId)
		 * pluginManager.deletePlugin(pluginId)
		 * 
		 * pluginManager.loadPlugin(pluginPath) pluginManager.startPlugin(pluginId)
		 * pluginManager.stopPlugin(pluginId) pluginManager.unloadPlugin(pluginId)
		 */

		// 加载、启动绝对路径指定的插件
		PluginUtils.loadAndStartPlugins(pluginManager, properties.getPlugins());

		/**
		 * 应用退出时，要调用shutdown来清理资源，关闭网络连接 注意：我们建议应用在JBOSS、Tomcat等容器的退出钩子里调用shutdown方法
		 */
		Runtime.getRuntime().addShutdownHook(new Pf4jShutdownHook(pluginManager));

		return pluginManager;
	}
	
	@Bean
	public MavenUpdateRepository mavenUpdateRepository(MavenClientTemplate mavenClientTemplate, PluginManager pluginManager) {
		return new MavenUpdateRepository("maven", mavenClientTemplate, pluginManager);
	}
	
	@Bean
	public UpdateManager updateManager(
			PluginManager pluginManager,
			@Autowired(required = false) List<UpdateRepository> repos,
			Pf4jProperties properties) {
		UpdateManager updateManager = null;
		if (StringUtils.hasText(properties.getReposJsonPath())) {
			updateManager = new UpdateManager(pluginManager, Paths.get(properties.getReposJsonPath()));
		} else if (!CollectionUtils.isEmpty(repos)) {
			updateManager = new UpdateManager(pluginManager, repos);
		} else {
			updateManager = new UpdateManager(pluginManager);
		}
		if(!CollectionUtils.isEmpty(properties.getRepos())) {
			for (Pf4jUpdateProperties repo : properties.getRepos()) {
				updateManager.addRepository(new DefaultUpdateRepository(repo.getId(), repo.getUrl(), repo.getPluginsJsonFileName()));
			}
		}
		if(!CollectionUtils.isEmpty(repos)) {
			for (UpdateRepository newRepo : repos) {
				updateManager.addRepository(newRepo);
			}
		}
		// auto update
		if(properties.isAutoUpdate()) {
			timer.schedule(new PluginUpdateTask(pluginManager, updateManager), properties.getPeriod());
		}
		return updateManager;
	}

}