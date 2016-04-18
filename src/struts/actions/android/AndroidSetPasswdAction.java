package struts.actions.android;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.json.JSONObject;

import server.commonutils.RandomToUidForPasswdReset;
import server.info.entites.transactionlevel.UserEntity;
import common.functions.emailutil.SendMail;
import db.dao.UserDao;

public class AndroidSetPasswdAction {

	private HttpServletResponse response = null;
	PrintWriter out = null;
	ObjectOutputStream oos = null;

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	private UserDao userDao;

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	private String userid;
	private String sessionid;
	private String password;
	
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getSessionid() {
		return sessionid;
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String passwd) {
		this.password = passwd;
	}

	private String mResult;
	private int mUid;
	
	public String execute() throws Exception {
		response = ServletActionContext.getResponse();
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		JSONObject objJson = new JSONObject();
		try {
			mResult = "fail";
			if (null == sessionid || sessionid.isEmpty()||null==password||password.isEmpty())
				return null;
			
			try{
			mUid=Integer.parseInt(userid);
			}catch(Exception e){
				mUid=0;
			}
			if(mUid<=0) return null;

			Integer orgUid=RandomToUidForPasswdReset.getData(sessionid);
			if(null==orgUid||mUid!=orgUid.intValue()) return null;
			
			if(userDao.setPasswd(mUid,password)){
				mResult="success";
				RandomToUidForPasswdReset.rmData(sessionid);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		objJson.put("status", mResult);
		out.print(objJson.toString());
		out.flush();
		out.close();
		return null;
	}
}
