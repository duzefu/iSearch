package struts.actions.android;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.text.DecimalFormat;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.org.apache.bcel.internal.generic.NEW;

import common.functions.webpagediagram.CoolDynamicBar;
import db.dao.UserDao;
import db.dbhelpler.InterestVo;
import db.dbhelpler.UserInterestValueHelper;

public class AndroidUserInterestAction {

	static Logger log = Logger.getLogger("interest");
	private HttpServletResponse response;
	private String username;
	private String userid;
	private String imei;
	private ArrayList<Entry<String, Double>> userInterest = new ArrayList<Map.Entry<String, Double>>();
	private HashMap<String, Double> interest = new HashMap<String, Double>();
	private UserDao userDao;

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public ArrayList<Entry<String, Double>> getUserInterest() {
		return userInterest;
	}

	public void setUserInterest(ArrayList<Entry<String, Double>> userInterest) {
		this.userInterest = userInterest;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String execute() throws Exception {
		if (username != null && !username.equals("")) {
			// 判断用户是否为登陆用户
			if (userDao.findUserByUsername(username) != null) {
				if (userid.equalsIgnoreCase(String.valueOf(userDao
						.findUserByUsername(username).get(0).getUid()))) {
					// 获取用户的兴趣变迁
					List<InterestVo> interests = UserInterestValueHelper
							.getUserInterestValIns(Integer.parseInt(userid), 1);
					// userInterest = (ArrayList<Entry<String, Double>>)
					// CoolDynamicBar.GetDataset(Integer.parseInt(userid),username);
					double sum = 0;
					for (int i = 0; i < interests.size(); i++) {
						InterestVo vo = interests.get(i);
						sum = sum + vo.getValue();
					}
					response = ServletActionContext.getResponse();
					response.setCharacterEncoding("UTF-8");
					PrintWriter out = response.getWriter();
					JSONObject objJson = new JSONObject();
					JSONArray array = new JSONArray();
					for (int i = 0; i < interests.size(); i++) {
						JSONObject elem = new JSONObject();
						InterestVo vo = interests.get(i);
						String key = vo.getName();
						Double value = vo.getValue();
						if (value < 0.01) {
							continue;
						}
						if ("".equals(key)) {
							elem.put("key", "other");
						} else {
							elem.put("key", key);
						}
						elem.put("value", djiewein(value / sum, 2));
						array.put(elem);
					}
					objJson.put("result", array);
					out.print(objJson.toString());
					out.flush();
					out.close();
				}
			}
			// 用户名不存在
		}
		return null;
	}

	// 截取double到小数点后n位
	protected double djiewein(double data, int n) {
		String ff = "#0.";
		for (int i = 0; i < n; i++) {
			ff = ff + "#";
		}
		DecimalFormat df = new DecimalFormat(ff);
		return Double.valueOf(df.format(data));
	}
}
