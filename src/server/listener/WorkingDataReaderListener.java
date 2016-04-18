package server.listener;

import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import common.entities.blackboard.GetBlackboardThread;
import common.textprocess.userXMLfilehelpler.UserXMLHelper;

public class WorkingDataReaderListener implements ServletContextListener{

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

	}
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
		//服务器启动时产生黑板实例，这个过程将黑板上的九大分类已经设置好
		GetBlackboardThread getBB=new GetBlackboardThread();
		//服务器启动时创建一个上下文文件
		Thread thread = new Thread(getBB);
		thread.start();
		//实现服务器启动时初始化用户兴趣xml文件
		new Thread(){
			@Override
			public void run(){
//				UserXMLHelper.getInstance().update();
			}
		}.start();
		
	}
}
