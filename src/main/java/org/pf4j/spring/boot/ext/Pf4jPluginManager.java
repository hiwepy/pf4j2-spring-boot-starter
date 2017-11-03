/*
 * Copyright (c) 2010-2020, vindell (https://github.com/vindell).
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
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.pf4j.DefaultPluginManager;

public class Pf4jPluginManager extends DefaultPluginManager {

	public Pf4jPluginManager(File path) {
		super(path.toPath());
	}

	public Pf4jPluginManager(String path) {
		super(FileSystems.getDefault().getPath(path));
	}

	public Pf4jPluginManager(Path pluginsRoot) {
		super(pluginsRoot);
	}

}
