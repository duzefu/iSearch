package server.video.engine.api;

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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.*;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

class BingThread extends Thread {
	
	private List<VideoInfo> lists;
	private Element element;
	private VideoPlayCount videoPlayCount;
	private int index;
	private Lock lock;
	
	public BingThread(List<VideoInfo> lists, Element element, VideoPlayCount videoPlayCount, int index, Lock lock) {
		this.lists = lists;
		this.element = element;
		this.videoPlayCount = videoPlayCount;
		this.index = index;
		this.lock = lock;
	}
	
	private Map<String, Object> parseJSON2Map(String strs) {
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
	
	private String filterDuration(String duration) {
		Pattern pattern = Pattern.compile("\\d{2,3}\\:\\d{2,3}");
		Matcher matcher = pattern.matcher(duration);
		if(matcher.find()) {
			return matcher.group(0);
		}
		else {
			return duration;
		}
	}
	
	private String BingPlayCount(String input) {
		Pattern pattern = Pattern.compile("([\\d\\,\\.])\\+?(\u4e07)?(\u4ebf)?");
		Matcher matcher = pattern.matcher(input);
		if(matcher.find()) {
			String ori_playCount = String.valueOf(matcher.group(1));
			String replace_playCount = ori_playCount.replaceAll("\\.", ",");
			String more = matcher.group(2);
			String muchmore = matcher.group(3);
			if(more != null && !more.equals("")) replace_playCount += "00000";
			if(muchmore != null && !muchmore.equals("")) replace_playCount +="000000000";
			return replace_playCount;
		}
		else 
			return null;
	}
	
	@Override
	public void run() {
		
		VideoInfo videoInfo = new VideoInfo();
		String video_url = this.element.children().select("a[href]").attr("href");
		String image_url = this.element.children().select("div.vthumb").select("img[src]").attr("src");
		String title     = this.element.children().select("div.tl").text();
		String text = this.element.children().select("div.vr_pubinfo_mmftb").text();
		String duration  = filterDuration(text);
//		this.lock.lock();
		String playCount = videoPlayCount.VideoSelector(video_url);
		videoInfo.setPlayCount(playCount);
//		this.lock.unlock();
		Map<String, Object> maps  = parseJSON2Map(element.children().select("a[vrhm]").attr("vrhm"));
		String siteText = (String)maps.get("s");
		videoInfo.setVideoUrl(video_url);	
		videoInfo.setIndex(this.index);
		videoInfo.setImageUrl(image_url);
		videoInfo.setDuration(duration);
		videoInfo.setTitle(title);
		videoInfo.setSite(siteText);
		System.out.println(videoInfo);
		this.lists.add(videoInfo);
		
	}
}


public class BingVideo extends VideoEngine{
	private final static String BASE_URL = "http://cn.bing.com/videos/asyncv2?q=%s&async=content&first=%d&count=%d&IID=video.1&CW=1280&CH=290&bop=123&form=QBVLPG";
    //默认每一次取一页会有20个视频,与前两个搜索引擎相等
	private final static int COUNT = 20;
	//	private VideoPlayCount videoPlayCount = new VideoPlayCount();
	
	private int getExactPage(int page) {
		return (COUNT + 10) * (page - 1);
	}
	
    private int getExactIndex(int page) {
        return (page - 1) * 20;
    }
	
	@Override
	protected int getMyResults(List<VideoInfo> resultsList, String keyword, int page, int timeout) {
		int amount = 0;
		try{
			List<VideoInfo> results = new ArrayList<>();
			int pn = getExactPage(page);
			String url = String.format(BASE_URL, URLEncoder.encode(keyword, "UTF-8"), pn, COUNT);
			System.out.println(url);
            results = getVideInfoByJsoup(url, timeout, page);
			if(results != null)
				amount = results.size();
			if(amount > 0 && results != null) {
//				System.out.print("Bing: ");
//				System.out.println(amount);
				resultsList.addAll(results);
			}
//			System.out.print("In Bing: ");
//			System.out.println(resultsList.size());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return amount;
	}
	
	private  List<VideoInfo> getVideInfoByJsoup(String url, int timeout, int page) {
		List<VideoInfo> lists = new ArrayList<>();
		try{
			Document document = Jsoup.connect(url).timeout(timeout)
					.userAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
	                .get();
			Elements elements = document.select("div.vidres").select("div.dg_u");
			BingThread bingThread[] = new BingThread[21];
			int threadCount = 0;
            int INDEX = getExactIndex(page);
            Lock lock = new ReentrantLock();
			for(Element element : elements) {
				VideoPlayCount videoPlayCount = new VideoPlayCount();
				bingThread[threadCount] = new BingThread(lists, element, videoPlayCount, INDEX, lock);
				bingThread[threadCount].start();
				threadCount ++;
				++ INDEX;
			}
			for(int i = 0; i < threadCount; ++i) {
				bingThread[i].join();
			}
			return lists;
		}
		catch (Exception e) {
//			e.printStackTrace();
		}
		return null;
	}
}
