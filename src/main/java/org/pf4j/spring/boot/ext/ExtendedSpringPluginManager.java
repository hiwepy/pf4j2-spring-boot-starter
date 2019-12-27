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
package org.pf4j.spring.boot.ext;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.pf4j.ExtensionFactory;
import org.pf4j.spring.SingletonSpringExtensionFactory;
import org.pf4j.spring.SpringExtensionFactory;
import org.pf4j.spring.SpringPluginManager;
import org.pf4j.spring.boot.ext.registry.Pf4jDynamicControllerRegistry;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * TODO
 * @author 		： <a href="https://github.com/hiwepy">hiwepy</a>
 */
public class ExtendedSpringPluginManager extends SpringPluginManager {
	
	/** Whether to automatically inject dependent objects */
	private boolean autowire = true;
	/** Whether always returns a singleton instance. */
	private boolean singleton = true;
	/** Whether to register the object to the spring context */
	private boolean injectable = true;
	
	public ExtendedSpringPluginManager(File pluginsRoot, boolean autowire, boolean singleton, boolean injectable ) {
		super(pluginsRoot.toPath());
		this.autowire = autowire;
		this.singleton = singleton;
		this.injectable = injectable;
	}

	public ExtendedSpringPluginManager(String pluginsRoot, boolean autowire, boolean singleton, boolean injectable ) {
		super(Paths.get(pluginsRoot));
		this.autowire = autowire;
		this.singleton = singleton;
		this.injectable = injectable;
	}

	public ExtendedSpringPluginManager(Path pluginsRoot, boolean autowire, boolean singleton, boolean injectable ) {
		super(pluginsRoot);
		this.autowire = autowire;
		this.singleton = singleton;
		this.injectable = injectable;
	}

    @Override
    protected ExtensionFactory createExtensionFactory() {
    	if(this.isSingleton()) {
    		return new SingletonSpringExtensionFactory(this, this.isAutowire());
    	}
        return new SpringExtensionFactory(this, this.isAutowire());
    }
    
    @Autowired
    private Pf4jDynamicControllerRegistry dynamicControllerRegistry;
    
	/**
     * This method load, start plugins and inject controller extensions in Spring
     */
    @PostConstruct
    public void init() {

		loadPlugins();
		startPlugins();

		if (this.isInjectable()) {
			ExtendedExtensionsInjector extensionsInjector = new ExtendedExtensionsInjector(this,
					dynamicControllerRegistry, getApplicationContext());
			extensionsInjector.injectExtensions();
		}
        
    }

	public boolean isAutowire() {
		return autowire;
	}
	
	public boolean isSingleton() {
		return singleton;
	}

	public boolean isInjectable() {
		return injectable;
	}
    
}
