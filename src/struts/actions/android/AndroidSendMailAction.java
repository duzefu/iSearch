package struts.actions.android;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.aspectj.util.LangUtil.StringChecker;
import org.json.JSONObject;
import org.omg.CORBA.PRIVATE_MEMBER;

import server.commonutils.Md5;
import server.commonutils.RandomToUidForPasswdReset;
import server.info.entites.transactionlevel.UserEntity;

import com.ibm.icu.util.Calendar;

import common.functions.emailutil.SendMail;
import db.dao.UserDao;
import db.hibernate.tables.isearch.User;

/**
 * 发送邮件找回密码
 * 
 * @return
 * @throws Exception
 */
public class AndroidSendMailAction {

	private HttpServletResponse response = null;
	PrintWriter out = null;
	ObjectOutputStream oos = null;

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

	// 传送的email地址
	private String emailadress;

	public String getEmailadress() {
		return emailadress;
	}

	public void setEmailadress(String emailadress) {
		this.emailadress = emailadress;
	}

	// 设备IMEI号
	private String imei;

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	private String mResult;
	private String mRandomNumber;
	private List<String> mResponseMsg;
	private UserEntity mTargetUser;
	private String mCookie;
	
	private final static String RESP_NOT_REGISTER_EMAILADDR="notRegisterUser";
	private final static String RESP_SEND_SUCCESS="sendMailSuccess";
	private final static String RESP_SEND_FAIL="sendMailFail";
	
	/**
	 * 发送邮件找回密码
	 * 
	 * @return
	 * @throws Exception
	 */
	public String execute() throws Exception {
		response = ServletActionContext.getResponse();
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		JSONObject objJson = new JSONObject();
		try {
			mResult = RESP_SEND_FAIL;
			if (null == emailadress || emailadress.isEmpty()){
				mResult=RESP_NOT_REGISTER_EMAILADDR;
				return null;
			}

			List<UserEntity> ulist = userDao.findUserByEmailAdress(emailadress);
			if (null == ulist || ulist.isEmpty()){
				mResult=RESP_NOT_REGISTER_EMAILADDR;
				return null;
			}
			mTargetUser = ulist.get(0);

			if (sendEmailMsg()){
				mResult = "sendMailSuccess";
				String username=mTargetUser.getUsername();
				username=null==username?"":username;
				mCookie=generateCookieId(username);
				RandomToUidForPasswdReset.saveData(mCookie, mTargetUser.getUid());
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		} 
		objJson.put("result", mResult);
		if ("sendMailSuccess".equals(mResult)) {
			objJson.put("mRandomNumber", mRandomNumber);
			objJson.put("mTargetUserID", String.valueOf(mTargetUser.getUid()));
			objJson.put("mCookie", mCookie);
		}
		out.print(objJson.toString());
		out.flush();
		out.close();
		return null;
	}

	private boolean sendEmailMsg() {

		Random random = new Random();
		int rval = random.nextInt();
		if (Integer.MIN_VALUE == rval)
			rval += 1;
		rval = Math.abs(rval) % 100000 + 100000;
		mRandomNumber = String.valueOf(rval);
		return SendMail.send(mTargetUser, mRandomNumber);
	}
	
	public String generateCookieId(String username) {

		Date curDate = new Date();
		Random rand = new Random();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String date = df.format(curDate);
		String ret = Md5.encrypt(username+date);
		return ret.trim();
	}
}
