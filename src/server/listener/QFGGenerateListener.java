//package server.listener;
//
//import java.util.Timer;
//import java.util.TimerTask;
//
//import javax.servlet.ServletContextEvent;
//import javax.servlet.ServletContextListener;
//
//import common.functions.recommendation.qfg.GenerateQFGThread;
//import server.commonutils.LogU;
//import server.commonutils.SpringBeanFactoryUtil;
//
///**
// * 这个类最初是用来将搜狗实验室提供的搜索日志导入click_log数据库表，以及生成查询流图的
// * 需要读取WebRoot/iSearch/Sougou-Logs/SogouQ.reduced文件
// * 通过在web.xml中配置updateQFG以及procSgLog两个值为false可以避免这个过程，或直接删除这个listener在web.xml中的配置
// * 后续不必再使用这个类
// * @author zhou
// *
// */
//public class QFGGenerateListener implements ServletContextListener {
//
//	private static String update = null;
//	private static String sgLogProcess=null;
//	
//	private static SougouLogProcessThread sglogProc;
//	private static GenerateQFGThread qfgTread;
//	
//	@Override
//	public void contextDestroyed(ServletContextEvent sce) {
//
//	}
//
//	@Override
//	public void contextInitialized(ServletContextEvent sce) {
//
//		try{
//		update = sce.getServletContext().getInitParameter("updateQFG");
//		sgLogProcess=sce.getServletContext().getInitParameter("procSgLog");
//		
////		WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(sce.getServletContext());
//		sglogProc=(SougouLogProcessThread) SpringBeanFactoryUtil.getBean("sglogProcThread");
//		qfgTread=(GenerateQFGThread) SpringBeanFactoryUtil.getBean("qfgGenThread");
//		}catch(Exception e){
//			LogU.printConsole(this.getClass(), e.toString());
//		}
//		
//		Timer timer = new Timer();
//		timer.schedule(new TimerTask() {
//			@Override
//			public void run() {
//
//				if("true".equals(sgLogProcess)){
//					Thread thread = new Thread(sglogProc);
//					thread.start();
//					LogU.printConsole(this.getClass().getName(),
//							"搜狗日志处理线程启动完成。");
//				}
//				
//				if ("true".equals(update)) {
//					Thread thread = new Thread(qfgTread);
//					thread.start();
//					LogU.printConsole(this.getClass().getName(),
//							"查询流图生成线程启动结束。");
//				}
//			}
//		}, 10, 365 * 24 * 60 * 60 * 1000);// 在1秒后执行此任务
//	}
//}
