package struts.actions.web;

import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import jade.wrapper.gateway.JadeGateway;

import java.io.IOException;
import java.util.ArrayList; //import java.util.HashMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import server.commonutils.LogU;
import server.commonutils.MyStringChecker;
import server.engine.api.EngineFactory;
import server.engine.api.EngineFactory.EngineName;
import server.info.config.LangEnvironment;
import server.info.config.LangEnvironment.ClientType;
import server.info.config.LangEnvironment.LangEnv;
import server.info.config.SessionAttrNames;
import server.info.entities.communication.RecommQueryAndPercent;
import server.threads.RelateSearchThread;
import agent.behaviours.gateway.MyBaseGWBehaviour;
import agent.data.inblackboard.SearchData;
import agent.data.inmsg.DataToInterfaceAgent;
import agent.data.inmsg.TransactionType.InterfaceAgentTxType;
import agent.entities.blackboard.SearchDataBlackboard;
import agent.utils.GatewayBehaviourUtil;
import common.entities.searchresult.Result;
import common.entities.searchresult.ResultPool;
import common.entities.searchresult.ResultPoolItem;
import common.entities.searchresult.SearchResultAmountChecker;
import common.utils.querypreprocess.DealingWithQuery;
import db.dao.UserDao;
import db.dbhelpler.UserHelper;

public class SearchAction {

	private static final int QUERY_RECOMM_LIST_COUNT = 8;// 查询词推荐最大数目
	private static final int CLICK_RECOMM_LIST_COUNT = 3;// 点击推荐最大数目
	private static final int RELATE_SEARCH_LIST_COUNT=8;//相关查询最大数目
	private static final int NUM = 10;// 智搜网页中一页显示的结果数量，用于判断搜索结果是否已经足够

	private CountDownLatch mDoneSignal;// 多线程互斥信号

	private UserDao userDao;// 用户数据库操作对象，其值由Spring通过setUserDao()注入，在beans.xml中关于bean id="SearchAction"的bean中配置

	/*下面这些值是直接保存客户端传来的数据，这些值在相应的set函数中不解析，因为有时候这种解析是可以推迟的，例如查询词为空*/
	private String m_strQuery;// 查询词，值由struts2通过调用setQuery()函数来设置
	private String m_strScheduleEngine;//用户选择的引擎调度策略
	private String m_strPage;//客户端传来的页码信息
	private String m_strFilterEngine;//客户端传来的按引擎筛选信息
	private String m_strLang;
	
	/*****下面这些值是在工作过程中计算的*****/
	/*
	 * 当前智搜的页数（不是成员搜索引擎的页数），
	 * 在搜索前，这个值是用户选择的，
	 * 在搜索后，这个值可能要修改（如果搜索结果不够的话），然后用在jsp页面上
	 */
	private int m_nPage;
	private LangEnv m_enuLang;
	boolean m_bIsLogin;// 用户是否已经登录
	private boolean m_bHasFilter; // 选定全部搜索引擎时为false,否则为true
	private Set<String> m_setFilterEngName;//保存分离出的按引擎筛选的搜索引擎的英文名称
	private Map<String,Double> m_mapScheEngine;
	private int m_nUserid;// 通过用户名确定的ID
	private String m_strUsername;
	
	private List<Result> results;//用来存放最终要显示的结果，包括点击推荐结果及普通结果
	private List<Result> allResult;// 包括了从第1条到最后1条的所有搜索引擎的结果（结果合成之后的）
	private List<String> m_lsRelateSearchResult;// 相关搜索结果
	private List<RecommQueryAndPercent> m_lsQueryRecomResult;// 查询词推荐结果
	private List<Result> m_lsClickRecommResult;// 点击推荐结果
	
	private ResultPoolItem m_sdcResultCache;//搜索结果缓存
	
	private boolean m_bSearchResultEngouth;
	private boolean m_bHasQueryRecomm;
	private boolean m_bHasClickRecomm;
	private boolean m_bHasRelateSearch;
	
	private HttpSession m_httpCurSession;
	
	/*由struts2在执行execute前调用*/
	public void setSchedule(String schedule) {
		m_strScheduleEngine = schedule;
	}
	public void setQuery(String query) {
		/*
		 * modified by jtr 2014-5-6 解决查询串中包含特殊字符时搜索导致系统抛出异常的问题，但未解决输入单个字符造成异常的问题
		 * add by zcl 20150707
		 * 这里主要是因为系统某些地方（涉及对查询词的处理，例如把查询词用来构造JSON对象等）操作不规范，导致相应的出现问题。
		 * 如果能够把系统其余地方对查询词的处理做正确的检查，这种强行修改查询词的做法是不应该使用的
		 */
		m_strQuery = DealingWithQuery.CorrectQuery(query);
	}
	public void setPage(String page) {
		m_strPage=page;
	}
	public void setLang(String lang){
		m_strLang=lang;
	}
	public void setFilterengine(String filterEng) {
		m_strFilterEngine = filterEng;
	}
	
