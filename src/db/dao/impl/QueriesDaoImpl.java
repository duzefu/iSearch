package db.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.SessionFactory;

import common.functions.recommendation.qfg.QueryTriple;
import common.textprocess.similarity.EditFeatures;
import db.dao.QueriesDao;
import db.hibernate.tables.isearch.Queries;

public class QueriesDaoImpl implements QueriesDao {

	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Override
	public int add(String query, Date date) {

		if(null==query||query.isEmpty()) return -1;
		int ret=-1;
		Queries target=this.getQueriesPojoByContent(query);
		if(null==target){
			target=new Queries();
			target.setQuery(query);
			target.setDate(date);
			this.sessionFactory.getCurrentSession().save(target);
		}
		
		return target.getId();
		
	}

	@Override
	public int getQueryID(String query) {
		
		if(null==query||query.isEmpty()) return -1;
		int ret=-1;
		Queries target=this.getQueriesPojoByContent(query);
		if(null!=target) ret=target.getId();
		return ret;
	}
	
	protected Queries getQueriesPojoByContent(String query){
		
		if (null == query || query.isEmpty())
			return null;
		Queries ret = null;
		Iterator<Queries> iterQueries = this.sessionFactory.getCurrentSession()
				.createQuery("from Queries q where q.query = :word")
				.setParameter("word", query).list().iterator();
		if (iterQueries.hasNext())
			ret = iterQueries.next();
		return ret;
		
	}

	@Override
	public List<String> getSimiliarWords(String query) {
		
		if(null==query) return null;
		List<String> ret=new ArrayList<String>();
		if(query.isEmpty()) return ret;
		
		EditFeatures ef=new EditFeatures();
		QueryTriple qt1=new QueryTriple();
		qt1.setQueryContent(query);
		ef.setQuery1(qt1);
		Iterator<Queries> iterQuery=this.sessionFactory.getCurrentSession().createQuery("from Queries").list().iterator();
		while(iterQuery.hasNext())
		{
			String curQuery=iterQuery.next().getQuery();
			QueryTriple qt2=new QueryTriple();
			qt2.setQueryContent(curQuery);
			ef.setQuery2(qt2);
			if(ef.isSimiliar()) ret.add(curQuery);
		}
		
		return ret;
	}

}
