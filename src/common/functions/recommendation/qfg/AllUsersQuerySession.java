package common.functions.recommendation.qfg;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class AllUsersQuerySession {
	//data
	private Map<String, UserQuerySession> querySession = new HashMap<String, UserQuerySession>();
	
	//getter and setter
	public Map<String, UserQuerySession> GetQuerySession()
	{
		return querySession;
	}
	
	//public method
	public void AddUserQuery(QueryTriple user)
	{
		//if already exists
		if(querySession.containsKey(user.getQueryUser()))
		{
			querySession.get(user.getQueryUser()).AddQueryTriple(user);
			return;
		}
		
		//new user
		UserQuerySession uQS = new UserQuerySession();
		uQS.AddQueryTriple(user);
		querySession.put(user.getQueryUser(), uQS);
	}

	/**
	 * 以时间为准，将超过30分钟的查询记录（不区分用户）分隔开
	 */
	public void FinishJob()
	{
		//查询会话中，每一个词对应的三元组按时间排序
		SortAllQueryLines();
		//断点，两条查询记录时间超过30分钟，为一个断点
		CalBreakPoints();
	}
	
	public void PrintMyself()
	{
		Iterator<Entry<String, UserQuerySession>> iter = querySession.entrySet().iterator();
		while (iter.hasNext()) 
		{
		    Entry<String, UserQuerySession> entry = iter.next();
		    System.out.println("This is user: " + entry.getKey());
		    System.out.println("*--------------------------------------------------------------------");
		    System.out.println("*--------------------------------------------------------------------");
		    entry.getValue().PrintMyself();
		    System.out.println("*--------------------------------------------------------------------");
		    System.out.println("*--------------------------------------------------------------------");
		} 
		
	}
	
	/**
	 * 逐一处理每一个查询词对应的查询记录，计算文本特征
	 */
	public void CalWeights()
	{
		Iterator<Entry<String, UserQuerySession>> iter = querySession.entrySet().iterator();
		while (iter.hasNext()) 
		{
		    Entry<String, UserQuerySession> entry = iter.next();
		    entry.getValue().CalWeights();
		    System.out.println("词："+entry.getKey()+"计算权值完成。");
//		    entry.getValue().PrintMyself();
		} 
	}
	
	//private method
	private void SortAllQueryLines()
	{
		Iterator<Entry<String, UserQuerySession>> iter = querySession.entrySet().iterator();
 		while (iter.hasNext()) 
		{
		    Entry<String, UserQuerySession> entry = iter.next();
		    entry.getValue().SortedSession();
		} 
	}
	
	//after SortAllQueryLines
	private void CalBreakPoints()
	{
		Iterator<Entry<String, UserQuerySession>> iter = querySession.entrySet().iterator();
		while (iter.hasNext()) 
		{
		    Entry<String, UserQuerySession> entry = iter.next();
		    entry.getValue().CalBreakPoints();
		} 
	}
}
