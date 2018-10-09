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
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.pf4j.DevelopmentPluginClasspath;
import org.pf4j.PluginClasspath;
import org.pf4j.spring.SpringPluginManager;
import org.pf4j.spring.boot.ext.webmvc.ControllerExtensionsInjector;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * TODO
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
public class ExtendedSpringPluginManager extends SpringPluginManager {

	/** Extended Plugin Class Directory **/
	private List<String> classesDirectories = new ArrayList<String>();
	/** Extended Plugin Jar Directory **/
	private List<String> libDirectories = new ArrayList<String>();
	
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
	
	@Override
	protected PluginClasspath createPluginClasspath() {
		return isDevelopment() ? new DevelopmentPluginClasspath() : new ExtendedPluginClasspath(getClassesDirectories(), getLibDirectories());
    }
	
	/**
     * This method load, start plugins and inject controller extensions in Spring
     */
    @PostConstruct
    public void init() {
    	
    	super.init();
    	
        AbstractAutowireCapableBeanFactory beanFactory = (AbstractAutowireCapableBeanFactory) getApplicationContext().getAutowireCapableBeanFactory();
        ControllerExtensionsInjector extensionsInjector = new ControllerExtensionsInjector(this, requestMappingHandlerMapping, beanFactory);
        extensionsInjector.injectExtensions();
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
	
}
