package server.engine.api;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class SogoPic {

	/*public static void main(String arg[])
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException {
		SogoPic e = new SogoPic();
		List<String> resultList = new ArrayList<String>();
		resultList = e.getResults(resultList, "cup", 1);
		for (String elem : resultList) {
		}
	}*/

	public static List<String> getResults(List<String> resultList, String indexWords) {
		String word2 = "";
		try {
			word2 = URLEncoder.encode(indexWords, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String url = "http://pic.sogou.com/pics?query=" + word2;
		resultList = parUrlFromS(getHTML(url, "utf-8"));
		return resultList;
	}

	public static String getHTML(String pageURL, String encoding) {

		StringBuilder pageHTML = new StringBuilder();

		try {

			URL url = new URL(pageURL);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();

			connection.setRequestProperty("User-Agent", "MSIE 7.0");

			BufferedReader br = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), encoding));

			String line = null;

			while ((line = br.readLine()) != null) {

				pageHTML.append(line);

				pageHTML.append("\r\n");

			}

			connection.disconnect();

		} catch (Exception e) {

			e.printStackTrace();

		}

		return pageHTML.toString();

	}

	public static List<String> parUrlFromS(String s) {
		// s="fjdsaokfjpwe-0frdfmcfrsdoplxmcsdpwwwafjasmflsa'kfkslmc.xfrsdopmdfkdwwwasl'fkas'fksafkfrsdopas'dwwwacmvndjgp'awfrsdopkwwwagasdvfrsdopmc.wwwamvvvsf;salflweomvclldmafkpoedvm.dsmc'awe";
		List<String> urlList = new ArrayList<String>();
//		String mark1 = "\"pic_url\":\"";//  "thumbUrl":
		String mark1 = "\"thumbUrl\":\"";//  "thumbUrl":
		String mark2 = "\",\"";
		int begin = s.indexOf("imgTempData");
		int end = s.indexOf("fatalParamList");
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