	/*由struts2在执行jsp页面时调用*/
	public String getUsername(){
		return m_strUsername;
	}
	public int getUserid() {
		return m_nUserid;
	}
	public String getQuery() {
		return m_strQuery;
	}
	public List<String> getRelatedSearch() {
		if(null==m_lsRelateSearchResult) m_lsRelateSearchResult=new LinkedList<String>();
		return m_lsRelateSearchResult;
	}
	public int getPage() {
		return m_nPage;
	}
	public LangEnv getLangEnv(){
		return m_enuLang;
	}
	public List<Result> getResults() {
		if(null==results) results=new LinkedList<Result>();
		return results;
	}
	public List<Result> getAllResult() {
		if(null==allResult) allResult=new LinkedList<Result>();
		return allResult;
	}
	public List<RecommQueryAndPercent> getQueryRecomResult() {
		if(null==m_lsQueryRecomResult) m_lsQueryRecomResult=new LinkedList<RecommQueryAndPercent>();
		return m_lsQueryRecomResult;
	}
	public Set<EngineName> getEngineOfResult(){
		
		Set<EngineName> ret=new HashSet<EngineName>();
		if(null==allResult) return ret;
		for(Iterator<EngineName> iter=EngineFactory.getAllEngineIterator();iter.hasNext();){
			EngineName curEng=iter.next();
			for(Iterator<Result> itRes=allResult.iterator();itRes.hasNext();){
				Result res=itRes.next();
				if(res.isFromTargetEngine(curEng)){
					ret.add(curEng);
					break;
				}
			}
		}
		return ret;
	}
	public Set<EngineName> getFilterEngines() {
		Set<EngineName> ret = new HashSet<EngineName>();
		if (null != m_setFilterEngName) {
			for (Iterator<String> it = m_setFilterEngName.iterator(); it.hasNext();) {
				ret.add(EngineFactory.getInnerEngineName(it.next()));
			}
		} else {
			EngineFactory.getAllEngineNames(ret);
		}
		return ret;
	}
	
	private List<Result> getClickRecomResult() {
		if(null==m_lsClickRecommResult){
			m_lsClickRecommResult=new LinkedList<Result>();
		}
		return m_lsClickRecommResult;
	}
	private Map<String, Double> getScheduleMap() {
		if(null==m_mapScheEngine){
			m_mapScheEngine=new HashMap<String, Double>();
		}
		return m_mapScheEngine;
	}
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	private Set<String> getFilterEngName(){
		if(null==m_setFilterEngName) m_setFilterEngName=new HashSet<String>();
		return m_setFilterEngName;
	}
	
