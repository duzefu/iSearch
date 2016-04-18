package struts.actions.android;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;

import server.commonutils.HotwordsUtil;
import server.engine.api.EngineFactory;
import server.info.entites.transactionlevel.UserEntity;
import db.dao.UserDao;

/**
 * 用户打开客户端后传递实时热点信息
 * 
 * @return
 */
public class AndroidRealtimeHotAction {

	private HttpServletRequest request = null;
	private HttpServletResponse response = null;
	PrintWriter out = null;

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	// userDao
	private UserDao userDao;

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	// 设备的IMEI号
	private String imei;

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	/**
	 * 用户打开客户端后传递实时热点信息
	 * 
	 * @return
	 */
	public String execute() throws Exception {
		response = ServletActionContext.getResponse();
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		JSONObject jsonObject = new JSONObject();
		List<String> words=new ArrayList<String>();
		int count, amount=HotwordsUtil.getHotwords(words);
		amount=amount>6?6:amount;
		List<String> respWords = new ArrayList<String>(amount);
		count=0;
		for(Iterator<String> it=words.iterator();it.hasNext();){
			String word=it.next();
			respWords.add(word);
			++count;
		}
		Set<String> enames=new HashSet<String>();
		enames.addAll(EngineFactory.getAllEngineCnName());
		JSONArray hotWordList = new JSONArray();
		JSONArray engineList = new JSONArray();
		for (int i = 0; i < respWords.size(); i++) {
			JSONObject elem = new JSONObject();
			elem.put("name", respWords.get(i));
			hotWordList.add(elem);
		}
		
		for (String element : enames) {
			JSONObject elem = new JSONObject();
			elem.put("name", element);
			engineList.add(elem);
		}
		jsonObject.put("hotWords", hotWordList);
		jsonObject.put("enames", engineList);
		out.print(jsonObject.toString());
		out.flush();
		out.close();
		return null;
	}
}
