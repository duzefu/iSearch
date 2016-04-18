package struts.actions.web;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.springframework.ui.Model;

import server.engine.api.*;
import server.info.config.SessionAttrNames;

import com.mysql.jdbc.Blob;
import com.opensymphony.xwork2.ActionSupport;
import common.utils.querypreprocess.DealingWithQuery;

public class PicSearchAction extends ActionSupport {
	private String query;// query就是用户查询的关键词
	private String page;// page是用户请求的页面数
	private int lastPage;// lastPage是每页显示NUM条数据时的页数
	private int items;// item是一共得到的数目
	//private String first="first";
	public List<PictureInfo> results = new ArrayList<PictureInfo>();// Results是请求页面的list
	ServletOutputStream sout;
	InputStream input=null;
	HttpServletResponse response = ServletActionContext.getResponse(); 
	private List<Integer> pageList=new ArrayList<Integer>();
	private String baidu;
	private String bing;
	private String youdao;
	private String sogo;
	private String yahoo;
	private PictureService service=new PictureService();
	private int userid;
	private int resultsDistribution[]=new int[4];
	public String execute() throws Exception {

		// 获取网页上的用户名
		HttpSession session = ServletActionContext.getRequest().getSession();
	//	String usernameinpage = (String) session.getAttribute(SessionAttrNames.USERNAME_ATTR);
		query=ServletActionContext.getRequest().getParameter("query");
		if (query==null||query.equals(""))return SUCCESS;
		
		
		page=ServletActionContext.getRequest().getParameter("page");
		if(page==null||page.equals("")||Integer.parseInt(page)<=0)page="1";
		
		baidu=ServletActionContext.getRequest().getParameter("baidu");
		sogo=ServletActionContext.getRequest().getParameter("sogo");
		youdao=ServletActionContext.getRequest().getParameter("youdao");
		bing=ServletActionContext.getRequest().getParameter("bing");
		yahoo=ServletActionContext.getRequest().getParameter("yahoo");
		if(baidu!=null)if (baidu.equals("checked")||baidu=="checked")service.baiduEngine=true;
		if(sogo!=null)if (sogo.equals("checked")||sogo=="checked")service.sogoEngine=true;
		if(youdao!=null)if (youdao.equals("checked")||youdao=="checked")service.youdaoEngine=true;
	 	if(bing!=null)if (bing.equals("checked")||bing=="checked")service.bingEngine=true;
		//if (yahoo.equals("checked")||yahoo=="checked")service.yahooEngine=true;
		if(!(service.baiduEngine||service.sogoEngine||service.bingEngine||service.youdaoEngine))
			return SUCCESS;
		results=service.getResult(query,Integer.parseInt(page));
		pageList=service.getPageList(Integer.parseInt(page));
		resultsDistribution=service.getResultDistribution(results);
		
		return SUCCESS;
	}// end execute
	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getBaidu() {
		return baidu;
	}

	public void setBaidu(String baidu) {
		this.baidu = baidu;
	}
	public String getBing() {
		return bing;
	}

	public void setBing(String bing) {
		this.bing = bing;
	}
	public String getSogo() {
		return sogo;
	}

	public void setSogo(String sogo) {
		this.sogo = sogo;
	}
	public String getYoudao() {
		return youdao;
	}

	public void setYoudao(String youdao) {
		this.youdao = youdao;
	}
	public String getYahoo() {
		return yahoo;
	}

	public void setYahoo(String yahoo) {
		this.yahoo = yahoo;
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

	public List<PictureInfo> getResults() {
		return results;
	}



	public void setResults(List<PictureInfo> results) {
		this.results = results;
	}
	
	
	public List<Integer> getPageList() {
		return pageList;
	}

	public void setPageList(List<Integer> pageList) {
		this.pageList = pageList;
	}
	public int[] getResultsDistribution()
	{
		return resultsDistribution;
	}
	public void setResultsDistribution(int[] resultsDistribution)
	{
		this.resultsDistribution=resultsDistribution;
	}
}
