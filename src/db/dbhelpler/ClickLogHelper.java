package db.dbhelpler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import server.commonutils.SpringBeanFactoryUtil;
import server.commonutils.MyStringChecker;
import server.info.config.SpringBeanNames;
import server.info.entites.transactionlevel.ClickRecordEntity;
import common.entities.searchresult.Result;
import common.functions.recommendation.click.QueryClickCountAndSim;
import db.dao.ClickLogDao;

public class ClickLogHelper {

	private ClickLogDao logdao;

	private static ClickLogHelper instance;

	private static ClickLogHelper getInstance() {
		if (null == instance) {
			synchronized (ClickLogHelper.class) {
				if (null == instance) {
					instance = new ClickLogHelper();
				}
			}
		}
		return instance;
	}

	private ClickLogHelper() {
		logdao = (ClickLogDao) SpringBeanFactoryUtil.getBean(SpringBeanNames.CLICK_LOG_DAO_BEAN_NAME);
	}

	private static ClickLogDao getLogDao(){
		return getInstance().logdao;
	}
	
	public static void getClickedResults(List<ClickRecordEntity> ret, Set<Integer> uidSet,String query){
		
		//参数检查
		if(null==ret||null==uidSet||uidSet.isEmpty()||MyStringChecker.isBlank(query)) return;
		
		//从数据库中取到所有用户的日志
		getLogDao().getLogOfUser(ret,uidSet,query);
		
	}
	
	public static void getClickedResults(List<ClickRecordEntity> ret, int userid,String query){
		
		//参数检查
		if(null==ret||!UserHelper.isLegalUserID(userid)||MyStringChecker.isBlank(query)) return;
		
		//从数据库中取到所有用户的日志
		getLogDao().getLogOfUser(ret,userid,query);
		
	}
	
	public static void getClickedResults(List<ClickRecordEntity> ret, Set<Integer> uidSet){
		
		//参数检查
		if(null==ret||null==uidSet||uidSet.isEmpty()) return;
		
		//从数据库中取到所有用户的日志
		getLogDao().getLogOfUser(ret,uidSet);
		
	}
	
	/**
	 * 查找数据库中用户日志，返回查询词-点击次数集合，重复出现的查询词的点击次数被累加
	 * @param ret 返回集合
	 * @param uidSet 用户ID集合
	 */
	public static void getLogWordCount(List<QueryClickCountAndSim> ret, Set<Integer> uidSet){
		
		if(null==ret||null==uidSet||uidSet.isEmpty()) return;
		
		List<ClickRecordEntity> logs=new LinkedList<ClickRecordEntity>();
		//TODO 有优化的空间与必要
		/*
		 * 这个函数目前会把群组用户所有的搜索日志返回，数量很大
		 * 而这里关心的是查询词-点击次数
		 * 这个过程应当放到clicklogdao中实现，减少数据处理量
		 * clicklogdao中构造大量的ClickLog对象并都要处理一遍是不可避免的
		 * （推荐是基于相似度的，相似度需要对每一个查询词都计算一次才知道大小）
		 * 因此这里的优化是避免构造ClickRecordEntity对象，直接返回需要的查询词-点击次数
		 */
		//由于目前的推荐方式，这个函数不能直接限定返回结果的数量
		getLogDao().getLogOrderbyQueryInc(logs, uidSet);
		if(logs.isEmpty()) return;
		
		Iterator<ClickRecordEntity> it=logs.iterator();
		QueryClickCountAndSim pair=new QueryClickCountAndSim();
		//第一个的处理方式不同
		while(it.hasNext()){
			ClickRecordEntity entity=it.next();
			if(null==entity) continue;
			pair.setQuery(entity.getQuery());
			pair.setCount(entity.getWeight());
			break;
		}
		
		//后续
		while(it.hasNext()){
			ClickRecordEntity entity=it.next();
			if(null==entity) continue;
			String query=entity.getQuery();
			if(pair.getQuery().equals(entity.getQuery())) pair.setCount(pair.getCount()+entity.getWeight());
			else{
				ret.add(pair);
				pair=new QueryClickCountAndSim(query);
				pair.setCount(entity.getWeight());
			}
		}
		
		ret.add(pair);//循环结束时，pair中的数据还没有添加到返回集合中
		
	}
}
