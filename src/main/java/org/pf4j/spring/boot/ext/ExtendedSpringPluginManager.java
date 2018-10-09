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
package org.pf4j.spring.boot.ext;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.pf4j.spring.SpringPluginManager;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * TODO
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
public class ExtendedSpringPluginManager extends SpringPluginManager {

	private final RequestMappingHandlerMapping requestMappingHandlerMapping;

	public ExtendedSpringPluginManager(File pluginsRoot, RequestMappingHandlerMapping requestMappingHandlerMapping) {
		super(pluginsRoot.toPath());
		this.requestMappingHandlerMapping = requestMappingHandlerMapping;
	}


	public ExtendedSpringPluginManager(String pluginsRoot, RequestMappingHandlerMapping requestMappingHandlerMapping) {
		super(Paths.get(pluginsRoot));
		this.requestMappingHandlerMapping = requestMappingHandlerMapping;
	}


	public ExtendedSpringPluginManager(Path pluginsRoot, RequestMappingHandlerMapping requestMappingHandlerMapping) {
		super(pluginsRoot);
		this.requestMappingHandlerMapping = requestMappingHandlerMapping;
	}
	
	/**
     * This method load, start plugins and inject controller extensions in Spring
     */
    @PostConstruct
    public void init() {
    	
    	 loadPlugins();
         startPlugins();
    	
        AbstractAutowireCapableBeanFactory beanFactory = (AbstractAutowireCapableBeanFactory) getApplicationContext().getAutowireCapableBeanFactory();
        ExtendedExtensionsInjector extensionsInjector = new ExtendedExtensionsInjector(this, beanFactory, requestMappingHandlerMapping);
        extensionsInjector.injectExtensions();
    }
	
}
