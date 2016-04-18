package struts.actions.android;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.apache.log4j.Logger;

import server.engine.api.Baidu;
import db.dao.UserDao;

public class AndroidRelatedSearchAction {

	private HttpServletResponse response;
	private HttpServletRequest request;
	private String query;
	private UserDao userDao;

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String[] getRelatedSearch() {
		return relatedSearch;
	}

	public void setRelatedSearch(String[] relatedSearch) {
		this.relatedSearch = relatedSearch;
	}

	private String[] relatedSearch = null;

	public String execute() throws Exception {
		response = ServletActionContext.getResponse();
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		JSONObject objJson = new JSONObject();
		Baidu b = new Baidu();
		List<String> result=new LinkedList<String>();
		int amount=b.getRelatedSearch(result, query);
		JSONArray array = new JSONArray();
		if(amount>0){
			
			int i=0;
			for(Iterator<String> it=result.iterator();it.hasNext();){
				JSONObject elem = new JSONObject();
				elem.put("word", it.next());
				array.put(elem);
			}
		}
		objJson.put("relateWords", array);
		out.print(objJson.toString());
		out.flush();
		out.close();
		return null;
	}

}
