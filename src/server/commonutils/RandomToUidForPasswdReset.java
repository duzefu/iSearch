package server.commonutils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RandomToUidForPasswdReset {

	private Map<String, Integer> r2uid;
	private ReadWriteLock lock;
	
	private RandomToUidForPasswdReset(){
		if(null==r2uid) r2uid=new HashMap<String,Integer>();
		if(null==lock) lock=new ReentrantReadWriteLock();
	}
	
	private static  RandomToUidForPasswdReset instance;
	
	private static RandomToUidForPasswdReset instance(){
		
		if(null==instance){
			synchronized (RandomToUidForPasswdReset.class) {
				instance=new RandomToUidForPasswdReset();
			}
		}
		return instance;
	}
	
	private void writeLock(){
		lock.writeLock().lock();
	}
	
	private void readLock(){
		lock.readLock().lock();
	}
	
	private void writeUnlock(){
		readLock();
		lock.writeLock().unlock();
		readUnlock();
	}
	
	private void readUnlock(){
		lock.readLock().unlock();
	}
	
	private boolean saveDataIns(String random, Integer uid){
		
		if(null==random||null==uid) return false;
		writeLock();
		r2uid.put(random, uid);
		writeUnlock();
		return true;
	}
	
	private void rmDataIns(String random){
		if(null==random) return;
		writeLock();
		r2uid.remove(random);
		writeUnlock();
		return;
	}
	
	private Integer getDataIns(String key){
		
		Integer ret=null;
		if(null==key||key.isEmpty()) return ret;
		readLock();
		ret=r2uid.get(key);
		readUnlock();
		return ret;
	}
	
	public static boolean saveData(String random, Integer uid){
		return instance().saveDataIns(random, uid);
	}
	
	public static void rmData(String random){
		instance().rmDataIns(random);
	}
	
	public static Integer getData(String random){
		return instance().getDataIns(random);
	}
	
}
