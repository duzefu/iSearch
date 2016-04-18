package struts.actions.android;

import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import jade.wrapper.gateway.JadeGateway;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import agent.behaviours.gateway.MyBaseGWBehaviour;
import agent.data.inblackboard.SearchData;
import agent.data.inmsg.DataToInterfaceAgent;
import agent.data.inmsg.TransactionType.InterfaceAgentTxType;
import agent.entities.blackboard.SearchDataBlackboard;
import agent.utils.GatewayBehaviourUtil;
import server.commonutils.MyStringChecker;
import server.engine.api.EngineFactory;
import sun.security.util.Length;
import common.entities.searchresult.Result;
import common.entities.searchresult.ResultPool;
import common.entities.searchresult.ResultPoolItem;
import common.functions.webpagediagram.VennDiagram;
import db.dbhelpler.UserHelper;

public class AndroidPercentageAction {

	private HttpServletRequest request = null;
	private HttpServletResponse response = null;

	private final static int NUM=10;
	private final static int NUMBER_OF_SUBTASK=1;//初始化mDoneSignal，这个类有可能出现需要搜索的情况（不太容易出现）

	private String imei = null;
	private String username=null;
	private String query = null;
	private int page;
	private String schedule = null;
	
	private int m_nUserid;
	private CountDownLatch mDoneSignal;
	private ResultPoolItem m_sdcResultCache;
	private static List<Result> m_lsResult;
	
	private static Map<String, Double> m_mapEnginePercentage = new HashMap<String, Double>();
	private Map<String,Double> m_mapScheEngine;

	private CountDownLatch getmDoneSignal(){
		if(null==mDoneSignal) mDoneSignal=new CountDownLatch(NUMBER_OF_SUBTASK);
		return mDoneSignal;
	}
	
	private final Map<String,Double> getScheduleMap(){
		if(null==m_mapScheEngine){
			m_mapScheEngine=new HashMap<String, Double>();
		}
		return m_mapScheEngine;
	}
	
	public Map<String, Double> getEnginePercentage() {
		return m_mapEnginePercentage;
	}

	public void setEnginePercentage(Map<String, Double> enginePercentage) {
		this.m_mapEnginePercentage = enginePercentage;
	}

	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	public String getPage() {
		return String.valueOf(page);
	}

	public void setPage(String page) {
		
		if(null==page||page.isEmpty()) this.page=1;
		try{
			this.page=Integer.parseInt(page);
		}catch(Exception e){
			this.page=1;
		}

	}

