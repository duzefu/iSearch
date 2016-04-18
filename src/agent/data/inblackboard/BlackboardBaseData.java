package agent.data.inblackboard;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import server.commonutils.LogU;

public class BlackboardBaseData {

	protected CountDownLatch doneSignal;
	
	public BlackboardBaseData(){
	}
	
	public BlackboardBaseData(CountDownLatch doneSig){
		
		this.doneSignal=doneSig;
	}
	
	/**
	 * 释放SearchAction的线程锁
	 */
	public void done(){
		if(null!=doneSignal){
			doneSignal.countDown();
		}
	}
	
	/**
	 * 等待工作Agent完成任务
	 * @param timeout 超时时间，单位为秒，小于等于0时，将不限时
	 * @throws InterruptedException
	 */
	public void waitForDone(int timeout) throws InterruptedException{
		if(null!=doneSignal){
			if(timeout<=0) doneSignal.await();
			else doneSignal.await(timeout,TimeUnit.SECONDS);
		}
	}
	
	/**
	 * 等待工作Agent完成任务
	 */
	public void waitForDone() throws InterruptedException{
		waitForDone(-1);
	}
	
}
