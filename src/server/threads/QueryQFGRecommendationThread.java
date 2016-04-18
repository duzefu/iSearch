//package server.threads;
//
//import java.util.HashSet;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.CountDownLatch;
//
//import server.info.entities.communication.RecommQueryAndPercent;
//import common.functions.recommendation.qfg.QueryQFGRecommendation;
//import db.dao.UserGroupDao;
//import db.dbhelpler.UserGroupHelper;
//
//public class QueryQFGRecommendationThread extends Thread {
//
//	private final CountDownLatch mDoneSignal;
//	private int userid;
//	private String query;
//	private List<RecommQueryAndPercent> recommResult;
//	private final static int RETURN_LIST_COUNT = 20;
//
//	public QueryQFGRecommendationThread(int userid, String query,
//			List<RecommQueryAndPercent> recommResult, CountDownLatch doneSignal) {
//
//		this.recommResult = recommResult;
//		this.userid = userid;
//		this.query = query;
//		this.mDoneSignal = doneSignal;
//	}
//
//	private List<RecommQueryAndPercent> getRecommResult() {
//		return recommResult;
//	}
//
//	@Override
//	public void run() {
//		this.doWork();
//		mDoneSignal.countDown();
//	}
//
//	private void doWork() {
//		try {
//			if (null == query || query.isEmpty() || userid <= 0
//					|| null == this.recommResult)
//				return;
//
//			List<RecommQueryAndPercent> recQListQfg = new LinkedList<RecommQueryAndPercent>();
//			QueryQFGRecommendation.getQueryReommendation(recQListQfg,query);
//			if (null != recQListQfg)
//				this.getRecommResult().addAll(recQListQfg);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return;
//	}
//
//}
