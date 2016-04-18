package common.functions.resultanalysis;

import server.engine.api.EngineFactory.EngineName;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import common.entities.searchresult.ResultBase;

public class ResultAnalyser {

	/**
	 * 获取搜索结果的来源
	 * @param ret 搜索结果来源引擎（返回值）
	 * @param ls 搜索结果列表
	 */
	public static void getSourceEngine(Set<EngineName> ret, List<ResultBase> ls){
		
		if(null==ret||null==ls||ls.isEmpty()) return;
		
		EngineName allEng[] = EngineName.values();
		for(int i=0;i<allEng.length;++i){
			EngineName name=allEng[i];
			Iterator<ResultBase> it=ls.iterator();
			while(it.hasNext()){
				if(it.next().isFromTargetEngine(name)){
					ret.add(name);
					break;
				}
			}
		}
	}
}
