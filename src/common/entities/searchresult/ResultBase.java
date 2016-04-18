package common.entities.searchresult;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import server.engine.api.EngineFactory.EngineName;

public class ResultBase {

	/**
	 * 标识搜索结果来源，
	 * 键值对表示：
	 * 搜索引擎名-该结果在相应搜索结果中的位置
	 */
	protected HashMap<EngineName, Integer> engToPos;
	
	protected ResultBase(){
		engToPos=new HashMap<EngineName, Integer>();
	}
	
	protected ResultBase(EngineName eng, int pos){
		this();
		if(pos>0) engToPos.put(eng, pos);
	}
	
	/**
	 * 添加来源
	 * @param eng 搜索引擎名称
	 * @param pos 位置
	 */
	public void addSrc(EngineName eng, int pos){
		engToPos.put(eng, pos);
	}
	
	/**
	 * 检查当前的结果是不是来自特定的搜索引擎（集合）
	 * @param engNames 搜索引擎名称
	 * @return
	 */
	public boolean isFromTargetEngine(Set<EngineName> engNames){
		
		boolean ret=false;
		if(null==engNames||engNames.isEmpty()) return ret;
		
		Iterator<EngineName> it=engNames.iterator();
		while(it.hasNext()){
			ret=engToPos.containsKey(it.next());
			if(ret) break;
		}
		
		return ret;
		
	}
	
	/**
	 * 检查当前的结果是不是来自特定的搜索引擎
	 * @param engName 搜索引擎名称
	 * @return
	 */
	public boolean isFromTargetEngine(EngineName engName){
		
		return engToPos.containsKey(engName);
		
	}
	
	public Set<EngineName> getSourceEngines(){
		return engToPos.keySet();
	}
	
}
