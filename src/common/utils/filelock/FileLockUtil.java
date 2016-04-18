package common.utils.filelock;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 这个类实现了用一个Map来管理文件读写锁；
 * 为每一个路径（可以是文件，也可以是文件夹）管理一把锁，保证互斥
 * 不会检查相应的文件/文件夹是否真的存在，也不会试图去读写这个文件，具体的读写操作由回调函数完成
 * 回调函数（FileOpCallback类中的相关函数），在文件读写完成之前，回调函数不要返回，否则文件锁会被提前释放
 * 
 * 这个类还可以优化：
 * 1. 避免反复new锁对象及其数量限制
 * 2. 实际上，锁的目标可以不局限于文件，
 * 只需要把readFile和WriteFile中的File参数修改为其他类型如String，作用只是资源的标识符，就可以实现对任意资源的锁管理
 * 
 * 这个类涉及比较多的多线程操作，是否存在bug还需要验证，或者找到比较好的开源实现来替换
 * @author zhou
 *
 */
public class FileLockUtil {

	/**
	 * 读写操作标识
	 */
	private static final int READ_LOCK = 1;
	private static final int READ_UNLOCK = 2;
	private static final int WRITE_LOCK = 3;
	private static final int WRITE_UNLOCK = 4;

	//目的是FileLockUtil以单例模式实现
	private static FileLockUtil fileLockUtil;
	//资源标识符-锁的映射表
	private Map<String, FileLockAndThreadCount> path2lock;

	/**
	 * 实现FileLockUtil为单例
	 * 其中synchronized语句块里面的if(null==fileLockUtil)是必须的
	 * 否则会出现两个以上的线程可以同时进入if(null==fileLockUtil)之后，synchronized之前，从而FileLockUtil被生成了两次以上
	 * @return
	 */
	public static FileLockUtil getInstance() {

		if (null == fileLockUtil) {
			synchronized (FileLockUtil.class) {
				if(null==fileLockUtil) fileLockUtil = new FileLockUtil();
			}
		}
		return fileLockUtil;
	}

	private FileLockUtil() {
		// nothing
	}

	private Map<String, FileLockAndThreadCount> getPath2lock() {

		if (null == path2lock) {
			synchronized (this) {
				if(null==path2lock) path2lock = new ConcurrentHashMap<String, FileLockAndThreadCount>();
			}
		}

		return path2lock;
	}

	/**
	 * 实际获取锁的过程，包括生成/管理锁对象，上锁过程
	 * @param path 文件路径（资源标识符）
	 * @param type 上锁类型（READ_LOCK或者WRITE_LOCK)
	 * @return 获得的锁对象
	 */
	private FileLockAndThreadCount getLockProcess(String path, int type) {

		if (null == path || path.isEmpty())
			return null;
		FileLockAndThreadCount ret = null;
		Map<String, FileLockAndThreadCount> p2l = getPath2lock();

		while (null == ret) {
			ret = p2l.get(path);
			if (null == ret) {
				ReadWriteLock tmp = new ReentrantReadWriteLock();
				ret = new FileLockAndThreadCount(tmp);
			}
			p2l.putIfAbsent(path, ret);
			if (null != (ret = p2l.get(path))) {
				if (!ret.countIncrease()) {
					ret = null;
					continue;
				}
			}
		}
		if (null != ret)
			lockOrUnlock(ret, type, path);
		return ret;
	}
	
	/**
	 * 获取文件读锁
	 * @param file 文件
	 * @return 锁
	 */
	private FileLockAndThreadCount getReadLockForFile(File file) {

		if (null == file)
			return null;

		String filePath=file.getAbsolutePath();
		return getLockProcess(filePath, READ_LOCK);
	}

	/**
	 * 获取文件写锁
	 * @param file 文件
	 * @return 锁
	 */
	private FileLockAndThreadCount getWriteLockForFile(File file) {

		if (null == file)
			return null;

		String filePath = file.getAbsolutePath();
		return getLockProcess(filePath, WRITE_LOCK);
	}

	/**
	 * 加锁/解锁的实际过程，同时完成锁的引用计数器管理，路径-锁映射表的维护
	 * @param lc 锁和计数器
	 * @param type 操作类型（读/写+加/解 锁）
	 * @param filePath 文件路径
	 */
	private void lockOrUnlock(FileLockAndThreadCount lc, int type, String filePath) {

		if (null==filePath||filePath.isEmpty()||null == lc)
			return;

		ReadWriteLock lock=lc.getLock();
		switch (type) {
		case READ_LOCK:
			lock.readLock().lock();
			break;
		case READ_UNLOCK:
			lock.readLock().unlock();
			if(!lc.countDecrease()) getPath2lock().remove(filePath, lc);
			break;
		case WRITE_LOCK:
			lock.writeLock().lock();
			break;
		case WRITE_UNLOCK:
			lock.readLock().lock();
			lock.writeLock().unlock();
			lock.readLock().unlock();
			if(!lc.countDecrease()) getPath2lock().remove(filePath,lc);
			break;
		default:
			break;
		}

		return;
	}

	/**
	 * 解除文件锁
	 * @param file
	 * @param lc
	 * @param type
	 */
	private void unlockFileLock(File file, FileLockAndThreadCount lc, int type) {

		if (null == file)
			return;
		unlockFileLock(file.getAbsolutePath(), lc, type);
	}

	/**
	 * 解除文件锁
	 * @param filePath
	 * @param lc
	 * @param type
	 */
	private void unlockFileLock(String filePath, FileLockAndThreadCount lc, int type) {

		if (null == lc)
			return;
		Map<String, FileLockAndThreadCount> p2l = getPath2lock();
		lockOrUnlock(lc, type, filePath);
	}

	/**
	 * 读操作入口
	 * @param file 文件对象，实际需要的是它的路径，起标识符作用，不检查实际是否存在
	 * @param data 本函数的调用者以及相应的文件操作回调函数作用域不同，使用data来实现在回调函数中将数据回传
	 * @param fopCB 包括文件实际读取过程的回调函数，在读取完毕前，不要返回
	 * @return 与回调函数返回值相同
	 */
	public boolean readFile(File file, Object data, FileOperationCallback fopCB) {

		boolean ret = false;
		FileLockAndThreadCount lc = null;
		if (null == fopCB || null == file)
			return ret;
		try {
			lc = getReadLockForFile(file);
			if (null == lc)
				return ret;
			ret = fopCB.doOperation(file, data);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != lc&&null!=lc.getLock())
				unlockFileLock(file, lc, READ_UNLOCK);
		}

		return ret;
	}

	/**
	 * 写操作入口
	 * @param file 文件对象，实际需要的是它的路径，起标识符作用，不检查实际是否存在
	 * @param data 用于向回调函数传递数据
	 * @param fopCB 包括实际写操作的回调函数
	 * @return 回调函数的返回值
	 */
	public boolean writeFile(File file, Object data, FileOperationCallback fopCB) {

		boolean ret = false;
		FileLockAndThreadCount lc = null;
		if (null == fopCB || null == file)
			return ret;
		try {
			lc = getWriteLockForFile(file);
			if (null == lc)
				return ret;
			ret = fopCB.doOperation(file, data);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != lc&&null!=lc.getLock())
				unlockFileLock(file, lc, WRITE_UNLOCK);
		}

		return ret;
	}

}
