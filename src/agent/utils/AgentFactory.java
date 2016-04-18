package agent.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javassist.runtime.Desc;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import server.commonutils.MyStringChecker;
import server.info.config.ConfigFilePath;
import server.info.config.agent.CommonAgentNames;
import server.info.config.agent.PathOfAgentsClass;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import jade.wrapper.gateway.JadeGateway;
import agent.containers.MainContainer;

/**
 * Agent的初始化生成（服务器启动时）、后续生成、结束等功能都集中在这个类实现，便于管理
 * 减少：
 * 		1) 让有交互关系的Agent去生成别的Agent，然后在生成的时候静态的设置自己的消息接收者列表（AID[]）
 * 如果一个Agent需要找一些Agent作为他的消息接收者，应该通过平台中的DF服务来根据服务名称找到所有能够提供该服务的Agent
 * 在这里添加的Agent，其setup()函数中应该向DF注册服务；在takeDown()函数中应该取消注册，否则其他Agent无法找到它。
 * 具体参考JADE手册（for beginners）中，关于DF介绍的部分
 * @author zhou
 *
 */
public class AgentFactory {

	final static private String CONFIG_FILE_PATH=ConfigFilePath.getProjectRoot()+"WEB-INF/classes/agent-amount.xml";

	public static boolean doneAgentStartedInServerStartup=false;
	/*
	 * 各类Agent在服务器启动时默认创建的数量及名字
	 * 优先考虑文件：agent-amount.xml中设置的数量及名字
	 * 如果通过文件无法确定应该启动多少Agent，就用这些默认值
	 */
	private final static int LOGIN_AGENT_AMOUNT = 1;
	private final static int REGIST_AGENT_AMOUNT = 1;
	private final static int SEARCH_ENTRY_AGENT_AMONT=1;
	private final static int MEMBER_SEARCH_AGENT_AMONT=10;
	private final static int RESULT_MERGE_AGENT_AMOUNT=2;
	private final static int RESULT_RECOMM_AGENT_AMOUNT=1;
	private final static int QUERY_RECOMM_AGENT_AMOUNT=1;
	private final static int QUERY_GRECOMM_AGENT_AMOUNT=1;
	private final static int QUERY_QFGRECOMM_AGENT_AMOUNT=1;
	private final static int CLICK_LOG_AGENT_AMOUNT=5;
	private final static int GROUP_DIVIDE_AMOUNT = 1;
	
	private final static String[] LOGIN_AGENT_NAMES = new String[LOGIN_AGENT_AMOUNT];
	private final static String[] REGIST_AGENT_NAMES = new String[REGIST_AGENT_AMOUNT];
	private final static String[] SEARCH_ENTRY_AGENT_NAMES = new String[SEARCH_ENTRY_AGENT_AMONT];
	private final static String[] MEMBER_SEARCH_AGENT_NAMES = new String[MEMBER_SEARCH_AGENT_AMONT];
	private final static String[] RESULT_MERGE_AGENT_NAMES = new String[RESULT_MERGE_AGENT_AMOUNT];
	private final static String[] RESULT_RECOMM_AGENT_NAMES = new String[RESULT_RECOMM_AGENT_AMOUNT];
	private final static String[] QUERY_RECOMM_AGENT_NAMES = new String[QUERY_RECOMM_AGENT_AMOUNT];
	private final static String[] QUERY_GRECOMM_AGENT_NAMES = new String[QUERY_GRECOMM_AGENT_AMOUNT];
	private final static String[] QUERY_QFGRECOMM_AGENT_NAMES = new String[QUERY_QFGRECOMM_AGENT_AMOUNT];
	private final static String[] CLICK_LOG_AGENT_NAMES=new String[CLICK_LOG_AGENT_AMOUNT];
	private final static String[] GROUP_DIVIDE_NAMES = new String[GROUP_DIVIDE_AMOUNT];
	
	/*
	 * 初始化Agent的默认名字列表
	 */
	static {
		initLoginAgentDefaultNames();
		initMemberSearchAgentDefaultNames();
		initQueryGroupRecommAgentDefaultNames();
		initQueryQFGRecommAgentDefaultNames();
		initQueryRecommAgentDefaultNames();
		initRegisterAgentDefaultNames();
		initResultMergeAgentDefaultNames();
		initResultRecommAgentDefaultNames();
		initSearchAgentDefaultNames();
		initClickLogAgentDefaultNames();
		initGroupDivideAgentDefaultNames();
	}
	
