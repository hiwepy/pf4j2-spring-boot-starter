package org.pf4j.spring.boot.ext;

import org.pf4j.DefaultPluginClasspath;
import org.pf4j.DefaultPluginManager;
import org.pf4j.DevelopmentPluginClasspath;
import org.pf4j.ExtensionFactory;
import org.pf4j.PluginClasspath;
import org.pf4j.spring.SpringExtensionFactory;

public class Pf4jJarPluginWhitSpringManager extends DefaultPluginManager {

	public Pf4jJarPluginWhitSpringManager() {
	}
	
	public Pf4jJarPluginWhitSpringManager(PluginClasspath pluginClasspath) {
		this.pluginClasspath = pluginClasspath;
	}
	
	@Override
	protected PluginClasspath createPluginClasspath() {
		if(this.pluginClasspath != null) {
			return pluginClasspath;
		}
		return isDevelopment() ? new DevelopmentPluginClasspath() : new DefaultPluginClasspath();
    }
	
	@Override
	protected ExtensionFactory createExtensionFactory() {
		return new SpringExtensionFactory(this);
	}
	
}
