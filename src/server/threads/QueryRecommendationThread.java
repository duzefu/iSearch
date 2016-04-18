//package server.threads;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.CountDownLatch;
//
//import server.info.entities.communication.RecommQueryAndPercent;
//
//public class QueryRecommendationThread extends Thread {
//
//	private final CountDownLatch mDoneSignal;
//	private CountDownLatch mChildDoneSignal;
//	private int userid;
//	private String query;
//	private List<RecommQueryAndPercent> recommResult;
//	private List<RecommQueryAndPercent> gRecommResult;
//	private List<RecommQueryAndPercent> qfgRecommResult;
//
//	private final static int RETURN_LIST_COUNT = 20;
//	private final static int CHILD_THREAD_COUNT = 2;
//
//	private CountDownLatch getmChildDoneSignal() {
//		if (null == mChildDoneSignal)
//			mChildDoneSignal = new CountDownLatch(CHILD_THREAD_COUNT);
//		return mChildDoneSignal;
//	}
//
//	public QueryRecommendationThread(int userid, String query,
//			List<RecommQueryAndPercent> recommResult, CountDownLatch doneSignal) {
//
//		this.recommResult = recommResult;
//		this.userid = userid;
//		this.query = query;
//		this.mDoneSignal = doneSignal;
//	}
//
//	private List<RecommQueryAndPercent> getgRecommResult() {
//
//		if (null == gRecommResult)
//			gRecommResult = new ArrayList<RecommQueryAndPercent>();
//		return gRecommResult;
//	}
//
//	private List<RecommQueryAndPercent> getQfgRecommResult() {
//		if (null == qfgRecommResult)
//			qfgRecommResult = new ArrayList<RecommQueryAndPercent>();
//		return qfgRecommResult;
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
//
//		try {
//			if (null == query || query.isEmpty() || userid <= 0
//					|| null == recommResult)
//				return;
//
//			QueryGroupRecommendationThread gRecommThr = new QueryGroupRecommendationThread(
//					userid, query, this.getgRecommResult(),
//					this.getmChildDoneSignal());
//			gRecommThr.start();
//			QueryQFGRecommendationThread qfgRecommThr = new QueryQFGRecommendationThread(
//					userid, query, this.getQfgRecommResult(),
//					this.getmChildDoneSignal());
//			qfgRecommThr.start();
//
//			getmChildDoneSignal().await();
//			mergeQueryRecommResult();
//			limitResultAmount();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	private void limitResultAmount(){
//		
//	}
//	
//	private void mergeQueryRecommResult() {
//
//		List<RecommQueryAndPercent> gresult = this.getgRecommResult(), qfgresult = this
//				.getQfgRecommResult();
//		int sizeGResult = null == gresult ? 0 : gresult.size(), sizeQFGResult = null == qfgresult ? 0
//				: qfgresult.size();
//
//		if (0 == sizeGResult && 0 == sizeQFGResult)
//			return;
//
//		if (null != gresult && !gresult.isEmpty())
//			Collections.sort(gresult, Collections.reverseOrder());
//		if (null != qfgresult && !qfgresult.isEmpty())
//			Collections.sort(qfgresult, Collections.reverseOrder());
//
//		this.resultMergeProcess(gresult, sizeGResult, qfgresult, sizeQFGResult);
//		return;
//	}
//
//	private void resultMergeProcess(List<RecommQueryAndPercent> rlist1,
//			int crl1, List<RecommQueryAndPercent> rlist2, int crl2) {
//
//		int resCount = crl1 + crl2;
//		resCount = resCount < RETURN_LIST_COUNT ? resCount : RETURN_LIST_COUNT;
//
//		if (0 == crl1) {
//			this.addAllResultDistinct(this.getRecommResult(), rlist2, resCount);
//		} else if (0 == crl2) {
//			this.addAllResultDistinct(this.getRecommResult(), rlist1, resCount);
//		} else {
//			Set<String> existWords = new HashSet<String>();
//			List<RecommQueryAndPercent> recommRes = this.getRecommResult();
//
//			Iterator<RecommQueryAndPercent> iterGroupRes = rlist1.iterator(), iterQfgRes = rlist2
//					.iterator(), iterRemain = null;
//			RecommQueryAndPercent resGroup = iterGroupRes.next(), resQFG = iterQfgRes
//					.next();
//			while (resCount > 0) {
//				if (resGroup.getPercent() > resQFG.getPercent()) {
//					if (!existWords.contains(resGroup.getQuery())) {
//						existWords.add(resGroup.getQuery());
//						recommRes.add(resGroup);
//						--resCount;
//					}
//					if (!iterGroupRes.hasNext()) {
//						iterRemain = iterQfgRes;
//						break;
//					}
//					resGroup = iterGroupRes.next();
//				} else {
//					if (!existWords.contains(resQFG.getQuery())) {
//						existWords.add(resQFG.getQuery());
//						recommRes.add(resQFG);
//						--resCount;
//					}
//					if (!iterQfgRes.hasNext()) {
//						iterRemain = iterGroupRes;
//						break;
//					}
//					resQFG = iterQfgRes.next();
//				}
//			}
//
//			while (resCount > 0 && iterRemain.hasNext()) {
//				RecommQueryAndPercent resRemain = iterRemain.next();
//				if (!existWords.contains(resRemain.getQuery())) {
//					existWords.add(resRemain.getQuery());
//					recommRes.add(resRemain);
//					--resCount;
//				}
//			}
//
//		}
//	}
//
//	private void addAllResultDistinct(List<RecommQueryAndPercent> dest,
//			List<RecommQueryAndPercent> src, int count) {
//
//		if (null == dest || null == src || src.isEmpty())
//			return;
//		Set<String> existWords = new HashSet<String>();
//
//		Iterator<RecommQueryAndPercent> iterDest = dest.iterator();
//		while (iterDest.hasNext())
//			existWords.add(iterDest.next().getQuery());
//
//		Iterator<RecommQueryAndPercent> iterSrc = src.iterator();
//		if (count <= 0 || count > src.size())
//			count = src.size();
//		while (iterSrc.hasNext()) {
//			RecommQueryAndPercent curRecomm = iterSrc.next();
//			String curQuery = curRecomm.getQuery();
//			if (!existWords.contains(curQuery)) {
//				existWords.add(curQuery);
//				dest.add(curRecomm);
//				if (--count == 0)
//					break;
//			}
//
//		}
//
//		return;
//	}
//}
