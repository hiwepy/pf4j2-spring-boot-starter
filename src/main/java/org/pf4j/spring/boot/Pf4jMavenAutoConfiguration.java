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
package org.pf4j.spring.boot;

import org.pf4j.spring.boot.ext.update.DefaultPluginInfoProvider;
import org.pf4j.spring.boot.ext.update.MavenUpdateRepository;
import org.pf4j.spring.boot.ext.update.PluginInfoProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.deployer.resource.maven.MavenProperties;
import org.springframework.cloud.deployer.resource.maven.MavenResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TODO
 * @author 		： <a href="https://github.com/hiwepy">hiwepy</a>
 */
@Configuration
@ConditionalOnClass({ MavenResource.class })
@ConditionalOnProperty(prefix = Pf4jMavenProperties.PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties({Pf4jMavenProperties.class})
public class Pf4jMavenAutoConfiguration {
	
	@Bean
	@ConditionalOnMissingBean
	public PluginInfoProvider pluginInfoProvider() {
		return new DefaultPluginInfoProvider();
	}

	@Bean
	public MavenUpdateRepository mavenUpdateRepository(MavenProperties mavenProperties,
			PluginInfoProvider pluginInfoProvider) {
		return new MavenUpdateRepository(mavenProperties, pluginInfoProvider);
	}
	
}
