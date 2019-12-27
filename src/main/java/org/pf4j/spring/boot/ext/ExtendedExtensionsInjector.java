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

import org.pf4j.PluginManager;
import org.pf4j.spring.ExtensionsInjector;
import org.pf4j.spring.boot.ext.registry.Pf4jDynamicControllerRegistry;
import org.pf4j.spring.boot.ext.utils.InjectorUtils;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

/**
 * TODO
 * @author 		： <a href="https://github.com/hiwepy">hiwepy</a>
 */
public class ExtendedExtensionsInjector extends ExtensionsInjector {
	
	protected Pf4jDynamicControllerRegistry dynamicControllerRegistry;
	
	public ExtendedExtensionsInjector(PluginManager pluginManager, 
			Pf4jDynamicControllerRegistry dynamicControllerRegistry,
			ApplicationContext applicationContext) {
		super(pluginManager, (AbstractAutowireCapableBeanFactory) applicationContext.getAutowireCapableBeanFactory());
		this.dynamicControllerRegistry = dynamicControllerRegistry;
	}
	
   /**
    * Register an extension as bean.
    * Current implementation register extension as singleton using {@code beanFactory.registerSingleton()}.
    * The extension instance is created using {@code pluginManager.getExtensionFactory().create(extensionClass)}.
    * The bean name is the extension class name.
    * Override this method if you wish other register strategy.
    */
	@Override
	protected void registerExtension(Class<?> extensionClass) {
       
		Object extension = pluginManager.getExtensionFactory().create(extensionClass);
		if(!InjectorUtils.isInjectNecessary(extensionClass)) {
			return;
		}
		String beanName = InjectorUtils.getBeanName(extensionClass, extension.getClass().getName());
		// 判断对象是否是Controller
		if (InjectorUtils.isController(extensionClass)) {
			dynamicControllerRegistry.registerController(beanName, extension);
		} else {
			beanFactory.registerSingleton(beanName, extension);
		}
		
	}
	

}
