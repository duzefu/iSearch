package agent.utils;

import jade.core.AID;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AgentIDPool {

	private AID[] mAgents;
	private int mIndex;
	private int mCurrentSize;
	private int mMaxSize;
	private final static int DEFAULT_POOL_SIZE=5;
	private List<Integer> mPosOfDel;
	
	public AgentIDPool(){
		this(DEFAULT_POOL_SIZE);
	}
	
	public AgentIDPool(int size){
		if(size<=0) size=DEFAULT_POOL_SIZE;
		mAgents=new AID[size];
		mIndex=0;
		mCurrentSize=0;
		mMaxSize=size;
		mPosOfDel=new ArrayList<Integer>();
	}
	
	public AgentIDPool(Set<AID> aidSet) {
		this();
		update(aidSet);
	}
	
	public boolean isFull(){
		return mCurrentSize>=mMaxSize;
	}
	
	public boolean isEmpty(){
		return 0==mCurrentSize;
	}
	
//	public synchronized boolean addAgent(List<AID> aidList){
//		
//		if(null==aidList||isFull()) return false;
//		if(aidList.isEmpty()) return true;
//		
//		Iterator<AID> itAid=aidList.iterator();
//		while(itAid.hasNext()){
//			AID aid=itAid.next();
//			if(null==aid) continue;
//			mAgents[mCurrentSize++]=aid;
//			if(isFull()) break;
//		}
//		
//		return true;
//	}
	
//	public synchronized boolean addAgent(AID aid){
//		
//		if(null==aid||isFull()) return false;
//		mAgents[mCurrentSize++]=aid;
//		return true;
//	}
	
	public synchronized AID getNext(){
		
		AID ret=null;
		if(isEmpty()) return ret;
		
		if(mIndex>=mCurrentSize) mIndex=0;
		ret=mAgents[mIndex++];
		
		return ret;
	}
	
	public synchronized void update(Set<AID> aidSet){
		
		if(null==aidSet||aidSet.isEmpty()) return;
		Set<AID> setForInsert=new HashSet<AID>(aidSet);
		if(mCurrentSize!=aidSet.size()) updateWhileSizeChange(aidSet);
		else{
			mPosOfDel.clear();
			for(int i=0;i<mAgents.length;++i){
				AID aid=mAgents[i];
				if(null==aid) continue;
				if(!aidSet.contains(aid)) mPosOfDel.add(i);
				else setForInsert.remove(aid);
			}
			if(mPosOfDel.isEmpty()) return;
			Iterator<AID> it=setForInsert.iterator();
			Iterator<Integer> itIndex=mPosOfDel.iterator();
			while(it.hasNext()){
				AID aid=it.next();
				int pos=itIndex.next();
				mAgents[pos]=aid;
			}
		}
	}
	
	private synchronized void updateWhileSizeChange(Set<AID> aidSet){
		
		mAgents=new AID[aidSet.size()];
		int index=0;
		Iterator<AID> it=aidSet.iterator();
		while(it.hasNext()){
			AID aid=it.next();
			if(null==aid) continue;
			mAgents[index++]=aid;
		}
		mCurrentSize=index;
		mMaxSize=mAgents.length;
		mIndex=0;
	}
}
