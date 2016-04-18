package common.entities.searchresult;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import server.info.entities.communication.RecommQueryAndPercent;

public class ResultPoolItem {

	private Map<String, Double> m_mapSchedule;
	private List<Result> m_lsSearchResultList;
	private int m_nUserId;
	private int m_nCurPage;
	private String m_strQuery;
	private List<Result> m_lsClickRecomm;
	private List<RecommQueryAndPercent> m_lsQueryRecomm;
	private List<String> m_lsRelateSearch;
	
	/*
	 * 这个类的数据会被多线程重用，这样一来对几乎所有的成员变量的访问都需要互斥；
	 * 这里设置这个标志位和读写锁，仅在获取这个类的对象时上锁解锁，
	 * 在获得当前对象时，通过读写锁控制对这个标志的访问，
	 * 当线程成员获得一个这类对象后，标志位被置为true
	 * 对于ResultPool类，检查到这个类的对象中标志为true时，就不再分配，保证一个对象同一时刻只被一个线程使用，
	 * 这使得线程对其他数据的操作是安全的，不必再上锁，例如写结果列表
	 */
	private boolean m_bUsedFlag;
	private ReadWriteLock m_objLock;
	
	public ResultPoolItem() {
		m_lsSearchResultList = new LinkedList<Result>();
		m_nUserId = -1;
		m_bUsedFlag = false;
		m_nCurPage=1;
		m_objLock=new ReentrantReadWriteLock();
	}

	/**
	 * 将一个结果列表项清空为初始状态
	 */
	public void resetResultListItem() {
		m_mapSchedule=null;
		m_lsSearchResultList.clear();
		m_nUserId = -1;
		m_nCurPage=1;
		m_strQuery=null;
		unUse(m_nUserId, m_strQuery);
	}

	public Map<String, Double> getSchedule() {
		return m_mapSchedule;
	}

	public void clearSchedule(){
		if(null!=m_lsSearchResultList) m_lsClickRecomm.clear();
		m_mapSchedule.clear();
	}
	
	/**
	 * 将调度结果设定为指定内容
	 * 如果当前缓存的结果已经有对应的调度结果（是第二次搜索），但是调度发生了变化，
	 * 搜索结果列表会被清空
	 * @param schedule 调度结果或用户指定的搜索引擎结果，传入null或空表不会导致任何修改，如果想清空缓存中的调度结果，应该调用clearSchedule函数
	 */
	public void setSchedule(Map<String, Double> schedule) {
		if(null==schedule||schedule.isEmpty()) return;
		if(scheduleChanged(schedule)){
			if(null!=m_lsSearchResultList) m_lsSearchResultList.clear();
			m_nCurPage=1;
		}
		m_mapSchedule = schedule;
	}

	public boolean hasScheduleResult(){
		return null!=m_mapSchedule&&!m_mapSchedule.isEmpty();
	}
	
	public String getQuery(){
		
		return m_strQuery;
	}
	
	public void setQuery(String query){
		m_strQuery=query;
	}
	
	private boolean scheduleChanged(Map<String, Double> newschedule){
		
		if(null==m_mapSchedule&&null==newschedule) return false;
		if(null!=m_mapSchedule&&null==newschedule||null==m_mapSchedule&&null!=newschedule) return true;
		Set<String> orgEnames=m_mapSchedule.keySet(), newEnames=newschedule.keySet();
		if(orgEnames.size()!=newEnames.size()) return true;
		Iterator<String> iterNewNames=newEnames.iterator();
		while(iterNewNames.hasNext()){
			String ename=iterNewNames.next();
			if(!orgEnames.contains(ename)) return true;
		}
		return false;
	}
	
	public boolean isInUsed(){
		
		boolean ret=true;
		m_objLock.readLock().lock();
		ret= m_bUsedFlag;
		m_objLock.readLock().unlock();
		return ret;
	}
	
	public void use(int userid, String query){
		
		m_objLock.writeLock().lock();
		m_bUsedFlag=true;
		m_nUserId=userid;
		m_strQuery=query;
		m_objLock.readLock().lock();
		m_objLock.writeLock().unlock();
		m_objLock.readLock().unlock();

	}
	
	public void unUse(int userid, String query){
		m_objLock.writeLock().lock();
		if(m_nUserId==userid&&!(null==query&&null!=m_strQuery)&&query.equals(m_strQuery)){
			m_bUsedFlag=false;
		}
		m_objLock.readLock().lock();
		m_objLock.writeLock().unlock();
		m_objLock.readLock().unlock();
	}
	
	public List<Result> getSearchResultList() {
		return m_lsSearchResultList;
	}

	public void setSearchResultList(List<Result> searchResultList) {
		m_lsSearchResultList = searchResultList;
	}

	public int getUserId() {
		return m_nUserId;
	}

	public void setUserId(int userId) {
		m_nUserId = userId;
	}

	public int getPageForMemberSearchEngine(){
		return m_nCurPage;
	}
	
	public void pageIncrease(){
		++m_nCurPage;
	}
	
	public int resultAmount(){
		return m_lsSearchResultList.size();
	}
	
	public void setClickRecommResult(List<Result> ls){
		m_lsClickRecomm=ls;
	}
	
	public List<Result> getClickRecommResult(){
		return m_lsClickRecomm;
	}
	
	public void setQueryRecommResult(List<RecommQueryAndPercent> ls){
		m_lsQueryRecomm=ls;
	}
	
	public List<RecommQueryAndPercent> getQueryRecommResult(){
		return m_lsQueryRecomm;
	}
	
	public void setRelateSearchResult(List<String> ls){
			m_lsRelateSearch=ls;
	}
	
	public List<String> getRelateSearchResult(){
		return m_lsRelateSearch;
	}
	
	public boolean hasClickRecommResult(){
		return null!=m_lsClickRecomm;
	}
	
	public boolean hasQueryRecommResult(){
		return null!=m_lsQueryRecomm;
	}
	
	public boolean hasRelateSearchResult(){
		return null!=m_lsRelateSearch;
	}
	
	public boolean hasSearchResult(){
		return null!=m_lsSearchResultList&&!m_lsSearchResultList.isEmpty();
	}
}
