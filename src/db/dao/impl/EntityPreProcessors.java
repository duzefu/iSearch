package db.dao.impl;

import server.commonutils.SpringBeanFactoryUtil;
import server.info.config.SpringBeanNames;
import server.info.entites.transactionlevel.CategoryEntity;
import db.dao.CategoryDao;

public class EntityPreProcessors {

	public static void categoryEntityPreprocess(CategoryEntity centity) {

		int cid = -1;
		String cname = null;
		//不处理的条件：参数为null、类别的ID与名称都已经合法、类别与名称都不合法
		if (null == centity 
				|| (cid = centity.getId()) > 0&& (cname = centity.getName()) != null&& !cname.isEmpty() 
				|| cid <= 0 && (null == cname || cname.isEmpty()))
			return;
		CategoryDao categoryDao = (CategoryDao) SpringBeanFactoryUtil
				.getBean(SpringBeanNames.CATEGORY_DAO_BEAN_NAME);
		if (cid <= 0){
			cid = categoryDao.getCategoryIDByName(cname);
			centity.setId(cid);
		}else{
			cname=categoryDao.getCategoryNameByID(cid);
			centity.setName(cname);
		}
		
		return;
	}
	
}
