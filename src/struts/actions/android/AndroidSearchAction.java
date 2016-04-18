package struts.actions.android;

import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import jade.wrapper.gateway.JadeGateway;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.json.JSONArray;
import org.json.JSONObject;

import agent.behaviours.gateway.MyBaseGWBehaviour;
import agent.data.inblackboard.SearchData;
import agent.data.inmsg.DataToInterfaceAgent;
import agent.data.inmsg.TransactionType.InterfaceAgentTxType;
import agent.entities.blackboard.SearchDataBlackboard;
import agent.utils.GatewayBehaviourUtil;
import server.commonutils.MyStringChecker;
import server.engine.api.EngineFactory;
import server.info.entities.communication.RecommQueryAndPercent;
import server.threads.RelateSearchThread;
import common.entities.searchresult.Result;
import common.entities.searchresult.ResultPool;
import common.entities.searchresult.ResultPoolItem;
import common.entities.searchresult.SearchResultAmountChecker;
import common.functions.webpagediagram.VennDiagram;
import db.dbhelpler.UserHelper;

public class AndroidSearchAction {

	HttpServletRequest request = null;
	HttpServletResponse response = null;
	final static int RETURN_LIST_COUNT = 20;
	private static final int QUERY_RECOMM_LIST_COUNT = 15;// 查询词推荐最大数目
	private static final int CLICK_RECOMM_LIST_COUNT = 3;// 点击推荐最大数目

	private int m_nUserid;
	private Map<String, Double> m_mapScheEngine;
	// query是用户查询的关键词
	private String query;
	// schedule是用户选择的成员搜索引擎
	private String schedule;
	// username是用户登录后的用户名
	private String username;
	// imei设备号
	private String imei;
	// page是用户请求的页面数
	private int page;
	private Set<String> themTemp;
	private Set<String> m_setFilterEngName;
	private String filterThem, filterEngine;
	// Results是请求页面的list
	private Map<String, Double> enginePercentage;
	private boolean isLogin;
	private List<Result> m_lsSearchRes;
	private List<Result> m_lsClickRecommRes;
	private List<RecommQueryAndPercent> m_lsQueryRecommRes;

	private ResultPoolItem m_sdcResultCache;
	private boolean m_bSearchResultEngouth;
	private boolean m_bHasQueryRecomm;
	private boolean m_bHasClickRecomm;
	private boolean m_bHasRelateSearch;

	private final static int NUM = 10;// 每一页结果的数量

	private CountDownLatch mDoneSignal;

	private final CountDownLatch getmDoneSignal() {
		return mDoneSignal;
	}

	public List<Result> getClickRecommResList() {
		if (null == m_lsClickRecommRes)
			// 这个列表增删操作多，按位置获得元素的操作少，用链表
			// 函数setQueryRecomResults()发现结果数量太多，需要删除时，假定了这个列表是链表，如果改成ArrayList要注意
			m_lsClickRecommRes = new LinkedList<Result>();
		return m_lsClickRecommRes;
	}