	private final List<Result> getResults() {
		if(null==m_lsResult) m_lsResult=new LinkedList<Result>();
		return m_lsResult;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String execute() throws Exception {
		JSONObject objJson = new JSONObject();
		response = ServletActionContext.getResponse();
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		// 若查询消息为空，直接返回
		if (query == null || query == "") {
			return null;
		}

		int userid = UserHelper.getUserIDByUsername(username);
		m_sdcResultCache = ResultPool.getResultListItem(userid, query);
		if(null==m_sdcResultCache||!m_sdcResultCache.hasSearchResult()){
			doSearch();
			waitForDone();
			ResultPool.releaseItem(m_sdcResultCache, m_nUserid, query);
		}
		// 得到查询结果
		// 引擎重合率
		List<Result> listCache = m_sdcResultCache.getSearchResultList();

		getResults().addAll(listCache);
		List<Result> lists = getResults();

		VennDiagram.InitData(lists);
		List<String> legend = new ArrayList<String>();
		Map<String, Double> venn = new HashMap<String, Double>();
		legend = VennDiagram.GetLegend();
		venn = VennDiagram.GetValue();

		// 引擎覆盖率
		m_mapEnginePercentage = getEnginePercentage(lists);
		JSONArray legends = new JSONArray();
		JSONArray venns = new JSONArray();
		JSONArray percentages = new JSONArray();
		for (Entry<String, Double> elem : m_mapEnginePercentage.entrySet()) {
			String key = elem.getKey();
			double value = elem.getValue();
			JSONObject json = new JSONObject();
			json.put("key", key);
			json.put("value", value);
			percentages.add(json);
		}
		Set<Entry<String, Double>> entrys = venn.entrySet();
		Iterator<Entry<String, Double>> iterator = entrys.iterator();
		while (iterator.hasNext()) {
			Entry<String, Double> entry = iterator.next();
			String key = entry.getKey();
			double value = entry.getValue();
			JSONObject json = new JSONObject();
			json.put("key", key);
			json.put("value", value);
			venns.add(json);
		}
		for (String entry : legend) {
			JSONObject json = new JSONObject();
			json.put("key", entry);
			legends.add(json);
		}
		objJson.put("percentages", percentages);
		objJson.put("venns", venns);
		objJson.put("legends", legends);
		out.print(objJson.toString());
		out.flush();
		out.close();
		return null;
	}

	private void waitForDone(){
		
		if(null!=mDoneSignal)
			try {
				mDoneSignal.await(20, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
	
	private int saveDataToBlackBoard(){
		
		int ret=0;
		SearchData searchData=new SearchData(getmDoneSignal(),m_nUserid, query, page*NUM, null);
		searchData.setResultListItem(m_sdcResultCache);
		searchData.setResultList(getResults());
		ret=SearchDataBlackboard.addData(searchData);
		return ret;
		
	}
	
	private void doSearch() throws IOException, StaleProxyException, ControllerException, InterruptedException{
		
		getEngineNames();
		int index=saveDataToBlackBoard();
		DataToInterfaceAgent data=new DataToInterfaceAgent(index);
		data.setTransactionType(InterfaceAgentTxType.search);
		MyBaseGWBehaviour b=GatewayBehaviourUtil.getBaseBehaviour(data);
		JadeGateway.execute(b);
	}
	
	private void getEngineNames(){
		
		if(MyStringChecker.isBlank(schedule)) return;
		String[] engNameArr=schedule.split("-");
		if(null==engNameArr) return;
		for(int i=0;i<engNameArr.length;++i){
			getScheduleMap().put(EngineFactory.getEnEngineName(engNameArr[i]),1.0);
		}
		
	}
	
	private Map<String, Double> getEnginePercentage(List<Result> result) {

		Map<String, Double> searchEngine = new HashMap<String, Double>();
		Map<String, Double> searchEngine1 = new HashMap<String, Double>();
		searchEngine.put("搜狗", new Double(0.0));
		searchEngine.put("百度", new Double(0.0));
		searchEngine.put("必应", new Double(0.0));
		searchEngine.put("搜搜", new Double(0.0));
		searchEngine.put("有道", new Double(0.0));
		searchEngine.put("即刻", new Double(0.0));
		searchEngine.put("雅虎", new Double(0.0));

		for (Result r : result) {
			if (r.getSource().contains("搜狗")) {
				double value = searchEngine.get("搜狗");
				value++;
				searchEngine.put("搜狗", value);
			}
			if (r.getSource().contains("百度")) {
				double value = searchEngine.get("百度");
				value++;
				searchEngine.put("百度", value);
			}
			if (r.getSource().contains("必应")) {
				double value = searchEngine.get("必应");
				value++;
				searchEngine.put("必应", value);
			}
			if (r.getSource().contains("搜搜")) {
				double value = searchEngine.get("搜搜");
				value++;
				searchEngine.put("搜搜", value);
			}
			if (r.getSource().contains("有道")) {
				double value = searchEngine.get("有道");
				value++;
				searchEngine.put("有道", value);
			}
			if (r.getSource().contains("即刻")) {
				double value = searchEngine.get("即刻");
				value++;
				searchEngine.put("即刻", value);
			}
			if (r.getSource().contains("雅虎")) {
				double value = searchEngine.get("雅虎");
				value++;
				searchEngine.put("雅虎", value);
			}
		}

		Set<String> keySet = searchEngine.keySet();
		double sum = 0.0;
		for (String key : keySet)
			sum += searchEngine.get(key);

		for (String key : keySet) {
			double percentage = 0.0;
			percentage = searchEngine.get(key) / sum;
			if (percentage > 0.000001)
				searchEngine1.put(key, percentage);
		}
		return searchEngine1;

	}
	
}