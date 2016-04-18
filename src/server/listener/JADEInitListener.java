package server.listener;

import jade.wrapper.StaleProxyException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import agent.utils.AgentFactory;

/**
 * 在服务器启动时，初始化JADE平台
 * 创建各类Agent
 * 
 * @author zcl
 */
public class JADEInitListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {

		try {
			AgentFactory.createAgentOnServerStartup();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
	}

}