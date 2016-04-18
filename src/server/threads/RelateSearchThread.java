package server.threads;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import server.commonutils.LogU;
import server.commonutils.MyStringChecker;
import server.engine.api.Baidu;
import common.entities.searchresult.ResultPoolItem;

public class RelateSearchThread extends Thread {

	private final CountDownLatch mDoneSignal;	//上层函数（SearchAction）设置的线程信号量，在本线程工作完成之后，使其减1即可
	private String query;	//查询词
	private List<String> relateResult;
	private ResultPoolItem cache;
	
	public RelateSearchThread(List<String> resultList,
			CountDownLatch doneSignal, String query, ResultPoolItem cache) {

		this.query = query;
		this.mDoneSignal = doneSignal;
		this.relateResult=resultList;
		this.cache=cache;
	}

	
	public List<String> getRelateResult() {
		//如果上层Action设置了这个值，这个列表不仅被设置到缓存对象中，还可以做为向Action的返回值
		//如果Action没有设置，表示Action要进行相关搜索，但是暂时不需要返回结果值，这里仍然新建一个列表，得到结果并放到缓存中
		if(null==relateResult) relateResult=new LinkedList<String>();
		return relateResult;
	}


	@Override
	public void run() {
		this.doWork();
		mDoneSignal.countDown();
	}

	private void doWork() {

		try {
			
			if (MyStringChecker.isBlank(query)) return;
			Baidu b = new Baidu();
			List<String> rList=getRelateResult();
			int amount=b.getRelatedSearch(rList, query);
			if(amount>0&&null!=cache){
				cache.setRelateSearchResult(rList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return;
	}

}
