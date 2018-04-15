package org.pf4j.spring.boot.hooks;

import org.pf4j.PluginManager;

public class Pf4jShutdownHook extends Thread {
	
	private PluginManager pluginManager;
	
	public Pf4jShutdownHook(PluginManager pluginManager) {
		this.pluginManager = pluginManager;
	}
	
	@Override
	public void run() {
		// 销毁插件
		if (pluginManager != null) {
			/*
			 * 调用Plugin实现类的stop()方法
			 */
			pluginManager.stopPlugins();
		}
	}
	
}