	private static void initLoginAgentDefaultNames(){
		for (int i = 0; i < LOGIN_AGENT_AMOUNT; ++i) {
			LOGIN_AGENT_NAMES[i] = "login-agent-" + i;
		}
	}
	private static void initRegisterAgentDefaultNames(){
		for (int i = 0; i < REGIST_AGENT_AMOUNT; ++i) {
			REGIST_AGENT_NAMES[i] = "regist-agent-" + i;
		}
	}
	private static void initSearchAgentDefaultNames(){
		for (int i = 0; i < SEARCH_ENTRY_AGENT_AMONT; ++i) {
			SEARCH_ENTRY_AGENT_NAMES[i] = "search-entry-agent-" + i;
		}
	}
	private static void initMemberSearchAgentDefaultNames(){
		for (int i = 0; i < MEMBER_SEARCH_AGENT_AMONT; ++i) {
			MEMBER_SEARCH_AGENT_NAMES[i] = "member-search-agent-" + i;
		}
	}
	private static void initResultMergeAgentDefaultNames(){
		for (int i = 0; i < RESULT_MERGE_AGENT_AMOUNT; ++i) {
			RESULT_MERGE_AGENT_NAMES[i] = "result-merge-agent-" + i;
		}
	}
	private static void initResultRecommAgentDefaultNames(){
		for (int i = 0; i < RESULT_RECOMM_AGENT_AMOUNT;++i) {
			RESULT_RECOMM_AGENT_NAMES[i] = "result-recomm-agent-" + i;
		}
	}
	private static void initQueryRecommAgentDefaultNames(){
		for (int i = 0; i < QUERY_RECOMM_AGENT_AMOUNT;++i) {
			QUERY_RECOMM_AGENT_NAMES[i] = "query-recomm-agent-" + i;
		}
	}
	private static void initQueryGroupRecommAgentDefaultNames(){
		for (int i = 0; i < QUERY_GRECOMM_AGENT_AMOUNT;++i) {
			QUERY_GRECOMM_AGENT_NAMES[i] = "query-group-recomm-agent-" + i;
		}
	}
	private static void initQueryQFGRecommAgentDefaultNames(){
		for (int i = 0; i < QUERY_QFGRECOMM_AGENT_AMOUNT;++i) {
			QUERY_QFGRECOMM_AGENT_NAMES[i] = "query-qfg-recomm-agent-" + i;
		}
	}
	private static void initClickLogAgentDefaultNames(){
		for (int i = 0; i < CLICK_LOG_AGENT_AMOUNT;++i) {
			CLICK_LOG_AGENT_NAMES[i] = "click-logger-agent-" + i;
		}
	}
	
	private static void initGroupDivideAgentDefaultNames(){
		for (int i = 0; i < GROUP_DIVIDE_AMOUNT;++i) {
			GROUP_DIVIDE_NAMES[i] = "group-divide-agent-" + i;
		}
	}
	
	/*
	 * 下面几个数组在初始化Agent时使用，有默认的对应关系：
	 * 1) 数组的大小要一致；
	 * 	2) 内容是对应的，其中：
	 * 		a) 第一个数组是agent-amount.xml中，定义了各类Agent的数量及名字前缀的元素的名称；
	 * 		b) 第二个数组是各Agent类的路径；
	 * 		c) 第三个数组是各Agent的默认名称数组。
	 * 各数组相同位置上的元素要对应，否则初始化会出问题。
	 * 以后新增了Agent，只需要：
	 * 		a) 在agent-amount中添加一个结点；
	 * 		b) 在这个类增加Agent的默认数量、默认名字数组的定义；
	 * 		c) 在下面三个数组中各增加一个元素。
	 */
	final static private String[] ELEMENT_NAMES={
		"LoginAgent",
		"RegisterAgent",
		"ClickLogAgent",
		"SearchAgent",
		"MemberSearchAgent",
		"ResultMergeAgent",
		"QueryRecommAgent",
		"QueryGroupRecommAgent",
		"QueryQFGRecommAgent",
		"ResultRecommAgent",
		"GroupDivideAgent"
		};
	final static private String[] AGENT_CLASSPATH={
		PathOfAgentsClass.UserLoginAgent(),
		PathOfAgentsClass.UserRegistAgent(),
		PathOfAgentsClass.UserClickLogAgent(),
		PathOfAgentsClass.SearchEntryAgent(),
		PathOfAgentsClass.MemberSearchAgent(),
		PathOfAgentsClass.ResultMergeAgent(),
		PathOfAgentsClass.QueryRecommEntryAgent(),
		PathOfAgentsClass.QueryGroupRecommAgent(),
		PathOfAgentsClass.QueryQFGRecommAgent(),
		PathOfAgentsClass.ClickRecommAgent(),
		PathOfAgentsClass.GroupDivideAgent()
	};
	final static private String[][] AGENT_DEFAULT_NAMES={
		LOGIN_AGENT_NAMES,
		REGIST_AGENT_NAMES,
		CLICK_LOG_AGENT_NAMES,
		SEARCH_ENTRY_AGENT_NAMES,
		MEMBER_SEARCH_AGENT_NAMES,
		RESULT_MERGE_AGENT_NAMES,
		QUERY_RECOMM_AGENT_NAMES,
		QUERY_GRECOMM_AGENT_NAMES,
		QUERY_QFGRECOMM_AGENT_NAMES,
		RESULT_RECOMM_AGENT_NAMES,
		GROUP_DIVIDE_NAMES
	};
	
