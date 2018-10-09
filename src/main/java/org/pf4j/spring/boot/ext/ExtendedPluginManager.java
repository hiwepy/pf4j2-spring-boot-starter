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

import org.pf4j.DefaultPluginManager;

public class ExtendedPluginManager extends DefaultPluginManager {

	public ExtendedPluginManager(File pluginsRoot) {
		super(pluginsRoot.toPath());
	}

	public ExtendedPluginManager(String pluginsRoot) {
		super(Paths.get(pluginsRoot));
	}

	public ExtendedPluginManager(Path pluginsRoot) {
		super(pluginsRoot);
	}

}
