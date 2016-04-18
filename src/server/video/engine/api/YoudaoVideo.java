package server.video.engine.api;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.exception.Nestable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.*;

class YoudaoThread extends Thread {
	
	private List<VideoInfo> lists;
	private Element element;
	private VideoPlayCount videoPlayCount;
	private int index;
	private Lock lock;
	
	private Pattern pattern = Pattern.compile("[\\w\\/\\d\\?\\.\\:]+url=([\\w\\/\\d\\.\\:]+)");
	private Pattern url_pattern = Pattern.compile("url=([\\w\\:\\.\\/]+)");
	
	public YoudaoThread(List<VideoInfo> lists, Element element, VideoPlayCount videoPlayCount, int index, Lock lock) {
		this.lists = lists;
		this.element = element;
		this.videoPlayCount = videoPlayCount;
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
		String origin_video_url = "";
		String video_url = this.element.children().select("a[href]").attr("href");
		try {
			origin_video_url = URLDecoder.decode(video_url, "UTF-8");
		}
		catch(Exception e) {}
		Matcher matcher = url_pattern.matcher(origin_video_url);
		if(matcher.find()) {
			video_url = matcher.group(1);
		}
//		this.lock.lock();
		String playCount = this.videoPlayCount.VideoSelector(video_url);
		videoInfo.setPlayCount(playCount);
//		this.lock.unlock();
		String image_url = this.element.children().select("div.fl-img").select("img[src]").attr("src");
		String title     = this.element.children().select("a[href]").attr("title");
		String duration  = filterDuration(this.element.children().select("div.fl-time").text());
		String siteText  = this.element.children().select("div.fl-movie").select("p.fl-orig").text();
		matcher = pattern.matcher(image_url);
		if(matcher.find()) {
			image_url = matcher.group(1);
		}
		if(siteText.length() > 3) {
			siteText = siteText.substring(3, siteText.length());
		}
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


public class YoudaoVideo extends VideoEngine{
	
    // BASE_URL中的参数,第一个%s代表了keyword搜索的关键字,第二个%d代表(页数-1)*20,代表这一页的获取的视频数为20个,与后面的length对应,第三个参数%d代表
	private final static String BASE_URL = "http://video.youdao.com/search?q=%s&start=%d&length=20&st=1&keyfrom=normal.page.%d&duration=&site=&har=0";	
	private int getExactPage(int page) {
		if(page < 1) page = 1;
		return (page - 1) * 20;
	}
	
	private int getExactIndex(int page) {
		return (page - 1) * 20;
	}
	
	@Override
	protected int getMyResults(List<VideoInfo> resultsList, String query, int page, int timeout) {
		int amount = 0;
		try{
			List<VideoInfo> results = new ArrayList<>();
			int pn = getExactPage(page);
			String url = String.format(BASE_URL, URLEncoder.encode(query, "UTF-8"), pn, page - 1 >= 0 ? page - 1 : 0);
			System.out.println(url);
			results = getVideoInfoByJsoup(url, timeout, page);
			if(results != null)
				amount = results.size();
			if(amount > 0 && results != null) {
//				System.out.print("Youdao: ");
//				System.out.println(amount);
				resultsList.addAll(results);
			}
//			System.out.print("In Youdao: ");
//			System.out.println(resultsList.size());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return amount;
		
	}
	
	private  List<VideoInfo> getVideoInfoByJsoup(String url, int timeout, int page) {
		List<VideoInfo> lists = new ArrayList<>();
		try{
			Document document = Jsoup.connect(url).timeout(timeout)
					.userAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
	                .get();
			Elements allElements = document.select("div.vrwrap");
			
			YoudaoThread youdaoThread[] = new YoudaoThread[21];
			int threadCount = 0;
			int INDEX = getExactIndex(page);
			Lock lock = new ReentrantLock();
			for(Element element : allElements){
				VideoPlayCount videoPlayCount = new VideoPlayCount();
				youdaoThread[threadCount] = new YoudaoThread(lists, element, videoPlayCount, INDEX, lock);
				youdaoThread[threadCount].start();
				threadCount ++;
				++ INDEX;
			}
			for(int i = 0; i < threadCount; ++i) {
				youdaoThread[i].join();
			}
			return lists;
		}
        catch (Exception e) {
        	e.printStackTrace();
        }
		return null;
	}
}