	/**
	 * 在服务器启动时统一调用，可以用来启动一些Agent。
	 * 根据classpath目录下的agent-amount.xml配置的数量及名字规则来初始化；
	 * 各类Agent独立初始化，某一类Agent初始化失败（配置文件不正确或没有写），则使用默认值
	 * @throws StaleProxyException 
	 */
	public static void createAgentOnServerStartup() throws StaleProxyException {

		ContainerController cc = MainContainer.get();
		// 初始化Gateway Agent（由非Agent部分到Agent部分的消息转发入口，全局唯一）
		JadeGateway.init(PathOfAgentsClass.GatewayAgent(), null);
		Document doc=readConfigFile();
		Element root=null;
		if(null!=doc) root=doc.getRootElement();
		createInterfaceAgent(cc);
		AgentInitDescriptor desc=new AgentInitDescriptor();
		desc.setAgentArgs(null);
		for(int i=0;i<ELEMENT_NAMES.length;++i){
			desc.setElementName(ELEMENT_NAMES[i]);
			desc.setAgentClassPath(AGENT_CLASSPATH[i]);
			desc.setAgentDefaultNames(AGENT_DEFAULT_NAMES[i]);
			configAgentNames(desc,root);
			createAgent(cc,desc);
		}
		
		doneAgentStartedInServerStartup = true;
	}
	
	private static void configAgentNames(AgentInitDescriptor desc, Element root){
		
		if(null==desc) return;
		if(null==root) desc.setAgentNames(desc.getAgentDefaultNames());
		
		try{
		String eleName=desc.getElementName();
		Element ele=root.element(eleName);
		int amount=getAgentAmount(ele);
		String prefix=getAgentNamePrefix(ele);
		if(amount>0&&!MyStringChecker.isBlank(prefix)){
			String[] names=new String[amount];
			for(int i=0;i<amount;++i){
				names[i]=prefix+i;
			}
			desc.setAgentNames(names);
		}else{
			desc.setAgentNames(desc.getAgentDefaultNames());
		}
		}catch(Exception e){
			desc.setAgentNames(desc.getAgentDefaultNames());
		}
		
		return;
	}
	
	private static void createAgent(ContainerController cc, AgentInitDescriptor desc){
		
		if(null==cc||null==desc) return;
		
		String[] agentNames=desc.getAgentNames();
		String agentPath=desc.getAgentClassPath();
		Object[] agentArgs=desc.getAgentArgs();
		if(null==agentNames) return;
		for(int i=0;i<agentNames.length;++i){
			try {
				cc.createNewAgent(agentNames[i], agentPath, agentArgs).start();
			} catch (StaleProxyException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static Document readConfigFile(){
		
		Document ret=null;
		SAXReader sax = new SAXReader();
		File file = new File(CONFIG_FILE_PATH);
		try {
			ret = sax.read(file);
		}catch (Exception e) {
				ret=null;
		}
		return ret;
	}
	
	/**
	 * 从配置文件中读取应该创建的Agent的数量
	 * @param ele xml文件的元素，具体要定位到agent-amount.xml文件中的xxxAgent元素级别
	 * @return ele元素的下级元素中，Amount元素的内容
	 */
	private static int getAgentAmount(Element ele) {

		int ret = 0;

		try {
			if (null != ele)
				ret = Integer.parseInt(ele.element("Amount").getText());
		} catch (Exception e) {

		}

		return ret;
	}
	
	/**
	 * 从配置文件中读取应该创建的Agent名字的前缀
	 * @param ele xml文件的元素，具体要定位到agent-amount.xml文件中的xxxAgent元素级别
	 * @return ele元素的下级元素中，Prefix元素的内容
	 */
	private static String getAgentNamePrefix(Element ele){
		
		String ret = null;

		try {
			if (null != ele)
				ret = ele.element("Prefix").getText();
		} catch (Exception e) {

		}

		return ret;
		
	}
	
	private static void createInterfaceAgent(ContainerController cc)
			throws StaleProxyException {
		AgentController ac = cc.createNewAgent(
				CommonAgentNames.INTERFACE_AGENT,
				PathOfAgentsClass.InterfaceAgent(), null);
		ac.start();
	}
	
}