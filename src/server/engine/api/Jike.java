///*
//* Copyright (c) 2012,Intelligent retrieval project team,ASE lab,Xidian university
//* All rights reserved.
//*
//* Filename: Baidu.java
//* Summary: searchAPI,get format results from Baidu
//*
//* Current Version: 1.0
//* Author: Bryan Zou
//* Completion Date: 2012.9.1
//*
//* Superseded versions: 0.9
//* Original Author: Bryan Zou
//* Completion Date: 2012.7.30
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
//
//public class Jike implements AbstractEngine{
//	/**
//	 * get results from SE Jike
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
//			add = "&page="+page;
//		}
//		String url = "http://www.jike.com/so?q="+indexWords+add;
//		Document doc = null;
//		try {
//			doc = Jsoup.connect(url).timeout(3000).get();
//			if(doc!=null)
//			{
//				Element tables = doc.select("ul.WebList").first();
//				int index = (page-1)*10;
//				int weight = 10*(101-page);
//				for(Element child:tables.children())
//				{
//					String title  = child.select("a.title").text();
//					String link = child.getElementsByTag("a").first().attr("href");
//					if(!child.select("div.TexCon").isEmpty())
//					{
//						String abs = child.select("div.TexCon").first().text();
//						resultList.add(new Result(title,abs,link,"即刻"+"("+(index+1)+")",weight));
//						index++;
//						weight--;
//					}
//					else
//					{
//						index++;
//						weight--;
//						continue;
//					}
//				}
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	/**
//	 * get Related Search from SE Jike
//	 * @param indexwords
//	 * @return
//	 * @throws IOException
//	 */
//	public String[] getRelatedSearch (String indexwords)
//	{
//		String url = "http://www.jike.com/so?q="+indexwords;
//		Document doc = null;
//		try {
//			doc = Jsoup.connect(url).timeout(6000).get();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Elements tables = doc.select("a.rResult");
//		int count = 0;
//		if(tables.size()>0)
//		{
//			String[] related = new String[tables.size()];
//			for(Element table:tables)
//			{
//				related[count] = table.text();
//				count++;
//			}
//			return related;
//		}
//		else
//		{
//			return null;
//		}
//	}
//}