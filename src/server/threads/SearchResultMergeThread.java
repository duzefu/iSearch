//package server.threads;
//
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//
//import common.entities.searchresult.Result;
//import common.functions.resultmerge.Merge;
//
//public class SearchResultMergeThread extends Thread {
//
//	private final CountDownLatch mDoneSignal;
//	private int userid;
//	private String query;
//	private List<Result> targetList;
//	private List<Result> newRes;
//	private boolean isLogin;
//
//	public SearchResultMergeThread(String query, int userid,
//			List<Result> targetList, List<Result> newRes, CountDownLatch doneSignal, boolean isLogin) {
//
//		this.newRes=newRes;
//		this.targetList = targetList;
//		this.userid = userid;
//		this.query = query;
//		this.mDoneSignal = doneSignal;
//		this.isLogin=isLogin;
//	}
//
//	@Override
//	public void run() {
//		this.doWork();
//		if(null!=mDoneSignal) mDoneSignal.countDown();
//	}
//
//	private void doWork() {
//
//		try {
//			if(null==targetList||null==newRes||newRes.isEmpty()||userid<=0||null==query||query.isEmpty()) return;
//			
//			Merge.resultMerge(targetList, newRes, userid, query, isLogin);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return;
//	}
//
//}
