package db.entityswithers;

import java.util.List;

import db.dao.CategoryDao;
import db.hibernate.tables.isearch.Category;
import db.hibernate.tables.isearch.ClickLog;
import db.hibernate.tables.isearch.User;
import db.hibernate.tables.isearch.UserInterestValue;
import server.commonutils.SpringBeanFactoryUtil;
import server.info.config.SpringBeanNames;
import server.info.entites.transactionlevel.CategoryEntity;
import server.info.entites.transactionlevel.ClickRecordEntity;
import server.info.entites.transactionlevel.UserInterestValueEntity;

public class UserInterestValueSwitcher {
	private static CategoryDao categoryDao;

	public static CategoryDao getCategoryDao() {
		if (null == categoryDao)
			categoryDao = (CategoryDao) SpringBeanFactoryUtil
					.getBean(SpringBeanNames.CATEGORY_DAO_BEAN_NAME);
		return categoryDao;
	}

	public static UserInterestValue userInterestEntityToPojo(
			UserInterestValueEntity entity) {

		if (null == entity)
			return null;

		UserInterestValue ret = new UserInterestValue();
		ret.setValue(entity.getValue());
		ret.setDate(entity.getDate());
		;

		int interestid = entity.getId();
		if (interestid > 0)
			ret.setId(interestid);

		int uid = entity.getUid();
		if (uid <= 0)
			return null;
		User user = new User();
		user.setUserid(uid);
		ret.setUser(user);

		int categoryId = entity.getCategory_id();
		if (categoryId <= 0)
			return null;
		CategoryEntity tarEntity = UserInterestValueSwitcher.getCategoryDao()
				.get(categoryId);
		Category tarCate = null;
		if (null != tarEntity)
			tarCate = CategorySwitcher.categoryPojoToEntity(tarEntity);
		ret.setCategory(tarCate);

		return ret;
	}

	public static void userinterestPojoToEntity(List<UserInterestValueEntity> ret,
			List<UserInterestValue> pojolist) {

		if (null == pojolist || pojolist.isEmpty() || null == ret)
			return;

		for (UserInterestValue logPojo : pojolist)
			ret.add(UserInterestValueSwitcher.userinterestPojoToEntity(logPojo));

	}

	public static UserInterestValueEntity userinterestPojoToEntity(
			UserInterestValue pojo) {

		UserInterestValueEntity ret = null;
		if (null == pojo)
			return ret;

		ret = new UserInterestValueEntity();

		Integer ival = pojo.getCategory().getId();
		ret.setCategory_id(ival.intValue());

		ival = pojo.getId();
		ret.setId(null == ival ? 0 : ival);
		ret.setUid(pojo.getUser().getUserid());

		ret.setDate(pojo.getDate());
		ret.setValue(pojo.getValue());

		return ret;
	}
}
