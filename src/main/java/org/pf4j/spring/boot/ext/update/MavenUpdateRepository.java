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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.maven.spring.boot.ext.MavenClientTemplate;
import org.eclipse.aether.metadata.DefaultMetadata;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.resolution.MetadataResult;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.pf4j.update.DefaultUpdateRepository;
import org.pf4j.update.FileDownloader;
import org.pf4j.update.PluginInfo;
import org.pf4j.update.PluginInfo.PluginRelease;
import org.pf4j.update.UpdateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.deployer.resource.maven.MavenResource;

/**
 * TODO
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
public class MavenUpdateRepository implements UpdateRepository {
	
    private static final Logger logger = LoggerFactory.getLogger(DefaultUpdateRepository.class);
    private String id;
    private URL url;
    private Map<String, PluginInfo> plugins;
	private PluginManager pluginManager;
	private MavenClientTemplate mavenClientTemplate;
	
	public MavenUpdateRepository(String id, MavenClientTemplate mavenClientTemplate, PluginManager pluginManager) {
		this.id = id;
		this.mavenClientTemplate = mavenClientTemplate;
		this.pluginManager = pluginManager;
	}
	
	public MavenUpdateRepository(String id, URL url, MavenClientTemplate mavenClientTemplate, PluginManager pluginManager) {
		this(id, mavenClientTemplate, pluginManager);
		this.url = url;
	}
	
	@Override
    public String getId() {
        return id;
    }

	@Override
	public URL getUrl() {
		return url;
	}
	
	@Override
    public Map<String, PluginInfo> getPlugins() {
        if (plugins == null) {
            initPlugins();
        }
        //MavenResource.parse(coordinates, properties).getFile();
        return plugins;
    }
	
    @Override
    public PluginInfo getPlugin(String coordinates) {
        return getPlugins().get(coordinates);
    }

    private void initPlugins() {
    	
    	// Maven仓库的更新仅支持本地已有插件的更新，不支持没有的插件
		for (PluginWrapper installed : pluginManager.getPlugins()) {
			
			PluginInfo info = new PluginInfo();
			
			PluginDescriptor descriptor = installed.getDescriptor();
			info.id = installed.getPluginId();
			info.description = descriptor.getPluginDescription(); 
			info.provider = descriptor.getProvider();
			info.setRepositoryId(getId());
			
			// 解析Maven版本信息
			List<MetadataResult> metadatas = mavenClientTemplate.metadata(installed.getPluginId());
			/*
			<?xml version="1.0" encoding="UTF-8"?>
			<metadata modelVersion="1.1.0">
			  <groupId>com.oracle</groupId>
			  <artifactId>ojdbc6dms</artifactId>
			  <versioning>
			    <latest>12.1.0.2</latest>
			    <release>12.1.0.2</release>
			    <versions>
			      <version>11.1.0.7.0</version>
			      <version>11.2.0.1.0</version>
			      <version>11.2.0.2.0</version>
			      <version>11.2.0.3</version>
			      <version>11.2.0.4</version>
			      <version>12.1.0.2</version>
			    </versions>
			    <lastUpdated>20181220183842</lastUpdated>
			  </versioning>
			</metadata>
			*/
			List<PluginInfo.PluginRelease> releases = new ArrayList<PluginInfo.PluginRelease>();
			metadatas.forEach(action -> {
				
				if (action.isResolved() && action.isUpdated()) {
					
					Metadata metadata = action.getMetadata();
					if(metadata instanceof DefaultMetadata) {
						
					}
					
					PluginRelease release = new PluginInfo.PluginRelease(); 
					
					release.version = metadata.getVersion();
					try {
						release.url = new MavenResource.Builder().groupId(metadata.getGroupId()).artifactId(metadata.getArtifactId())
								.version(metadata.getVersion()).build().getURI().toString();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					String.format("%s:%s:%s", metadata.getGroupId(), metadata.getArtifactId(), metadata.getVersion()) ;
					metadata.getProperties();
					
				}
				
			});
			info.releases = releases;
			
			plugins.put(installed.getPluginId(), info);
		}
     
		logger.debug("Found {} plugins in repository '{}'", plugins.size(), id);
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
        return new MavenFileDownloader(mavenClientTemplate);
    }

}
