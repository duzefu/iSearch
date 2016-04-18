package db.dao.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import server.info.entites.transactionlevel.CategoryEntity;
import server.info.entites.transactionlevel.UserFavorWordsEntity;
import db.dao.UserFavorWordsDao;
import db.entityswithers.UserFavorWordsSwitcher;
import db.hibernate.tables.isearch.Category;
import db.hibernate.tables.isearch.UserFavorWords;

public class UserFavorWordsDaoImpl implements UserFavorWordsDao {

	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Override
	public int add(UserFavorWordsEntity entity) {

		if (null == entity)
			return -1;

		UserFavorWords target = UserFavorWordsSwitcher
				.favorwordsPojoToEntity(entity);
		if (null == target)
			return -1;
		this.sessionFactory.getCurrentSession().save(target);

		return target.getWordid();
	}

	@Override
	public List<Integer> getIDByWordAndUser(String word, int uid) {

		if (null == word || word.isEmpty() || uid <= 0)
			return null;

		List<UserFavorWords> target = this.getPojoByWordAndUser(word, uid);
		List<Integer> ret = new ArrayList<Integer>();
		if (null == target)
			return ret;
		for (UserFavorWords pojo : target) {
			if (null == pojo)
				continue;
			ret.add(pojo.getWordid());
		}

		return ret;
	}

	@Override
	public List<String> getAllWordsByUser(int uid) {

		if (uid <= 0)
			return null;

		List<String> ret = new ArrayList<String>();
		List<UserFavorWords> pojoList = this.getPojoByWordAndUser(null, uid);
		if (null == pojoList)
			return ret;
		for (UserFavorWords pojo : pojoList) {
			if (null == pojo)
				continue;
			ret.add(pojo.getWord());
		}

		return ret;
	}

	@Override
	public int update(UserFavorWordsEntity entity) {

		if (null == entity || entity.getId() <= 0)
			return -1;

		UserFavorWords target = this.getPojoByID(entity.getId());
		if (null == target)
			return -1;
		this.changePojo(target, entity);
		this.sessionFactory.getCurrentSession().update(target);

		return target.getWordid();
	}

	@Override
	public List<UserFavorWordsEntity> getWordsOfUserOrderByWeightDesc(int uid) {

		return null;
	}

	@Override
	public boolean delete(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int findByWordAndCategory(String word, String categoryName) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int findByWordAndCategory(String word, int catetoryID) {
		// TODO Auto-generated method stub
		return 0;
	}

	protected List<UserFavorWords> getPojoByWordAndUser(String word, int uid) {

		if (uid <= 0)
			return null;

		boolean hasWord = null != null && !word.isEmpty();
		StringBuilder hql = new StringBuilder(
				"from UserFavorWords favor where favor.user.id = :uid");
		if (hasWord)
			hql.append(" and favor.word = :word");
		Query query = this.sessionFactory.getCurrentSession()
				.createQuery(hql.toString()).setParameter("uid", uid);
		if (hasWord)
			query = query.setParameter("word", word);
		Iterator<UserFavorWords> iterTarget = query.list().iterator();

		List<UserFavorWords> ret = new ArrayList<UserFavorWords>();
		while (iterTarget.hasNext()) {
			UserFavorWords currentPojo = iterTarget.next();
			if (null != currentPojo)
				ret.add(currentPojo);
		}

		return ret;
	}

	protected UserFavorWords getPojoByID(int id) {

		if (id <= 0)
			return null;

		UserFavorWords ret = null;
		Iterator<UserFavorWords> iterTarget = this.sessionFactory
				.getCurrentSession()
				.createQuery("from UserFavorWords favor where favor.id = :id")
				.setParameter("id", id).list().iterator();
		if (null != iterTarget)
			ret = iterTarget.next();

		return ret;
	}

	protected void changePojo(UserFavorWords pojo, UserFavorWordsEntity entity) {

		if (null == pojo || null == entity)
			return;

		Category orgCategory = pojo.getCategory();
		if (null == orgCategory)
			orgCategory = new Category();
		CategoryEntity centity = entity.getCategory();
		EntityPreProcessors.categoryEntityPreprocess(centity);
		orgCategory.setId(centity.getId());
		orgCategory.setCategoryName(centity.getName());

		
	}

	@Override
	public List<UserFavorWordsEntity> getWordsOfUser(int uid) {

		if (uid <= 0)
			return null;

		List<UserFavorWords> pojoList = this.sessionFactory
				.getCurrentSession()
				.createQuery(
						"from UserFavorWords favor where favor.user.id = :uid")
				.setParameter("uid", uid).list();
		return UserFavorWordsSwitcher.favorwordsListPojoToEntity(pojoList);
	}

}
