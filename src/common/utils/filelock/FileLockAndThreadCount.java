package common.utils.filelock;

import java.util.concurrent.locks.ReadWriteLock;
/**
 * 这个类用于锁的管理，绑定了锁+已获得锁的线程计数
 * @author zhou
 *
 */
public class FileLockAndThreadCount {

	//读写锁
	private ReadWriteLock lock;
	//已获得锁的线程数
	private int count;
	/*
	 * 标志当前这个对象是否有效，用于多线程修改count时的同步；
	 * 特别用于：
	 * 		线程1准备释放锁，线程2准备获得锁
	 * 		线程1首先释放锁，调用这个类的countDecrease()，count下降为1（没有其他线程得到了该锁），则还要将这个锁从FileLockUtil的Map中移除
	 * 		在移除操作之前，线程2有可能已经得到了这个锁的引用，
	 * 		但是上一步完成后，这个锁实际上已经无效了
	 * 		（不在Map中，假设有第三个线程也在申请锁，会发现Map中没有该文件对应的锁，然后重新生成一个，因此现在这个锁不能再使用）
	 */
	private boolean valid;
	
	public FileLockAndThreadCount(ReadWriteLock lock){
		this.lock=lock;
		this.count=0;
		this.valid=true;
	}
	
	public ReadWriteLock getLock(){
		return lock;
	}
	
	/**
	 * 使线程计数器上升1——要获得锁
	 * @return 如果这个对象实际上已经无效了（被其他线程释放了），返回false，否则返回true
	 */
	public boolean countIncrease(){
		
		synchronized (this) {
			if(valid)	++count;
			return valid;
		}
	}
	
	/**
	 * 使线程计数器下降1——要释放锁
	 * @return 释放锁之后该锁已经没有其他线程在占用则返回false，否则返回true
	 */
	public boolean countDecrease(){
		
		synchronized (this) {
			if(--count<=1) valid=false;
			return valid;
		}
	}
	
}
