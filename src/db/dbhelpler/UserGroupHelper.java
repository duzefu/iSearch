package db.dbhelpler;

import java.util.Set;

import server.commonutils.SpringBeanFactoryUtil;
import server.info.config.SpringBeanNames;

import com.sun.javafx.collections.MappingChange.Map;

import db.dao.ClickLogDao;
import db.dao.UserGroupDao;

public class UserGroupHelper {

	//单例模式相关
	private UserGroupDao groupdao;
	private static UserGroupHelper instance;
	private static UserGroupHelper getInstance() {
		if (null == instance) {
			synchronized (UserGroupHelper.class) {
				if (null == instance) {
					instance = new UserGroupHelper();
				}
			}
		}
		return instance;
	}
	
	private UserGroupHelper() {
		groupdao = (UserGroupDao) SpringBeanFactoryUtil.getBean(SpringBeanNames.USER_GROUP_DAO_BEAN_NAME);
	}
	
	public static UserGroupDao getGroupDao(){
		return getInstance().groupdao;
	}
	
	public static void getGroupUserID(int userid, Set<Integer> ret){
		
		if(!UserHelper.isLegalUserID(userid)||null==ret) return;
		
		getGroupDao().getGroupUserID(userid,ret);
	}
	
}
