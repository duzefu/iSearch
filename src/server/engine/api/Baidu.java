package server.engine.api;

import java.util.List;

import javafx.scene.chart.PieChart.Data;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import server.commonutils.HtmlSpecialCharUtil;
import server.commonutils.JsoupUtil;
import server.commonutils.MyStringChecker;
import common.entities.searchresult.*;

public class Baidu extends AbstractEngine {
	
	private final static String URL_BASE="http://www.baidu.com/s?tn=baidulocal&ie=utf-8";
	
	public int getRelatedSearch(List<String> ret, String query) {

		int amount=0;
		if(null==ret||MyStringChecker.isBlank(query)) return amount;

		String url = "http://www.baidu.com/s?wd=" + query + "&cl=3&rn=15";
		Document doc = null;
		try {
			doc = JsoupUtil.getHtmlDocument(url);
			if (null == doc) return amount;
			Elements tables = doc.select("div#rs");
			if (null == tables || tables.isEmpty()) return amount;
			Elements contentlist = tables.first().getElementsByTag("a");
			if (contentlist != null && contentlist.size() > 0) {
				for (Element relQuery : contentlist) {
					String word=JsoupUtil.getTextInHtml(relQuery);
					if(MyStringChecker.isBlank(word)) continue;
					ret.add(word);
					++amount;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return amount;
	}

	@Override
	public int getMyResults(List<Result> resultList, String query, int page,
			int timeout, int lastamount) {
		
		int amount=0;
		try {
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
					.data("pn", pn)
					.timeout(timeout)
					.get();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return doc;
	}

	private final Elements getResultElements(Document doc){
		return doc.select("td.f");
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