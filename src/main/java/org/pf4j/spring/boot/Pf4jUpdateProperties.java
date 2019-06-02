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

import org.pf4j.spring.boot.ext.property.Pf4jPluginRepoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = Pf4jUpdateProperties.PREFIX)
public class Pf4jUpdateProperties {

	public static final String PREFIX = "spring.pf4j.update";

	/** Enable Pf4j Update. */
	private boolean enabled = false;
	/** Whether to automatically update the plugin **/
	private boolean autoUpdate = false;
	/** The delay of plugin automatic update check, default：10000 milliseconds **/
	private long delay = 10000;
	/** The period of plugin automatic update check, default：10 seconds **/
	private long period = 1000 * 60 * 10;
	/** Local Repos Path , i.e : repositories.json **/
	private String reposJsonPath;
	/** Remote Repos Path **/
	private List<Pf4jPluginRepoProperties> repos = new ArrayList<Pf4jPluginRepoProperties>();

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
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

	public List<Pf4jPluginRepoProperties> getRepos() {
		return repos;
	}

	public void setRepos(List<Pf4jPluginRepoProperties> repos) {
		this.repos = repos;
	}

}
