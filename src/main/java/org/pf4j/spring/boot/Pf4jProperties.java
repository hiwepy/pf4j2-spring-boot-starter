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

import java.util.ArrayList;
import java.util.List;

import org.pf4j.RuntimeMode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ： <a href="https://github.com/vindell">vindell</a>
 */
@ConfigurationProperties(prefix = Pf4jProperties.PREFIX)
public class Pf4jProperties {

	public static final String PREFIX = "spring.pf4j";

	/** Enable Pf4j. */
	private boolean enabled = false;
	/** Whether to automatically inject dependent objects */
	private boolean autowire = true;
	/** Whether to register the object to the spring context */
	private boolean injectable = true;
	/** Whether always returns a singleton instance. */
	private boolean singleton = true;
	/** Extended Plugin Class Directory **/
	private List<String> classesDirectories = new ArrayList<String>();
	/** Extended Plugin Jar Directory **/
	private List<String> libDirectories = new ArrayList<String>();
	/** Runtime Mode：development、 deployment **/
	private String mode = RuntimeMode.DEPLOYMENT.toString();
	/**
	 * Plugin root directory: default “plugins”; when non-jar mode plugin, the value
	 * should be an absolute directory address
	 **/
	private String pluginsRoot = "plugins";
	/** Plugin address: absolute address **/
	private List<String> plugins = new ArrayList<String>();
	/** Whether the plugin is a JAR package **/
	private boolean jarPackages = true;

	/** Whether to automatically update the plugin **/
	private boolean autoUpdate = false;
	/** The period of plugin automatic update check, default：5000 milliseconds **/
	private long period = 5000;
	/** Local Repos Path **/
	protected String reposJsonPath = "repositories.json";
	/** Remote Repos Path **/
	protected List<Pf4jUpdateProperties> repos = new ArrayList<Pf4jUpdateProperties>();

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

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
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

	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	public String getReposJsonPath() {
		return reposJsonPath;
	}

	public void setReposJsonPath(String reposJsonPath) {
		this.reposJsonPath = reposJsonPath;
	}

	public List<Pf4jUpdateProperties> getRepos() {
		return repos;
	}

	public void setRepos(List<Pf4jUpdateProperties> repos) {
		this.repos = repos;
	}
	
}
