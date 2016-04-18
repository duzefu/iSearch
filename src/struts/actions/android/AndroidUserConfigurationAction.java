package struts.actions.android;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.json.JSONObject;

import common.entities.blackboard.UserAgentThread;
import server.commonutils.Md5;
import server.info.entites.transactionlevel.UserEntity;
import db.dao.UserDao;
import db.hibernate.tables.isearch.User;

/**
 * 验证用户登陆
 * 
 * @return
 */
public class AndroidUserConfigurationAction {

	
	private final static String LOGIN_SUCCESS="authenticationSuccess";
	private final static String INTERNAL_FAIL="serverFail";
	private final static String USERNAME_NOT_EXIST="errorUsername";
	private final static String INCORRECT_PASSWD="errorPasswd";
	
	private HttpServletResponse response = null;

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	// UserDao
	private UserDao userDao;

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	// 用户名
	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	// 用户密码
	private String password;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	// 用户IEMI
	private String imei;

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	/**
	 * 验证用户登陆
	 * 
	 * @return
	 */
	public String execute() throws Exception {
		List<String> loginInfo = new ArrayList<String>();
		String ret=null, result = INTERNAL_FAIL, strUserid=null, strUsername=null;
		try {
			List<UserEntity> login = userDao.findUserByUsername(username);

			if(null==login||login.isEmpty()){
				result=USERNAME_NOT_EXIST;
				return ret;
			}			
			String newpwd = Md5.encrypt(password);
			
				for (int i = 0; i < login.size(); i++) {
					String dbpasswd = login.get(i).getPassword();
					if (null != dbpasswd && !dbpasswd.isEmpty()
							&& dbpasswd.equals(newpwd)) {
						result = LOGIN_SUCCESS;
						strUserid=String.valueOf(login.get(i).getUid());
						strUsername=login.get(i).getUsername().toString();
						UserAgentThread uat = new UserAgentThread();
						uat.setUserThreadId(login.get(0).getUid());
						uat.start();
					}else{
						result=INCORRECT_PASSWD;
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			loginInfo.add(result);
			if(null!=strUserid){
				loginInfo.add(strUserid);
				loginInfo.add(strUsername);
			}
			response = ServletActionContext.getResponse();
			response.setCharacterEncoding("utf-8");
			PrintWriter out = response.getWriter();
			JSONObject jsonObject = new JSONObject();
			
			JSONObject elem = new JSONObject();
			elem.put("strUserid", strUserid);
			elem.put("strUsername", strUsername);
			
			jsonObject.put("reCode", result);
			jsonObject.put("loginInfo", elem);
			out.print(jsonObject.toString());
			out.flush();
			out.close();
		}
		return ret;
	}
}