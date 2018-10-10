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
package org.pf4j.spring.boot.ext.utils;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
public class InjectorUtils {

	public static boolean isController(Object bean) {

		RestController restController = bean.getClass().getAnnotation(RestController.class);
		if (restController != null) {
			return true;
		}

		Controller controller = bean.getClass().getAnnotation(Controller.class);
		if (controller != null) {
			return true;
		}

		return false;
	}

	public static boolean isInjectNecessary(Object bean) {

		RestController restController = bean.getClass().getAnnotation(RestController.class);
		if (restController != null) {
			return true;
		}

		Controller controller = bean.getClass().getAnnotation(Controller.class);
		if (controller != null) {
			return true;
		}

		Component component = bean.getClass().getAnnotation(Component.class);
		if (component != null && StringUtils.hasText(component.value())) {
			return true;
		}

		Service service = bean.getClass().getAnnotation(Service.class);
		if (service != null) {
			return true;
		}

		Repository repository = bean.getClass().getAnnotation(Repository.class);
		if (repository != null && StringUtils.hasText(repository.value())) {
			return true;
		}

		return false;

	}

	public static String getBeanName(Object bean, String defaultName) {

		RestController restController = bean.getClass().getAnnotation(RestController.class);
		if (restController != null && StringUtils.hasText(restController.value())) {
			return restController.value();
		}

		Controller controller = bean.getClass().getAnnotation(Controller.class);
		if (controller != null && StringUtils.hasText(controller.value())) {
			return controller.value();
		}

		Component component = bean.getClass().getAnnotation(Component.class);
		if (component != null && StringUtils.hasText(component.value())) {
			return component.value();
		}

		Service service = bean.getClass().getAnnotation(Service.class);
		if (service != null) {
			return service.value();
		}

		Repository repository = bean.getClass().getAnnotation(Repository.class);
		if (repository != null && StringUtils.hasText(repository.value())) {
			return repository.value();
		}

		return defaultName;
	}

}