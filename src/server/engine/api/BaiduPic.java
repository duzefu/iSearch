package server.engine.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class BaiduPic {


	public List<String> getResults(List<String> resultList, String indexWords,
			int page) {
		java.util.Scanner input = null;
		StringBuffer html = new StringBuffer();
		URL url = null;
		String word2 = "";
		try {
			word2 = URLEncoder.encode(indexWords, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String urls = "http://image.baidu.com/search/index?tn=baiduimage"+"&word="+word2;
		try {
			url = new URL(urls);
			input = new java.util.Scanner(url.openStream());
			while (input.hasNext()) {
				html.append(input.nextLine());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (input != null)
				input.close();
		}
		if (html != null) {
			resultList = parUrlFromS(html.toString());
		}
		return resultList;
	}

	// 该方法没有解析或者没有获得百度图片搜索结果中前两三张推荐的图片
	public List<String> parUrlFromS(String s) {
		List<String> urlList = new ArrayList<String>();
		// String mark1 = "\"pic_url\":\"";// "thumbUrl":
		String mark1 = "\"thumbURL\":\"";// "thumbUrl":
		String mark2 = "\",";
		int begin = s.indexOf("'imgData'");
		int end = s.indexOf(");", begin);
		s = s.substring(begin, end);

		while (s.contains(mark1)) {
			int index1 = s.indexOf(mark1);
			int index2 = s.indexOf(mark2, index1);
			String src = s.substring(index1 + mark1.length(), index2);
			urlList.add(src);
			s = s.substring(index2);
		}

		return urlList;
	}
}
