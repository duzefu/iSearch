package db.dao.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;

import server.commonutils.TimeUtil;
import common.textprocess.similarity.EditFeatures;
import db.dao.QFGGenDao;
import db.dao.QueriesDao;
import db.entityswithers.QFGFeaturesSwitcher;
import db.hibernate.tables.isearch.QfgFeatures;
import db.hibernate.tables.isearch.Queries;

public class QFGGenDaoImpl implements QFGGenDao {

	private SessionFactory sessionFactory;
	private QueriesDao queriesDao;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public QueriesDao getQueriesDao() {
		return queriesDao;
	}

	public void setQueriesDao(QueriesDao queriesDao) {
		this.queriesDao = queriesDao;
	}

	@Override
	public int add(EditFeatures weight, String cookie) {

		if (null == weight || null == cookie || cookie.isEmpty())
			return -1;

		int ret = -1;
		String query1 = weight.getQuery1().getQueryContent(), query2 = weight
				.getQuery2().getQueryContent();
		Calendar cal = TimeUtil.fromTimeStringToCalendar(weight.getQuery1()
				.getQueryTime(), ":");
		cal.set(Calendar.YEAR, 2015);
		cal.set(Calendar.MONTH, 3);
		cal.set(Calendar.DAY_OF_MONTH, 28);
		Date dateq1 = cal.getTime();

		cal = TimeUtil.fromTimeStringToCalendar(weight.getQuery2()
				.getQueryTime(), ":");
		cal.set(Calendar.YEAR, 2015);
		cal.set(Calendar.MONTH, 3);
		cal.set(Calendar.DAY_OF_MONTH, 28);
		Date dateq2 = cal.getTime();

		QfgFeatures target = this.getPojoByQueries(query1, query2, cookie);
		if (null == target) {
			int idQ1 = this.queriesDao.add(query1, dateq1), idQ2 = this.queriesDao
					.add(query2, dateq2);
			if (idQ1 <= 0 && idQ2 <= 0)
				return ret;
			Queries q1 = new Queries(), q2 = new Queries();
			q1.setId(idQ1);
			q2.setId(idQ2);
			target = QFGFeaturesSwitcher.qfgFeaturesEntityToPojo(weight,
					cookie, q1, q2);
			this.sessionFactory.getCurrentSession().save(target);
		}

		return target.getId();
	}

	@Override
	public EditFeatures getFeatures(String query1, String query2, String cookie) {

		if (null == query1 || null == query2 || query1.isEmpty()
				|| query2.isEmpty() || null == cookie || cookie.isEmpty())
			return null;

		EditFeatures ret = null;
		QfgFeatures targetPojo = this.getPojoByQueries(query1, query2, cookie);
		if (null != targetPojo)
			ret = QFGFeaturesSwitcher.qfgFeaturesPojoToEntity(targetPojo);

		return ret;
	}

	protected QfgFeatures getPojoByQueries(String query1, String query2,
			String cookie) {

		if (null == query1 || null == query2 || null == cookie
				|| query1.isEmpty() || query2.isEmpty() || cookie.isEmpty())
			return null;

		QfgFeatures ret = null;
		Set<String> qset = new HashSet<String>();
		qset.add(query1);
		qset.add(query2);
		Iterator<QfgFeatures> iterPojo = this.sessionFactory
				.getCurrentSession()
				.createQuery(
						"from QfgFeatures feature where feature.cookie=:cookie and feature.queriesByQueryFirst.query in (:qset1) and feature.queriesByQuerySecond.query in (:qset2)")
				.setParameter("cookie", cookie).setParameterList("qset1", qset)
				.setParameterList("qset2", qset).list().iterator();
		if (iterPojo.hasNext())
			ret = iterPojo.next();

		return ret;
	}

	@Override
	public boolean getFeaturesByFirstWord(List<EditFeatures> ret,
			Set<String> wset) {

		if (null == wset||null==ret)
			return false;
		if (wset.isEmpty())
			return true;

		List<QfgFeatures> lsPojo = sessionFactory.getCurrentSession()
				.createQuery(	"from QfgFeatures feature where feature.queriesByQueryFirst.query in (:wset)")
				.setParameterList("wset", wset).list();
		QFGFeaturesSwitcher.qfgFeaturesPojoToEntity(ret, lsPojo);
		
		return true;
	}

}
