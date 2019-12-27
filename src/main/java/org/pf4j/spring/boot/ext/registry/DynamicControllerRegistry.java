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
package org.pf4j.spring.boot.ext.registry;

import java.io.IOException;

/**
 * TODO
 * @author 		： <a href="https://github.com/hiwepy">hiwepy</a>
 */
public interface DynamicControllerRegistry{

    /**
     * 动态注册SpringMVC Controller到Spring上下文
     * @param controllerBeanName	: The name of controller 
     * @param controller			: The instance of controller
     */
	public void registerController(String controllerBeanName, Object controller);
	
    /**
     * 动态从Spring上下文删除SpringMVC Controller
     * @param controllerBeanName		: The name of controller 
     * @throws IOException if io error
     */
    public void removeController(String controllerBeanName) throws IOException;
	
	
}
