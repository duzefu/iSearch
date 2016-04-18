package db.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.SessionFactory;

import server.info.entites.transactionlevel.CategoryEntity;
import server.info.entites.transactionlevel.UserInterestValueEntity;
import db.dao.CategoryDao;
import db.dao.UserInterestValueDao;
import db.entityswithers.CategorySwitcher;
import db.entityswithers.UserInterestValueSwitcher;
import db.hibernate.tables.isearch.Category;
import db.hibernate.tables.isearch.User;
import db.hibernate.tables.isearch.UserInterestValue;

public class UserInterestValueDaoImpl implements UserInterestValueDao {
	private SessionFactory sessionFactory;
	private static CategoryDao categoryDao;

	public CategoryDao getCategoryDao() {
		return categoryDao;
	}

	public void setCategoryDao(CategoryDao categoryDao) {
		this.categoryDao = categoryDao;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Override
	public int add(UserInterestValueEntity interest) {
		int ret = -1;
		if (null == interest)
			return ret;
		UserInterestValue target = UserInterestValueSwitcher
				.userInterestEntityToPojo(interest);
		if (target != null) {
			this.sessionFactory.getCurrentSession().save(target);
			ret = target.getId();
		}
		return ret;
	}

	@Override
	public void delete(int uid, int cid) {
		if (uid <= 0)
			return;
		UserInterestValue target = this.getPojoByuId(uid, cid);
		if (null != target)
			this.sessionFactory.getCurrentSession().delete(target);
		return;
	}

	@Override
	public int update(UserInterestValueEntity interest) {
		int ret = -1;
		int interestId = interest.getId();
		if (interestId <= 0)
			return ret;
		UserInterestValue target = this.getPojoByuId(interestId,
				interest.getCategory_id());
		if (null == target)
			return ret;
		this.changePojo(target, interest);
		this.sessionFactory.getCurrentSession().update(target);
		ret = target.getId();
		return ret;
	}

	@Override
	public UserInterestValueEntity get(int id) {
		UserInterestValueEntity ret = null;
		if (id <= 0)
			return ret;
		UserInterestValue tarPojo = null;
		Iterator<UserInterestValue> itLogs = this.sessionFactory
				.getCurrentSession()
				.createQuery(
						"from UserInterestValue interest where interest.id = :id")
				.setParameter("id", id).list().iterator();
		if (itLogs.hasNext()) {
			tarPojo = itLogs.next();
			ret = UserInterestValueSwitcher.userinterestPojoToEntity(tarPojo);
		}

		return ret;
	}
	
	@Override
	public List<UserInterestValueEntity> getEntitys(int uid) {
		List<UserInterestValueEntity> ret= null;
		if (uid <= 0)
			return ret;
		UserInterestValue tarPojo = null;
		Iterator<UserInterestValue> itLogs = this.sessionFactory
				.getCurrentSession()
				.createQuery(
						"from UserInterestValue interest where interest.user.id = :id")
				.setParameter("id", uid).list()
				.iterator();
		if (itLogs.hasNext()) {
			ret = new ArrayList<UserInterestValueEntity>();
		}
		while(itLogs.hasNext()){
			tarPojo = itLogs.next();
			ret.add(UserInterestValueSwitcher.userinterestPojoToEntity(tarPojo));
		}
		return ret;
	}

	@Override
	public UserInterestValueEntity getEntity(int uid, int cid, Date date) {
		UserInterestValueEntity ret = null;
		if (uid <= 0)
			return ret;
		UserInterestValue tarPojo = null;
		Iterator<UserInterestValue> itLogs = this.sessionFactory
				.getCurrentSession()
				.createQuery(
						"from UserInterestValue interest where interest.user.id = :id and category.id = :cid and date = :date")
				.setParameter("id", uid).setParameter("cid", cid).setParameter("date", date).list()
				.iterator();
		if (itLogs.hasNext()) {
			tarPojo = itLogs.next();
			ret = UserInterestValueSwitcher.userinterestPojoToEntity(tarPojo);
		}

		return ret;
	}

	@Override
	public void getInterestThemsOfUser(List<UserInterestValueEntity> ret,
			int userid) {
		if (userid <= 0 || null == ret)
			return;

		List<UserInterestValue> interestPojoList = sessionFactory
				.getCurrentSession()
				.createQuery(
						"from UserInterestValue interest where interest.user.id = :uid")
				.setParameter("uid", userid).list();
		if (null != interestPojoList)
			UserInterestValueSwitcher.userinterestPojoToEntity(ret,
					interestPojoList);
	}

	protected UserInterestValue getPojoByuId(int uid, int cid) {

		UserInterestValue ret = null;
		if (uid <= 0)
			return ret;
		Iterator<UserInterestValue> itTarget = this.sessionFactory
				.getCurrentSession()
				.createQuery(
						"from UserInterestValue interest where interest.user.userid = :uid and category.id = :cid")
				.setParameter("uid", uid).setParameter("cid", cid).list()
				.iterator();
		if (itTarget.hasNext())
			ret = itTarget.next();
		return ret;
	}

	protected void changePojo(UserInterestValue pojo,
			UserInterestValueEntity entity) {

		if (null == pojo || null == entity)
			return;
		pojo.setId(entity.getId());
		int uid = entity.getUid();
		if (uid <= 0)
			return;
		User user = new User();
		user.setUserid(uid);
		pojo.setUser(user);

		int categoryId = entity.getCategory_id();
		if (categoryId <= 0)
			return;
		CategoryEntity tarCateEntity = categoryDao.get(categoryId);
		Category tarCate = null;
		if (null != tarCateEntity)
			tarCate = CategorySwitcher.categoryPojoToEntity(tarCateEntity);
		pojo.setCategory(tarCate);
		pojo.setDate(entity.getDate());
		pojo.setValue(entity.getValue());
		return;
	}

}
