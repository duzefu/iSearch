//package server.engine.api;
//
//import java.io.IOException;
//import java.util.LinkedList;
//import java.util.List;
//
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import server.commonutils.JsoupUtil;
//import common.entities.searchresult.*;
//
//public class Google extends AbstractEngine{
//	
//	private final static String URL_BASE="https://www.google.com.hk/webhp?hl=zh-CN#newwindow=1&safe=strict";
//	
//	public static void main(String argv[]){
//		
//		try {
//			Document document=JsoupUtil.getHtmlDocument("https://www.google.com.hk/");
//			System.out.println(JsoupUtil.getTextInHtml(document));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		System.exit(0);
//	}
//	
//	@Override
//	protected int getMyResults(List<Result> resultList, String query, int page,
//			int timeout, int lastamount) {
//
//		int amount = 0;
//		try {
//			Document doc = getHtmlDocument(page, query, timeout);
//			if (null == doc)	return amount;
//
//			Elements tables = getResultElements(doc);
//			if (null == tables || tables.isEmpty()) return amount;
//			int index = lastamount + 1;
//			Result res = new Result(null, null, null, "谷歌(" + index + ")");
//			for (Element table : tables) {
//				if (null == table) continue;
//				if (extractResult(table, index, res)) {
//					resultList.add(res);
//					res = new Result(null, null, null, "谷歌" + "(" + (++index)
//							+ ")", 0);
//					++amount;
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return amount;
//	}
//	
//	private Document getHtmlDocument(int page, String query,int timeout) {
//
//		Document doc = null;
//		try {
//			doc = Jsoup.connect(URL_BASE)
//					.data("q",query)
//					.data("start",String.valueOf((page-1)*10))
//					.timeout(timeout)
//					.get();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return doc;
//	}
//
//	private final Elements getResultElements(Document doc){
//		
//		Elements ret=null==doc?null:doc.select("div.srg");
//		if(null!=ret) ret=ret.select("div.g");
//		return ret;
//	}
//	
//	private boolean extractResult(Element table, int index, Result res) {
//		
//		boolean ret=false;
//		if (null == table||null==res) return ret;
//		
//		try {
//			getResultBasicInfo(table, res);
//			if (res.isUsable()) ret=true;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return ret;
//	}
//
//	private void getResultBasicInfo(Element ele, Result res) {
//
//		if (null == ele || null == res) return;
//
//		String abstr = null;
//		Elements tmpEles = null;
//		Element resEle = null;
//		try {
//			String title = null;
//			tmpEles = ele.select("a");
//			if (null == tmpEles || tmpEles.isEmpty()) return;
//			resEle = tmpEles.first();
//			if(null==resEle) return;
//			res.setLink(resEle.attr("href"));
//			title=resEle.text();
//			res.setTitle(title);
//			abstr=getAbstract(ele);//得到摘要
//			res.setAbstr(abstr);
//
//		} catch (Exception e) {
//			if (null == res.getAbstr())
//				res.setAbstr("");
//			e.printStackTrace();
//		}
//
//		return;
//	}
//	
//	private String getAbstract(Element ele){
//		
//		String ret=null;
//		Elements eles=ele.select("span.st");
//		if(null!=eles&&!eles.isEmpty()){
//			ret=JsoupUtil.getTextInHtml(eles.first());
//		}
//		return ret;
//	}
//	
//}
