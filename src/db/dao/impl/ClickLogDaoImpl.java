package db.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import server.commonutils.MyStringChecker;
import server.info.entites.transactionlevel.CategoryEntity;
import server.info.entites.transactionlevel.ClickRecordEntity;
import common.functions.recommendation.click.QueryClickCountAndSim;
import common.textprocess.textclassifier.BayesClassifier;
import common.textprocess.textclassifier.ClassifyResult;
import common.textprocess.textsegmentation.CreateWordList;
import common.textprocess.textsegmentation.Word;
import common.textprocess.textsegmentation.WordList;
import db.dao.CategoryDao;
import db.dao.ClickLogDao;
import db.dbhelpler.UserHelper;
import db.entityswithers.CategorySwitcher;
import db.entityswithers.ClickLogSwitcher;
import db.hibernate.tables.isearch.Category;
import db.hibernate.tables.isearch.ClickLog;
import db.hibernate.tables.isearch.User;

public class ClickLogDaoImpl implements ClickLogDao {

	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	private static CategoryDao categoryDao;

	public CategoryDao getCategoryDao() {
		return categoryDao;
	}

	public void setCategoryDao(CategoryDao categoryDao) {
		this.categoryDao = categoryDao;
	}

	@Override
	public int add(ClickRecordEntity log) {

		int ret = -1;
		if (null == log)
			return ret;
		ClickLog target = ClickLogSwitcher.clickLogEntityToPojo(log);
		if (target != null) {
			this.sessionFactory.getCurrentSession().save(target);
			ret = target.getId();
		}
		return ret;
	}

	@Override
	public void delete(int id) {

		if (id <= 0)
			return;
		ClickLog target = this.getPojoById(id);
		if (null != target)
			this.sessionFactory.getCurrentSession().delete(target);
		return;
	}

	@Override
	public int update(ClickRecordEntity log) {

		int ret = -1;
		int logId = log.getId();
		if (logId <= 0)
			return ret;
		ClickLog target = this.getPojoById(logId);
		if (null == target)
			return ret;
		this.changePojo(target, log);
		this.sessionFactory.getCurrentSession().update(target);
		ret = target.getId();
		return ret;
	}

	@Override
	public ClickRecordEntity get(int id) {

		ClickRecordEntity ret = null;
		if (id <= 0)
			return ret;
		ClickLog tarPojo = null;
		Iterator<ClickLog> itLogs = this.sessionFactory.getCurrentSession()
				.createQuery("from ClickLog clog where clog.id = :id")
				.setParameter("id", id).list().iterator();
		if (itLogs.hasNext()) {
			tarPojo = itLogs.next();
			ret = ClickLogSwitcher.clickLogPojoToEntity(tarPojo);
		}

		return ret;
	}

	protected ClickLog getPojoById(int id) {

		ClickLog ret = null;
		if (id <= 0)
			return ret;
		Iterator<ClickLog> itTarget = this.sessionFactory.getCurrentSession()
				.createQuery("from ClickLog log where log.id = :id")
				.setParameter("id", id).list().iterator();
		if (itTarget.hasNext())
			ret = itTarget.next();
		return ret;
	}

	protected void changePojo(ClickLog pojo, ClickRecordEntity entity) {

		if (null == pojo || null == entity)
			return;
		pojo.setAbstr(entity.getAbstr());
		pojo.setDate(entity.getDatetime());
		pojo.setId(entity.getId());
		pojo.setQuery(entity.getQuery());
		pojo.setTitle(entity.getTitle());
		pojo.setUrl(entity.getUrl());

		int uid = entity.getUid();
		if (uid <= 0)
			return;
		User user = new User();
		user.setUserid(uid);
		pojo.setUser(user);

		int categoryId = entity.getCategoryId();
		if (categoryId <= 0)
			return;
		CategoryEntity tarCateEntity = categoryDao.get(categoryId);
		Category tarCate = null;
		if (null != tarCateEntity)
			tarCate = CategorySwitcher.categoryPojoToEntity(tarCateEntity);
		pojo.setCategory(tarCate);
		return;
	}

