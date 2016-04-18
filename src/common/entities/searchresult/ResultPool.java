package common.entities.searchresult;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 结果池，根据用户ID及查询词，将历史搜索结果暂时缓存
 * @author zhou
 *
 */
public class ResultPool {
	
	/*
	 * 注：
	 * 这个类对于登录用户没有问题，可以找到他的历史搜索结果，（如翻页时重用，计算搜索结果分布率时）
	 * 对于非登录用户，由于用户ID都是1，不同用户的搜索结果列表会被视为同一个用户的
	 * 为了保证搜索结果正确，在得到ResultListItem对象后，应该通过setSchedule函数设置指定的搜索引擎，避免搜索结果来源错乱。
	 * 特别是以后增加了调度功能，或者像安卓端一样允许用户指定搜索引擎。
	 * 但是这里始终有一个假设：对于非登录用户，只要搜索引擎相同，其结果、结果排序都一样。如果这条假设不正确，这个类要重新设计。
	 */
	
	/*
	 * 	结果池目前的设计思想是“缓存仓库”而不是“工厂”，
	 * 其中的ResultPoolItem对象不由结果池产生，同时如果池满了，总是把旧的丢弃
	 */
	
	/*
	 * 池的大小，被设置为2的幂次，用于处理索引值越界问题，
	 * 索引值在每一次增加之后，为了避免越界，应该用“条件判断+置0”或者模运算，
	 * 设置为2的幂次，在每一次增加后做一次按位与运算即可实现模运算
	 */
	private final static int MAX_POOL_SIZE = 1<<10;
	
	private ResultPoolItem[] m_arrPool;//缓存池
	private int m_nCurIndex;
	private ReadWriteLock m_objLock;
	
	private ResultPool(){
		
		m_arrPool=new ResultPoolItem[MAX_POOL_SIZE];
		m_objLock=new ReentrantReadWriteLock();
		m_nCurIndex=0;
	}
	
	private static ResultPool instance;
	
	private static ResultPool getInstance(){
		if(null==instance){
			synchronized (ResultPool.class) {
				if(null==instance) instance=new ResultPool();
			}
		}
		return instance;
	}
	
	private final void lockForRead(){
		m_objLock.readLock().lock();
	}
	private final void lockForWrite(){
		m_objLock.writeLock().lock();
	}
	private final void unlockForRead(){
		m_objLock.readLock().unlock();
	}
	private final void unlockForWrite(){
		m_objLock.readLock().lock();
		m_objLock.writeLock().unlock();
		m_objLock.readLock().unlock();
	}
	
	/**
	 * 搜索一个已存在的Item
	 * @param userid 用户ID
	 * @param query 查询词
	 * @return
	 */
	private ResultPoolItem searchForHistoryItem(int userid, String query){
		
		ResultPoolItem ret=null;
		
		lockForRead();
		for (int i = 0; i < m_arrPool.length; ++i) {
			ResultPoolItem tmp = m_arrPool[i];
			if (null == tmp || tmp.isInUsed() || tmp.getUserId() != userid
					|| !tmp.getQuery().equals(query))
				continue;
			ret = tmp;
			ret.use(userid,query);
			break;
		}
		unlockForRead();
		return ret;
		
	}
	
	/**
	 * 参数指定用户ID，与用户查询词，提供一个可用的结果列表项
	 * 
	 * @param userid
	 *            用户的ID
	 * @param query
	 *            用户查询词
	 * @return 返回userid对应的结果列表项
	 */
	public static ResultPoolItem getResultListItem(int userid, String query) {

		ResultPool ins=ResultPool.getInstance();
		ResultPoolItem ret= ins.searchForHistoryItem(userid, query);
		if(null!=ret) return ret;

		//池中没有该用户搜索当前查询词的历史记录（或者因为池的竞争被替换掉了）
		//为其新生成一个Item对象并返回
		ret=new ResultPoolItem();
		ret.use(userid, query);
		ins.addItem(ret);
		return ret;
	}

	private void addItem(ResultPoolItem item){
		
		if(null==item) return;
		
		lockForWrite();
		m_arrPool[m_nCurIndex]=item;
		m_nCurIndex=(1+m_nCurIndex)&(MAX_POOL_SIZE-1);
		unlockForWrite();
		
	}
	
	/**
	 * 释放一个缓存项，参数中的用户ID及查询词用来验证；
	 * 当item中的用户ID及查询词与参数不匹配时（可能性不大），将不会释放
	 * @param item 要释放的项
	 * @param userid 用户ID
	 * @param query 查询词
	 */
	public static void releaseItem(ResultPoolItem item, int userid, String query){
		
		if(null==item) return;
		item.unUse(userid, query);
	}
	
}// 类定义结束

