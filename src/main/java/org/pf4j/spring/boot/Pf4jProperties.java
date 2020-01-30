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
package org.pf4j.spring.boot;

import java.util.ArrayList;
import java.util.List;

import org.pf4j.RuntimeMode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ： <a href="https://github.com/hiwepy">hiwepy</a>
 */
@ConfigurationProperties(prefix = Pf4jProperties.PREFIX)
public class Pf4jProperties {

	public static final String PREFIX = "pf4j";

	/** Enable Pf4j. */
	private boolean enabled = false;
	/** Whether to automatically inject dependent objects */
	private boolean autowire = true;
	/**
	 * Set to true to allow requires expression to be exactly x.y.z. The default is
	 * false, meaning that using an exact version x.y.z will implicitly mean the
	 * same as  &gt;=x.y.z
	 */
	private boolean exactVersionAllowed = false;
	/** Whether to register the object to the spring context */
	private boolean injectable = true;
	/** Whether always returns a singleton instance. */
	private boolean singleton = true;
	/** Extended Plugin Class Directory **/
	private List<String> classesDirectories = new ArrayList<String>();
	/** Extended Plugin Jar Directory **/
	private List<String> libDirectories = new ArrayList<String>();
	/** Runtime Mode：development、 deployment **/
	private RuntimeMode runtimeMode = RuntimeMode.DEPLOYMENT;
	/**
	 * Plugin root directory: default “plugins”; when non-jar mode plugin, the value
	 * should be an absolute directory address
	 **/
	private String pluginsRoot = "plugins";
	/** Plugin address: absolute address **/
	private List<String> plugins = new ArrayList<String>();
	/** Whether the plugin is a JAR package **/
	private boolean jarPackages = true;
	/* The system version used for comparisons to the plugin requires attribute. */
	private String systemVersion = "0.0.0";
	
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isAutowire() {
		return autowire;
	}

	public void setAutowire(boolean autowire) {
		this.autowire = autowire;
	}

	public boolean isInjectable() {
		return injectable;
	}

	public void setInjectable(boolean injectable) {
		this.injectable = injectable;
	}

	public boolean isSingleton() {
		return singleton;
	}

	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
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

	public RuntimeMode getRuntimeMode() {
		return runtimeMode;
	}

	public void setRuntimeMode(RuntimeMode runtimeMode) {
		this.runtimeMode = runtimeMode;
	}

	public String getSystemVersion() {
		return systemVersion;
	}

	public void setSystemVersion(String systemVersion) {
		this.systemVersion = systemVersion;
	}

	public String getPluginsRoot() {
		return pluginsRoot;
	}

	public void setPluginsRoot(String pluginsRoot) {
		this.pluginsRoot = pluginsRoot;
	}

	public List<String> getPlugins() {
		return plugins;
	}

	public void setPlugins(List<String> plugins) {
		this.plugins = plugins;
	}

	public boolean isJarPackages() {
		return jarPackages;
	}

	public void setJarPackages(boolean jarPackages) {
		this.jarPackages = jarPackages;
	}

	/**
	 * Set to true to allow requires expression to be exactly x.y.z. The default is
	 * false, meaning that using an exact version x.y.z will implicitly mean the
	 * same as  &gt;=x.y.z
	 *
	 * @param exactVersionAllowed set to true or false
	 */
	public void setExactVersionAllowed(boolean exactVersionAllowed) {
		this.exactVersionAllowed = exactVersionAllowed;
	}

	public boolean isExactVersionAllowed() {
		return exactVersionAllowed;
	}

}
