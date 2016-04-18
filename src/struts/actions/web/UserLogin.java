package struts.actions.web;

import jade.wrapper.gateway.JadeGateway;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.CountDownLatch;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.json.JSONException;
import org.json.JSONObject;

import common.entities.blackboard.UserAgentThread;
import server.commonutils.LogU;
import server.commonutils.MyStringChecker;
import server.info.config.LangEnvironment;
import server.info.config.SessionAttrNames;
import server.info.config.VisibleConstant;
import server.info.config.LangEnvironment.ClientType;
import server.info.config.LangEnvironment.LangEnv;
import server.info.config.MyEnums.UserLoginResult;
import server.info.config.VisibleConstant.ContentNames;
import agent.behaviours.gateway.MyBaseGWBehaviour;
import agent.data.inblackboard.GroupDivideData;
import agent.data.inblackboard.LoginData;
import agent.data.inmsg.DataToInterfaceAgent;
import agent.data.inmsg.TransactionType.InterfaceAgentTxType;
import agent.entities.blackboard.GroupDivideDataBlackboard;
import agent.entities.blackboard.LoginDataBlackboard;
import agent.utils.GatewayBehaviourUtil;
import db.dao.UserDao;
import db.dbhelpler.UserHelper;

public class UserLogin {

	/*
	 * 字符串常量，是返回时使用的json的key，要与register.js中取JSON值时使用的key相同
	 */
	private final static String JSKEY_RESULT="result";
	private final static String JSKEY_FAIL_REASON="reason";
	
	// 登录数据黑板中的数据对象
	private LoginData data;
	// 用于初始化data中的线程锁，表示最后要等待1个线程（LoginProcessAgent）执行结束
	private final static int DONE_SIG_INIT_VAL = 1;
	
	private GroupDivideData dataGP;

	// 这两个值是被struts2通过它们的set函数自动赋值的
	private String username;
	private String password;
	private LangEnv m_enuLang;
	
	private UserLoginResult m_enuResult;
	private int m_nUserid;
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setLang(String lang){
		WebParamParser.parseLang(lang, ServletActionContext.getRequest().getSession());
	}
	
	/**
	 * 获得封装了登录数据的对象（成员变量 ，如果没有初始化就会生成一个）
	 * 
	 * @return
	 */
	private LoginData getData() {

		if (null == data) {
			CountDownLatch doneSig = new CountDownLatch(DONE_SIG_INIT_VAL);
			data = new LoginData(doneSig, username, password);
		}

		return data;
	}

	/**
	 * 获得封装了登录数据的对象（成员变量 ，如果没有初始化就会生成一个）
	 * 
	 * @return
	 */
	private GroupDivideData getDataGP() {

		if (null == dataGP) {
			CountDownLatch doneSig = new CountDownLatch(DONE_SIG_INIT_VAL);
			dataGP = new GroupDivideData(doneSig,m_nUserid);
		}

		return dataGP;
	}

	public void execute() throws Exception {

		try{
		checkClientParams();
		if(null==m_enuResult){
			getData();
			int blackboardIndex = saveDataToBlackboard();
			if (!LoginDataBlackboard.isValidIndex(blackboardIndex)) return;

			// 生成Gateway Agent的行为并添加到其行为队列
			DataToInterfaceAgent dataForGWAgent = new DataToInterfaceAgent(
					blackboardIndex, InterfaceAgentTxType.login);
			MyBaseGWBehaviour behaviour = GatewayBehaviourUtil
					.getBaseBehaviour(dataForGWAgent);
			JadeGateway.execute(behaviour);
			// 等待相关Agent完成工作
			data.waitForDone();
			
			m_enuResult=data.getResult();
			m_nUserid=data.getUserid();
			
			LoginDataBlackboard.removeData(blackboardIndex);
			
			groupDivide();
		}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sendResponse();
		}
	}

	/**
	 * 初步检查客户端参数，结果没有问题，m_enuResult的值不会被改变，否则被设置了检查结果
	 */
	private void checkClientParams(){
		if(MyStringChecker.isBlank(username)) m_enuResult=UserLoginResult.no_exist_user;
		else if(MyStringChecker.isBlank(password)) m_enuResult=UserLoginResult.error_passwd;
	}
	
	private void groupDivide(){
		try{
		//划分群组用户关系，不用上面的方法，这次试用Agent来做
		getDataGP();
		// 数据存放到黑板中
		int blackboardIndexGP = saveDataToBlackboardGP();
		if (!GroupDivideDataBlackboard.isValidIndex(blackboardIndexGP))
			return;
		// 生成Gateway Agent的行为并添加到其行为队列
		DataToInterfaceAgent dataForGWAgent = new DataToInterfaceAgent(blackboardIndexGP, InterfaceAgentTxType.groupDivide);
		MyBaseGWBehaviour behaviour = GatewayBehaviourUtil.getBaseBehaviour(dataForGWAgent);
		JadeGateway.execute(behaviour);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 发送响应
	 */
	private void sendResponse() {

		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = null;
			out = response.getWriter();
			out.print(getResponseContent());
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获得要发送给网页的数据，是json的字符串格式
	 * @return 将res按JSON对象封装之后转成的字符串
	 */
	private String getResponseContent() {

		if(null==m_enuLang) setLang(null);//有时候发送请求时不带有lang=xxx，就会导致这个成员变量还没有设置好，这里主动设置一次
		JSONObject joRes = new JSONObject();
		try {
			switch (m_enuResult) {
			case success:
				HttpSession session = ServletActionContext.getRequest()	.getSession();
				session.setAttribute(SessionAttrNames.USERNAME_ATTR, username);
				joRes.put(JSKEY_RESULT, "success");
				break;
			case no_exist_user:
				joRes.put(JSKEY_FAIL_REASON, VisibleConstant.getWebpageContent(ContentNames.loginfail_error_username, m_enuLang));
				break;
			case error_passwd:
				joRes.put(JSKEY_FAIL_REASON, VisibleConstant.getWebpageContent(ContentNames.loginfail_error_passwd, m_enuLang));
				break;
			default:
				break;
			}
			JsDataBundler.getJsonWhileLogin(joRes, m_enuLang);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return joRes.toString();
	}

	/**
	 * 把数据存放到黑板中
	 * 
	 * @return 数据在黑板中的索引
	 */
	private int saveDataToBlackboard() {

		int ret = -1;
		if (null == data) return ret;
		ret = LoginDataBlackboard.addData(data);
		return ret;

	}
	
	/**
	 * 把数据存放到黑板中
	 * 
	 * @return 数据在黑板中的索引
	 */
	private int saveDataToBlackboardGP() {

		int ret = -1;
		if (null == dataGP) return ret;
		ret = GroupDivideDataBlackboard.addData(dataGP);
		return ret;

	}
}
