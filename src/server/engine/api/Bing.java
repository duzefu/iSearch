package server.engine.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import server.commonutils.CharUtil;
import common.entities.searchresult.*;

public class Bing extends AbstractEngine {
	
	private final static String URL_BASE= "http://cn.bing.com/search";
	
	private static final Map<String, String> dataForEn;
	private static final Map<String, String> dataForCn;
	private static final Map<String, String> dataForAll;

	static {
		
		dataForEn = new HashMap<String, String>();
		dataForAll = new HashMap<String, String>();
		dataForCn = new HashMap<String, String>();

		dataForEn.put("intlF", "1");
		dataForEn.put("FORM", "TIPEN1");

		dataForCn.put("intlF", "");
		dataForCn.put("upl", "zh-chs");
		dataForCn.put("FORM", "TIPCN1");

		dataForAll.put("intlF", "");
		dataForAll.put("FORM", "TIPALL");
	}

	@Override
	protected int getMyResults(List<Result> resultList, String query, int page,
			int timeout, int lastamount) {
		
		int amount=0;
		try {
			Document doc = getHtmlDocument(page, query, timeout);
			Elements tables = doc.select("div#b_content").first().select("li.b_algo");
			int index = lastamount + 1;

			Result curRes=new Result(null, null, null, "必应" + "("	+ (index) + ")", 0);
			for (Element table : tables) {
				if (null == table) continue;
				if(extractResult(table, index, curRes)){
					resultList.add(curRes);
					curRes=new Result(null, null, null, "必应" + "("	+ (++index) + ")", 0);
					++amount;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return amount;
	}
	
	private Document getHtmlDocument(int page, String query, int timeout) {

		Document doc = null;
		String first = Integer.toString((page - 1) * 10 - 1);
		try {
			Connection conn = Jsoup.connect(URL_BASE)
					.data("q", query)
					.data("qs", "n")
					.data("sc", "8-2")
					.data("sp", "-1")
					.data("first", first);
			if (CharUtil.isEnglishPattern(query)) conn = conn.data(dataForEn);
			else if (CharUtil.containChinese(query))	conn = conn.data(dataForCn);
			else conn = conn.data(dataForAll);
			doc = conn.timeout(timeout).get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}


	private boolean extractResult(Element ele, int index, Result res) {

		boolean ret=false;
		if (null == ele) return ret;
		
		try {
			getResultBasicInfo(ele, res);
			if (res.isUsable()) ret=true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	private void getResultBasicInfo(Element ele, Result res) {

		if (null == ele) return;

		String abstr = null;
		Elements tmpEles = null;
		Element resEle = null;
		try {
			tmpEles = ele.select("a");
			if (null == tmpEles || tmpEles.isEmpty())
				return;
			resEle = tmpEles.first();
			res.setLink(resEle.absUrl("href"));
			res.setTitle(resEle.text());
			abstr=getAbstr(ele);
			res.setAbstr(abstr);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return;
	}
	
	private String getAbstr(Element ele){
		
		if (null == ele) return null;

		Elements tmpEles = null;
		String abstr = "";
		tmpEles = ele.getElementsByTag("p");
		if (null == tmpEles) return abstr;
		for (int i = 0; i < tmpEles.size(); ++i) {
			Element e = tmpEles.get(i);
			if (null != e && e.hasText())
				abstr += " " + e.text();
		}
		
		return abstr;
	}

}