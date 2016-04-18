package common.functions.resultmerge;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import server.commonutils.MyStringChecker;

import common.entities.searchresult.Result;

public class ResultCounter {

	/**
	 * 统计搜索结果中来自特定搜索引擎的结果的数量
	 * @param resList 总的搜索结果列表
	 * @param listForMerge 用于合成的列表（还没有合并到总列表的）
	 * @param enameSet 指定的搜索引擎名称（必须是英文名称）
	 * @return 两个列表中来源于enameSet中的成员搜索引擎的结果的数量
	 */
	public static int getResultCountOfEngine(List<Result> resList,
			List<Result> listForMerge, Set<String> enameSet) {

		int ret = 0;

		ret += getResultAmount(resList, enameSet);
		ret += getResultAmount(listForMerge, enameSet);
		return ret;
	}
	
	public static int getResultCountOfEngine(List<Result> resList,
			List<Result> listForMerge, String ename) {

		int ret = 0;

		ret += getResultAmount(resList, ename);
		ret += getResultAmount(listForMerge, ename);
		return ret;
	}
	
	private static int getResultAmount(List<Result> rlist, String ename){
		
		int ret=0;
		if(null==rlist||rlist.isEmpty()||MyStringChecker.isBlank(ename)) return ret;
		
		Iterator<Result> itRes=rlist.iterator();
		while(itRes.hasNext()){
			Result r=itRes.next();
			if(null==r) continue;
			Set<String> srcNames=r.getSourceEngineEnName();
			if(null==srcNames) continue;
			if(srcNames.contains(ename)) ++ret;
		}
		
		return ret;
	}
	
	private static int getResultAmount(List<Result> rlist, Set<String> enameSet){
		
		int ret=0;
		if(null==rlist||rlist.isEmpty()) return ret;
		
		Iterator<Result> it=rlist.iterator();
		while(it.hasNext()){
			Result r=it.next();
			if(null==r) continue;
			Set<String> srcNames=r.getSourceEngineEnName();
			if(null==srcNames) continue;
			Iterator<String> itTarName=enameSet.iterator();
			while(itTarName.hasNext()){
				String name=itTarName.next();
				if(srcNames.contains(name)){
					++ret;
					break;
				}
			}
		}
		return ret;
	}
	
}