	/**
	 * 点击搜索按钮后执行，负责获取搜索结果展示页面所需要的所有数据
	 * 
	 * @return： 表示搜索执行的结果情况的字符串
	 */
	public String execute() throws Exception {

		try {

			//即使不是搜索请求，语言环境、用户名信息也是必须要解析的
			m_httpCurSession= ServletActionContext.getRequest().getSession();
			m_enuLang=WebParamParser.parseLang(m_strLang, m_httpCurSession);
			m_strUsername= (String) m_httpCurSession.getAttribute(SessionAttrNames.USERNAME_ATTR);
			m_nUserid = UserHelper.getUserIDByUsername(m_strUsername);
			m_bIsLogin=UserHelper.isLoginUser(m_nUserid);
			
			if (MyStringChecker.isWhitespace(m_strQuery)) {
				return "search";
			}
			parseClientParams();
			getAndCheckSearchResultCache();
			setThreadMutex();
			
			int index=saveDataToBlackBoard();
			DataToInterfaceAgent data=new DataToInterfaceAgent(index);
			doSearch(data);
			relatedSearch();
			if(m_bIsLogin){
				queryRecomm(data);
				clickRecomm(data);
			}
			
			waitForDone();
			
			setQueryRecomResults();
			setClickRecomResults();
			setRelateSearchContent();
			setSearchResults();
			ResultPool.releaseItem(m_sdcResultCache, m_nUserid, m_strQuery);
			SearchDataBlackboard.removeData(index);
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
		return "success";
	}

	private void parseClientParams(){
		/*
		 * 注：
		 * 网页版中，用户可以随意修改HTML元素及URL，
		 * 因此，网页版解析搜索引擎名字之类客户端传来的信息时，必须用tryParseString方式；
		 * 同时，URL中相同名字的参数不一定只出现一次，如果多次出现，传入的字符串将是多次出现的值以逗号拼接而成（struts2做的），
		 * 为了服务器端稳定，这里解析时必须做好异常处理，考虑客户端参数异常时应如何对待，
		 * 保证系统内部工作时，客户端的数据已经被正确的转换为内部数据类型（如枚举）
		 * 对于安卓端，虽然用户改动传递的参数没有网页版这么容易，不过还是应该做同样的异常处理
		 */
		parsePage();
		parseScheduleEngine();
		parseFilterEngine();
	}
	
	/**
	 * 解析客户端传递的页码参数，如果存在多个page参数，以第一个能够非空且正确的值为准
	 */
	private void parsePage(){
		
		m_nPage=1;
		if(null==m_strPage) return;
		String arr[]=m_strPage.split(",");
		if(null!=arr&&arr.length>=1){
			for(int i=0;i<arr.length;++i){
				String curPage=arr[i].trim();
				if(MyStringChecker.isBlank(curPage)) continue;
				try{
					m_nPage=Integer.parseInt(arr[0].trim());
				}catch(Exception e){
					continue;
				}
				break;
			}
		}
	}
	
	private void parseScheduleEngine(){
		
		if(MyStringChecker.isBlank(m_strScheduleEngine)) return;
		String[] arr=m_strScheduleEngine.split(",");
		Map<String, Double> target=getScheduleMap();
		if(null!=arr){
			for(int i=0;i<arr.length;++i){
				EngineName innerName=EngineFactory.tryParseString(arr[i].trim());
				if(null==innerName) continue;
				String enName=EngineFactory.getEnNameString(innerName);//转成枚举对应的小写形式，由于很多地方还是使用这种字符串
				target.put(enName, 1.0);
			}
		}
	}
	
	private void parseFilterEngine(){
		if(!MyStringChecker.isBlank(m_strFilterEngine)){
		String[] arr=m_strFilterEngine.split(",");
		Set<String> target=getFilterEngName();
		if(null!=arr){
			for(int i=0;i<arr.length;++i){
				EngineName innerName=EngineFactory.tryParseString(arr[i]);
				if(null==innerName) continue;
				String enName=EngineFactory.getEnNameString(innerName);//由于转成枚举对应的小写形式，由于很多地方还是使用这种字符串
				target.add(enName);
			}
		}
		}
		//目前这个集合会传到黑板里面，因此搜索之后这个集合可能有数据，到时候不便于判断用户是不是有筛选请求
		m_bHasFilter=null!=m_setFilterEngName&&!m_setFilterEngName.isEmpty();
	}
	
	private void getAndCheckSearchResultCache(){
		m_sdcResultCache=ResultPool.getResultListItem(m_nUserid,m_strQuery);
		m_sdcResultCache.setSchedule(m_mapScheEngine);//在检查缓存前，先设置调度结果，如果用户指定的搜索引擎变了，也肯定需要清空现有的搜索结果
		m_bHasClickRecomm=m_sdcResultCache.hasClickRecommResult();
		m_bHasQueryRecomm=m_sdcResultCache.hasQueryRecommResult();
		m_bHasRelateSearch=m_sdcResultCache.hasRelateSearchResult();
		SearchResultAmountChecker searchJudge = new SearchResultAmountChecker(m_sdcResultCache.getSearchResultList(),m_setFilterEngName, m_nPage*NUM);
		m_bSearchResultEngouth=searchJudge.resultIsEnough();
	}
	
	/**
	 * 设置线程锁信号量mDoneSignal
	 */
	private void setThreadMutex(){
		
		int count=0;
		if(!m_bHasRelateSearch) ++count;
		if(!m_bSearchResultEngouth) ++count;
		if(m_bIsLogin){
			if(!m_bHasClickRecomm) ++count;
			if(!m_bHasQueryRecomm) ++count;
		}
		if(count>0){
			mDoneSignal=new CountDownLatch(count);
		}
	}
	
	private int saveDataToBlackBoard(){
		
		int ret=0;
		SearchData searchData=new SearchData(mDoneSignal,m_nUserid, m_strQuery, m_nPage*NUM, null);
		searchData.setResultListItem(m_sdcResultCache);
		if(!m_bSearchResultEngouth){
			searchData.setSchedule(m_mapScheEngine);
			searchData.setResultList(getAllResult());
			searchData.setFilterEngines(m_setFilterEngName);
		}
		if(m_bIsLogin){
			if(!m_bHasClickRecomm) searchData.setClickRecommResultList(getClickRecomResult());
			if(!m_bHasQueryRecomm) searchData.setQueryRecommResultList(getQueryRecomResult());
		}
		ret=SearchDataBlackboard.addData(searchData);
		return ret;
	}
	
	private void relatedSearch(){
		
		//由于相关搜索没有什么特殊性，也很简单，
		//这里保留了原有的线程实现机制，没有改成Agent
		if(!m_bHasRelateSearch){
			RelateSearchThread relSThread = new RelateSearchThread(getRelatedSearch(), mDoneSignal, m_strQuery, m_sdcResultCache);
			relSThread.start();
		}else{
			getRelatedSearch().addAll(m_sdcResultCache.getRelateSearchResult());
		}
	}
	
	private void doSearch(DataToInterfaceAgent data) throws IOException, StaleProxyException, ControllerException, InterruptedException{
		
		if(!m_bSearchResultEngouth){
			data.setTransactionType(InterfaceAgentTxType.search);
			MyBaseGWBehaviour b=GatewayBehaviourUtil.getBaseBehaviour(data);
			JadeGateway.execute(b);
		}else{
			//搜索缓存中的数量已经足够了，不再向搜索Agent发消息
			getAllResult().addAll(m_sdcResultCache.getSearchResultList());
		}
	}
	
	private void queryRecomm(DataToInterfaceAgent data) throws IOException, StaleProxyException, ControllerException, InterruptedException{
		
		if(!m_bHasQueryRecomm){
			data.setTransactionType(InterfaceAgentTxType.queryRecomm);
			MyBaseGWBehaviour bq=GatewayBehaviourUtil.getBaseBehaviour(data);
			JadeGateway.execute(bq);
		}else{
			getQueryRecomResult().addAll(m_sdcResultCache.getQueryRecommResult());
		}
	}
	
	private void clickRecomm(DataToInterfaceAgent data) throws IOException, StaleProxyException, ControllerException, InterruptedException{
		
		if(!m_bHasClickRecomm){
			data.setTransactionType(InterfaceAgentTxType.clickRecomm);
			MyBaseGWBehaviour bcr=GatewayBehaviourUtil.getBaseBehaviour(data);
			JadeGateway.execute(bcr);
		}else{
			getClickRecomResult().addAll(m_sdcResultCache.getClickRecommResult());
		}
	}
	
	private void waitForDone(){
		
		if(null!=mDoneSignal)
			try {
				mDoneSignal.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
	
	/**
	 * 根据是否指定按引擎筛选，将搜索线程返回的搜索结果填到最终的列表中
	 */
	
	private void setSearchResults(){
		
		List<Result> tmpRlist = null;
		
		if (m_bHasFilter) {
			//按引擎筛选时，先把无关的结果去除
			tmpRlist = new ArrayList<Result>();
			Iterator<Result> iterAllRes = getAllResult().iterator();
			
			while (iterAllRes.hasNext()) {
				Result re = iterAllRes.next();
				if (re.isFromTargetEngine(m_setFilterEngName))
					tmpRlist.add(re);
			}
		} else {
			tmpRlist = allResult;
		}
		
		int startIndex = (m_nPage - 1) * NUM, outOfEndIndex = startIndex + NUM, resAmount=tmpRlist.size();
		if(outOfEndIndex>=resAmount){
			outOfEndIndex=resAmount;
			if(outOfEndIndex<startIndex){
				m_nPage=resAmount/NUM+1;
				startIndex=(m_nPage-1)*NUM;
			}
		}
		
		List<Result> shownLs=getResults();
		if(m_nPage>1){
			//do nothing
		}else{
			shownLs.addAll(getClickRecomResult());
		}
		getClickRecomResult().clear();
		shownLs.addAll(tmpRlist.subList(startIndex, outOfEndIndex));
	}
	
	private void setQueryRecomResults() {

		try {
			if (null != m_lsQueryRecomResult) {
				LinkedList<RecommQueryAndPercent> ls = (LinkedList<RecommQueryAndPercent>) m_lsQueryRecomResult;// 这里假定了是链表形式，没有处理其他可能的List
				while (ls.size() > QUERY_RECOMM_LIST_COUNT) {
					ls.removeLast();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setClickRecomResults(){

		try {
			if (null != m_lsClickRecommResult) {
				LinkedList<Result> ls = (LinkedList<Result>) m_lsClickRecommResult;// 这里假定了是链表形式，没有处理其他可能的List
				while (ls.size() > CLICK_RECOMM_LIST_COUNT) {
					ls.removeLast();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setRelateSearchContent(){
		
		try {
			if (null != m_lsRelateSearchResult) {
				LinkedList<String> ls = (LinkedList<String>) m_lsRelateSearchResult;// 这里假定了是链表形式，没有处理其他可能的List
				while (ls.size() > RELATE_SEARCH_LIST_COUNT) {
					ls.removeLast();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
