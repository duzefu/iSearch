package server.usedfordebug;

import java.util.ArrayList;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import common.entities.searchresult.Result;
import server.commonutils.SpringBeanFactoryUtil;
import server.engine.api.Baidu;
import server.engine.api.IEEEScientificAPI;
import server.engine.api.Sogou;
import server.info.config.SpringBeanNames;
import db.dao.UserDao;
import db.dbhelpler.UserHelper;

/**
 * 调试专用listener
 * 用这个类来做调试工作，在提交的时候，把自己修改过的部分删除，不要上传；
 * 否则容易出现冲突
 * 特别留意，上面的import也会因为自动添加新的内容
 * @author zcl
 */
public class DebugEntryListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {

		try{
//			Baidu elem = new Baidu();
			/*IEEEScientificAPI elem = new IEEEScientificAPI();
			ArrayList<Result> list = new ArrayList<Result>();
			elem.getMyResults(list, "agent", 1, 100000, 1);
			System.out.println(list.size());*/
			//elem.getMyResults("agent", 0, 100000, 0);
		/*******************************开始****************************/
//		MyClass.AStaticFuncion(***);
//		
//		MyClass mc=new MyClass(****);
//		mc.funcion();
		/*******************************结束****************************/
		}catch(Throwable t){
			t.printStackTrace();
		}
		
		return;
	}

}
