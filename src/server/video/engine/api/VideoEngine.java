package server.video.engine.api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import edu.stanford.smi.protegex.owl.testing.owldl.NoPropertiesWithClassAsRangeOWLDLTest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public abstract class VideoEngine {
	/**
	 * 
	 * 视频搜索基类
	 * @param resultsList 结果存放列表，不为null
	 * @param keyword 查询关键词不能为空
	 * @param page 查询第几页，不大于0时候等于1
	 * @param timeout 超时时间（毫秒）， 不大于0时候等于100000
	 * @param pattern 需要匹配的正则表达式
	 * 需要重载getMyResults
	 * */
	
	protected abstract int getMyResults(List<VideoInfo> resultsList, String query, int page, int timeout);
	
	private final static boolean isBlank(String strs){
		return null == strs || 0 == strs.length();
	}
	
	private final static boolean isWhitespace(String strs) {
		boolean ret = true;
		if(VideoEngine.isBlank(strs)) return ret=true;
		ret = strs.matches("^\\s*$");
		return ret;
	}
	
	public int getResults(List<VideoInfo> resultsList, String query, int page, int timeout) {
		if (VideoEngine.isWhitespace(query)) return 0;
		if (page <= 0) page = 1;
		if (timeout <= 0) timeout = 100000; 
		return getMyResults(resultsList, query, page, timeout);
	}
}
