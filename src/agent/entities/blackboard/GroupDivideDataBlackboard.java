package agent.entities.blackboard;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import agent.data.inblackboard.GroupDivideData;
public class GroupDivideDataBlackboard {
	private static GroupDivideDataBlackboard instance;
	private int mIndex;
	private Map<Integer, GroupDivideData> mDataPool;
	private ReadWriteLock mLock;
	private final static int MAX_INDEX=(1<<31)-1;
	private GroupDivideDataBlackboard(){
		mLock=new ReentrantReadWriteLock();
		mIndex=1;
		mDataPool=new HashMap<Integer, GroupDivideData>();
	}
	
	private static GroupDivideDataBlackboard getInstance(){
		
		if(null==instance){
			synchronized (LoginDataBlackboard.class) {
				if(null==instance) instance=new GroupDivideDataBlackboard();
			}
		}
		
		return instance;
	}
	
	//一些基本函数
	private void mIndexIncrease(){
		mIndex=(1+mIndex)&MAX_INDEX;
	}
	
	//GroupDivadeDataBlackboard的对象函数部分（非static，特别是注意线程同步的问题）列在这下面
	private GroupDivideData get(int targetIndex){
		
		Lock rLock=null;
		GroupDivideData ret=null;
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
	
	private int addDataToPool(GroupDivideData data){
		
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
	 * 把数据从数据黑板中移除
	 * 这个函数目前没有添加读写锁机制，是出于以下考虑：
	 * 		1) 基本上索引值不可能同时被多个线程同时使用（整型数有21亿个，不容易重复）；也不会出现两个线程分别在移除与添加数据，而索引值一样的情况；
	 * 		2) 只在Action里面调用函数把数据移除，也不会有多线程同时试图移除同一个数据；也不会出现一个线程在移除时，其他线程正在读数据的情况。
	 * 	如果后续发现这种冲突出现了，就要添加读写锁。
	 * @param index
	 */
	private void removeDataFromPool(int index){
		
			mDataPool.remove(index);
	}
	
	
	//对外的类接口部分列在下面
	//（static函数，实际上总是先获取GourpDivideDataBlackboard的单例对象，再调用上面的对象函数）
	public static boolean isValidIndex(int index){
		return index>0;
	}
	
	public static GroupDivideData getData(int targetIndex){
		return getInstance().get(targetIndex);
	}
	
	public static int addData(GroupDivideData data){
		
		if(null==data) return -1;
		return getInstance().addDataToPool(data);
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
