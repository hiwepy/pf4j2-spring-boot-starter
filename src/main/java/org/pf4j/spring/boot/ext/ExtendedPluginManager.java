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

import org.pf4j.DefaultPluginManager;
import org.pf4j.DevelopmentPluginClasspath;
import org.pf4j.PluginClasspath;

public class ExtendedPluginManager extends DefaultPluginManager {

	/** Extended Plugin Class Directory **/
	private List<String> classesDirectories = new ArrayList<String>();
	/** Extended Plugin Jar Directory **/
	private List<String> libDirectories = new ArrayList<String>();

	public ExtendedPluginManager(File pluginsRoot) {
		super(pluginsRoot.toPath());
	}

	public ExtendedPluginManager(File pluginsRoot, List<String> classesDirectories, List<String> libDirectories) {
		super(pluginsRoot.toPath());
		this.classesDirectories.addAll(classesDirectories);
		this.libDirectories.addAll(libDirectories);
	}

	public ExtendedPluginManager(String pluginsRoot) {
		super(Paths.get(pluginsRoot));
	}

	public ExtendedPluginManager(String pluginsRoot, List<String> classesDirectories, List<String> libDirectories) {
		super(Paths.get(pluginsRoot));
		this.classesDirectories.addAll(classesDirectories);
		this.libDirectories.addAll(libDirectories);
	}

	public ExtendedPluginManager(Path pluginsRoot) {
		super(pluginsRoot);
	}

	public ExtendedPluginManager(Path pluginsRoot, List<String> classesDirectories, List<String> libDirectories) {
		super(pluginsRoot);
		this.classesDirectories.addAll(classesDirectories);
		this.libDirectories.addAll(libDirectories);
	}
	
	@Override
	protected PluginClasspath createPluginClasspath() {
		return isDevelopment() ? new DevelopmentPluginClasspath()
				: new ExtendedPluginClasspath(getClassesDirectories(), getLibDirectories());
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
