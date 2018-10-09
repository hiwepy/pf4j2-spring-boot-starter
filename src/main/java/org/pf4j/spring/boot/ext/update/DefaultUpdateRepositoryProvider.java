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
package org.pf4j.spring.boot.ext.update;

import java.util.ArrayList;
import java.util.List;

import org.pf4j.spring.boot.Pf4jUpdateProperties;
import org.pf4j.update.DefaultUpdateRepository;
import org.pf4j.update.UpdateRepository;

/**
 * TODO
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */

public class DefaultUpdateRepositoryProvider implements UpdateRepositoryProvider {

	protected List<UpdateRepository> repos = new ArrayList<UpdateRepository>();

	public DefaultUpdateRepositoryProvider() {
	}
	
	public DefaultUpdateRepositoryProvider(List<Pf4jUpdateProperties> repoList) {
		for (Pf4jUpdateProperties repo : repoList) {
			repos.add(new DefaultUpdateRepository(repo.getId(), repo.getUrl(), repo.getPluginsJsonFileName()));
		}
	}
	
	@Override
	public List<UpdateRepository> getRepos() {
		return repos;
	}

	public void setRepos(List<UpdateRepository> repos) {
		this.repos = repos;
	}
	
}
