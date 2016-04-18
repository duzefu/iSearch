package server.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import server.info.config.ConfigFilePath;
import server.info.config.EmailInfo;

/**
 * 初始化服务器相关常量值
 * 
 * @author zcl
 */
public class ServerInitListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {

		//获取项目根目录
		String root = arg0.getServletContext().getRealPath("/");
		ConfigFilePath.init(root);
		EmailInfo.initEmailAddr();
		//webRoot环境变量会用在log4j.properties文件中
		System.setProperty("webRoot",root);
	}

}