///*
//* Copyright (c) 2012,Intelligent retrieval project team,ASE lab,Xidian university
//* All rights reserved.
//*
//* Filename: Soso.java
//* Summary: searchAPI,get format results from Soso
//*
//* Current Version: 1.1
//* Author: Bryan Zou
//* Completion Date: 2014.1.13
//* 原因：2013年12月5日开始，搜搜使用搜狗提供的检索结果
//* 
//* Superseded versions: 1.0
//* Original Author: Bryan Zou
//* Completion Date: 2012.9.1
//*/
//
//package server.engine.api;
//
//import java.io.IOException;
//import java.util.List;
//
//import org.apache.log4j.Logger;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import server.commonutils.LogU;
//import common.entities.searchresult.*;
//import common.functions.resultmerge.ResultWeightCalculator;
//import common.textprocess.textsegmentation.IK;
//
//public class Soso implements AbstractEngine{
//	
//	private static Logger debugLogger = null;
//	private static Logger errorLogger = null;
//
//	private Logger getDebugLogger() {
//
//		if (this.debugLogger == null) {
//			synchronized (Baidu.class) {
//				debugLogger = LogU.getDebugLogger(this.getClass()
//						.getName());
//			}
//		}
//		return this.debugLogger;
//	}
//
//	private Logger getErrorLogger() {
//
//		if (this.errorLogger == null) {
//			synchronized (Baidu.class) {
//				errorLogger = LogU.getErrorLogger(this.getClass()
//						.getName());
//			}
//		}
//		return this.errorLogger;
//	}
//	
//	public void getResults(List<Result> resultList, String indexWords, int page,int seconds, int lastCount) {
//
//		String soso = "http://www.soso.com/q";
//		Document doc = null;
//		try {
//			doc = Jsoup.connect(soso)
//					.data("pid","s.idx")
//					.data("cid","s.idx.se")
//					.data("w",indexWords)
//					.data("pg",Integer.toString(page))
//					.timeout(9000)
//					.get();
//			if(doc!=null)
//			{
//				int index = lastCount + 1;
//				Elements tables = doc.select("div.results").first().select("div.rb");
//				List<String> ls=IK.fenci(indexWords);
//				for(Element table:tables)
//				{
//					Element title = table.select("a").first();
//					String url = title.attr("href");
//					String abstr = null;
//					if(!table.select("div.ft").isEmpty())
//					{
//						abstr = table.select("div.ft").first().text();
//						if(abstr==null) abstr="";
//					}
//					if(abstr==null) abstr="";
//					
//					Result curRes=new Result(title.text(),abstr,url,"搜搜"+"("+(index+1)+")",0);
//					curRes.setValue(ResultWeightCalculator.calculateWeight(ls, curRes, page, index));
//					resultList.add(curRes);
//					index++;
//				}
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	
//}
