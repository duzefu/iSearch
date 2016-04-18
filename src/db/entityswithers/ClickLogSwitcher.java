package db.entityswithers;

import java.util.ArrayList;
import java.util.List;

import server.commonutils.SpringBeanFactoryUtil;
import server.info.config.SpringBeanNames;
import server.info.entites.transactionlevel.CategoryEntity;
import server.info.entites.transactionlevel.ClickRecordEntity;
import db.dao.CategoryDao;
import db.dbhelpler.ClickLogLengthLimit;
import db.hibernate.tables.isearch.Category;
import db.hibernate.tables.isearch.ClickLog;
import db.hibernate.tables.isearch.User;

public class ClickLogSwitcher {

	private static CategoryDao categoryDao;

	public static CategoryDao getCategoryDao() {
		if (null == categoryDao)
			categoryDao = (CategoryDao) SpringBeanFactoryUtil
					.getBean(SpringBeanNames.CATEGORY_DAO_BEAN_NAME);
		return categoryDao;
	}

	public static ClickLog clickLogEntityToPojo(ClickRecordEntity entity) {

		if (null == entity)
			return null;

		ClickLog ret = new ClickLog();
		ret.setAbstr(entity.getAbstr());
		ret.setDate(entity.getDatetime());

		int logid = entity.getId();
		if (logid > 0)
			ret.setId(logid);
		ret.setQuery(entity.getQuery());
		ret.setTitle(entity.getTitle());
		ret.setUrl(entity.getUrl());
		ret.setValue(1);
		ret.setClickRank(entity.getClickRank());
		ret.setRank(entity.getRank());

		int uid = entity.getUid();
		if (uid <= 0)
			return null;
		User user = new User();
		user.setUserid(uid);
		ret.setUser(user);

		int categoryId = entity.getCategoryId();
		if (categoryId <= 0)
			return null;
		CategoryEntity tarEntity = ClickLogSwitcher.getCategoryDao().get(categoryId);
		Category tarCate = null;
		if (null != tarEntity)
			tarCate = CategorySwitcher.categoryPojoToEntity(tarEntity);
		ret.setCategory(tarCate);

		if (!ClickLogLengthLimit.process(ret))
			ret = null;

		return ret;
	}
	
	public static void clickLogPojoToEntity(List<ClickRecordEntity>ret, List<ClickLog> pojolist){
		
		if(null==pojolist||pojolist.isEmpty()||null==ret) return;
		
		for(ClickLog logPojo:pojolist) ret.add(ClickLogSwitcher.clickLogPojoToEntity(logPojo));
		
	}
	
	public static ClickRecordEntity clickLogPojoToEntity(ClickLog pojo) {

		ClickRecordEntity ret = null;
		if (null == pojo)
			return ret;

		ret = new ClickRecordEntity();
		Integer ival = pojo.getCategory().getId();
		ret.setCategoryId(null == ival ? 0 : ival.intValue());
		ival = pojo.getClickRank();
		ret.setClickRank(null == ival ? 0 : ival.intValue());
		ival = pojo.getRank();
		ret.setRank(null == ival ? 0 : ival.intValue());
		ival = pojo.getId();
		ret.setId(null == ival ? 0 : ival);
		ret.setUid(pojo.getUser().getUserid());

		ret.setDatetime(pojo.getDate());
		ret.setAbstr(pojo.getAbstr());
		ret.setQuery(pojo.getQuery());
		ret.setTitle(pojo.getTitle());
		ret.setUrl(pojo.getUrl());
		ret.setWeight(pojo.getValue());

		return ret;
	}

}
