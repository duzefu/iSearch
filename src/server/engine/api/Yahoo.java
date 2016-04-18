package server.engine.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import server.commonutils.LogU;
import server.engine.api.AbstractEngine;
import common.entities.searchresult.Result;
import common.functions.resultmerge.ResultWeightCalculator;
import common.textprocess.textsegmentation.IK;

public class Yahoo extends AbstractEngine {
	
//	private final static String URL_BASE="https://search.yahoo.com/search?toggle=1&cop=mss&ei=UTF-8&fr=yfp-t-308&fp=1";
	private final static String URL_BASE="https://search.yahoo.com/search;_ylt=Aq.We.oXE2.9CtwtMIYnjNubvZx4?toggle=1&cop=mss&ei=UTF-8&fr=yfp-t-901&fp=1";
// https://search.yahoo.com/search;_ylt=A0LEV1sVIjdWoNIAvwdXNyoA;_ylc=X1MDMjc2NjY3OQRfcgMyBGZyA3lmcC10LTkwMQRncHJpZAMEbl9yc2x0AzAEbl9zdWdnAzAEb3JpZ2luA3NlYXJjaC55YWhvby5jb20EcG9zAzAEcHFzdHIDBHBxc3RybAMEcXN0cmwDMwRxdWVyeQNkb2cEdF9zdG1wAzE0NDY0NTM4NDM-?p=dog&fr2=sb-top-search&fr=yfp-t-901&fp=1
//	private final static String URL_BASE = "https://search.yahoo.com/search;_ylt=A0LEV1sVIjdWoNIAvwdXNyoA;_ylc=X1MDMjc2NjY3OQRfcgMyBGZyA3lmcC10LTkwMQRncHJpZAMEbl9yc2x0AzAEbl9zdWdnAzAEb3JpZ2luA3NlYXJjaC55YWhvby5jb20EcG9zAzAEcHFzdHIDBHBxc3RybAMEcXN0cmwDMwRxdWVyeQNkb2cEdF9zdG1wAzE0NDY0NTM4NDM-? fr2=sb-top-search&fr=yfp-t-901&fp=1";
	@Override
	protected int getMyResults(List<Result> resultList, String query, int page,
			int timeout, int lastamount) {
		
		int amount=0;
		try{
			Document doc = getHtmlDocument(page, query, timeout);
			if(null==doc) return amount;
			Elements tables=getResultElements(doc);
			if(null==tables||tables.isEmpty()) return amount;
	
			int index = lastamount + 1;

			Result curRes = new Result(null, null, null,"雅虎" + "(" + (index) + ")", 0);
			for (Element table : tables) {
				if (null == table) continue;
				if(extractResult(table, curRes)){
					resultList.add(curRes);
					curRes=new Result(null, null, null,"雅虎" + "(" + (++index) + ")", 0);
					++amount;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return amount;
	}
	
	private Elements getResultElements(Document doc){
		
		Elements ret=doc.select("div.yst");
		if(null==ret) return ret;
		ret=ret.select("div.result");
		return ret;
	}
	
	private Document getHtmlDocument(int page, String query, int timeout) {

		Document doc = null;
		try{
			query = query.replace(" ", "%20");
			int b = (page-1) * 10 + 1;
			doc = Jsoup.connect(URL_BASE)
					.data("p", query)
					.timeout(timeout).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return doc;
	}
	
	private boolean extractResult(Element table, Result curRes) {

		boolean ret=false;
		if (null == table || null==curRes) return ret;

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
		Elements tmpEles=null;
		Element resEle=null;
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
			
		if(null==ele) return null;
		
		Element tmpEle=null;
		String abstr="";
		tmpEle = ele.getElementsByTag("p").first();
		
		abstr = tmpEle.text();
		
		if(null!=abstr) 
		{
			return abstr;

		}else{
			abstr = "";
			return abstr;
		}
		
	}

}
