package agent.data.inblackboard;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import server.info.entities.communication.RecommQueryAndPercent;
import common.entities.searchresult.Result;
import common.entities.searchresult.ResultPoolItem;
import common.entities.searchresult.SearchResultAmountChecker;
import common.functions.resultmerge.ResultCounter;
import db.dbhelpler.UserGroupHelper;

public class SearchData extends BlackboardBaseData{

	//以下的成员变量应该是由SearchAction来设置的（必须赋值）
	/*
	 * 本次搜索要达到的搜索结果数量
	 * 如果targetEngine被设置了，则表示相应的搜索引擎的结果必须达到这个数量
	 * 但返回的结果中，依然是所有搜索引擎合成之后的结果，只是其中targetEngine的结果数已经足够多了
	 */
	private int m_nTargetAmount;
	private String m_strQuery;//查询词
	private int m_nUserid;//用户ID
	
	/*
	 * 下面两个成员变量用于群组推荐
	 * 其中：
	 * 第一个是群组用户ID集合，
	 * 第二个是控制集合读（检查是否为null）、写（获取群组用户ID）的锁。
	 * 这个集合会被查询词群组推荐Agent及点击推荐Agent两个线程访问，
	 * 但应该只向数据库查询一次即可（对于每一次查询请求），
	 * 所以需要多线程互斥访问，用于getGroupUserID函数。
	 */
	private Set<Integer> m_setGroupUserId;
	private ReadWriteLock m_objLockForGroupSet;
	
	//以下两个变量应该是可以由SearchAction赋值的（可选赋值）
	private List<Result> m_lsResult;//最终搜索结果的存放位置
	private List<Result> m_lsResultRecomm;//结果推荐
	private List<RecommQueryAndPercent> m_lsQueryRecomm;//查询词推荐

	/*
	 *注：调度与搜索引擎筛选都可以指定若干个搜索引擎，但是作用不同：
	 *		调度目前没有使用，按以前的逻辑，调度是指只用这几个搜索引擎搜索，不用其他的了
	 *		而筛选不会影响具体用哪个搜索引擎来搜索，只是用来判断搜索结果数量是不是已经足够了
	 *筛选是在用户看到搜索结果之后的行为；而调度是在看到搜索结果之前产生的，例如安卓端的设置搜索引擎功能。
	 */
	private Map<String, Double> m_mapSelectedEngine;//用户指定的搜索引擎（或者调度结果）
	

	private Set<String> m_setFilterEngines;	//如果用户在页面上筛选了搜索引擎，则这个值应该设置为相应的搜索引擎名字；否则不设置即可

	//以下的变量相当于是搜索过程中的临时变量，不是由SearchAction赋值的
	private ResultPoolItem m_sdcPoolItem;//搜索结果池的一个项
	
	//搜索时使用的两个“缓冲区”
	//每轮搜索前被清空，是各个成员搜索引擎Agent把搜索结果回填的位置（未合并）
	private List<Result> m_lsBufferForSearch;
	//每轮搜索前被设置为mBufferForSearch，总是存放着待合并的结果（第一轮是空）
	private List<Result> m_lsBufferForMerge;
	
	//查询词推荐的两种方法使用的“缓冲区”
	private List<RecommQueryAndPercent> m_lsBufferForGroupRecomm;
	private List<RecommQueryAndPercent> m_lsBufferForQFGRecomm;
	
	private int m_nMemberSearchDoneCount;				//已完成任务的成员搜索引擎Agent数量
	private int m_nMemberSearchEngineCount;			//分派任务总共的成员搜索引擎Agent数量
	private boolean m_bResultMergeDone;					//结果合成Agent任务完成标志
	private boolean m_bGroupQueryRecommDone;	//查询词群组推荐Agent完成任务标志
	private boolean m_bQfgQueryRecommDone;		//查询词流图推荐Agent完成任务标志
	
	public SearchData(CountDownLatch doneSig,int userid, String query,
			int targetCount, List<Result> resultList) {

		super(doneSig);
		m_nUserid=userid;
		m_strQuery=query;
		m_nTargetAmount=targetCount;
		m_lsResult=resultList;
	}
	
	public void setFilterEngines(Set<String> enames){
		
		if(null==enames||enames.isEmpty()) return;
		if(null==m_setFilterEngines) m_setFilterEngines=new HashSet<String>();
		m_setFilterEngines.addAll(enames);
	}
	
	public void setResultList(List<Result> ls){
		m_lsResult=ls;
	}
	
	public void setClickRecommResultList(List<Result> ls){
		m_lsResultRecomm=ls;
	}
	
