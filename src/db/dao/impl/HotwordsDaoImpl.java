package db.dao.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import server.commonutils.MyStringChecker;
import db.dao.HotwordsDao;
import db.hibernate.tables.isearch.HotWords;

public class HotwordsDaoImpl implements HotwordsDao {

	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Override
	public int getWords(List<String> ret, Date time) {
		
		int count = 0;
		if (null == ret || null == time)
			return count;
		Iterator<HotWords> it = sessionFactory.getCurrentSession()
				.createQuery("from HotWords hw where hw.date = :date")
				.setParameter("date", time).list().iterator();
		while (it.hasNext()) {
			ret.add(it.next().getHotwords());
			++count;
		}
		return count;
	}

	@Override
	public void updateHotwords(List<String> words, Date time) {
		
		if(null==words||words.isEmpty()||null==time) return;
		
		Set<String> reqAdd=new HashSet<String>(words);
		rmOlder(reqAdd, time);
		addWord(reqAdd,time);
	}
	
	/**
	 * 把数据库中旧的实时热点词删除（不在reqAdd中的词）；
	 * 同时删除reqAdd集合中不必插入的词（已经存在），函数结束后，集合中的词是必须插入到数据库中的。
	 * @param reqAdd 本次需要添加的实时热点词
	 * @param time 日期
	 */
	private void rmOlder(Set<String> reqAdd, Date time) {

		if(null==reqAdd||reqAdd.isEmpty()||null==time) return;
		Session session = sessionFactory.getCurrentSession();
		Iterator<HotWords> itOrg = session
				.createQuery("from HotWords hw where hw.date = :date")
				.setParameter("date", time).list().iterator();
		while (itOrg.hasNext()) {
			HotWords next = itOrg.next();
			String hotword = next.getHotwords();
			if (!reqAdd.contains(next.getHotwords()))
				session.delete(next);
			reqAdd.remove(hotword);
		}
	}
	
	/**
	 * 把新的实时热点词插入数据库
	 * @param wordSet 新词
	 * @param time 日期
	 */
	private void addWord(Set<String> wordSet, Date time){
		
		if(null==wordSet||wordSet.isEmpty()||null==time) return;
		
		Session session=sessionFactory.getCurrentSession();
		for(Iterator<String> it=wordSet.iterator();it.hasNext();){
			String word=it.next();
			if(MyStringChecker.isBlank(word)) continue;
			HotWords newObj=new HotWords();
			newObj.setDate(time);
			newObj.setHotwords(word);
			session.save(newObj);
		}
	}

}
