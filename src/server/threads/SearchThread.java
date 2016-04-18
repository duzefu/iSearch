//package server.threads;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//
//import server.info.config.ConstantValue;
//import common.entities.searchresult.Result;
//import common.entities.searchresult.ResultPool;
//import common.entities.searchresult.ResultPoolItem;
//import common.functions.resultmerge.Merge;
//import common.functions.resultmerge.ResultCounter;
//import common.utils.querypreprocess.QueryParticiple;
//
//public class SearchThread extends Thread {
//
//	private final CountDownLatch mDoneSignal; // 上层函数（SearchAction）设置的线程信号量，在本线程工作完成之后，使其减1即可
//	private int userid; // 用户ID
//	/*
//	 * 本次搜索要达到的搜索结果数量 如果targetEngine被设置了，则表示相应的搜索引擎的结果必须达到这个数量
//	 * 但返回的结果中，依然是所有搜索引擎合成之后的结果，只是其中targetEngine的结果数已经足够多了
//	 */
//	private int totalAmount;
//	private String query; // 查询词
//	private List<Result> resultList;// 最终搜索结果的存放位置，如果线程构造之后不是null，本线程不再重新生成，此时便于上层函数取到搜索结果数据。本线程在往其中插入数据时考虑了多线程锁的问题
//
//	private Set<String> mTargetEngine; // 如果用户在页面上筛选了搜索引擎，然后翻页触发了查询，则这个值应该设置为相应的搜索引擎名字；否则不设置即可
//	private Map<String, Double> schedule;// 调度结果，如果这个值不是空的，表示用户指定了成员搜索引擎，不会再调度；否则本线程将自己调度
//
//	private boolean isLogin;// 用户是否已经登录
//	private ResultPoolItem poolItem;// 搜索结果池的一个项
//	// 搜索失败导致的已重试次数
//	// 由于搜索过程与结果合成过程采用双缓冲列表，导致第一次搜索时一定会被判断为查询失败
//	// （因为第一轮搜索之后，搜索结果的数量不会改变，还没有合成）
//	private int repeatedTimes;
//
//	/**
//	 * 搜索线程构造函数，其中的参数是必须提供的，
//	 * 还可以通过setSchedule与setTargetEngine进一步设置搜索引擎调度内容及结果筛选的目标
//	 * 
//	 * @param userid
//	 *            当前用户在数据库中的ID
//	 * @param query
//	 *            查询词
//	 * @param targetAmount
//	 *            线程结束时，搜索结果要达到的数量
//	 * @param resultList
//	 *            线程结束时，搜索结果的存放位置
//	 * @param doneSignal
//	 *            调用者线程的阻塞信号量
//	 */
//	public SearchThread(int userid, String query, int targetAmount,
//			List<Result> resultList, CountDownLatch doneSignal) {
//
//		this.totalAmount = targetAmount;
//		this.resultList = resultList;
//		this.userid = userid;
//		this.query = query;
//		this.mDoneSignal = doneSignal;
//	}
//
//	/**
//	 * 设置调度结果，会指定只用相应的搜索引擎进行搜索
//	 * 
//	 * @param schedule
//	 *            调度结果，Map的key是搜索引擎名称（最好英文），值是其得分，暂时没用，可以随便设置
//	 */
//	public void setSchedule(Map<String, Double> schedule) {
//		if (null == schedule || schedule.isEmpty()) {
//			Set<String> allEngName = ConstantValue.getAllEngineEnName();
//			Iterator<String> itEname = allEngName.iterator();
//			while (itEname.hasNext()) {
//				schedule.put(itEname.next(), 1.0);
//			}
//		} else {
//			this.schedule.putAll(schedule);
//		}
//	}
//
//	/**
//	 * 设置目标搜索引擎（搜索结果筛选），不会影响搜索引擎调度
//	 * 
//	 * @param enames
//	 *            目标搜索引擎，必须是英文名称
//	 */
//	public void setTargetEngine(Set<String> enames) {
//		this.mTargetEngine.addAll(enames);
//	}
//
//	/**
//	 * 根据用户ID，获得缓存池中的结果列表
//	 * 
//	 * @return
//	 */
//	private ResultPoolItem getResultListItem() {
//		if (null == poolItem)
//			poolItem = ResultPool.getResultListItem(userid, query);
//		return poolItem;
//	}
//
//	public List<Result> getResultList() {
//		return resultList;
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
//					|| totalAmount <= 0)
//				return;
//
//			ResultPoolItem rpItem = getResultListItem();
//			setScheduleResult(rpItem);
//            
//			
//			if (null == mTargetEngine || mTargetEngine.isEmpty()) {
//				mTargetEngine = ConstantValue.getAllEngineEnName();
//			}
//
//			// 搜索结果首先存放到缓存池中
//			// 这个列表会在每一轮搜索时被结果合成线程填充，因此最好不要使用最终的结果列表，那个列表的访问需要多线程互斥
//			List<Result> innerResList = rpItem.getSearchResultList();
//			getSearchResult(innerResList);
//
//			// 把搜索结果放到调用者线程提供的列表中
//			if (null != resultList && null != innerResList) {
//				int rlistSize = innerResList.size();
//				if (rlistSize > 1) {
//					synchronized (resultList) {
//						resultList.addAll(innerResList.subList(1,
//								innerResList.size()));
//					}
//				}
//			}
//			// 由于缓存池的竞争，搜索后这个rpItem可能已经从缓存池中移除了（被其他item抢占）
//			// 但还是回存一次，同时释放其中的used标志
//			ResultPool.releaseItem(rpItem);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return;
//	}
//
//	/**
//	 * 设置需要使的搜索引擎
//	 */
//	private void setScheduleResult(ResultPoolItem rpItem) {
//
//		/*
//		 * 设置搜索引擎的逻辑： 1. 如果用户指定了成员搜索引擎（this.schedule不空），则把缓存列表中的调度结果设置为用户指定的 2.
//		 * 如果用户没有指定搜索引擎，采用缓存中已经存在的 3.
//		 * 如果缓存中也没有调度结果（这个词是新搜索的，用户也没有指定搜索引擎），所有搜索引擎都调用
//		 * 其中，setSchedule函数如果发现调度结果发生了变化，会清空搜索结果列表
//		 * 注：这里用户指定搜索引擎是指用户只希望搜索结果来自这些搜索引擎；该功能目前在网页版还没有
//		 */
//		if (null != this.schedule && !this.schedule.isEmpty())
//			// 用户指定了搜索引擎
//			rpItem.setSchedule(this.schedule);
//		else if (!rpItem.hasScheduleResult()) {
//			// 用户没有指定搜索引擎，而且缓存中也没有调度结果
//			schedule = new HashMap<String, Double>();
//			Iterator<String> itEname = ConstantValue.getAllEngineEnName()
//					.iterator();
//			while (itEname.hasNext())
//				schedule.put(itEname.next(), 1.0);
//			rpItem.setSchedule(this.schedule);
//		} else {
//			// 用户没有指定搜索引擎，但是缓存中有调度结果
//			schedule = rpItem.getSchedule();
//		}
//	}
//
//	/**
//	 * 获取搜索结果直到数量足够
//	 * 
//	 * @param result
//	 *            搜索结果列表，新的结果将追加到这个列表后面
//	 */
//	private void getSearchResult(List<Result> innerRlist) {
//
//		if (null == innerRlist)
//			return;
//
//		// 第一个位置不是真实的结果，其中主要的信息是查询词及各成员搜索引擎已经搜索的页数
//		if (innerRlist.isEmpty()) {
//			innerRlist.add(new Result(query, Integer.toString(1), query, query,
//					99999));
//		}
//
//		query = QueryParticiple.participle(query);
//
//		// 搜索结果获取与合成过程并行
//		// listForSearch列表用于存放每一轮各个搜索引擎的搜索结果
//		// listForMerge是上一轮搜索结果，是还没有被合成的结果，要合并到result中去
//		List<Result> listForSearch = new ArrayList<Result>(), listForMerge = new ArrayList<Result>();
//		while (true) {
//
//			int curPage = Integer.parseInt(innerRlist.get(0).getAbstr());
//			int formerRsize = innerRlist.size();
//			searchProcess(curPage, innerRlist, listForSearch, listForMerge);
//
//			innerRlist.get(0).setAbstr(Integer.toString(++curPage));
//
//			if (resultEnough(innerRlist, formerRsize))
//				break;
//
//			List<Result> temp = listForMerge;
//			listForMerge = listForSearch;
//			listForSearch = temp;
//			listForSearch.clear();
//		}
//		// return result;
//	}
//
//	private boolean resultEnough(List<Result> innerRlist, int formerSize) {
//
//		if (null == innerRlist)
//			return true;
//
//		boolean ret = false;
//		// 这里不传递resultForMerge的原因是：
//		// 如果判断搜索结果足够，就不会进行下一轮操作，也就是此时resultForMerge中的结果是不会被返回的，不能计算在内
//		int curRsize = ResultCounter.getResultCountOfEngine(innerRlist, null,
//				mTargetEngine);
//		if (curRsize <= formerSize)
//			++repeatedTimes;
//		if (curRsize > totalAmount || repeatedTimes >= 4)
//			ret = true;
//
//		return ret;
//	}
//
//	private void searchProcess(int curPage, List<Result> innerRlist,
//			List<Result> listForSearch, List<Result> listForMerge) {
//
//		if (null == innerRlist)
//			return;
//		Set<String> enameSet = schedule.keySet();
//		Iterator<String> iterEname = enameSet.iterator();
//		CountDownLatch mseDoneSignal = new CountDownLatch(enameSet.size() + 1);
//		while (iterEname.hasNext()) {
//			String ename = iterEname.next();
//			// 这里要把两个列表都统计
//			// 因为这个数量被用来生成搜索结果中的“来自 百度(1)”中的数字
//			// 必须计算搜索引擎已经提供了多少结果，而listForMerge中的结果也要算在内
//			int lastCount = ResultCounter.getResultCountOfEngine(innerRlist,
//					listForMerge, ename);
//			MemberSearchEngineThread mseThr = new MemberSearchEngineThread(
//					query, curPage, lastCount, ename, listForSearch,
//					mseDoneSignal);
//			mseThr.start();
//		}
//
//		SearchResultMergeThread resMerge = new SearchResultMergeThread(query,
//				userid, innerRlist, listForMerge, mseDoneSignal, isLogin);
//		resMerge.start();
//
//		try {
//			mseDoneSignal.await(20, TimeUnit.SECONDS);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	/**
//	 * 统计搜索结果列表中来自各个引擎的结果数量
//	 * 
//	 * @param ret
//	 *            统计的结果，Key是搜索引擎的英文名字，值为result中这个搜索引擎的结果数量
//	 * @param result
//	 *            目前的搜索结果
//	 */
//	private void getResultAmount(Map<String, Integer> ret, List<Result> result) {
//
//		if (null == ret || null == result)
//			return;
//		Iterator<Result> iterRes = result.iterator();
//		if (iterRes.hasNext())
//			iterRes.next();
//		while (iterRes.hasNext()) {
//			Result r = iterRes.next();
//			Set<String> setEname = r.getSourceEngineEnName();
//			Iterator<String> iterEname = setEname.iterator();
//			while (iterEname.hasNext()) {
//				String ename = iterEname.next();
//				Integer count = ret.get(ename);
//				count = 1 + ((null == count) ? 0 : count.intValue());
//				ret.put(ename, count);
//			}
//		}
//		return;
//	}
//
//	private boolean resultEnough(List<Result> result, int page, String engName) {
//
//		boolean ret = false;
//		if (null == result)
//			return ret;
//
//		page = page <= 0 ? 1 : page;
//		if (result.size() > 10 * (page + 2))
//			ret = true;
//
//		return ret;
//	}
//
//	private static Map<String, Integer> initOriginalResultCount(
//			List<Result> rlist) {
//
//		Map<String, Integer> ret = new HashMap<String, Integer>();
//		Set<String> engEnNames = ConstantValue.getAllEngineCnName();
//		Iterator<String> iterEngName = engEnNames.iterator();
//		while (iterEngName.hasNext())
//			ret.put(iterEngName.next(), 0);
//
//		if (null == rlist)
//			return ret;
//		for (Result res : rlist) {
//			if (null == res)
//				continue;
//			Set<String> srcEngName = res.getSourceEngineCnName();
//			if (null == srcEngName)
//				continue;
//			Iterator<String> iterSrcEngName = srcEngName.iterator();
//			while (iterSrcEngName.hasNext()) {
//				String engEnName = ConstantValue.getEnEngineName(iterSrcEngName
//						.next());
//				ret.put(engEnName, ret.get(engEnName) + 1);
//			}
//		}
//
//		return ret;
//
//	}
//}