	public void setQueryRecommResultList(List<RecommQueryAndPercent> ls){
		m_lsQueryRecomm=ls;
	}
	
	public void setSchedule(Map<String, Double> m_mapSelectedEngine) {
		this.m_mapSelectedEngine = m_mapSelectedEngine;
	}
	
	public String getQuery(){
		return m_strQuery;
	}
	
	public List<Result> getSearchResultBuffer(){
		if(null==m_lsBufferForSearch) m_lsBufferForSearch=new LinkedList<Result>();
		return m_lsBufferForSearch;
	}
	
	public List<Result> getMergeResultBuffer(){
		if(null==m_lsBufferForMerge) m_lsBufferForMerge=new LinkedList<Result>();
		return m_lsBufferForMerge;
	}
	
	public List<RecommQueryAndPercent> getGroupRecommBuffer(){
		if(null==m_lsBufferForGroupRecomm){
			synchronized (this) {
				if(null==m_lsBufferForGroupRecomm) m_lsBufferForGroupRecomm=new LinkedList<RecommQueryAndPercent>();
			}
		}
		return m_lsBufferForGroupRecomm;
	}
	
	public List<RecommQueryAndPercent> getQFGRecommBuffer(){
		if(null==m_lsBufferForQFGRecomm){
			synchronized (this) {
				if(null==m_lsBufferForQFGRecomm) m_lsBufferForQFGRecomm=new LinkedList<RecommQueryAndPercent>();
			}
		}
		return m_lsBufferForQFGRecomm;
	}
	
	public int getUserid(){
		return m_nUserid;
	}
	
	public List<Result> getTargetListForMerge(){
		if(null==m_sdcPoolItem) return null;
		return m_sdcPoolItem.getSearchResultList();
	}
	
	public void memberSearchFinish(){
		++m_nMemberSearchDoneCount;
	}
	
	public void resultMergeFinish(){
		m_bResultMergeDone=true;
	}
	
	public void groupQueryRecommFinish(){
		m_bGroupQueryRecommDone=true;
	}
	
	public void qfgQueryRecommFinish(){
		m_bQfgQueryRecommDone=true;
	}
	
	public boolean memberSearchIsDone(){
		return m_nMemberSearchDoneCount>=m_nMemberSearchEngineCount;
	}
	
	public boolean resultMergeIsDone(){
		return m_bResultMergeDone;
	}
	
	public boolean queryGroupRecommIsDone(){
		return m_bGroupQueryRecommDone;
	}
	
	public boolean queryQfgRecommIsDone(){
		return m_bQfgQueryRecommDone;
	}
	
	/**
	 * 检查搜索结果数量是不是已经足够
	 * @return
	 */
	public boolean resultEnough(){
		
		SearchResultAmountChecker searchJudge = new SearchResultAmountChecker(
				m_sdcPoolItem.getSearchResultList(), m_setFilterEngines,
				m_nTargetAmount);
		return searchJudge.resultIsEnough();
	}
	
	public ResultPoolItem getResultListItem(){
		return m_sdcPoolItem;
	}
	
	/**
	 * SearchEntryAgent在一轮搜索结束后，发现还需要再启动一次搜索时，
	 * 调用这个函数交换搜索、结果合成缓冲区，
	 * 上一轮的搜索缓冲区中的结果变为待合成结果，
	 * 上一轮已经合成过的缓冲区用来放本轮的搜索结果，
	 * 调用这个函数后可能还要调用clearSearchBuffer清空搜索缓冲区
	 */
	public void swapBuffer(){
		List<Result> temp = m_lsBufferForMerge;
		m_lsBufferForMerge = m_lsBufferForSearch;
		m_lsBufferForSearch = temp;
	}
	
	/**
	 * SearchEntryAgent在一轮搜索结束后，发现还需要再启动一次搜索时，
	 * 调用这个函数清空搜索结果缓冲区（按目前的逻辑，里面的结果是上上轮的结果，已经被合成）
	 */
	public void clearSearchBuffer(){
		if(null!=m_lsBufferForSearch) m_lsBufferForSearch.clear();
	}
	
	public boolean hasSelectEngine(){
		return null!=m_mapSelectedEngine&&!m_mapSelectedEngine.isEmpty();
	}
	
	public Map<String, Double> getSchedule(){
		return m_mapSelectedEngine;
	}

	/**
	 * 把Agent系统本次搜索到的结果存放到最终结果列表中，进一步被SearchAction使用
	 */
	public void setResultForAction() {
		if (null != m_lsResult && null != m_sdcPoolItem)
			m_lsResult.addAll(m_sdcPoolItem.getSearchResultList());
	}
	
