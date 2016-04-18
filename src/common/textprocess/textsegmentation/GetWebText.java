/*
 * Copyright (c) 2013,Intelligent retrieval project team,ASE lab,Xidian university
 * All rights reserved.
 *
 * Filename: GetWebText.java
 * Summary: using open-source jsoup get web pages and parse to string
 *
 * Current Version: 0.1
 * Author: Bryan Zou
 * Completion Date: 2013.3.7
 *
 */

package common.textprocess.textsegmentation;

import org.jsoup.nodes.Document;

import server.commonutils.JsoupUtil;

public class GetWebText {
	/**
	 * get webpage text ,include title and text
	 * 
	 * @param url
	 * @return
	 */
	public static String[] gettext(String url) {
		
		if(null==url||url.isEmpty()) return null;
		String[] web = new String[2];
		Document doc = null;
		
		try {
			doc=JsoupUtil.getHtmlDocument(url, 10000);
			if (doc != null) {
				web[0] = JsoupUtil.getTitle(doc);
				web[1] = JsoupUtil.getBodyText(doc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return web;
	}
}