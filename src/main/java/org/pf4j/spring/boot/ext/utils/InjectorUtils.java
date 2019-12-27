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
package org.pf4j.spring.boot.ext.utils;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO
 * @author 		： <a href="https://github.com/hiwepy">hiwepy</a>
 */
public class InjectorUtils {

	public static boolean isController(Class<?> extensionClass) {

		RestController restController = extensionClass.getAnnotation(RestController.class);
		if (restController != null) {
			return true;
		}

		Controller controller = extensionClass.getAnnotation(Controller.class);
		if (controller != null) {
			return true;
		}

		return false;
	}

	public static boolean isInjectNecessary(Class<?> extensionClass) {

		RestController restController = extensionClass.getAnnotation(RestController.class);
		if (restController != null) {
			return true;
		}

		Controller controller = extensionClass.getAnnotation(Controller.class);
		if (controller != null) {
			return true;
		}

		Component component = extensionClass.getAnnotation(Component.class);
		if (component != null && StringUtils.hasText(component.value())) {
			return true;
		}

		Service service = extensionClass.getAnnotation(Service.class);
		if (service != null) {
			return true;
		}

		Repository repository = extensionClass.getAnnotation(Repository.class);
		if (repository != null && StringUtils.hasText(repository.value())) {
			return true;
		}

		return false;

	}

	public static String getBeanName(Class<?> extensionClass, String defaultName) {

		RestController restController = extensionClass.getAnnotation(RestController.class);
		if (restController != null && StringUtils.hasText(restController.value())) {
			return restController.value();
		}

		Controller controller = extensionClass.getAnnotation(Controller.class);
		if (controller != null && StringUtils.hasText(controller.value())) {
			return controller.value();
		}

		Component component = extensionClass.getAnnotation(Component.class);
		if (component != null && StringUtils.hasText(component.value())) {
			return component.value();
		}

		Service service = extensionClass.getAnnotation(Service.class);
		if (service != null) {
			return service.value();
		}

		Repository repository = extensionClass.getAnnotation(Repository.class);
		if (repository != null && StringUtils.hasText(repository.value())) {
			return repository.value();
		}

		return defaultName;
	}

}