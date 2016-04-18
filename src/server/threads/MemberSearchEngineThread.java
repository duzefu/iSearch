//package server.threads;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//
//import server.engine.api.AbstractEngine;
//import server.engine.api.EngineFactory;
//import server.info.config.ConstantValue;
//import common.entities.searchresult.Result;
//
//public class MemberSearchEngineThread extends Thread {
//
//	private final CountDownLatch mDoneSignal;
//	private int page;
//	private String query;
//	private String engName;
//	private List<Result> resultList;
//	private int lastCount;
//	
//	public MemberSearchEngineThread(String query, int page, int lastCount, String engName,
//			List<Result> resultList, CountDownLatch doneSignal) {
//
//		this.engName = engName;
//		this.resultList = resultList;
//		this.page = page;
//		this.query = query;
//		this.mDoneSignal = doneSignal;
//		this.lastCount=lastCount;
//	}
//
//	@Override
//	public void run() {
//		this.doWork();
//		mDoneSignal.countDown();
//	}
//
//	private void doWork() {
//
//		try {
//			if (null == query || query.isEmpty() || null == resultList)
//				return;
//			
//			if(lastCount<0) lastCount=0;
//			AbstractEngine e = EngineFactory.engineFactory(engName);
//			if (null != e) {
//				List<Result> tempRlist = new ArrayList<Result>();
//				e.getResults(tempRlist, query, page, 5000,lastCount);
//				if (null != tempRlist) {
//					synchronized (resultList) {
//						resultList.addAll(tempRlist);
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return;
//	}
//
//}
