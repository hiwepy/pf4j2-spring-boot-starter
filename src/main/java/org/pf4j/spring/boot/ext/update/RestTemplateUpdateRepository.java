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

import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.pf4j.update.FileDownloader;
import org.pf4j.update.PluginInfo;
import org.pf4j.update.PluginInfo.PluginRelease;
import org.pf4j.update.SimpleFileDownloader;
import org.pf4j.update.UpdateRepository;
import org.pf4j.update.util.LenientDateTypeAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * TODO
 * @author 		： <a href="https://github.com/hiwepy">hiwepy</a>
 */
public class RestTemplateUpdateRepository implements UpdateRepository {
	
	private static final Logger log = LoggerFactory.getLogger(RestTemplateUpdateRepository.class);
	private static final String REST_REPOSITORY = "rest";
	 
    private String id = REST_REPOSITORY;
    // http://say-hello/sayHello
    private String url;
    private RestTemplate restTemplate;
    private Map<String, PluginInfo> plugins;
	
	public RestTemplateUpdateRepository(String url, RestTemplate restTemplate) {
		this.url = url;
		this.restTemplate = restTemplate;
	}
	
	public RestTemplateUpdateRepository(String id, String url, RestTemplate restTemplate) {
		this.id = id;
		this.url = url;
		this.restTemplate = restTemplate;
	}
	
    @Override
    public String getId() {
        return id;
    }

    @Override
    public URL getUrl() {
        return null;
    }
	
	@Override
    public Map<String, PluginInfo> getPlugins() {
    	initPlugins();
        return plugins;
    }
	
	private void initPlugins() {
        Reader pluginsJsonReader;
        try {
        	
        	String json = getRestTemplate().getForObject(url, String.class);
            log.debug("Read plugins of '{}' repository from '{}'", id, url);
            pluginsJsonReader = new StringReader(json);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            plugins = Collections.emptyMap();
            return;
        }

        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new LenientDateTypeAdapter()).create();
        PluginInfo[] items = gson.fromJson(pluginsJsonReader, PluginInfo[].class);
        plugins = new HashMap<>(items.length);
        for (PluginInfo p : items) {
            for (PluginRelease r : p.releases) {
                try {
                    r.url = new URL(getUrl(), r.url).toString();
                    if (r.date.getTime() == 0) {
                        log.warn("Illegal release date when parsing {}@{}, setting to epoch", p.id, r.version);
                    }
                } catch (MalformedURLException e) {
                    log.warn("Skipping release {} of plugin {} due to failure to build valid absolute URL. Url was {}{}", r.version, p.id, getUrl(), r.url);
                }
            }
            p.setRepositoryId(getId());
            plugins.put(p.id, p);
        }
        log.debug("Found {} plugins in repository '{}'", plugins.size(), id);
    }
	
	@Override
    public PluginInfo getPlugin(String id) {
        return getPlugins().get(id);
    }

    /**
     * Causes {@code plugins.json} to be read again to look for new updates from repositories.
     */
    @Override
    public void refresh() {
        plugins = null;
    }

    @Override
    public FileDownloader getFileDownloader() {
        return new SimpleFileDownloader();
    }
    
	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

}
