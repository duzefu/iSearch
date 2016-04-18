package struts.actions.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Comparator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

import common.utils.querypreprocess.DealingWithQuery;
import oracle.sql.NUMBER;
import server.video.engine.api.BaiduVideo;
import server.video.engine.api.BingVideo;
import server.video.engine.api.JedisVideo;
import server.video.engine.api.VideoEngine;
import server.video.engine.api.VideoInfo;
import server.video.engine.api.YoudaoVideo;

class ThreadEngine extends Thread {
	
	private List<VideoInfo> m_results;
	private VideoEngine m_videoEngine;
	private Lock m_lock;
	private String m_query;
	private int m_timeout;
	private int m_ptr;
	private CountDownLatch m_countDownLatch;
	
	private static final int BLANK = 1;
	
	public ThreadEngine(List<VideoInfo> results, VideoEngine videoEngine, Lock lock, String query,int timeout, int ptr, CountDownLatch countDownLatch) {
		// TODO Auto-generated constructor stub
		this.m_results = results;
		this.m_videoEngine = videoEngine;
		this.m_lock = lock;
		this.m_query = query;
		this.m_timeout = timeout;
		this.m_ptr = ptr;
		this.m_countDownLatch = countDownLatch;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		int amount = -1;
		try{
			
			for(int i = m_ptr - BLANK > 0 ? m_ptr - BLANK : 1; i <= m_ptr; ++i) {
//				this.m_lock.lock();
				int page = i;
				if(this.m_results == null) {
					this.m_lock.unlock();
					break;
				}
				amount = this.m_videoEngine.getResults(this.m_results, this.m_query, page, this.m_timeout);
				if(amount <= 0) {
//					this.m_lock.unlock();
					break;
				}
//				this.m_lock.unlock();
			}
		}
		catch(InternalError e) {
			e.printStackTrace();
		}
		finally {
			m_countDownLatch.countDown();
		}
	}
}

class WorkThread extends Thread {
	private static final JedisVideo JEDIS = new JedisVideo();
	private static final int NUM = 20;
	private HttpSession m_httpCurSession;
	private int m_page;
	private CountDownLatch m_countDownLatch;
	private List<VideoInfo> m_result;
	private String m_query;
	
	public WorkThread(List<VideoInfo> result, String query, int page, CountDownLatch countDownLatch, HttpSession httpCurSession) {
		this.m_httpCurSession = httpCurSession;
		this.m_page = page;
		this.m_countDownLatch = countDownLatch;
		this.m_result = result;
		this.m_query = query;
	}
	
