package struts.actions.web;

import jade.wrapper.gateway.JadeGateway;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.CountDownLatch;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.json.JSONObject;

import server.commonutils.LogU;
import server.commonutils.MyStringChecker;
import server.info.config.LangEnvironment;
import server.info.config.SessionAttrNames;
import server.info.config.VisibleConstant;
import server.info.config.LangEnvironment.ClientType;
import server.info.config.LangEnvironment.LangEnv;
import server.info.config.MyEnums.RegisterResult;
import server.info.config.VisibleConstant.ContentNames;
import agent.behaviours.gateway.MyBaseGWBehaviour;
import agent.data.inblackboard.RegistData;
import agent.data.inmsg.DataToInterfaceAgent;
import agent.data.inmsg.TransactionType.InterfaceAgentTxType;
import agent.entities.blackboard.RegistDataBlackboard;
import agent.utils.GatewayBehaviourUtil;
import db.dao.UserDao;

public class UserRegister {

	/*
	 * 字符串常量，是返回时使用的json的key，要与register.js中取JSON值时使用的key相同
	 */
	private final static String JSKEY_RESULT="result";
	private final static String JSKEY_FAIL_REASON="reason";
	
	private String username;
	private String password;
	private String emailaddress;

	// 其中doneSigVal用于初始化data中的CountDownLatch
	// doneSigVal也等于由execute函数直接启动并等待的线程数（实际上是Agent）
	private final static int DONE_SIG_INIT_VAL = 1;
	private RegistData data;

	private RegisterResult m_enuResult;
	
	private RegistData getData() {

		if (null == data) {
			CountDownLatch doneSig = new CountDownLatch(DONE_SIG_INIT_VAL);
			data = new RegistData(doneSig, username, password, emailaddress);
		}
		return data;
	}

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

	public String getEmailaddress() {
		return emailaddress;
	}

	public void setEmailadress(String emailadress) {
		this.emailaddress = emailadress;
	}

	public String execute() throws Exception {
		
//		Cookie[] cookies = ServletActionContext.getRequest().getCookies();
//		for (Cookie ck : cookies) {//拿到本请求的cookie
//			if (ck.getName().equalsIgnoreCase("iSearchCookie")) {
//				cookieid = ck.getValue();
//			}
//		}
		
		checkClientParams();
		if(null==m_enuResult){
			getData();
			int blackboardIndex = saveDataToBlackboard();//将data存放到黑板里
			if (!RegistDataBlackboard.isValidIndex(blackboardIndex)) {
				// do nothing
			}else{
				DataToInterfaceAgent dataForGWAgent=new DataToInterfaceAgent(blackboardIndex, InterfaceAgentTxType.regist);
				MyBaseGWBehaviour behaviour=GatewayBehaviourUtil.getBaseBehaviour(dataForGWAgent);
				JadeGateway.execute(behaviour);
				data.waitForDone();
				m_enuResult=data.getRegisterResult();
			}
			RegistDataBlackboard.removeData(blackboardIndex);
		}
		
		LangEnv lang=(LangEnv)ServletActionContext.getRequest().getSession().getAttribute(SessionAttrNames.LANG_ATTR);
		if(null==lang) lang=LangEnvironment.currentEnv(ClientType.web);
		JSONObject respJson=new JSONObject();
		switch (m_enuResult) {
		case success:
			respJson.put(JSKEY_RESULT, "success");
			break;
		case illegal_info:
			respJson.put(JSKEY_FAIL_REASON, VisibleConstant.getWebpageContent(ContentNames.regfail_info_error, lang));
			break;
		case username_exit:
			respJson.put(JSKEY_FAIL_REASON, VisibleConstant.getWebpageContent(ContentNames.regfail_exist_name, lang));
			break;
		case email_exist:
			respJson.put(JSKEY_FAIL_REASON, VisibleConstant.getWebpageContent(ContentNames.regfail_exist_email, lang));
		default:
			break;
		}
		JsDataBundler.getJsonWhileRegister(respJson, lang);
		response(respJson.toString());
		return null;
	}

	/**
	 * 检查客户端传来的参数，检查的结果保存在m_enuResult成员变量中；
	 * 如果没有错误，m_enuResult的值与调用本函数前一样
	 */
	private void checkClientParams(){
		checkEmail();
		checkUserName();
		checkPasswd();
	}
	
	private void checkEmail(){
		//邮箱暂时只做简单检查
		if(MyStringChecker.isBlank(emailaddress)||emailaddress.contains(" ")) m_enuResult=RegisterResult.illegal_info;
	}
	
	private void checkUserName(){
		
		if(MyStringChecker.isBlank(username)||username.contains(" ")) m_enuResult=RegisterResult.illegal_info;
	}
	
	private void checkPasswd(){
		if(MyStringChecker.isBlank(password)||password.contains(" ")) m_enuResult=RegisterResult.illegal_info;
	}
	
	private int saveDataToBlackboard() {

		int ret = -1;
		if (null == data)
			return ret;
		ret = RegistDataBlackboard.addData(data);
		return ret;

	}

	private void response(String content){
		
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}

		out.print(content);
		out.flush();
		out.close();
	}
}