	public List<RecommQueryAndPercent> getQueryRecommResultList() {
		if (null == m_lsQueryRecommRes)
			m_lsQueryRecommRes = new LinkedList<RecommQueryAndPercent>();
		return m_lsQueryRecommRes;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getPage() {
		return String.valueOf(page);
	}

	public void setPage(String page) {
		if (null == page)
			this.page = 1;
		else {
			try {
				this.page = Integer.parseInt(page);
			} catch (Exception e) {
				this.page = 1;
			}
		}
	}

	public List<Result> getResults() {
		if (null == m_lsSearchRes)
			m_lsSearchRes = new LinkedList<Result>();
		return m_lsSearchRes;
	}

	public void setResults(List<Result> results) {
		if (null == results)
			results = new LinkedList<Result>();
		m_lsSearchRes = results;
	}

	
	public String getFilterThem() {
		return filterThem;
	}

	public void setFilterThem(String filterThem) {
		this.filterThem = filterThem;
	}

	public String getFilterEngine() {
		return filterEngine;
	}

	public void setFilterEngine(String filterEngine) {
		this.filterEngine = filterEngine;
	}

	public Map<String, Double> getEnginePercentage() {
		if (null == enginePercentage)
			enginePercentage = new HashMap<String, Double>();
		return enginePercentage;
	}

	private final Map<String, Double> getScheduleMap() {
		if (null == m_mapScheEngine) {
			m_mapScheEngine = new HashMap<String, Double>();
		}
		return m_mapScheEngine;
	}

	// action方法：
	public String execute() throws Exception {
		response = ServletActionContext.getResponse();
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		JSONObject objJson = new JSONObject();
		if (MyStringChecker.isBlank(query)) {
			return null;
		}

		m_nUserid = UserHelper.getUserIDByUsername(username);
		isLogin = UserHelper.isLoginUser(m_nUserid);
		getEngineNames();
		checkSearchResultCache();
		setThreadMutex();

		int index = saveDataToBlackBoard();
		DataToInterfaceAgent data = new DataToInterfaceAgent(index);
		doSearch(data);
		relatedSearch();
		if (isLogin) {
			queryRecomm(data);
			clickRecomm(data);
		}

		waitForDone();
		setQueryRecomResults();
		setClickRecomResults();
		if (!isBlank(filterThem) || !isBlank(filterEngine)) {
			themTemp = new HashSet<String>();
			m_setFilterEngName = new HashSet<String>();
			themTemp.add(filterThem);
			m_setFilterEngName.add(filterEngine);
			setSearchResults();
		}
		ResultPool.releaseItem(m_sdcResultCache, m_nUserid, query);
		SearchDataBlackboard.removeData(index);

		Map<String, Double> engToScore = this.getEngineToScore();
		Set<String> categoryList = getCategoryNamesOfResult(this.getResults());
		
		JSONArray results = new JSONArray();
		JSONArray clickRecomm = new JSONArray();
		JSONArray queryRecomm = new JSONArray();
		JSONArray engToScores = new JSONArray();
		JSONArray categorys = new JSONArray();
		List<Result> resultList = getResults();
		List<Result> clickRecommList = getClickRecommResList();
		List<RecommQueryAndPercent> queryRecommList = getQueryRecommResultList();
		for (int i = 0; i < resultList.size(); i++) {
			JSONObject elem = new JSONObject();
			Result result = resultList.get(i);
			elem.put("title", result.getTitle());
			elem.put("abstr", result.getAbstr());
			elem.put("url", result.getLink());
			elem.put("source", result.getSource());
			elem.put("spare", result.getSpare());
			elem.put("value", result.getValue());
			elem.put("classification", result.getClassification());
			results.put(elem);
		}
		for (int i = 0; i < clickRecommList.size(); i++) {
			JSONObject elem = new JSONObject();
			Result result = clickRecommList.get(i);
			elem.put("title", result.getTitle());
			elem.put("abstr", result.getAbstr());
			elem.put("url", result.getLink());
			elem.put("source", result.getSource());
			elem.put("spare", result.getSpare());
			elem.put("value", result.getValue());
			elem.put("classification", result.getClassification());
			clickRecomm.put(elem);
		}
		for (int i = 0; i < queryRecommList.size(); i++) {
			JSONObject elem = new JSONObject();
			RecommQueryAndPercent result = queryRecommList.get(i);
			elem.put("query", result.getQuery());
			elem.put("percent", result.getPercent());
			queryRecomm.put(elem);
		}
		for (Entry<String, Double> it : engToScore.entrySet()) {
			JSONObject elem = new JSONObject();
			elem.put("engine", it.getKey());
			elem.put("score", it.getValue());
			engToScores.put(elem);
		}
		for (String it:categoryList) {
			JSONObject elem = new JSONObject();
			elem.put("category", it);
			categorys.put(elem);
		}
		objJson.put("results", results);
		objJson.put("clickRecomm", clickRecomm);
		objJson.put("queryRecomm", queryRecomm);
		objJson.put("engToScores", engToScores);
		objJson.put("categorys", categorys);
		out.print(objJson.toString());
		out.flush();
		out.close();
		return null;
	}

	private void setQueryRecomResults() {

		try {
			if (null != m_lsQueryRecommRes) {
				LinkedList<RecommQueryAndPercent> ls = (LinkedList<RecommQueryAndPercent>) m_lsQueryRecommRes;// 这里假定了是链表形式，没有处理其他可能的List
				while (ls.size() > QUERY_RECOMM_LIST_COUNT) {
					ls.removeLast();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setSearchResults() {

		List<Result> tmpRlist = null;

		// 按引擎筛选时，先把无关的结果去除
		tmpRlist = new ArrayList<Result>();
		List<Result> iterAllRes = getResults();

		for (Result result : iterAllRes) {

			if (isBlank(filterEngine)) {
				if (result.isFromTargetEngine(themTemp))
					tmpRlist.add(result);
			}else if (isBlank(filterThem)) {
				if (result.isFromTargetEngine(m_setFilterEngName))
					tmpRlist.add(result);
			}else{
				if (result.isFromTargetEngine(themTemp) && result.isFromTargetEngine(m_setFilterEngName))
					tmpRlist.add(result);
			}
		}
		setResults(tmpRlist);
	}

	private void setClickRecomResults() {

		try {
			if (null != m_lsClickRecommRes) {
				LinkedList<Result> ls = (LinkedList<Result>) m_lsClickRecommRes;// 这里假定了是链表形式，没有处理其他可能的List
				while (ls.size() > CLICK_RECOMM_LIST_COUNT) {
					ls.removeLast();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void waitForDone() {

		if (null != mDoneSignal)
			try {
				mDoneSignal.await(20, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}

	private void getEngineNames() {

		if (MyStringChecker.isBlank(schedule))
			return;
		String[] engNameArr = schedule.split("-");
		if (null == engNameArr)
			return;
		for (int i = 0; i < engNameArr.length; ++i) {
			getScheduleMap().put(EngineFactory.getEnEngineName(engNameArr[i]),
					1.0);
		}

	}

	private Set<String> getCategoryNamesOfResult(List<Result> rlist) {

		Set<String> ret = new HashSet<String>();
		if (null == rlist || rlist.isEmpty())
			return ret;
		Iterator<Result> iterRes = rlist.iterator();
		if (iterRes.hasNext())
			iterRes.next();
		while (iterRes.hasNext()) {
			Result curRes = iterRes.next();
			String category = curRes.getClassification();
			if (null != category && !category.isEmpty())
				ret.add(category);
		}

		return ret;
	}

	private Map<String, Double> getEngineToScore() {

		Map<String, Double> ret = new HashMap<String, Double>();
		Iterator<String> itEngine = EngineFactory.getAllEngineEnName()
				.iterator();
		List<Result> rls = getResults();

		while (itEngine.hasNext()) {
			// 遍历系统中所有可能的成员搜索引擎名称，检查当前结果列表是不是有来自这个引擎的
			String ename = itEngine.next();
			if (null == ename)
				continue;
			Iterator<Result> itRes = rls.iterator();

			while (itRes.hasNext()) {
				Result r = itRes.next();
				if (null == r)
					continue;
				if (r.isFromTargetEngine(ename)) {
					ret.put(EngineFactory.getCnEngineName(ename), 1.0);
					break;
				}
			}

		}

		return ret;
	}

	private void relatedSearch() {

		// 由于相关搜索没有什么特殊性，也很简单，
		// 这里保留了原有的线程实现机制，没有改成Agent
		// 虽然安卓端目前相关搜索不是在搜索时同时获取的，这里仍然搜索并存放到缓存中
		if (!m_bHasRelateSearch) {
			RelateSearchThread relSThread = new RelateSearchThread(null,
					getmDoneSignal(), query, m_sdcResultCache);/*
																 * 安卓端现在暂时不需要返回相关搜索结果
																 * ，所以第一个参数为null
																 */
			relSThread.start();
		}
	}

	private void doSearch(DataToInterfaceAgent data) throws IOException,
			StaleProxyException, ControllerException, InterruptedException {

		if (!m_bSearchResultEngouth) {
			data.setTransactionType(InterfaceAgentTxType.search);
			MyBaseGWBehaviour b = GatewayBehaviourUtil.getBaseBehaviour(data);
			JadeGateway.execute(b);
		} else {
			// 搜索缓存中的数量已经足够了，不再向搜索Agent发消息
			getResults().addAll(m_sdcResultCache.getSearchResultList());
		}

	}

	private void queryRecomm(DataToInterfaceAgent data) throws IOException,
			StaleProxyException, ControllerException, InterruptedException {

		if (!m_bHasQueryRecomm) {
			data.setTransactionType(InterfaceAgentTxType.queryRecomm);
			MyBaseGWBehaviour bq = GatewayBehaviourUtil.getBaseBehaviour(data);
			JadeGateway.execute(bq);
		} else {
			getQueryRecommResultList().addAll(
					m_sdcResultCache.getQueryRecommResult());
		}
	}

	private void clickRecomm(DataToInterfaceAgent data) throws IOException,
			StaleProxyException, ControllerException, InterruptedException {

		if (!m_bHasClickRecomm) {
			data.setTransactionType(InterfaceAgentTxType.clickRecomm);
			MyBaseGWBehaviour bcr = GatewayBehaviourUtil.getBaseBehaviour(data);
			JadeGateway.execute(bcr);
		} else {
			getClickRecommResList().addAll(
					m_sdcResultCache.getClickRecommResult());
		}
	}

	private void checkSearchResultCache() {
		m_sdcResultCache = ResultPool.getResultListItem(m_nUserid, query);
		m_sdcResultCache.setSchedule(m_mapScheEngine);
		m_bHasClickRecomm = m_sdcResultCache.hasClickRecommResult();
		m_bHasQueryRecomm = m_sdcResultCache.hasQueryRecommResult();
		m_bHasRelateSearch = m_sdcResultCache.hasRelateSearchResult();
		SearchResultAmountChecker searchJudge = new SearchResultAmountChecker(
				m_sdcResultCache.getSearchResultList(), page * NUM);
		m_bSearchResultEngouth = searchJudge.resultIsEnough();
	}

	/**
	 * 设置线程锁信号量mDoneSignal
	 */
	private void setThreadMutex() {

		int count = 0;
		if (!m_bHasRelateSearch)
			++count;
		if (!m_bSearchResultEngouth)
			++count;
		if (isLogin) {
			if (!m_bHasClickRecomm)
				++count;
			if (!m_bHasQueryRecomm)
				++count;
		}
		if (count > 0)
			mDoneSignal = new CountDownLatch(count);
	}

	private int saveDataToBlackBoard() {

		int ret = 0;
		SearchData searchData = new SearchData(getmDoneSignal(), m_nUserid,
				query, page * NUM, null);
		searchData.setResultListItem(m_sdcResultCache);
		if (!m_bSearchResultEngouth) {
			searchData.setResultList(getResults());
		}
		if (isLogin) {
			if (!m_bHasClickRecomm)
				searchData.setClickRecommResultList(getClickRecommResList());
			if (!m_bHasQueryRecomm)
				searchData.setQueryRecommResultList(getQueryRecommResultList());
		}
		ret = SearchDataBlackboard.addData(searchData);
		return ret;

	}

	/**
	 * 判断字符串是不是空字符串，
	 * 
	 * @param m_setFilterEngName2
	 * @return 空返回true否则返回false
	 */
	private boolean isBlank(String s) {
		boolean result = false;
		if (s == null || s.length() <= 0) {
			result = true;
		}
		return result;
	}
}
