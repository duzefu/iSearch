package common.functions.resultanalysis;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import server.engine.api.EngineFactory.EngineName;
import common.entities.searchresult.ResultBase;

public class CopyOfResultProportionAnalysis {
	
	/**
	 * 计算搜索引擎名称集合所有非空子集的第一步，先产生只包含一个名称的子集
	 * @param ret 返回内容
	 * @param eng 搜索引擎名称集合
	 */
	private static void getAllSubsetFirstRound(Set<Set<EngineName>> ret, Set<EngineName> eng){
		
		ret.clear();
		for(Iterator<EngineName> it=eng.iterator();it.hasNext();){
			Set<EngineName> tmpSet=new HashSet<EngineName>();
			tmpSet.add(it.next());
			ret.add(tmpSet);
		}
	}
	
	/**
	 * 计算搜索引擎名称集合的所有非空子集
	 * @param ret 返回内容
	 * @param eng 搜索引擎名称集合
	 */
	private static void getAllSubset(Set<Set<EngineName>> ret, Set<EngineName> eng){
		
		getAllSubsetFirstRound(ret, eng);
		Set<Set<EngineName>> tmpRet=new HashSet<Set<EngineName>>(ret);//里面放的总是上一轮的结果
		//后面每一轮都在上一轮的基础上添加一个搜索引擎
		int i=1,size=eng.size();
		do{
			for(Iterator<Set<EngineName>> it=tmpRet.iterator();it.hasNext();){
				Set<EngineName> lastRound=it.next();
				for(Iterator<EngineName> itEng=eng.iterator();itEng.hasNext();){
					EngineName name=itEng.next();
					lastRound.add(name);
					if(!ret.contains(lastRound)){
						Set<EngineName> newSet=new HashSet<EngineName>(lastRound);
						ret.add(newSet);
						tmpRet.add(newSet);
					}
					lastRound.remove(name);
				}
				tmpRet.remove(lastRound);
			}
		}while(i++<size);
	}
	
	/**
	 * 搜索结果重合率计算时，初始化返回的Hash表，
	 * 其中的键包括搜索结果来源搜索引擎（集合）的所有可能的非空子集，值都是0
	 * @param ret 函数返回的结果
	 * @param ls 搜索结果
	 */
	private static void initCoincidenceRateMap(Map<Set<EngineName>, Double> ret, List<ResultBase> ls){
		
		ret.clear();
		Set<EngineName> src=new HashSet<EngineName>();
		ResultAnalyser.getSourceEngine(src, ls);
		setSubsetOfEngineName(ret,src);
	}
	
	/**
	 * 计算搜索结果重合率时初始化返回的Hash表的实际工作函数
	 * @param ret 返回值
	 * @param src （搜索结果）来源搜索引擎集合
	 */
	private static void setSubsetOfEngineName(Map<Set<EngineName>, Double> ret, Set<EngineName> src){
		
		Set<Set<EngineName>> subSet=new HashSet<Set<EngineName>>();
		getAllSubset(subSet, src);
		for(Iterator<Set<EngineName>> it=subSet.iterator();it.hasNext();){
			ret.put(it.next(), 0.0);
		}
	}
	
	/**
	 * 根据搜索结果来源（src参数）搜索引擎，对ret中的内容进行增加。
	 * 例如，如果src={baidu,youdao}，则ret={...,"baidu-1","youdao-2","baidu youdao - 1",...}将：
	 * 		1) "baidu-1.0"会变成"baidu-2.0"；
	 * 		2) "youdao-2.0"会变成"youdao-3.0"；
	 * 		3) "baidu youdao - 1.0"会变成"baidu youdao - 2.0"
	 * 而其余的值不会变。
	 * @param ret
	 * @param src
	 */
	private static void addCount(Map<Set<EngineName>, Double> ret,Set<EngineName> src){
		
		Set<Set<EngineName>> subSet=new HashSet<Set<EngineName>>();
		getAllSubset(subSet, src);
		for(Iterator<Set<EngineName>> it=subSet.iterator();it.hasNext();){
			Set<EngineName> engSet=it.next();
			ret.put(engSet, ret.get(engSet)+1);
		}
	}
	
	public static void getResultCoincidenceRate(Map<Set<EngineName>, Double> ret, List<ResultBase> ls){
		
		if(null==ret||null==ls||ls.isEmpty()) return;
		
		initCoincidenceRateMap(ret,ls);
		for(Iterator<ResultBase> it=ls.iterator();it.hasNext();){
			ResultBase res=it.next();
			if(null==res) continue;
			Set<EngineName> src=res.getSourceEngines();
			addCount(ret,src);
		}
		double amount=ls.size();
		for(Iterator<Entry<Set<EngineName>, Double>> it=ret.entrySet().iterator();it.hasNext();){
			Entry<Set<EngineName>, Double> entry=it.next();
			entry.setValue(entry.getValue()/amount);
		}
	}
	
	/**
	 * 获取搜索结果分布率
	 * @param ret 返回结果，内容为：搜索引擎名-百分比值，搜索引擎名称为中文
	 * @param ls 搜索结果列表
	 */
	public static void getResultDistributionRate(Map<EngineName,Double> ret, List<ResultBase> ls){
		
		if(null==ret||null==ls||ls.isEmpty()) return;
		
		int sum=0;
		EngineName allNames[]=EngineName.values();
		ret.clear();
		for(int i=0;i<allNames.length;++i){
			ret.put(allNames[i], 0.0);
		}
		
		Iterator<ResultBase> it=ls.iterator();
		while(it.hasNext()){
			ResultBase res=it.next();
			if(null==res) continue;
			Iterator<EngineName> itsrc=res.getSourceEngines().iterator();
			while(itsrc.hasNext()){
				EngineName engName=itsrc.next();
				ret.put(engName, 1.0+ret.get(engName));
				++sum;
			}
		}

		Iterator<Entry<EngineName, Double>> itMap=ret.entrySet().iterator();
		while(itMap.hasNext()){
			Entry<EngineName, Double> ent=itMap.next();
			ent.setValue(ent.getValue()/sum);
		}

	}
	
	
}
