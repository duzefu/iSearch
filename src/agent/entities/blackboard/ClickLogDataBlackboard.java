package agent.entities.blackboard;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import agent.data.inblackboard.ClickLogData;

public class ClickLogDataBlackboard {
	private static ClickLogDataBlackboard instance;
	private final static int MAX_INDEX=(1<<31)-1;
	private int mIndex;
	private Map<Integer, ClickLogData> mDataPool;
	private ReadWriteLock mLock;

	public ClickLogDataBlackboard() {
		mLock = new ReentrantReadWriteLock();
		mIndex = 1;
		mDataPool = new HashMap<Integer, ClickLogData>();
	}

	// 获得黑板的单例
	private static ClickLogDataBlackboard getInstance() {

		if (null == instance) {
			synchronized (LoginDataBlackboard.class) {
				if (null == instance)
					instance = new ClickLogDataBlackboard();
			}
		}

		return instance;
	}

	// 一些基本函数
	private void mIndexIncrease() {
		mIndex=(1+mIndex)&MAX_INDEX;
	}

	// ClickLogDataBlackboard的对象函数部分（非static，特别是注意线程同步的问题）列在这下面
	private ClickLogData get(int targetIndex) {

		Lock rLock = null;
		ClickLogData ret = null;
		if (!isValidIndex(targetIndex))
			return ret;

		try {
			rLock = mLock.readLock();
			if (null != rLock) {
				rLock.lock();
				ret = mDataPool.get(targetIndex);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != rLock) {
				rLock.unlock();
			}
		}

		return ret;
	}

	private int addDataToPool(ClickLogData data) {

		int ret = -1;
		if (null == data)
			return ret;
		Lock wLock = null, rLock = null;

		try {
			wLock = mLock.writeLock();
			if (null != wLock)
				wLock.lock();
			mIndexIncrease();
			while (mDataPool.containsKey(mIndex)) {
				mIndexIncrease();
			}
			mDataPool.put(mIndex, data);
			ret = mIndex;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != wLock) {
				rLock = mLock.readLock();
				rLock.lock();
				wLock.unlock();
				rLock.unlock();
			}
		}

		return ret;
	}
	
	/**
	 * 把数据从数据黑板中移除
	 * @param index
	 */
	private void removeDataFromPool(int index){
		
			mDataPool.remove(index);
	}

	public static boolean isValidIndex(int index) {
		// TODO Auto-generated method stub
		return index > 0;
	}

	public static ClickLogData getData(int targetIndex) {
		return getInstance().get(targetIndex);
	}

	public static int addData(ClickLogData data) {

		if (null == data)
			return -1;
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
