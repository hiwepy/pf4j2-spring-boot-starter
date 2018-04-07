/*
 * Copyright (c) 2017, vindell (https://github.com/vindell).
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

import java.util.List;

import org.pf4j.DefaultPluginClasspath;

public class Pf4jPluginClasspath extends DefaultPluginClasspath {

	public Pf4jPluginClasspath(String... classesDirectories) {
		super();
		addClassesDirectories(classesDirectories);
	}

	public Pf4jPluginClasspath(List<String> classesDirectories, List<String> libDirectories) {
		this(classesDirectories.toArray(new String[classesDirectories.size()]), libDirectories.toArray(new String[libDirectories.size()]));
	}
	
	public Pf4jPluginClasspath(String[] libDirectories, String... classesDirectories) {
		super();
		addClassesDirectories(classesDirectories);
		addLibDirectories(libDirectories);
	}


}