	@Override
	public void run() {
		try{
			m_countDownLatch.await();
//			System.out.println("Begin start redis");
			int count = 0;
			List<VideoInfo> temp = new ArrayList<>();
			for(VideoInfo videoInfo:this.m_result) {
				if(count % NUM == 0) {
					if(temp.size() == NUM) {
						String key = String.format("%s+%s+%s", m_httpCurSession.getId(), m_query, m_page + count / NUM - 1);
//						System.out.println(m_page + count / NUM - 1);
						try {
							boolean sign = JEDIS.setVideoInfoList(key, temp);
							if(sign != true) {
//								System.out.println("Fuck Redis");
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						temp.clear();
					}
				}
				temp.add(videoInfo);
				count ++;
			}
//			System.out.println("Begin stop redis");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}

public class VideoSearchAction extends ActionSupport{
	
	private static final int NUM = 20; // 每一页展示的视频数量
	private static final int THREADNUM = 3;
	private static int TIMEOUT = 100000;
	
	private int TEST = 0;
	
	//搜索引擎
	private BaiduVideo m_BaiduVideo = new BaiduVideo();
	private YoudaoVideo m_YoudaoVideo = new YoudaoVideo();
	private BingVideo m_BingVideo = new BingVideo();
	
	//redis服务器
	private static JedisVideo JEDIS = new JedisVideo();
	
	private Lock m_lock = new ReentrantLock();
	private boolean FIRST = true;
	private String m_query; //用户查询关键字
	private int m_page; //用户请求的页数
	private List<VideoInfo> m_results = new ArrayList<>(); //搜索得出的结果
	private List<VideoInfo> m_allresults = new ArrayList<>();
	private HttpSession m_httpCurSession ;
	private int CHECK = -1;
	
	private int beforeSearch() {
		int res = -1;
		this.m_httpCurSession= ServletActionContext.getRequest().getSession();
		List<VideoInfo> temp = null;
		String key = String.format("%s+%s+%s", this.m_httpCurSession.getId(), m_query, m_page);
		try {
			temp = JEDIS.getVideoInfoList(key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(temp != null && temp.size() >= 20) {
			m_results = temp;
			res = 1;
			if(temp.size() >= 40) {
				res = 0;
			}
		}
		return res;
	}
	
	@Override
	public String execute() throws Exception {
		
		try{
			long startTime = System.currentTimeMillis();
			m_httpCurSession= ServletActionContext.getRequest().getSession();
			CHECK = beforeSearch();
			if(CHECK != 0) {
				ThreadEngine threadEngine[] = new ThreadEngine[THREADNUM];
				if(CHECK != -1) {
					String query = VideoInfo.getQuery();
					if(!(query != null && !query.isEmpty() && query.equals(m_query))) {
						CHECK = -1;
					}
				}
				if(CHECK == -1) {
					VideoInfo.setQuery(m_query);
					FIRST = true;
				}
				CountDownLatch countDownLatch = new CountDownLatch(THREADNUM);
				threadEngine[0] = new ThreadEngine(m_allresults, m_YoudaoVideo, m_lock, m_query, TIMEOUT, m_page + 1, countDownLatch);
				threadEngine[1] = new ThreadEngine(m_allresults, m_BingVideo, m_lock, m_query, TIMEOUT, m_page + 1, countDownLatch);
				threadEngine[2] = new ThreadEngine(m_allresults, m_BaiduVideo, m_lock, m_query, TIMEOUT, m_page + 1, countDownLatch);
				for(ThreadEngine threadEngine2: threadEngine) {
					threadEngine2.start();
				}
				if(CHECK == -1) {
					try {
						for(ThreadEngine threadEngine2 : threadEngine) {
							threadEngine2.join();
						}
					}
					catch(InterruptedException e) {}
				}
				else {
					if(m_allresults != null) {
						m_allresults = sort(m_allresults);
						WorkThread workThread = new WorkThread(m_allresults, m_query, m_page, countDownLatch, m_httpCurSession);
						workThread.start();
					}
				}
			}
			System.out.println(m_allresults.size());
			long endTime = System.currentTimeMillis();
			System.out.print("Spending Time: ");
			System.out.println(endTime - startTime);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}// end execute
	
	public void setQuery(String query) {
		this.m_query = DealingWithQuery.CorrectQuery(query);
	}
	
	public String getQuery() {
		return this.m_query;
	}
	
	public int getPage() {
		return this.m_page;
	}
	
	public void setPage(int page) {
		this.m_page = page;
	}
	public void setResults(List<VideoInfo> results) {
		this.m_results = results;
	}
	
	private List<VideoInfo> sort(List<VideoInfo> lists) {
		if(lists.size() == 0) return lists;
		VideoInfo pivot = lists.get(0);
		List<VideoInfo> lresult = new ArrayList<>();
		List<VideoInfo> rresult = new ArrayList<>();
		for(VideoInfo videoInfo:lists) {
			if(videoInfo == null || videoInfo == pivot) continue;
			if(videoInfo.Weight() <= pivot.Weight()) {
				lresult.add(videoInfo);
			}
			else {
				rresult.add(videoInfo);
			}
		}
		List<VideoInfo> sorted = new ArrayList<>();
		sorted.addAll(sort(lresult));
		sorted.add(pivot);
		sorted.addAll(sort(rresult));
		return sorted;
	}
	
	private void filterResults() {
		try {
			HashSet<VideoInfo> setVideo = new HashSet<>();
			System.out.println(m_allresults.size());
			setVideo.addAll(m_allresults);
			m_allresults.clear();
			m_allresults.addAll(setVideo);

			int count = 0;
			int redis_count = 0;
			List<VideoInfo> temp = new ArrayList<>();
			for(VideoInfo videoInfo:m_allresults) {
				if(count % NUM == 0) {
//					System.out.print("Temp result size: ");
//					System.out.println(temp.size());
					if(temp.size() == NUM) {
						String key = String.format("%s+%s+%s", m_httpCurSession.getId(), m_query, m_page + count / NUM - 1);
						try {
							boolean sign = JEDIS.setVideoInfoList(key, temp);
//							System.out.print("Redis Count: ");
//							System.out.println(redis_count);
							redis_count ++;
							if(sign != true) {
								System.out.println("Fuck Redis");
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							System.out.println("WTF on the redis set");
							e.printStackTrace();
						}
						temp.clear();
					}
				}
				temp.add(videoInfo);
				count ++;
			}
//			System.out.print("Count number: ");
//			System.out.println(count);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public List<VideoInfo> getResults() {
		
		if(FIRST == true) {
			filterResults();
			FIRST = false;
		}
		if(m_results.size() == 0) {
			String key = String.format("%s+%s+%s", m_httpCurSession.getId(), m_query, m_page);
			try {
				m_results = JEDIS.getVideoInfoList(key);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		m_results = sort(m_results);
		int length = m_results.size();
		if(length < NUM) {
			return m_results.subList(0, length);
		}
		return m_results.subList(0, NUM);
	}
}
