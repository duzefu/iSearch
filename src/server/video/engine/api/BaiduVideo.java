package server.video.engine.api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


class BaiduThread extends Thread {
	
	private List<VideoInfo> lists;
	private VideoPlayCount videoPlayCount;
	private Map<String, Object> maps;
	private int index;
	private Lock lock;
	
	public BaiduThread(List<VideoInfo> lists, Map<String, Object> maps, VideoPlayCount videoPlayCount, int index, Lock lock) {
		this.lists = lists;
		this.videoPlayCount = videoPlayCount;
		this.maps = maps;
		this.index = index;
		this.lock = lock;
	}
	
	protected  String filterDuration(String duration) {
		Pattern pattern = Pattern.compile("\\d{2,3}\\:\\d{2,3}");
		Matcher matcher = pattern.matcher(duration);
		if(matcher.find()) {
			return matcher.group(0);
		}
		else {
			return duration;
		}
	}
	
	@Override
	public void run() {
		VideoInfo videoInfo = new VideoInfo();
		String title = (String)this.maps.get("ti");
		String video_url = (String)this.maps.get("origin_url");
//		this.lock.lock();
		String playCount = this.videoPlayCount.VideoSelector(video_url);
		videoInfo.setPlayCount(playCount);
//		this.lock.unlock();
		String image_url = (String)this.maps.get("pic");
		String duration = filterDuration((String)this.maps.get("duration"));
		String site     = (String)this.maps.get("srcShortUrlExt");
		videoInfo.setVideoUrl(video_url);
		videoInfo.setImageUrl(image_url);
		videoInfo.setDuration(duration);
		videoInfo.setIndex(this.index);
		videoInfo.setTitle(title);
		videoInfo.setSite(site);
		System.out.println(videoInfo);
		this.lists.add(videoInfo);
		
	}
}

public class BaiduVideo extends VideoEngine{
	
	//word = query, pn = （page - 1） * 20 根据关键字，每次获取20个视频
	private final static String BASE_URL = "http://v.baidu.com/v?word=%s&ct=301989888&rn=20&pn=%d&db=0&s=0&fbl=800&ie=utf-8#pn=%d";
	private final static String PATTERN = "\"data\"\\:([\"\\[\\{\\w\\d\\:\\,\\/\\.\\\\\\=\\&\\<\\>\\ \\#\\?\\$\\-\\+\\}\\]\\(\\)\\_\\;\\!\\*\\^\\%\\@]+)\\,\"dispNum\"";	
	
	private int getExactPage(int page) {
		if(page < 1) page = 1;
		return (page - 1) * 20;
	}
	
	private int getExactIndex(int page) {
		return (page - 1) * 20;
	}
	
	protected  List<Map<String, Object>> parseJSON2List(String strs) {
		List<Map<String, Object>> lists = new ArrayList<>();
		if(strs.equals("No Match")) return lists;
		JSONArray jsonArray = JSONArray.fromObject(strs);
		Iterator<JSONObject> iterator = jsonArray.iterator();
		while(iterator.hasNext()) {
			JSONObject jsonObject = iterator.next();
			lists.add(parseJSON2Map(jsonObject.toString()));
		}
		return lists;
	}
	
	protected  Map<String, Object> parseJSON2Map(String strs) {
		Map<String, Object> maps = new HashMap<>();
		if(strs.equals("No Match")) return maps;
		JSONObject jsonObject = JSONObject.fromObject(strs);
		for(Object key : jsonObject.keySet()) {
			Object value = jsonObject.get(key);
			if (value instanceof JSONArray) {
				List<Map<String, Object>> lists = new ArrayList<>();
				Iterator<JSONObject> iterator = ((JSONArray)value).iterator();
				boolean is_json = true;
                while(iterator.hasNext()) {
                    Object obj = iterator.next();
                    if(obj instanceof JSONObject) {
                        JSONObject jsonObject2 = (JSONObject) obj;
                        lists.add(parseJSON2Map(jsonObject2.toString()));
                    }
                    else {
                        is_json = false;
                        break;
                    }
                }
                if(!is_json) continue;
                maps.put(key.toString(), lists);
			}
			else {
				maps.put(key.toString(), value);
			}
		}
		return maps;
	}
	
	protected  String filterHTML(String pattern, String html) {
		Pattern regEx = Pattern.compile(pattern);
		Matcher matcher = regEx.matcher(html);
		if(matcher.find()) {
			return matcher.group(1);
		}
		else {
			return "No Match";
		}
	}
	
	protected  List<Map<String, Object>> getListByUrl(String pattern, String url, int timeout) {
		return parseJSON2List(filterHTML(pattern, getResponseByUrl(url, timeout)));
	}
	protected  String getResponseByUrl(String url, int timeout) {
        try {
        	if(url != null && !url.equals("")) {
        		URL openUrl = new URL(url);
                HttpURLConnection urlConnection = (HttpURLConnection)openUrl.openConnection();
                urlConnection.setConnectTimeout(timeout);
                urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

                InputStream inputStream = openUrl.openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer sBuffer = new StringBuffer();
                String line;
                while((line = reader.readLine()) != null) {
                    sBuffer.append(line);
                }
                return sBuffer.toString();
        	}
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
	@Override
	protected int getMyResults(List<VideoInfo> resultsList, String query, int page, int timeout) {
		// TODO Auto-generated method stub
		int amount = 0;
		try{
			int pn = getExactPage(page);
			List<VideoInfo> results = new ArrayList<>();
			String url = String.format(BASE_URL, URLEncoder.encode(query, "UTF-8"), pn, pn);
			List<Map<String, Object>> lists = getListByUrl(PATTERN, url, timeout);
			Iterator<Map<String, Object>> iterator = lists.iterator();
			
			BaiduThread baiduThread[] = new BaiduThread[21];
			int threadCount = 0;
            int INDEX = getExactIndex(page);
            Lock lock = new ReentrantLock();
			while(iterator.hasNext()) {
				Map<String, Object> maps = iterator.next();
				VideoPlayCount videoPlayCount = new VideoPlayCount();
				baiduThread[threadCount] = new BaiduThread(results, maps, videoPlayCount, INDEX, lock);
				baiduThread[threadCount].start();
				++threadCount;
				++ amount;
				++ INDEX;
				if(amount >= 20) {
					break;
				}
			}
			for(int i = 0; i < threadCount; ++i) {
				baiduThread[i].join();
			}
			if(amount > 0 && results != null) {
//				System.out.print("Baidu: ");
//				System.out.println(amount);
				resultsList.addAll(results);
			}
//			System.out.print("In Baidu: ");
//			System.out.println(resultsList.size());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return amount;
	}
}
