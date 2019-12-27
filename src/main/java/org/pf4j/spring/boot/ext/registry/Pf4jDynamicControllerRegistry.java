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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.PathMatcher;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * 动态注册Controller
 * @author 		： <a href="https://github.com/hiwepy">hiwepy</a>
 */
public class Pf4jDynamicControllerRegistry implements DynamicControllerRegistry, ApplicationContextAware {

	// RequestMappingHandlerMapping
	protected static Method detectHandlerMethodsMethod = ReflectionUtils.findMethod(RequestMappingHandlerMapping.class,
			"detectHandlerMethods", Object.class);
	protected static Method getMappingForMethodMethod = ReflectionUtils.findMethod(RequestMappingHandlerMapping.class,
			"getMappingForMethod", Method.class, Class.class);

	protected static Field mappingRegistryField = ReflectionUtils.findField(RequestMappingHandlerMapping.class,
			"mappingRegistry");

	protected static Field injectionMetadataCacheField = ReflectionUtils
			.findField(AutowiredAnnotationBeanPostProcessor.class, "injectionMetadataCache");

	/**
	 * 自动注入的时候，如果有多个对象会优选有@Primary注解的对象
	 */
	@Autowired(required = false)
	protected RequestMappingHandlerMapping requestMappingHandlerMapping;
	protected AbstractAutowireCapableBeanFactory beanFactory;
	protected ApplicationContext applicationContext;
	
	static {
		detectHandlerMethodsMethod.setAccessible(true);
		getMappingForMethodMethod.setAccessible(true);
		// urlMapField.setAccessible(true);
		mappingRegistryField.setAccessible(true);
		injectionMetadataCacheField.setAccessible(true);
	}
	
	@Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.beanFactory = (AbstractAutowireCapableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
    }

	public Pf4jDynamicControllerRegistry() {
	}
	
