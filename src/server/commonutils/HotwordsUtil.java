package server.commonutils;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import db.dao.HotwordsDao;
import server.info.config.SpringBeanNames;

public class HotwordsUtil {
	
	private final static String HOTWORDS_URL = "http://top.baidu.com/buzz.php?p=top10";
	private final static int HOTWORDS_TIMEOUT=10000;//获取热点超时时间为10秒
	
	/*
	 * 可能有多个线程都进入刷新实时热点词的函数（临界区），
	 * 这里实现为：
	 * 最先进入函数的那个线程完成一次更新动作并设置相应的更新时间，
	 * 后续线程如果仍然进入临界区，通过时间间隔设置（5分钟内），可以避免再次更新
	 */
	private final static int NO_UPDATE_INTERVAL=5*60*1000;
	private Date lastUpdate;
	
	private HotwordsDao dao;
	
	private HotwordsUtil(){
		lastUpdate=new Date(0);
	}
	
	private static HotwordsUtil ins;
	
	private static HotwordsUtil getInstance(){
		if(null==ins){
			synchronized (HotwordsUtil.class) {
				if(null==ins) ins=new HotwordsUtil();
			}
		}
		return ins;
	}
	
	private final HotwordsDao getHotwordsDao(){
		
		if(null==dao){
			synchronized (this) {
				dao=(HotwordsDao) SpringBeanFactoryUtil.getBean(SpringBeanNames.HOTWORDS_DAO_BEAN_NAME);
			}
		}
		return dao;
	}
	
	/**
	 * 对外接口，获取实时热点词，如果数据库中还没有相关的词，则从百度获取一次
	 * @param ret 返回值
	 * 
	 * @return 本次获得的实时热点词数量
	 */
	public static int getHotwords(List<String> ret){
		
		if(null==ret) return 0;
		return getInstance().getHotwordsIns(ret);
	}
	
	/**
	 * 对外接口，更新数据库中的实时热点词
	 * @return 本次获得的实时热点词数量
	 */
	public static int updateHotwords(){
		return getInstance().updateHotwordsIns(null);
	}
	
	/**
	 * 获得实时热点词
	 * @param ret 用于返回实时热点词，不能为null
	 * @return 本次获得的实时热点词数量，可能为0
	 */
	private int getHotwordsIns(List<String> ret){
		
		/*
		 * 逻辑：
		 * 		1）从数据库获取当天的热点词；
		 * 		2）如果结果为空，调用实时热点词更新函数，向百度获取一次；
		 * 		3）返回（即使结果仍然为空）。
		 * 其中，被调用的函数要负责：
		 * 		1）负责从百度获取实时热点词的功能（线程互斥）；
		 * 		2）检查获取时间间隔，上一次获取的时间很近，不应该再请求；
		 * 		3）把新获得的实时热点词插入数据库。
		 */
		int count=0;
		if(null==ret) return count;
		count=getHotwordsDao().getWords(ret,Calendar.getInstance().getTime());
		if(0==count){
			count=updateHotwordsIns(ret);
		}
		return count;
	}
	
	/**
	 * 更新数据库中的实时热点词（线程互斥）
	 * @param ret 本次从百度获得的实时热点词的存放位置；可以为null，为null时表示不需要返回获得的新词
	 * @return 本次获得的新词的数量
	 */
	private int updateHotwordsIns(List<String> ret) {

		int count = 0;
		if (null == ret) return count;
		
		boolean updated = false;
		Date now = Calendar.getInstance().getTime();
		synchronized (this) {
			updated = now.getTime() - lastUpdate.getTime() >= NO_UPDATE_INTERVAL;
			if (updated) {
				count = getHotWordsFromBaidu(ret);
				if (count > 0) getHotwordsDao().updateHotwords(ret, Calendar.getInstance().getTime());
				lastUpdate = now;
			}
		}
		if (!updated) count = getHotwordsDao().getWords(ret, now);
		
		return count;
	}

	/**
	 * 从百度获取实时热点词
	 * @param words 返回值
	 * @return 本次获取的实时热点词数量
	 */
	private int getHotWordsFromBaidu(List<String> words) {
		
		int count = 0;
		Document doc = null;
		if (null != words) {
			try {
				//百度实时热点网页的字符集实际上是GBK，但设计网页的人在html头部写成了gb2312，
				//这里强行指定按gbk解码，否则某些字会乱码
				//如果以后发现乱码要留意这里
				doc = JsoupUtil.getHtmlDocument(HOTWORDS_URL, HOTWORDS_TIMEOUT, "gbk");
				if(null==doc) return count;
				Elements tables = doc.select("td.keyword");
				if(null==tables||tables.isEmpty()) return count;
				for (Element table : tables) {
					try {
						Element title = table.select("a").first();
						words.add(title.text());
						++count;
					} catch (Exception e) {
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return count;
	}
	
}
