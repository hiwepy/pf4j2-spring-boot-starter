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
package org.pf4j.spring.boot.ext.update;

import java.net.URL;
import java.util.Map;

import org.pf4j.update.FileDownloader;
import org.pf4j.update.PluginInfo;
import org.pf4j.update.UpdateRepository;
import org.springframework.cloud.deployer.resource.maven.MavenProperties;

/**
 * TODO
 * @author 		： <a href="https://github.com/hiwepy">hiwepy</a>
 */
public class MavenUpdateRepository implements UpdateRepository {
	
    private static final String MAVEN_REPOSITORY = "maven";
    
    private URL url;
    private Map<String, PluginInfo> plugins;
    private MavenProperties mavenProperties;
	private PluginInfoProvider pluginInfoProvider;
	
	public MavenUpdateRepository(MavenProperties mavenProperties,PluginInfoProvider pluginInfoProvider) {
		this.mavenProperties = mavenProperties;
		this.pluginInfoProvider = pluginInfoProvider;
	}
	
	@Override
    public String getId() {
        return MAVEN_REPOSITORY;
    }

	@Override
	public URL getUrl() {
		return url;
	}
	
	@Override
    public Map<String, PluginInfo> getPlugins() {
        if (plugins == null) {
        	plugins = getPluginInfoProvider().plugins();
        	if(plugins != null) {
        		plugins.entrySet().forEach(info -> {
            		info.getValue().setRepositoryId(MAVEN_REPOSITORY);
            	});
        	}
        }
        return plugins;
    }
	
    @Override
    public PluginInfo getPlugin(String coordinates) {
        return getPlugins().get(coordinates);
    }

    /**
     * Causes plugins.json to be read again to look for new updates from repos
     */
    @Override
    public void refresh() {
        plugins = null;
    }

    @Override
    public FileDownloader getFileDownloader() {
        return new MavenFileDownloader(mavenProperties);
    }

	public PluginInfoProvider getPluginInfoProvider() {
		return pluginInfoProvider;
	}

}
