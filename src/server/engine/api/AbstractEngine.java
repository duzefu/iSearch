package server.engine.api;

import java.util.List;

import server.commonutils.MyStringChecker;
import common.entities.searchresult.*;

/**
 * 搜索引擎抽象基类
 * @author zhou
 *
 */
public abstract class AbstractEngine {
	
	/**
	 * 成员搜索引擎搜索入口
	 * @param resultList 搜索结果存放列表，不能为null
	 * @param query 查询词，不能为空
	 * @param page 要查询第几页，不大于0时等同于1
	 * @param timeout 超时时间（毫秒），不大于0时等于于10秒
	 * @param lastCount 上一次查询之后搜索结果已经有多少条，为负时等同于0
	 * @return 本次所获得的结果的数量（是resultList中新增结果的数量）
	 */
	final public int getResults(List<Result> resultList,String query,int page,int timeout, int lastamount){
		
		if(null==resultList||MyStringChecker.isBlank(query)) return 0;
		if(page<=0) page=1;
		if(timeout<=0) timeout=5000000;
		if(lastamount<0) lastamount=0;
		return getMyResults(resultList, query, page, timeout, lastamount);
	}
	
	/**
	 * 各子类负责实现获得自己搜索结果的过程，参数可不必再做非法检查
	 * @param resultList 搜索结果位置
	 * @param query 查询词
	 * @param page 目标页号
	 * @param timeout 超时时间
	 * @param lastamount 已有结果数量，决定本次搜索时每一条结果的顺序号
	 * @return 本次所获得的结果的数量（是resultList中新增结果的数量）
	 */
	abstract protected int getMyResults(List<Result> resultList,String query,int page,int timeout, int lastamount);
}
