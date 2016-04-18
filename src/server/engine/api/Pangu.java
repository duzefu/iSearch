///*
//* Copyright (c) 2012,Intelligent retrieval project team,ASE lab,Xidian university
//* All rights reserved.
//*
//* Filename: Pnagu.java
//* Summary: searchAPI,get format results from Pangu
//*
//* Current Version: 1.0
//* Author: Bryan Zou
//* Completion Date: 2014.1.1
//*/
//
//package server.engine.api;
//
//import java.io.IOException;
//import java.util.List;
//
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import common.entities.searchresult.*;
//
//public class Pangu implements AbstractEngine{
//	/**
//	 * get results from SE Pangu
//	 * @param indexWords
//	 * @param page
//	 * @return
//	 * @throws IOException
//	 */
//	
//	public void getResults(List<Result> resultList,String indexWords,int page,int seconds, int lastCount)
//	{
//		String add="";
//		if(page>1)
//		{
//			add = "&n=10&p="+(page-1);
//		}
//		String url = "http://search.panguso.com/pagesearch.htm?q="+indexWords;
//		
//		Document doc = null;
//		try {
//			
//			doc = Jsoup.connect(url).userAgent("Mozilla/4.0").timeout(6000).get();
//			
//			if(doc!=null)
//			{				
//				Elements tables = doc.select("ol#result_ol").first().select("li.fy");
//				
//				
//				int index = (page-1)*10;
//				int weight = 10*(101-page);
//				for(Element table:tables)
//				{
//					String title = table.getElementsByTag("a").first().text();
//					String link = table.getElementsByTag("a").first().attr("href");
//					String abstr = table.getElementsByTag("p").first().text();
//					
//					resultList.add(new Result(title,abstr,link,"盘古"+"("+(index+1)+")",weight));
//					index++;
//					weight--;
//				}
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//}