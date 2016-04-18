package common.functions.recommendation.qfg;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import server.commonutils.SpringBeanFactoryUtil;
import server.commonutils.MyStringChecker;
import server.info.config.SpringBeanNames;
import server.info.entities.communication.RecommQueryAndPercent;
import common.textprocess.similarity.EditFeatures;
import common.textprocess.similarity.RUCSimilarity;
import db.dao.QFGGenDao;
import db.dao.QueriesDao;

public class QueryQFGRecommendation {

	private QueryQFGRecommendation(){}
	private static QueryQFGRecommendation instance;
	private static QueryQFGRecommendation getInstance(){
		if(null==instance){
			synchronized (QueryQFGRecommendation.class) {
				if(null==instance) instance=new QueryQFGRecommendation();
			}
		}
		return instance;
	}
	
	private QueriesDao queriesDao;
	private QFGGenDao qfgDao;
	private static final int RESULT_QUERY_AMOUNT=15;//需要的查询词数量
	
	private QueriesDao getQueriesDao() {

		if (null == queriesDao)
			queriesDao = (QueriesDao) SpringBeanFactoryUtil
					.getBean(SpringBeanNames.QUERIES_DAO_BEAN_NAME);
		return queriesDao;
	}
	
	private QFGGenDao getQfgDao() {

		if (null == qfgDao)
			qfgDao = (QFGGenDao) SpringBeanFactoryUtil
					.getBean(SpringBeanNames.QFG_DAO_BEAN_NAME);
		return qfgDao;
	}
	
	/**
	 * 根据查询流图获取推荐结果，非多线程安全
	 * @param ret 结果存储位置，往其中存放结果的动作没有考虑多线程互斥，并且没有按相似度最终排序
	 * @param query 查询词
	 */
	public static void getQueryReommendation(List<RecommQueryAndPercent> ret, String query){
		
		if(null==ret||null==query||query.isEmpty()) return;
		
		//查询词预处理
		QueryQFGRecommendation ins=QueryQFGRecommendation.getInstance();
		Set<String> queryForSearch=new HashSet<String>();
		ins.getQueryForSearch(queryForSearch, query);
		
		//根据查询流图获取推荐词
		List<EditFeatures> sucWList=new LinkedList<EditFeatures>();
		ins.getRelateWordList(sucWList, queryForSearch);
		if(sucWList.isEmpty()) return;
		
		Collections.sort(sucWList, Collections.reverseOrder());
		Iterator<EditFeatures> iterFeature=sucWList.iterator();
		while(iterFeature.hasNext())
		{
			EditFeatures curf=iterFeature.next();
			String curw=null;
			try
			{
				curw=curf.getQuery2().getQueryContent();
			}
			catch(Exception e){
				curw=null;
			}
			
			if(MyStringChecker.isBlank(curw)) continue;
			
			RecommQueryAndPercent singleRec=new RecommQueryAndPercent();
			singleRec.setQuery(curw);
			double sim=RUCSimilarity.getSimilarity(query, curw);
			if(sim<0.000001) continue;
			singleRec.setPercent(sim);
			ret.add(singleRec);
			if(RESULT_QUERY_AMOUNT<=ret.size()) break;
		}
		
	}
	
	private void getQueryForSearch(Set<String> ret, String query){
		
		if(null==ret||MyStringChecker.isBlank(query)) return;
		ret.add(query);
		ret.add(query.trim());
	}
	
	private void getRelateWordList(List<EditFeatures> ret, Set<String> querySet)
	{
		if(null==ret||null==querySet||querySet.isEmpty()) return;
		
		QFGGenDao qfgdao=this.getQfgDao();
		qfgdao.getFeaturesByFirstWord(ret, querySet);
	}
	
}
