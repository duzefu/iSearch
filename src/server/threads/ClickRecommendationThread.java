//package server.threads;
//
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.CountDownLatch;
//
//import common.entities.searchresult.Result;
//import common.functions.recommendation.click.ClickRecommendation;
//
//public class ClickRecommendationThread extends Thread {
//
//	private final CountDownLatch mDoneSignal;
//	private int userid;
//	private String query;
//	private List<Result> resultList;
//	private final static int RETURN_LIST_COUNT = 3;
//
//	public ClickRecommendationThread(int userid, String query,
//			List<Result> resultList, CountDownLatch doneSignal) {
//
//		this.resultList = resultList;
//		this.userid = userid;
//		this.query = query;
//		this.mDoneSignal = doneSignal;
//	}
//
//	public List<Result> getResultList() {
//		return resultList;
//	}
//
//	public void setResultList(List<Result> resultList) {
//		this.resultList = resultList;
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
//		if (null == query || query.isEmpty() || userid <= 0
//				|| null == resultList)
//			return;
//
//			List<Result> clickRecom = new LinkedList<Result>();
//			ClickRecommendation.getClickRecommendation(clickRecom, query,
//					userid);
//			if (null != clickRecom && !clickRecom.isEmpty()) {
//				int count = clickRecom.size();
//				count = count < RETURN_LIST_COUNT ? count : RETURN_LIST_COUNT;
//				this.getResultList().addAll(clickRecom.subList(0, count));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return;
//	}
//
//}