	/**
	 * 由SearchEntryAgent来设置
	 * @param item
	 */
	public void setResultListItem(ResultPoolItem item){
		
		if(null==item) return;
		m_sdcPoolItem=item;
	}
	
	/**
	 * 检查当前某一搜索引擎已经有多少结果（用于判断搜索是不是已经可以结束了）
	 * @param engineName 搜索引擎名字（英文）
	 * @return
	 */
	public int getResultAmount(String engineName){
		
		return ResultCounter.getResultCountOfEngine(m_sdcPoolItem.getSearchResultList(), m_lsBufferForMerge, engineName);
	}
	
	/**
	 * 设置成员搜索引擎数量，
	 * 由SearchEntryAgent在分发任务之后设置，用于判断成员搜索Agent任务是不是都完成了
	 * @param amount
	 */
	public void setMemberSearchEngineAmount(int amount){
		m_nMemberSearchEngineCount=amount;
	}
	
	public void resetSearchDoneStatus(){
		m_nMemberSearchDoneCount=0;
		m_bResultMergeDone=false;
	}
	
	public void resetQueryRecommDoneStatus(){
		m_bGroupQueryRecommDone=false;
		m_bQfgQueryRecommDone=false;
	}
	
	/**
	 * 获取控制群组用户ID集合对象的读写锁对象
	 * @return
	 */
	private ReadWriteLock getLockForGroupUserIDSet(){
	
		/*
		 * 本质上等价于在成员变量定义时写成：
		 * ReadWriteLock m_objLockForGroupSet=new ReentrantReadWriteLock();
		 * 这样写只是实现延迟加载
		 */
		if(null==m_objLockForGroupSet){
			synchronized (this) {
				if(null==m_objLockForGroupSet) m_objLockForGroupSet=new ReentrantReadWriteLock();
			}
		}
		return m_objLockForGroupSet;
	}
	
	/**
	 * 获取用户ID，如果有多个Agent都调用，第一个调用时就通过数据库取得群组用户ID
	 * 是多线程安全的
	 * @return
	 */
	public Set<Integer> getGroupUserID(){
		
		/*
		 * 用读写锁的作用等同于synchronized(this)，只是避免上锁粒度太大
		 */
		if (null==m_setGroupUserId) {
			ReadWriteLock lock=getLockForGroupUserIDSet();
			lock.writeLock().lock();
			if (null==m_setGroupUserId){
				Set<Integer> tmp=new HashSet<Integer>();
				UserGroupHelper.getGroupUserID(m_nUserid, tmp);
				m_setGroupUserId=new HashSet<Integer>(tmp);
			}
			lock.readLock().lock();
			lock.writeLock().unlock();
			lock.readLock().unlock();
		}
		return m_setGroupUserId;
	}
	
	public void addGroupUserID(Set<Integer> idSet){
		if(null!=idSet) getGroupUserID().addAll(idSet);
	}
	
	public void saveClickRecommResult(List<Result> ls){
		
		if(null==ls||ls.isEmpty()) return;
		if(null!=m_lsResultRecomm){
			synchronized (m_lsResultRecomm) {
				m_lsResultRecomm.addAll(ls);
			}
		}
		if(null!=m_sdcPoolItem) m_sdcPoolItem.setClickRecommResult(ls);
	}
	
	public void saveQueryRecommResult(List<RecommQueryAndPercent> ls){
		
		if(null==ls||ls.isEmpty()) return;
		if(null!=m_lsQueryRecomm){
			synchronized (m_lsQueryRecomm) {
				m_lsQueryRecomm.addAll(ls);
			}
		}
		if(null!=m_sdcPoolItem) m_sdcPoolItem.setQueryRecommResult(ls);
	}
	
	public boolean hasSearchResultCache(){
		return null!=m_sdcPoolItem;
	}
	
	public void saveGroupQueryRecommResult(List<RecommQueryAndPercent> ls){
		if(null==ls||ls.isEmpty()) return;
		List<RecommQueryAndPercent> buf=getGroupRecommBuffer();
		synchronized (buf) {
			buf.addAll(ls);
		}
	}
	
	public void saveQfgQueryRecommResult(List<RecommQueryAndPercent> ls){
		if(null==ls||ls.isEmpty()) return;
		List<RecommQueryAndPercent> buf=getQFGRecommBuffer();
		synchronized (buf) {
			buf.addAll(ls);
		}
	}
	
}
