package server.engine.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class YoudaoPic {


	public static List<String> getResults(List<String> resultList, String indexWords) {
//		String url = "http://image.youdao.com/search?q=" + indexWords;
		String url = "http://image.youdao.com/search?q=" + indexWords;
		String word2 = "";
		try {
			word2 = URLEncoder.encode(indexWords, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			url = url + word2;
			Document doc = Jsoup.connect(url).timeout(100000).get();
			Element link = doc.getElementById("resultLists");
			Elements links = link.select("img.imgthumb");
			for (Element e : links) {
				String src = e.attr("src");
				resultList.add(src);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}

}
