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

import java.util.ArrayList;
import java.util.List;

import org.pf4j.RuntimeMode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 
 * @className	： Pf4jProperties
 * @description	： TODO(描述这个类的作用)
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 * @date		： 2017年10月31日 下午6:24:59
 * @version 	V1.0
 */
@ConfigurationProperties(prefix = Pf4jProperties.PREFIX)
public class Pf4jProperties {

	public static final String PREFIX = "pf4j";

	/** 是否启用 **/
	protected boolean enabled = false;
	/** 数据库列与表达式对应关系 **/
	protected List<String> classesDirectories = new ArrayList<String>();
	protected List<String> libDirectories = new ArrayList<String>();
	/** 运行模式：development、 deployment **/
	protected String mode = RuntimeMode.DEPLOYMENT.toString();
	/** 插件目录：默认 plugins;非jar模式的插件时，该值应该是绝对目录地址  **/
	protected String pluginsDir = "plugins";
	/** 插件地址：绝对地址 **/
	protected List<String> plugins = new ArrayList<String>();
	/** 是否注册插件到Spring上下文 **/
	protected boolean spring = false;
	/** 插件是否jar包 **/
	protected boolean jarPackages = true;
	/** 是否延时加载、启动插件 **/
	protected boolean lazy = true;
	/** 插件延时加载、启动时间，单位毫秒  **/
	protected long delay = 0;
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public List<String> getClassesDirectories() {
		return classesDirectories;
	}

	public void setClassesDirectories(List<String> classesDirectories) {
		this.classesDirectories = classesDirectories;
	}

	public List<String> getLibDirectories() {
		return libDirectories;
	}

	public void setLibDirectories(List<String> libDirectories) {
		this.libDirectories = libDirectories;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getPluginsDir() {
		return pluginsDir;
	}

	public void setPluginsDir(String pluginsDir) {
		this.pluginsDir = pluginsDir;
	}
	
	public List<String> getPlugins() {
		return plugins;
	}

	public void setPlugins(List<String> plugins) {
		this.plugins = plugins;
	}

	public boolean isSpring() {
		return spring;
	}

	public void setSpring(boolean spring) {
		this.spring = spring;
	}

	public boolean isJarPackages() {
		return jarPackages;
	}

	public void setJarPackages(boolean jarPackages) {
		this.jarPackages = jarPackages;
	}

	public boolean isLazy() {
		return lazy;
	}

	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}
	
}
