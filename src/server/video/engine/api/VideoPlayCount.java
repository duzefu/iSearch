package server.video.engine.api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class VideoPlayCount {
	
	private static String NOFOUND = "0";
	private static int TIMEOUT = 10000;
	
	private Matcher getMatcher(String pattern, String text) {
		Pattern p1 = Pattern.compile(pattern);
		Matcher m1 = p1.matcher(text);
		if(m1.find()) {
			return m1;
		}
		else return null;
	}
	
	private String replacePoint(String text) {
		String replaceText = text.replaceAll("\\.", ",");
		return replaceText;
	}

	private String promotePlayCount(String text) {
		String pattern = "([\\d\\,\\.]+)(\\u4e07)?(\\u4ebf)?";
        Matcher matcher = getMatcher(pattern, text);
        if(matcher != null) {
            String playCount = matcher.group(1);
            String more = matcher.group(2);
            String morethanmore = matcher.group(3);

            if(more != null && !more.equals("")) playCount += "00000";
            if(morethanmore != null && !morethanmore.equals("")) playCount += "000000000";
            return replacePoint(playCount);
        }
        return text;
	}
	
	private Document getDocument(String url, boolean isIgnore) {
		try {
			Document document = null;
			if(isIgnore)
			{
				document =  Jsoup.connect(url).ignoreContentType(true)
						.userAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
						.timeout(TIMEOUT).get();
			}
			else {
				document =  Jsoup.connect(url)
						.userAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
						.timeout(TIMEOUT).get();
			}
                    
			return document;
		}
		catch(Exception e) {
//			e.printStackTrace();
			return null;
		}
	}
	
	protected  String getResponseByUrl(String url, int timeout) {
        try {
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
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
	
	private  String getBaiduPlayCount(String text) {
		String pattern = "src\\=\"([\\w\\d\\/\\.\\?\\&\\=\\:\\;]+)\"";
        Matcher matcher = getMatcher(pattern, text);
        if(matcher != null) {
            String url = matcher.group(1);
            Document document = getDocument(url, false);
            if(document != null) {
            	String playCount = document.select("div.title-cont").select("p.title-info").select("span.play").text();
                return promotePlayCount(playCount);
            }
        }
		return NOFOUND;
	}
	
	private  String getBaomihuaPlayCount(String text) {
		String pattern = "var\\svvsum\\s\\=\\s\"([\\d\\,\\.]+)(\\u4e07)?(\\u4ebf)?\"";
        Matcher matcher = getMatcher(pattern, text);
        if(matcher != null) {
            return promotePlayCount(matcher.group(1));
        }
        return NOFOUND;
	}
	
	private  String getYoukuPlayCount(String text) {
		//获取videoID和catId
        String dynamic = "http://v.youku.com/QVideo/~ajax/getVideoPlayInfo?id=%s&sid=0&type=vv&catid=%s";
        String p1 = "var\\s?videoId\\s?\\=\\s?[\'\"](\\d+)[\'\"]\\;";
        String p2 = "var\\s?catId\\s?\\=\\s?[\'\"](\\d+)[\'\"]\\;";
        Matcher m1 = getMatcher(p1, text);
        Matcher m2 = getMatcher(p2, text);
        if(m1 != null && m2 != null) {
            String url = String.format(dynamic, m1.group(1), m2.group(1));
            Map<String, Object> maps = parseJSON2Map(getResponseByUrl(url, TIMEOUT));
            return promotePlayCount(replacePoint(String.valueOf(maps.get("vv"))));
        }
        return NOFOUND;
	}
	
	private String getIqiyiPlayCount(String text) {
		String dynamic = "http://mixer.video.iqiyi.com/jp/mixin/videos/%s?callback=window.Q.__callbacks__.cb7rpoue&status=1";
        String pattern = "(tvId\\:)\\s?[\'\"]?(\\d+)[\'\"]?";
        Matcher matcher = getMatcher(pattern, text);
        if(matcher != null) {
            String url = String.format(dynamic, matcher.group(2));
            Document document = getDocument(url,true);
            if(document != null) {
            	String playCount_pattern = "\"playCount\"\\s?\\:\\s?([\\d\\,\\.]+)\\+?(\\u4e07)?(\\u4ebf)?";
            	Matcher matcher2 = getMatcher(playCount_pattern, document.toString());
            	if(matcher2 != null) {
            		String playCount = matcher2.group(1);
                    String more = matcher2.group(2);
                    String morethanmore = matcher2.group(3);
                    
                    if(more != null && !more.equals("")) playCount += "00000";
                    if(morethanmore != null && !morethanmore.equals("")) playCount += "000000000";
                    return replacePoint(playCount);
            	}
            }
        }
        return NOFOUND;
	}
	
	private String getTudouPlayCount(String text) {
		//可以匹配icode取得后缀
        String dynaimc = "http://dataapi.youku.com/getData?jsoncallback=page_play_model_exponentModel__getNum&num=200001&icode=%s";
        String pattern = "icode\\:\\s?[\'\"]([\\d\\w]+)[\'\"]";
        Matcher matcher = getMatcher(pattern, text);
        if(matcher != null) {
            String url = String.format(dynaimc, matcher.group(1));
            Document document = getDocument(url, false);
            if(document != null) {
            	String pattern_json = "\\(([\\[\\]\\{\\}\\w\\d\\,\\:\"\\.]+)\\)";
            	Matcher matcher2 = getMatcher(pattern_json, document.toString());
            	if(matcher2 != null) {
            		Map<String, Object> maps = parseJSON2Map(matcher2.group(1));
                    JSONObject result = (JSONObject)maps.get("result");
                    return promotePlayCount(replacePoint(String.valueOf(result.get("vv"))));
            	}
            }
        }
        return NOFOUND;
	}
	
	private String getKu6PlayCount(String text) {
		String dynamic = "http://v0.stat.ku6.com/dostatv.do?method=getVideoPlayCount&v=%s";
        String pattern = "vid=([\\w\\d\\.]+)";
        Matcher matcher = getMatcher(pattern, text);
        if(matcher != null) {
            String url = String.format(dynamic, matcher.group(1));
            Document document = getDocument(url, false);
            if(document != null) {
            	String pattern_json = "\\[([\\{\\}\\w\\d\\:\\,\"\\.]+)\\]";
            	Matcher matcher2 = getMatcher(pattern_json, document.toString());
            	if(matcher2 != null) {
            		return promotePlayCount(replacePoint(String.valueOf(parseJSON2Map(matcher2.group(1)).get("count"))));
            	}
            }
        }
        return NOFOUND;
	}
	
	private String getPPTVPlayCount(String text) {
		String pattern = "<a href=\"###\" title=\"\"><i class=\"ic7\"></i>([\\d\\.\\,]+)(\\u4e07)?(\\u4ebf)?次播放</a>";
        Matcher matcher = getMatcher(pattern, text);
        if(matcher != null) {
            String playCount = matcher.group(1);
            String more = matcher.group(2);
            String morethanmore = matcher.group(3);

            if(more != null && !more.equals("")) playCount += "00000";
            if(morethanmore != null && !morethanmore.equals("")) playCount += "000000000";
            return replacePoint(playCount);
        }
        return NOFOUND;
	}
	
	private String getLetvPlayCount(String text) {
		// 获取vid,pid以及cid
        String dynamic = "http://v.stat.letv.com/vplay/queryMmsTotalPCount?callback=cid=%s&vid=%s&pid=%s";
        String pattern = "cid\\:\\s*?(\\d+)\\,\\s*pid\\:\\s*?(\\d+)\\,\\s*vid\\:\\s*(\\d+)";
        Matcher matcher = getMatcher(pattern, text);
        if(matcher != null) {
        	 String cid = matcher.group(1);
             String pid = matcher.group(2);
             String vid = matcher.group(3);
             String url = String.format(dynamic, cid, vid, pid);
            Document document = getDocument(url, false);
            if(document != null) {
            	String pattern_playcount = "\"plist_play_count\"\\s?\\:\\s?([\\d\\,\\.]+)\\+?(\\u4e07)?(\\u4ebf)?";
            	Matcher matcher2 = getMatcher(pattern_playcount, document.toString());
            	if(matcher2 != null) {
            		String playCount = matcher2.group(1);
                    String more = matcher2.group(2);
                    String morethanmore = matcher2.group(3);

                    if(more != null && !more.equals("")) playCount += "00000";
                    if(morethanmore != null && !morethanmore.equals("")) playCount += "000000000";
                    return replacePoint(playCount);
            	}
            }
        }
        return NOFOUND;
	}
	
	private String getTencentPlayCount(String text) {
		//只需要获取id
        String dynamic = "http://data.video.qq.com/fcgi-bin/data?tid=70&appid=10001007&appkey=e075742beb866145&callback=&low_login=1&idlist=%s&otype=json";
        String pattern = "id\\s?\\:[\"\']([\\d\\w]+)[\'\"]\\,";
        Matcher matcher = getMatcher(pattern, text);
        if(matcher != null) {
            String url = String.format(dynamic, matcher.group(1));
            Document document = getDocument(url, true);
            if(document != null) {
            	String pattern_json = "(\\{[\\[\\]\\{\\}\\w\\d\\,\\:\"\\.\\_]+\\})";
            	Matcher matcher2 = getMatcher(pattern_json, document.toString());
            	if(matcher2 != null) {
            		List<Map<String, Object>> objarr = (List<Map<String, Object>>)parseJSON2Map(matcher2.group(1)).get("results");
                    JSONObject obj = (JSONObject)objarr.get(0).get("fields");
                    return promotePlayCount(replacePoint(String.valueOf(obj.get("allnumc"))));
            	}
            }
        }
        return NOFOUND;
	}
	
	private String getSohuPlayCount(String text) {
		String dynamic = "http://vstat.v.blog.sohu.com/dostat.do?method=getVideoPlayCount&v=%s&n=bvidmenu_box_playlist";
		String pattern = "var\\s?vid\\s?\\=\\s[\"\'](\\d+)[\"\']\\;";
        Matcher matcher = getMatcher(pattern, text);
        if(matcher != null) {
            String url = String.format(dynamic, matcher.group(1));
            Document document = getDocument(url, false);
            if(document != null) {
            	String p1 = "\\[([\\[\\]\\{\\}\\w\\d\\,\\:\"\\.\\_]+)\\]";
                Matcher m1 = getMatcher(p1, document.toString());
                if(m1 != null) {
                	return replacePoint(String.valueOf(parseJSON2Map(m1.group(1)).get("count")));
                }
            }
        }
        return NOFOUND;
	}
	
	private String get1905PlayCount(String text) {
		String pattern = "hits\\s?\\:\\s?[\'\"]([\\d\\.\\,]+)(\\u4e07)?(\\u4ebf)?[\'\"]";
        Matcher matcher = getMatcher(pattern, text);
        if(matcher.find()) {
            String playCount = matcher.group(1);
            String more = matcher.group(2);
            String morethanmore = matcher.group(3);

            if(more != null && !more.equals("")) playCount += "00000";
            if(morethanmore != null && !morethanmore.equals("")) playCount += "000000000";
            return replacePoint(playCount);
        }
        return NOFOUND;
	}
	
	private String getWasuPlayCount(String text) {
		String pattern = "<p><span>播放：([\\d\\.\\,]+)(\\u4e07)?(\\u4ebf)?</span></p>";
        Matcher matcher = getMatcher(pattern, text);
        if(matcher != null) {
            String playCount = matcher.group(1);
            String more = matcher.group(2);
            String morethanmore = matcher.group(3);

            if(more != null && !more.equals("")) playCount += "00000";
            if(morethanmore != null && !morethanmore.equals("")) playCount += "000000000";
            return replacePoint(playCount);
        }
        return NOFOUND;
	}
	
	private String getFeixiangPlayCount(String text) {
		String pattern = "<span class=\"a5\" id=\"hitmsg\">([\\d\\.\\,]+)(\\u4e07)?(\\u4ebf)?次播放</span>";
        Matcher matcher = getMatcher(pattern, text);
        if(matcher != null) {
            String playCount = matcher.group(1);
            String more = matcher.group(2);
            String morethanmore = matcher.group(3);

            if(more != null && !more.equals("")) playCount += "00000";
            if(morethanmore != null && !morethanmore.equals("")) playCount += "000000000";
            return replacePoint(playCount);
        }
        return NOFOUND;
	}
	
	private String get56PlayCount(String text) {
		//从text获取EndId, 也可以从url中获取../v_EnId.html
		String dynamic = "http://vstat.v.blog.sohu.com/vv/?id=%s&callback=jsonp_flv_sohu";
		String pattern = "[\'\"]EnId[\"\']\\:[\"\']([\\d\\w]+)[\"\']";
		Matcher matcher = getMatcher(pattern, text);
		if(matcher != null) {
			String url = String.format(dynamic, matcher.group(1));
			Document document = getDocument(url, false);
			if(document != null) 
				return promotePlayCount(document.toString());
		}
        return NOFOUND;
	}
	
	public String VideoSelector(String url) {
		Document document = getDocument(url, false);
		if (document == null) {
			return NOFOUND;
		}
		String text = document.toString();
		if(url.startsWith("http://baidu")) {
			String Baidu = getBaiduPlayCount(text);
//			System.out.println(String.format("Baidu: %s  %s",url, Baidu));
			return Baidu;
		}
		else if(url.startsWith("http://video.baomihua.com")) {
			String Baomihua = getBaomihuaPlayCount(text);
//			System.out.println(String.format("Baomihua: %s  %s",url, Baomihua));
			return Baomihua;
		}
		else if(url.startsWith("http://v.youku.com")) {
			String Youku = getYoukuPlayCount(text);
//			System.out.println(String.format("Youku: %s  %s",url, Youku));
			return Youku;
		}
		else if(url.startsWith("http://www.iqiyi.com")) {
			String Iqiyi = getIqiyiPlayCount(text);
//			System.out.println(String.format("Iqiyi: %s  %s",url, Iqiyi));
			return Iqiyi;
		}
		else if(url.startsWith("http://www.tudou.com")) {
			String Tudou = getTudouPlayCount(text);
//			System.out.println(String.format("Tudou: %s  %s",url, Tudou));
			return Tudou;
		}
		else if(url.startsWith("http://v.ku6.com")) {
			String Ku6 = getKu6PlayCount(text);
//			System.out.println(String.format("Ku6: %s  %s",url, Ku6));
			return Ku6;
		}
		else if(url.startsWith("http://v.pptv.com")) {
			String PPTV = getPPTVPlayCount(text);
//			System.out.println(String.format("PPTV: %s  %s",url, PPTV));
			return PPTV;
		}
		else if(url.startsWith("http://www.le.com") || url.startsWith("http://www.letv.com")) {
			String Letv = getLetvPlayCount(text);
//			System.out.println(String.format("Letv: %s  %s",url, Letv));
			return Letv;
		}
		else if(url.startsWith("http://v.qq.com")) {
			String Tencent = getTencentPlayCount(text);
//			System.out.println(String.format("Tencent: %s  %s",url, Tencent));
			return Tencent;
		}
		else if(url.startsWith("http://www.feixiangtv.com")) {
			String Feixiang = getFeixiangPlayCount(text);
//			System.out.println(String.format("Feixiang: %s  %s",url, Feixiang));
			return Feixiang;
		}
		else if(url.startsWith("http://my.tv.sohu.com")) {
			String Sohu = getSohuPlayCount(text);
//			System.out.println(String.format("Sohu: %s  %s",url, Sohu));
			return Sohu;
		}
		else if(url.startsWith("http://www.1905.com")) {
			String M1905 = get1905PlayCount(text);
//			System.out.println(String.format("M1905: %s  %s",url, M1905));
			return M1905;
		}
		else if(url.startsWith("http://www.wasu.cn")) {
			String Wasu = getWasuPlayCount(text);
//			System.out.println(String.format("Wasu: %s  %s",url, Wasu));
			return Wasu;
		}
		else if(url.startsWith("http://www.56.com")) {
			String M56 = get56PlayCount(text);
//			System.out.println(String.format("M56: %s  %s",url, M56));
			return M56;
		}
		else if(url.startsWith("http://360kan")) {
			String pattern = "url=([\\d\\w\\/\\:\\.\\-\\_\\%\\#\\?]+)";
			Matcher matcher = getMatcher(pattern, url);
			if(matcher != null) {
				try {
					String url2 = URLDecoder.decode(matcher.group(1), "UTF-8");
					return VideoSelector(url2);
				} 
				catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return NOFOUND;
	}
}
