package struts.actions.android;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.json.JSONObject;

import server.commonutils.MyStringChecker;
import server.info.entites.transactionlevel.UserEntity;
import common.textprocess.userXMLfilehelpler.UserXMLHelper;
import db.dao.UserDao;
import db.dbhelpler.UserHelper;

/**
 * 安卓用户注册
 * 
 * @return
 */
public class AndroidUserRegisterAction {

	PrintWriter out = null;
	ObjectOutputStream oos = null;
	private HttpServletResponse response = null;

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	private UserEntity user;

	private UserDao userDao;

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	private String m_strUsername;
	private String m_strPasswd;
	private String m_strEmail;
	private String m_strImei;
	
	/**
	 * 用户注册
	 * 
	 * @return
	 */
	public String execute() throws Exception {
		response = ServletActionContext.getResponse();
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		JSONObject objJson = new JSONObject();
		String output = "LackInfo";
		// 验证username,email,cookieid是否已存在
		try {
			extractRequestInfo();
			if(!registInfoEnough()) return null;

			if (UserHelper.isExistE(m_strEmail)) {
				output = "Fail_cause_of_Email";
			} else if (UserHelper.isExistU(m_strUsername)) {
				output = "Fail_cause_of_Username";
			} else {
				userDao.add(user);
				output = "registerSuccess";
				try{
				UserXMLHelper.getInstance().createUserXMLFile(
						String.valueOf(user.getUid()));
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			output = "Exception";
		} finally {
			objJson.put("result", output);
			out.print(objJson.toString());
			out.flush();
			out.close();
		}
		
		return null;
	}

	private void extractRequestInfo(){
		m_strEmail=user.getEmail();
		m_strUsername=user.getUsername();
		m_strPasswd=user.getPassword();
		m_strImei=user.getImei();
	}
	
	/**
	 * 登录信息中，用户名与密码是必须存在的
	 * 
	 * @return
	 */
	private final boolean registInfoEnough() {

		return !MyStringChecker.isBlank(m_strUsername)
				&& !MyStringChecker.isBlank(m_strPasswd) && !MyStringChecker.isBlank(m_strPasswd);
	}
}