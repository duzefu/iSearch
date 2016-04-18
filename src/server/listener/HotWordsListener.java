package server.listener;

import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import server.commonutils.HotwordsUtil;

public class HotWordsListener implements ServletContextListener{

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

	}
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
		//实现服务器启动时获取热门词汇，并于之后的每一个小时更新一次
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {	
			@Override
			public void run() {
				HotwordsUtil.updateHotwords();
			}
		}, 1000, 3600000);//在1秒后执行此任务,每次间隔1小时.
	
	}
}
