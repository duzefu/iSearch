package agent.entities.blackboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import agent.data.inblackboard.SearchData;

import common.entities.searchresult.Result;

import db.dbhelpler.UserGroupHelper;
import server.commonutils.LogU;

/**
 * 搜索功能需要的数据黑板
 * @author zhou
 *
 */
public class SearchDataBlackboard {

	private final static int MAX_INDEX=(1<<31)-1;
	private static SearchDataBlackboard instance;
	
	private int mIndex;
	private Map<Integer, SearchData> mDataPool;
	private ReadWriteLock mLock;
	
	private SearchDataBlackboard(){
		mLock=new ReentrantReadWriteLock();
		mIndex=0;
		mDataPool=new HashMap<Integer, SearchData>();
	}
	
	private static SearchDataBlackboard getInstance(){
		
		if(null==instance){
			synchronized (SearchDataBlackboard.class) {
				if(null==instance) instance=new SearchDataBlackboard();
			}
		}
		
		return instance;
	}
	
	//一些基本函数
	private void mIndexIncrease(){
		mIndex=(1+mIndex)&MAX_INDEX;
	}
	
	//LoginDataBlackboard的对象函数部分列在这下面（非static，特别要注意线程同步的问题）
	private SearchData get(int targetIndex){
		
		Lock rLock=null;
		SearchData ret=null;
		if(!isValidIndex(targetIndex)) return ret;
		
		try{
			rLock=mLock.readLock();
			if(null!=rLock){
				rLock.lock();
				ret=mDataPool.get(targetIndex);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(null!=rLock){
				rLock.unlock();
			}
		}
		
		return ret;
	}
	
	private int addDataToPool(SearchData data){
		
		int ret=-1;
		if(null==data) return ret;
		Lock wLock=null, rLock=null;
		
		try{
			wLock=mLock.writeLock();
			if(null!=wLock) wLock.lock();
			mIndexIncrease();
			while(mDataPool.containsKey(mIndex)){
				mIndexIncrease();
			}
			mDataPool.put(mIndex, data);
			ret=mIndex;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(null!=wLock){
				rLock=mLock.readLock();
				rLock.lock();
				wLock.unlock();
				rLock.unlock();
			}
		}
		
		return ret;
	}
	
	/**
	 * 被成员搜索引擎Agent回调，将其搜索结果存到缓冲区中
	 * @param results
	 * @param index
	 */
	private void addResultToPool(List<Result> results, int index){
		
		SearchData data=mDataPool.get(index);
		List<Result> rForSearch = null;
		if (data!=null) {
			rForSearch=data.getSearchResultBuffer();
		}
		if(null==rForSearch) return;
		synchronized (rForSearch) {
			rForSearch.addAll(results);
		}
	}
	
	/**
	 * 把数据从数据黑板中移除
	 * @param index
	 */
	private void removeDataFromPool(int index){
		
			mDataPool.remove(index);
	}
	
	//对外的类接口部分列在下面
	//（static函数，实际上总是先获取LoginDataBlackboard的单例对象，再调用上面的对象函数）
	public static boolean isValidIndex(int index){
		return index>=0;
	}
	
	/**
	 * 获取黑板中相应位置的数据
	 * @param targetIndex 索引
	 * @return 数据
	 */
	public static SearchData getData(int targetIndex){
		return getInstance().get(targetIndex);
	}
	
	/**
	 * 将数据存放到黑板中
	 * @param data 数据
	 * @return 数据的索引
	 */
	public static int addData(SearchData data){
		
		if(null==data) return -1;
		return getInstance().addDataToPool(data);
	}
	
	/**
	 * 成员搜索引擎Agent将结果存放到集中的缓冲区中
	 * @param results 成员搜索引擎的搜索结果
	 * @param index 成员搜索引擎在搜索前得知的SearchData在黑板中的索引
	 */
	public static void addMemberSearchResult(List<Result> results, int index){
		
		if(null==results||results.isEmpty()||!isValidIndex(index)) return;
		getInstance().addResultToPool(results, index);
	}
	
	/**
	 * 将数据从黑板中移除
	 * @param index 数据的索引
	 */
	public static void removeData(int index){
		
		if(!isValidIndex(index)) return;
		getInstance().removeDataFromPool(index);
	}
	
}
