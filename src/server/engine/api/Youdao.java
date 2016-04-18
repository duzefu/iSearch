package server.engine.api;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import server.commonutils.MyStringChecker;
import common.entities.searchresult.*;

public class Youdao extends AbstractEngine {

//	public String[] getRelatedSearch(String indexWords) {
//		String youdao = "http://www.youdao.com/search?q=" + indexWords;
//		Document doc = null;
//		try {
//			doc = Jsoup.connect(youdao).timeout(8000).get();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		Elements tables = doc.select("div.c-relatedkeys").first()
//				.getElementsByTag("td");
//		int count = 0;
//		if (tables.size() > 0) {
//			String[] related = new String[tables.size()];
//			for (Element table : tables) {
//				related[count] = table.text();
//				count++;
//			}
//			return related;
//		} else {
//			return null;
//		}
//	}

	private final static String URL_BASE="http://www.youdao.com/search?ue=utf8";
	
	@Override
	protected int getMyResults(List<Result> resultList, String query, int page,
			int timeout, int lastamount) {
		
		int amount=0;
		
		try {
			Document doc = getHtmlDocument(page, query, timeout);
			Elements tables = getResultElements(doc);
			int index = lastamount + 1;

			Result curRes = new Result(null, null, null,"有道" + "(" + (index) + ")", 0);
			for (Element table : tables) {
				if (null == table) continue;
				if(extractResult(table, curRes)){
					resultList.add(curRes);
					curRes=new Result(null, null, null,"有道" + "(" + (++index) + ")", 0);
					++amount;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return amount;
	}

	private Elements getResultElements(Document doc){
		
		Elements ret=doc.select("ol#results");
		if(null!=ret&&!ret.isEmpty()){
			ret=ret.first().select("li.res-list");
		}
		return ret;
	}
	
	private Document getHtmlDocument(int page, String query, int timeout) {

		int startFrom=10*(page-1)+1;
		Document doc = null;
		try {
			doc = Jsoup.connect(URL_BASE)
					.data("q", query)
					.data("start", String.valueOf(startFrom))
					.timeout(timeout)
					.get();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return doc;
	}

	private boolean extractResult(Element table, Result curRes) {

		boolean ret=false;
		if (null == table||null==curRes) return ret;

		try {
			getResultBasicInfo(table, curRes);
			if (curRes.isUsable()) ret=true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	private void getResultBasicInfo(Element ele, Result res) {

		if (null == ele || null == res)
			return;

		String abstr = null;
		Elements tmpEles=null;
		Element resEle=null;
		try {
			tmpEles = ele.select("a");
			if (null == tmpEles || tmpEles.isEmpty())
				return;
			resEle = tmpEles.first();
			res.setLink(resEle.attr("href"));
			res.setTitle(resEle.text());
			abstr=getAbstr(ele);
			res.setAbstr(abstr);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return;
	}
	
	private String getAbstr(Element ele){
		
		if(null==ele) return null;
		
		Elements tmpEles=null;
		String abstr="";
		tmpEles = ele.getElementsByTag("p");
		if(null!=tmpEles&&!tmpEles.isEmpty()){
			for (int i = 0; i < tmpEles.size(); ++i) {
				Element e = tmpEles.get(i);
				if (null != e && e.hasText())
					abstr += " " + e.text();
			}
		}
		
		if(!MyStringChecker.isBlank(abstr)) return abstr;
		
		tmpEles=ele.select("div.mohe-cont");
		if(null!=tmpEles&&!tmpEles.isEmpty()){
			abstr=tmpEles.first().text();
		}
		
		if (null == abstr)
			abstr = "";
		
		return abstr;
	}

}