	public Pf4jDynamicControllerRegistry(AbstractAutowireCapableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	
	@Override
	public void registerController(String beanName, Object controller) {
		
		Assert.notNull(controller, "controller must not null");

		beanName = StringUtils.isEmpty(beanName) ? controller.getClass().getName() : beanName;

		// 1、如果RequestMapping存在则移除
		removeRequestMappingIfNecessary(beanName);
		// 2、注册新的Controller
		getBeanFactory().registerSingleton(beanName, controller);
		// 3、注册新的RequestMapping
		registerRequestMappingIfNecessary(beanName);
	}

	@Override
	public void removeController(String controllerBeanName) throws IOException {
		// 如果RequestMapping存在则移除
		removeRequestMappingIfNecessary(controllerBeanName);
	}

	@SuppressWarnings("unchecked")
	protected void removeRequestMappingIfNecessary(String controllerBeanName) {

		if (!getBeanFactory().containsBean(controllerBeanName)) {
			return;
		}

		RequestMappingHandlerMapping requestMappingHandlerMapping = getRequestMappingHandlerMapping();

		// remove old
		Class<?> handlerType = getApplicationContext().getType(controllerBeanName);
		final Class<?> userType = ClassUtils.getUserClass(handlerType);

		/*
		 * Map<RequestMappingInfo, HandlerMethod> handlerMethods =
		 * requestMappingHandlerMapping.getHandlerMethods(); 返回只读集合：
		 * 特别说明：因requestMappingHandlerMapping.getHandlerMethods()方法获取到的结果是只读集合，不能进行移除操作，
		 * 所以需要采用反射方式获取目标对象
		 */
		Object mappingRegistry = ReflectionUtils.getField(mappingRegistryField, requestMappingHandlerMapping);
		Method getMappingsMethod = ReflectionUtils.findMethod(mappingRegistry.getClass(), "getMappings");
		getMappingsMethod.setAccessible(true);
		Map<RequestMappingInfo, HandlerMethod> handlerMethods = (Map<RequestMappingInfo, HandlerMethod>) ReflectionUtils
				.invokeMethod(getMappingsMethod, mappingRegistry);

		/*
		 * 查找URL映射：解决 Ambiguous handler methods mapped for HTTP path “” 问题
		 */
		Field urlLookupField = ReflectionUtils.findField(mappingRegistry.getClass(), "urlLookup");
		urlLookupField.setAccessible(true);
		MultiValueMap<String, RequestMappingInfo> urlMapping = (MultiValueMap<String, RequestMappingInfo>) ReflectionUtils
				.getField(urlLookupField, mappingRegistry);

		final RequestMappingHandlerMapping innerRequestMappingHandlerMapping = requestMappingHandlerMapping;
		Set<Method> methods = MethodIntrospector.selectMethods(userType, new ReflectionUtils.MethodFilter() {
			@Override
			public boolean matches(Method method) {
				return ReflectionUtils.invokeMethod(getMappingForMethodMethod, innerRequestMappingHandlerMapping,
						method, userType) != null;
			}
		});

		for (Method method : methods) {

			RequestMappingInfo requestMappingInfo = (RequestMappingInfo) ReflectionUtils
					.invokeMethod(getMappingForMethodMethod, requestMappingHandlerMapping, method, userType);

			handlerMethods.remove(requestMappingInfo);

			PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();
			Set<String> patterns = patternsCondition.getPatterns();
			// (Set<String>) ReflectionUtils.invokeMethod(getMappingPathPatternsMethod,
			// requestMappingHandlerMapping, mapping);

			PathMatcher pathMatcher = requestMappingHandlerMapping.getPathMatcher();
			// (PathMatcher) ReflectionUtils.invokeMethod(getPathMatcherMethod,
			// requestMappingHandlerMapping);

			for (String pattern : patterns) {
				if (!pathMatcher.isPattern(pattern)) {
					urlMapping.remove(pattern);
				}
			}
		}

	}

	protected void registerRequestMappingIfNecessary(String controllerBeanName) {

		RequestMappingHandlerMapping requestMappingHandlerMapping = getRequestMappingHandlerMapping();
		// spring 3.1 开始
		ReflectionUtils.invokeMethod(detectHandlerMethodsMethod, requestMappingHandlerMapping, controllerBeanName);

	} 
	@SuppressWarnings("unchecked")
	protected Map<String, InjectionMetadata> getInjectionMetadataCache() {

		AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor = getApplicationContext()
				.getBean(AutowiredAnnotationBeanPostProcessor.class);

		Map<String, InjectionMetadata> injectionMetadataMap = (Map<String, InjectionMetadata>) ReflectionUtils
				.getField(injectionMetadataCacheField, autowiredAnnotationBeanPostProcessor);

		return injectionMetadataMap;
	}

	protected RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
		try {
			
			if (requestMappingHandlerMapping != null) {
				return requestMappingHandlerMapping;
			}
			
			Map<String, RequestMappingHandlerMapping> beans = BeanFactoryUtils.beansOfTypeIncludingAncestors(
					getApplicationContext(), RequestMappingHandlerMapping.class, true, false);
			if (!beans.isEmpty()) {
				List<RequestMappingHandlerMapping> mappings = new ArrayList<>(beans.values());
				for(RequestMappingHandlerMapping handlerMapping : beans.values()) {
					if(handlerMapping.getClass().getName().equals(RequestMappingHandlerMapping.class.getName())) {
						requestMappingHandlerMapping = handlerMapping;
						return handlerMapping;
					}
				}
				AnnotationAwareOrderComparator.sort(mappings);
				requestMappingHandlerMapping = mappings.get(0);
				return requestMappingHandlerMapping;
			}
			requestMappingHandlerMapping = getApplicationContext().getBean(RequestMappingHandlerMapping.class);
			return requestMappingHandlerMapping;
		} catch (Exception e) {
			throw new IllegalArgumentException("applicationContext must has RequestMappingHandlerMapping");
		}
	}

	public AbstractAutowireCapableBeanFactory getBeanFactory() {
		return beanFactory;
	}
	
	public void setBeanFactory(AbstractAutowireCapableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
}
