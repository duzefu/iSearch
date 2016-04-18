package common.functions.recommendation.group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.TreeMap;

import server.commonutils.MyStringChecker;
import server.info.entites.transactionlevel.ClickRecordEntity;
import server.info.entities.communication.RecommQueryAndPercent;
import common.functions.recommendation.click.QueryClickCountAndSim;
import common.textprocess.similarity.RUCSimilarity;
import db.dbhelpler.ClickLogHelper;

public class QueryGroupRecommendation {

	private QueryGroupRecommendation(){}
	private static QueryGroupRecommendation instance;
	private static QueryGroupRecommendation getInstance(){
		if(null==instance){
			synchronized (QueryGroupRecommendation.class) {
				if(null==instance) instance=new QueryGroupRecommendation();
			}
		}
		return instance;
	}
	
	private static final int RESULT_QUERY_AMOUNT=15;//需要的查询词数量
	private static final double SIMILARITY_THRESHOLD=0.5;//相似度门限，与查询词相似度不超过此门限的词语不会被在第一轮选择

	/**
	 * 获取查询词推荐内容
	 * @param ret 推荐结果，有可能未按相似度排序
	 * @param guidSet 群组用户ID
	 * @param query 查询词
	 */
	public static void getQueryReommendation(List<RecommQueryAndPercent> ret,
			Set<Integer> guidSet, String query) {
		
		//参数检查
		if(null==ret||null==guidSet||guidSet.isEmpty()||MyStringChecker.isBlank(query)) return;
		
		QueryGroupRecommendation ins=getInstance();
		//从数据库中获得群组用户的日志记录并计算相似度
		List<QueryClickCountAndSim> words=new LinkedList<QueryClickCountAndSim>();
		ins.getSearchLogsFromDatabase(words,guidSet,query);
		
		//生成推荐结果
		Set<String> existWord=new HashSet<String>();
		ins.getRecommWordBySimilariry(ret,words,existWord);
		if(ret.size()<RESULT_QUERY_AMOUNT) ins.getRecommWordByCount(ret, words,existWord);
	}

	/**
	 * 从数据库查找群组用户的搜索记录，并计算搜索记录中查询词与当前查询词的相似度
	 * @param ret 返回结果，查询词不重复出现，重复出现的查询词的点击次数累加
	 * @param guid 群组用户ID 
	 * @param query 当前查询词
	 */
	private void getSearchLogsFromDatabase(List<QueryClickCountAndSim> ret, Set<Integer> guid, String query) {

		if (null==ret) return;
		
		ClickLogHelper.getLogWordCount(ret, guid);
		if(ret.isEmpty()) return;
		
		Iterator<QueryClickCountAndSim> it=ret.iterator();
		while(it.hasNext()){
			QueryClickCountAndSim ele=it.next();
			if(null==ele) continue;
			double similarity=RUCSimilarity.getSimilarity(query, ele.getQuery());
			ele.setSimilarity(similarity);
		}
	}
	

	/**
	 * 处理群组用户日志，根据查询词与日志中的查询词相似度从高到低选择推荐结果；
	 * 对于不超过门限的词不会选择。
	 * @param ret 结果
	 * @param dataList 群组用户的查询词数据
	 * @param existWord 已经确定的推荐词，避免重复
	 */
	private void getRecommWordBySimilariry(List<RecommQueryAndPercent> ret,
			List<QueryClickCountAndSim> dataList,Set<String> existWord) {

		if (null == ret || null == dataList || dataList.isEmpty()||null==existWord)
			return;

		sortRecommQueryByWeight(dataList);
		Iterator<QueryClickCountAndSim> it = dataList.iterator();
		while (it.hasNext()) {
			QueryClickCountAndSim data = it.next();
			String query = data.getQuery();
			if (existWord.contains(query)) continue;
			double similarity = data.getSimilarity();
			if(similarity < SIMILARITY_THRESHOLD) break;
			
			RecommQueryAndPercent tmpRes = new RecommQueryAndPercent();
			tmpRes.setQuery(query);
			tmpRes.setPercent(similarity);
			ret.add(tmpRes);
			existWord.add(query);
			if (ret.size() >= RESULT_QUERY_AMOUNT)
				break;
		}
	}
	
	/**
	 * 处理用户查询日志，根据各词语出现的次数选择推荐结果
	 * @param ret 结果
	 * @param dataList 群组用户的查询词数据
	 * @param existWord 已经确定的推荐词，避免重复
	 */
	private void getRecommWordByCount(List<RecommQueryAndPercent> ret,
			List<QueryClickCountAndSim> dataList, Set<String> existWord) {

		if (null == ret || null == dataList || dataList.isEmpty()||null==existWord)
			return;

		sortRecommQueryByCount(dataList);
		Iterator<QueryClickCountAndSim> it = dataList.iterator();
		while (it.hasNext()) {
			QueryClickCountAndSim data = it.next();
			String query = data.getQuery();
			if (existWord.contains(query))
				continue;

			RecommQueryAndPercent tmpRes = new RecommQueryAndPercent();
			double similarity = data.getSimilarity();
			tmpRes.setQuery(query);
			tmpRes.setPercent(similarity);// 按点击次数来选词，但点击次数后面就不需要了，但相似度还是必要的，用来做最终排序
			ret.add(tmpRes);
			existWord.add(query);
			if (ret.size() >= RESULT_QUERY_AMOUNT)
				break;
		}
	}
	
	/**
	 * 对去重并加权后的用户日志数据列表根据相似度从高到低排序
	 * @param list 待排序的列表
	 */
	private void sortRecommQueryByWeight(List<QueryClickCountAndSim> list) {

		if (list == null || list.isEmpty())
			return;
		
		Collections.sort(list, new Comparator<QueryClickCountAndSim>() {

			@Override
			public int compare(QueryClickCountAndSim o1, QueryClickCountAndSim o2) {
				
				double w1=o1.getSimilarity(), w2=o2.getSimilarity();
				if(w1<w2) return 1;
				else if(w1>w2) return -1;
				return 0;
			}
		});
	}

	/**
	 * 对去重并加权后的用户日志数据列表根据点击次数从高到低排序
	 * @param list 待排序的列表
	 */
	private void sortRecommQueryByCount(List<QueryClickCountAndSim> list) {

		if (list == null || list.isEmpty())
			return;
		
		Collections.sort(list, new Comparator<QueryClickCountAndSim>() {

			@Override
			public int compare(QueryClickCountAndSim o1, QueryClickCountAndSim o2) {
				
				double c1=o1.getCount(), c2=o2.getCount();
				if(c1<c2) return 1;
				else if(c1>c2) return -1;
				return 0;
			}
		});
	}

}
