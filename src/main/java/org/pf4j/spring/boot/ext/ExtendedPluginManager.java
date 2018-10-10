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

import org.pf4j.DefaultExtensionFactory;
import org.pf4j.DefaultPluginManager;
import org.pf4j.ExtensionFactory;
import org.pf4j.SingletonExtensionFactory;
import org.pf4j.spring.SingletonSpringExtensionFactory;
import org.pf4j.spring.SpringExtensionFactory;

public class ExtendedPluginManager extends DefaultPluginManager {

	/** Whether to register the object to the spring context */
	private boolean injectable = true;
	/** Whether always returns a singleton instance. */
	private boolean singleton = true;
	
	public ExtendedPluginManager(File pluginsRoot, boolean injectable, boolean singleton ) {
		super(pluginsRoot.toPath());
		this.injectable = injectable;
		this.singleton = singleton;
	}

	public ExtendedPluginManager(String pluginsRoot, boolean injectable, boolean singleton ) {
		super(Paths.get(pluginsRoot));
		this.injectable = injectable;
		this.singleton = singleton;
	}

	public ExtendedPluginManager(Path pluginsRoot, boolean injectable, boolean singleton ) {
		super(pluginsRoot);
		this.injectable = injectable;
		this.singleton = singleton;
	}
	
	@Override
	protected ExtensionFactory createExtensionFactory() {

		if (this.isInjectable()) {
			if (this.isSingleton()) {
				return new SingletonSpringExtensionFactory(this);
			}
			return new SpringExtensionFactory(this);

		} else {
			if (this.isSingleton()) {
				return new SingletonExtensionFactory();
			}
			return new DefaultExtensionFactory();
		}

	}

	public boolean isInjectable() {
		return injectable;
	}

	public boolean isSingleton() {
		return singleton;
	}

}
