package struts.actions.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import server.engine.api.SogoPic;
import server.info.config.SessionAttrNames;

import com.opensymphony.xwork2.ActionSupport;
import common.utils.querypreprocess.DealingWithQuery;

public class PictureSearchAction extends ActionSupport {

	private String query;// query就是用户查询的关键词
	private String page;// page是用户请求的页面数
	private int lastPage;// lastPage是每页显示NUM条数据时的页数
	private int items;// item是一共得到的数目
	public List<String> results = new ArrayList<String>();// Results是请求页面的list

	private int userid;

	/**
	 * 点击搜索按钮后执行，负责获取搜索结果展示页面所需要的所有数据
	 * 
	 * @return： 表示搜索执行的结果情况的字符串
	 */
	public String execute() throws Exception {

		// 获取网页上的用户名
		HttpSession session = ServletActionContext.getRequest().getSession();
		String usernameinpage = (String) session.getAttribute(SessionAttrNames.USERNAME_ATTR);
		results = SogoPic.getResults(results, query);

		return SUCCESS;
	}// end execute

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public int getItems() {
		return items;
	}

	public void setItems(int items) {
		this.items = items;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		// this.query = query;
		/*
		 * modified by jtr 2014-5-6 解决查询串中包含特殊字符时搜索导致系统抛出异常的问题，但未解决输入单个字符造成异常的问题
		 */
		this.query = DealingWithQuery.CorrectQuery(query);
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public int getLastPage() {
		return lastPage;
	}

	public void setLastPage(int lastPage) {
		this.lastPage = lastPage;
	}

	public List<String> getResults() {
		return results;
	}

	public void setResults(List<String> results) {
		this.results = results;
	}

}// end class
