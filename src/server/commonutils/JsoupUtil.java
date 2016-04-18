package server.commonutils;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;

/**
 * Jsoup的封装，
 * 实现一些根据URL获取Document对象或者根据Document、Element对象提取其中的文本的接口
 * @author zhou
 *
 */
public class JsoupUtil {

	/**
	 * 在获得网页时，通常来说，
	 * 网页<head>标签中的<meta content="text/html;charset=xxx">中指定了这个网页文件的字符集，
	 * 但是程序员在写这个页面时，如果不注意，很有可能这里是错的，
	 * 例如在windows下写的程序，网页的字符集实际上可能是gbk，但程序员在这里写了gb2312或者utf8，
	 * 这将导致jsoup.connect直接获得的网页中出现乱码，
	 * 所以需要针对网页来指定字符集（最好是找字符集自动探测的开源项目）。
	 * 这里指定了在调用这个类相关函数时能够指定的字符集，用来检查传入的字符集参数是否正确。
	 */
	private final static Set<String> charsetNames=new HashSet<String>();
	private final static int DEFAULT_TIMEOUT=10000;
	private final static int DEFAULT_RETRY_TIMES=4;
	
	static{
		charsetNames.add("utf8");
		charsetNames.add("utf-8");
		charsetNames.add("UTF8");
		charsetNames.add("UTF-8");
		charsetNames.add("gbk");
		charsetNames.add("GBK");
		charsetNames.add("GB2312");
		charsetNames.add("gb2312");
		charsetNames.add("ISO8859-1");
	}
	
	private final static boolean isLegalCharset(String charset){
		return charsetNames.contains(charset);
	}
	
	/**
	 * 获取url对应的html文件，超时时间10秒
	 * @param url URL，不可为空
	 * @return 获得的HTML文件，可能为null（如访问超时）
	 * @throws IOException 连接异常
	 */
	public static Document getHtmlDocument(String url) throws IOException {
		return JsoupUtil.getHtmlDocument(url, -1, null,null);
	}

	/**
	 * 获取url对应的html文件
	 * @param url URL，不能为空
	 * @param timeout 超时时间，不超过1毫秒时等同于10秒
	 * @return 获得的HTML文件，可能为null（如访问超时）
	 * @throws IOException 连接打开异常
	 */
	public static Document getHtmlDocument(String url, int timeout) throws IOException {
		return JsoupUtil.getHtmlDocument(url, timeout, null, null);
	}
	
	/**
	 * 获取url对应的html文件
	 * @param url 基础URL，不可为空
	 * @param timeout 连接超时时间，不超过1毫秒时会被设置为10秒
	 * @param pageCharset URL所获取的网页对应的字符集，设置为null时，会按照网页上相关元素指定的字符集来解析
	 * @return html文档，如果解析过程发生异常，如超时，会返回null
	 * @throws IOException 连接异常
	 */
	public static Document getHtmlDocument(String url, int timeout, String pageCharset) throws IOException{
		return JsoupUtil.getHtmlDocument(url, timeout, null, pageCharset);
	}
	
	/**
	 * 获取url对应的html文件
	 * @param url 基础URL，不可为空
	 * @param timeout 连接超时时间，不超过1毫秒时会被设置为10秒
	 * @param paramlist 参数列表，其中map的key:value最终对应于URL中的xxx=xxx，可为null
	 * @param pageCharset URL所获取的网页对应的字符集，设置为null时，会按照网页上相关元素指定的字符集来解析
	 * @return html文档，如果解析过程发生异常，如超时，会返回null
	 * @throws IOException 连接异常
	 */
	public static Document getHtmlDocument(String url, int timeout,
			Map<String, String> paramlist, String pageCharset) throws IOException{

		Document ret = null;
		int halfTOut;
		
		if (MyStringChecker.isBlank(url)) return ret;

		if (timeout <= 0) timeout = DEFAULT_TIMEOUT;
		halfTOut = timeout >> 1;
		
		if (!MyStringChecker.isBlank(pageCharset)&&!isLegalCharset(pageCharset)) pageCharset = null;
		if (!url.matches("http(\\S)*")) url = "http://" + url;

		URLConnection conn =new URL(url).openConnection();
		
		conn.setConnectTimeout(halfTOut);
		conn.setReadTimeout(halfTOut);
		if (null != paramlist) {
			Iterator<String> itkeys = paramlist.keySet().iterator();
			while (itkeys.hasNext()) {
				String key = itkeys.next(), value = paramlist.get(key);
				conn.addRequestProperty(key, value);
			}
		}
		ret = Jsoup.parse(conn.getInputStream(), pageCharset, url);

		return ret;
	}
	
	/**
	 * Jsoup获取到的Html文档得到文档中的文本
	 * @param doc Html文件
	 * @return 如果doc为null或者其中提取不到title或body两类元素，将返回空字符串；否则返回title+body中的文本。
	 */
	public static String getTextInHtml(Document doc){
		
		if(null==doc) return "";
		
		String ret=null, title=JsoupUtil.getTitle(doc), body=JsoupUtil.getBodyText(doc);
		ret=(ret==null?"":ret)+JsoupUtil.getBodyText(doc);
		
		return ret;
	}
	
	/**
	 * Jsoup获取到的Html文档中某个元素下的文本（包括子元素）
	 * @param doc Html文件中的某一个元素结点
	 * @return 如果doc为null，将返回空字符串
	 */
	public static String getTextInHtml(Element element){
		
		if(null==element) return "";
		
		String ret=null;
		ret=element.hasText()?element.text():"";
		ret=Jsoup.clean(ret, Whitelist.simpleText());
		ret=HtmlSpecialCharUtil.unEscapeHtml(ret);
		
		return ret;
	}
	
	/**
	 * 获取url对应的网页，然后取到其中body元素下面的所有文本； 如果取url的内容发生异常时，返回null
	 * 
	 * @param url
	 * @return
	 */
	public static String getBodyText(String url) {

		String ret = null;
		if (null == url || url.isEmpty())
			return ret;

		Document doc = null;
		try {
			doc = JsoupUtil.getHtmlDocument(url);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return JsoupUtil.getBodyText(doc);
	}

	/**
	 * 取html文件doc下body元素中的文本
	 * 
	 * @param doc
	 *            html文件
	 * @return doc中的正文文本
	 */
	public static String getBodyText(Document doc) {

		if (null == doc)
			return null;
		String ret = null;
		Element body = doc.body();
		if (null != body)
			ret = body.hasText() ? body.text() : "";
		ret = Jsoup.clean(ret, Whitelist.simpleText());
		ret = HtmlSpecialCharUtil.unEscapeHtml(ret);
		return ret;
	}

	/**
	 * 取url对应的网页，并取到其中的标题
	 * 
	 * @param url
	 *            URL
	 * @return url对应网页的标题
	 */
	public static String getTitle(String url) {

		String ret = null;
		if (null == url || url.isEmpty())
			return ret;
		Document doc = null;

		try {
			doc = JsoupUtil.getHtmlDocument(url);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return JsoupUtil.getTitle(doc);
	}

	/**
	 * 取到html文件doc中的标题
	 * 
	 * @param doc
	 *            html文件
	 * @return 其中的标题
	 */
	public static String getTitle(Document doc) {

		if (null == doc)
			return null;
		doc=HtmlSpecialCharUtil.unEscapeHtml(doc);
		String title = doc.title();
		if (null != title)
			title = Jsoup.clean(title, Whitelist.simpleText());
		if (null != title)
			title = HtmlSpecialCharUtil.unEscapeHtml(title);
		return title;
	}

}
