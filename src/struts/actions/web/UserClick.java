package struts.actions.web;

import jade.wrapper.gateway.JadeGateway;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import server.commonutils.LogU;
import server.info.config.SessionAttrNames;
import agent.behaviours.gateway.MyBaseGWBehaviour;
import agent.data.inblackboard.ClickLogData;
import agent.data.inmsg.DataToInterfaceAgent;
import agent.data.inmsg.TransactionType.InterfaceAgentTxType;
import agent.entities.blackboard.ClickLogDataBlackboard;
import agent.entities.blackboard.RegistDataBlackboard;
import agent.utils.GatewayBehaviourUtil;

import com.opensymphony.xwork2.ActionSupport;

import common.functions.userinterest.UserClickLogger;
import db.dao.UserDao;

public class UserClick extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private UserDao userDao;
	private String query;
	private String title;
	private String abstr;
	private String clickaddress;
	private String sources;
	private final static int DONE_SIG_INIT_VAL = 1;
	private ClickLogData data;
	private ClickLogData getData(int userId,String date) {
		
		if (null == data) {
			CountDownLatch doneSig = new CountDownLatch(DONE_SIG_INIT_VAL);
			data = new ClickLogData(doneSig,userId, query, title, clickaddress,date,sources,abstr);
		}
		return data;
	}
	/**
	 * 用户点击后执行，记录点击信息
	 * 
	 * @throws IOException
	 */
	public String execute() throws Exception {

		// 获取用户名
		
		HttpSession session = ServletActionContext.getRequest().getSession();
		String usernameinpage = (String) session.getAttribute(SessionAttrNames.USERNAME_ATTR);
		// 获取用户ID——即使未登录用户也获取了1
		int userid = 1;
		if (usernameinpage != null) {
			if (userDao.findUserByUsername(usernameinpage).size() > 0) {
				userid = userDao.findUserByUsername(usernameinpage).get(0)
						.getUid();
			}
		} 
		String responseText = "{'flag':'true','url':'" + clickaddress + "'}";
		sendResponse(responseText);
		
		// 到这里，页面完成了跳转
		// clickaddress=java.net.URLDecoder.decode(clickaddress,"gbk");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Calendar cal = Calendar.getInstance();
		Date datenow = cal.getTime();
		String date = df.format(datenow);
		getData(userid,date);
		int blackboardIndex = saveDataToBlackboard();// 将data存放到黑板里
		if (!ClickLogDataBlackboard.isValidIndex(blackboardIndex)) {
			// do nothing
		} else {
			DataToInterfaceAgent dataForGWAgent=new DataToInterfaceAgent(blackboardIndex, InterfaceAgentTxType.clicklog);
			MyBaseGWBehaviour behaviour=GatewayBehaviourUtil.getBaseBehaviour(dataForGWAgent);
			JadeGateway.execute(behaviour);
			data.waitForDone();
		}
		ClickLogDataBlackboard.removeData(blackboardIndex);
		return null;
	}
	
	/**
	 * 发送响应
	 * @param respText 要响应的字符串
	 */
	private void sendResponse(String respText) {
		HttpSession session = ServletActionContext.getRequest().getSession();
		HttpServletResponse res = ServletActionContext.getResponse();
		res.reset();
		res.setContentType("text/html;charset=utf-8");
		PrintWriter pw = null;
		try {
			pw = res.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		pw.print(respText);

		pw.flush();
		pw.close();
	}
	private int saveDataToBlackboard() {

		int ret = -1;
		if (null == data)
			return ret;
		ret = ClickLogDataBlackboard.addData(data);
		return ret;

	}
	
	public String getSources() {
		return sources;
	}

	public void setSources(String sources) {
		this.sources = sources;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAbstr() {
		return abstr;
	}

	public void setAbstr(String abstr) {
		this.abstr = abstr;
	}

	public String getClickaddress() {
		return clickaddress;
	}

	public void setClickaddress(String clickaddress) {
		this.clickaddress = clickaddress;
	}

}// end class