	@Override
	public void getLogOfUser(List<ClickRecordEntity> ret, int userid) {
		
		if(!UserHelper.isLegalUserID(userid)||null==ret) return;
		
		List<ClickLog> logPojoList=
				sessionFactory.getCurrentSession()
				.createQuery("from ClickLog log where log.user.id = :uid")
				.setParameter("uid", userid).list();
		if(null!=logPojoList) ClickLogSwitcher.clickLogPojoToEntity(ret,logPojoList);
		
	}

	@Override
	public void getLogOfUser(List<ClickRecordEntity> ret, Set<Integer> uidSet) {
		
		//参数检查
		if(null==uidSet||uidSet.isEmpty()||null==ret) return;
		
		//查数据库
		List<ClickLog> logPojoList=
				sessionFactory
				.getCurrentSession()
				.createQuery("from ClickLog log where log.user.id in (:uidlist)")
				.setParameterList("uidlist", uidSet)
				.list();
		
		//数据类型转换
		if(null!=logPojoList) ClickLogSwitcher.clickLogPojoToEntity(ret,logPojoList);
		
	}
	
	@Override
	public void getLogOfUser(List<ClickRecordEntity> ret, Set<Integer> uidSet,
			String query) {
		
		//参数检查
		if(null==uidSet||uidSet.isEmpty()||null==ret||MyStringChecker.isBlank(query)) return;
		
		//查数据库
		List<ClickLog> logPojoList=
				sessionFactory
				.getCurrentSession()
				.createQuery("from ClickLog log where log.user.id in (:uidlist) and log.query = :query")
				.setParameterList("uidlist", uidSet)
				.setParameter("query", query)
				.list();
		
		//数据类型转换
		if(null!=logPojoList) ClickLogSwitcher.clickLogPojoToEntity(ret,logPojoList);
		
	}

	@Override
	public void updateClassification() {
		
		try{
		Session session=this.sessionFactory.getCurrentSession();
		Iterator<ClickLog> log=session.createQuery("from ClickLog log where (log.classification is null or log.category is null or log.category.id is null) order by log.id").list().iterator();
		Map<String, Integer> ctid=new HashMap<String, Integer>();
		int count=0;
		while(log.hasNext()){
			
			ClickLog cl=log.next();
			String c=null;
			String[] info = new String[2];
			String title=cl.getTitle(), abstr=cl.getAbstr();
			title=(null==title)?"":title;
			abstr=(null==abstr)?"":abstr;
			info[0] = title;
			info[1] = abstr;
			WordList wl = CreateWordList.get(info);
					String classificationt=null;
			try{
				classificationt= BayesClassifier.bayes(wl);
			}catch(Exception e){
				e.printStackTrace();
				classificationt="education";
			}
			Integer cid=ctid.get(classificationt);
			if(null==cid){
				cid=categoryDao.getCategoryIDByName(classificationt);
				ctid.put(classificationt, cid);
			}
			cl.setClassification(classificationt);
			Category category=new Category();
			category.setId(cid);
			cl.setCategory(category);
			session.update(cl);
			if(++count==200) break;
		}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void getLogOrderbyQueryInc(List<ClickRecordEntity> ret,
			Set<Integer> uidSet) {
		
		if(null==ret||null==uidSet||uidSet.isEmpty()) return;
		
		List<ClickLog> ls=sessionFactory.getCurrentSession()
		.createQuery("from ClickLog log where log.user.userid in (:uidset) order by log.query")
		.setParameterList("uidset", uidSet).list();
		
		ClickLogSwitcher.clickLogPojoToEntity(ret, ls);
		
	}

	@Override
	public void getLogOfUser(List<ClickRecordEntity> ret, int userid,
			String query) {
		
		if(null==ret||!UserHelper.isLegalUserID(userid)||MyStringChecker.isBlank(query)) return;
		
		List<ClickLog> ls=sessionFactory.getCurrentSession()
				.createQuery("from ClickLog log where log.user.userid = :userid and log.query = :query")
				.setParameter("userid", userid).setParameter("query", query).list();
		
		ClickLogSwitcher.clickLogPojoToEntity(ret, ls);
	}


}
