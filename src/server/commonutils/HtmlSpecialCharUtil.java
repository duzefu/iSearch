package server.commonutils;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import edu.stanford.smi.protege.ui.SpringUtilities;

/**
 * 用于处理html文档中的特殊符号，有两种可能的用途： 
 * 1. 从成员搜索引擎获得结果后，为了避免其中出现的部分特殊符号（特别是摘要，内容形式如&nbsp，将其实际的内容转换回来；）
 * 2.Jsoup.clean函数，使用该函数可以把从html中提取的文本中恶意文本
 * （脚本）删除，但是这个函数把其中的符号全部转成html的标识符，空格变成&nbsp，使用这个类的工具转换回去
 * 
 * unescapeUrl函数的作用是：URL里面出现如“%3D”之类的字符时，可以转回中文；
 * 该函数与字符集有关，目前设置为gb2312，对于百度搜索结果中出现的百度贴吧URL转码正常。
 * 该函数不是非常必要时不要使用。
 * @author zhou
 *
 */
public class HtmlSpecialCharUtil {

	public static Document unEscapeHtml(Document doc){
		
		if(null==doc) return null;
		return Jsoup.parse(HtmlSpecialCharUtil.unEscapeHtml(doc.html()));
	}
	
	public static String unEscapeHtml(String doc){
		
		if(null==doc) return null;
		return StringEscapeUtils.unescapeHtml(doc);
	}
	
	public static Document escapeHtml(Document doc){
		
		if(null==doc) return null;
		return Jsoup.parse(HtmlSpecialCharUtil.escapeHtml(doc.html()));
	}
	
	public static String escapeHtml(String doc){
		
		if(null==doc) return null;
		return StringEscapeUtils.escapeHtml(doc);
	}
	
}
