package server.commonutils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadLocal {

	private Map values = new ConcurrentHashMap<Thread, Object>();
	private Map count = new ConcurrentHashMap<Thread, Integer>();

	public Integer getCount() {

		Integer ret = (Integer) this.count.get(Thread.currentThread());
		return ret;
	}

	/**
	 * 在需要关闭Session时调用，与get函数不同之处在于不会改变计数器。
	 * 
	 * @return
	 */
	public Object getForClose() {
		Thread currentThread = Thread.currentThread();
		Object result = null;
		result = values.get(currentThread);
		return result;
	}

	/**
	 * 为当前线程获取Session映射表中存放的Session，并使计数器加1；如果线程没有对应的Session，返回null
	 * 
	 * @return
	 */
	public Object get() {
		Thread currentThread = Thread.currentThread();
		Object result=null;
		synchronized (this) {
			result = values.get(currentThread);
			if (result != null) {
				count.put(currentThread, (Integer) count.get(currentThread) + 1);
			}
		}
		return result;
	}

	/**
	 * 在映射表中为当前线程加一个“线程-Session“的映射，并初始化计数器为1；如果当前线程已经有对应的映射关系，会使计数器加1
	 * 
	 * @param newValue
	 *            Session
	 */
	synchronized public void set(Object newValue) {
		values.put(Thread.currentThread(), newValue);
		count.put(Thread.currentThread(), 1);
	}

	public void releaseSession() {
		Thread curThread = Thread.currentThread();
		synchronized (this) {
			count.put(curThread, (Integer) count.get(curThread) - 1);
			if ((Integer) count.get(curThread) == 0) {
				values.remove(curThread);
				count.remove(curThread);
			}
		}
	}

	public Object initialValue() {
		return null;
	}
}
