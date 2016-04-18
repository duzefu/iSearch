package server.engine.api;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import server.commonutils.MyStringChecker;
import common.entities.searchresult.*;

public class Sogou extends AbstractEngine {

//	public String[] getRelatedSearch(String indexWords) {
//		String sogou = "http://www.sogou.com/web";
//		Document doc = null;
//		try {
//			doc = Jsoup.connect(sogou).data("query", indexWords).timeout(8000)
//					.get();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		Elements tables = doc.select("table.hint").first()
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

	private final static String URL_BASE= "http://www.sogou.com/web";
	
	@Override
	protected int getMyResults(List<Result> resultList, String query, int page,
			int timeout, int lastamount) {
		
		int amount=0;
		try {
			Document doc = getHtmlDocument(page, query,timeout);
			if (null == doc) return amount;

			Elements tables = getResultElements(doc);
			if(null==tables||tables.isEmpty()) return amount;
			
			int index = (page - 1) * 10 + 1;
			Result curRes = new Result(null, null, null, "搜狗" + "(" + (index)+ ")", 0);
			for (Element table : tables) {
				if (null == table) continue;
				if (extractResult(table, curRes)) {
					resultList.add(curRes);
					curRes = new Result(null, null, null, "搜狗" + "("	+ (++index) + ")", 0);
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
		try {
			doc = Jsoup.connect(URL_BASE)
					.data("query", query)
					.data("page", Integer.toString(page))
					.timeout(timeout).get();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return doc;
	}

	/**
	 * 根据html文件，提取到所有搜索结果组成的元素列表
	 * @param doc
	 * @return
	 */
	private final Elements getResultElements(Document doc){
		
		Elements ret=null;
		ret=doc.select("div#main");
		if(null==ret||ret.isEmpty()) return ret;
		ret=ret.first().select("div.rb,div.vrwrap");
		return ret;
	}
	
	private boolean extractResult(Element table, Result curRes) {

		boolean ret=false;
		if (null == table || null == curRes)	return ret;

		try {
			getResultBasicInfo(table, curRes);
			if (curRes.isUsable()) ret=true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	private void getResultBasicInfo(Element ele, Result res) {

		if (null == ele || null == res)	return;

		String abstr = null;
		Elements tmpEles = null;
		Element resEle = null;
		try {
			String title = null;
			tmpEles = ele.select("a");
			if (null == tmpEles || tmpEles.isEmpty()) return;
			resEle = tmpEles.first();
			res.setLink(resEle.attr("href"));
			title = resEle.text();
			res.setTitle(title);
			abstr=getAbstr(ele);
			res.setAbstr(abstr);
		} catch (Exception e) {
			if (null == res.getAbstr()) res.setAbstr("");
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
		
		if(MyStringChecker.isBlank(abstr)){
			tmpEles=ele.select("div.ft");
			if(null!=tmpEles&&!tmpEles.isEmpty()) abstr=tmpEles.first().text();
		}
		
		if (null == abstr) abstr = "";
		
		return abstr;
	}

}
