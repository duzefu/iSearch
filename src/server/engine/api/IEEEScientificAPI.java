package server.engine.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import junit.framework.Assert;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import server.commonutils.HtmlSpecialCharUtil;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.ScriptResult;

import common.entities.searchresult.Result;

public class IEEEScientificAPI {
private final static String URL_BASE="http://link.springer.com/search?query=meta-search";
	
	public int getMyResults(List<Result> resultList, String query, int page,
			int timeout, int lastamount) throws FailingHttpStatusCodeException,
			MalformedURLException, IOException{
		
		int amount=0;
		try {
			/*final WebClient webClient = new WebClient();
			final HtmlPage startPage = webClient.getPage(URL_BASE);*/
			URL url = new URL(URL_BASE);  
		    HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();  
		    InputStreamReader input = new InputStreamReader(httpConn  
		            .getInputStream(), "utf-8");  
		    BufferedReader bufReader = new BufferedReader(input);  
		    String line = "";  
		    StringBuilder contentBuf = new StringBuilder();  
		    while ((line = bufReader.readLine()) != null) {  
		        contentBuf.append(line);  
		    }  
		    System.out.println("captureJavascript()的结果：\n" + contentBuf.toString());  
			
			Document doc = getHtmlDocument(page, query,timeout);
			if (null == doc)	return amount;

			Elements tables = getResultElements(doc);
			if(null==tables||tables.isEmpty()) return amount;
			int index = lastamount + 1;

			Result curRes=new Result(null, null, null, "百度" + "("	+ (index) + ")", 0);
			for (Element table : tables) {
				if (null == table) continue;
				if(extractResult(table, index,curRes)){
					resultList.add(curRes);
					curRes=new Result(null, null, null, "百度" + "("	+ (++index) + ")", 0);
					++amount;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return amount;
	}
	
	private Document getHtmlDocument(int page, String query,int timeout) {

		String pn =String.valueOf(10 * (page - 1));
		Document doc = null;
		try {
			doc = Jsoup.connect(URL_BASE)
					.data("wd", query)
					.timeout(timeout)
					.get();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return doc;
	}

	private final Elements getResultElements(Document doc){
		return doc.select("div.result sc_default_result xpath-log");
	}
	
	private boolean extractResult(Element table, int index, Result res) {
		
		boolean ret=false;
		if (null == table||null==res) return ret;
		
		try {
			getResultBasicInfo(table, res);
			if (res.isUsable()) ret=true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	private void getResultBasicInfo(Element ele, Result res) {

		if (null == ele || null == res) return;

		String abstr = null;
		String link = null;
		Elements tmpEles = null;
		Element resEle = null;
		try {
			String title = null;
			tmpEles = ele.select("a");
			if (null == tmpEles || tmpEles.isEmpty())
				return;
			resEle = tmpEles.first();
			if(null==resEle) return;
			link = resEle.attr("href");
			res.setLink(link);
			title=resEle.text();
			res.setTitle(title);
			String resText=ele.text(), linkText=ele.select("[color=#008000]").text();
			int count = resText.indexOf(linkText);
			abstr=resText.substring(title.length(), count);//得到摘要
			res.setAbstr(abstr);

			if(title.contains("贴吧") && linkText.contains("kw=")){
				String resLink = getTiebaLink(link, linkText);
				res.setLink(resLink);
			}
			
		} catch (Exception e) {
			if (null == res.getAbstr())
				res.setAbstr("");
			e.printStackTrace();
		}

		return;
	}

	/**
	 * @param link
	 * @param linkText
	 * @return
	 */
	private String getTiebaLink(String link, String linkText) {
		String resLink = null;
		if (linkText.contains("&")) {
			int linkstart=linkText.indexOf("kw=")+4, linkmid = linkText.indexOf("&")-1,linkend=linkText.indexOf(HtmlSpecialCharUtil.unEscapeHtml("&nbsp;"));
			String former=linkText.substring(0,linkstart-1), kw=linkText.substring(linkstart,linkmid), latter = linkText.substring(linkmid+1, linkend);
			int kwIndex = link.indexOf("kw=")+4;
			resLink = link.substring(0, kwIndex-1);
			resLink = resLink.concat(kw);
			resLink = resLink.concat(latter);
		}else{
			int linkstart=linkText.indexOf("kw=")+4, linkend=linkText.indexOf(HtmlSpecialCharUtil.unEscapeHtml("&nbsp;"))-1;
			String former=linkText.substring(0,linkstart-1),latter = linkText.substring(linkstart, linkend);
			int kwIndex = link.indexOf("kw=")+4;
			resLink = link.substring(0, kwIndex-1);
			resLink = resLink.concat(latter);
		}
		return resLink;
	}
}
