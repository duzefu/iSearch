package struts.actions.android;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import common.functions.userinterest.UserClickLogger;
import common.functions.userinterest.UserInterestModel;
import db.dao.UserDao;

/**
 * 记录用户在移动端的点击记录
 * 
 * @return
 * @throws Exception
 */
public class AndroidUserBehaviorAction {

	// UserDao
	private UserDao userDao;

	// query是用户查询的关键词
	private String query;
	private String title;
	private String abstr;
	private String sources;
	private String address;
	private String imei;
	private String username;

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

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	/**
	 * 记录用户在移动端的点击记录
	 * 
	 * @return
	 * @throws Exception
	 */
	public String execute() throws Exception {
		
		if(null==username||username.isEmpty()) return null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Calendar cal = Calendar.getInstance();
		Date datenow = cal.getTime();
		String date = df.format(datenow);
		int userid = this.getUserID();
		if (userid <= 0)
			return null;
		
		UserClickLogger idb = new UserClickLogger();
		idb.record(userid, query, title, abstr, address, date, sources);
		return null;
	}

	private int getUserID() {

		int ret = -1;

		if (username != null && !username.equalsIgnoreCase("")) {
			if (userDao.findUserByUsername(username).size() > 0) {
				ret = userDao.findUserByUsername(username).get(0).getUid();
			}
		} else if (null != imei && !"".equals(imei)) {
			ret = userDao.findUserByImei(imei).get(0).getUid();
		}

		return ret;
	}
}
