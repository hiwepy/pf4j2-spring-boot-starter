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

import org.pf4j.spring.boot.ext.property.Pf4jPluginRepoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = Pf4jUpdateProperties.PREFIX)
public class Pf4jUpdateProperties {

	public static final String PREFIX = "pf4j.update";

	/** Enable Pf4j Update. */
	private boolean enabled = false;
	/** Local Repos Path , i.e : repositories.json **/
	private String reposJsonPath;
	/** Local Repos Path , i.e : http://say-hello/repos **/
	private String reposRestPath;
	/** Remote Repos Path **/
	private List<Pf4jPluginRepoProperties> repos = new ArrayList<Pf4jPluginRepoProperties>();

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getReposJsonPath() {
		return reposJsonPath;
	}

	public void setReposJsonPath(String reposJsonPath) {
		this.reposJsonPath = reposJsonPath;
	}
	
	public String getReposRestPath() {
		return reposRestPath;
	}

	public void setReposRestPath(String reposRestPath) {
		this.reposRestPath = reposRestPath;
	}

	public List<Pf4jPluginRepoProperties> getRepos() {
		return repos;
	}

	public void setRepos(List<Pf4jPluginRepoProperties> repos) {
		this.repos = repos;
	}

}
