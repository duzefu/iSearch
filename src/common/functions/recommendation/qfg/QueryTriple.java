package common.functions.recommendation.qfg;

public class QueryTriple implements Comparable<QueryTriple> {
	//data
	private String queryContent;
	private String queryUser;
	private String queryTime;
	
	//constructor
	public QueryTriple(){}
	
	
	public String getQueryContent() {
		return queryContent;
	}

	public String getQueryUser() {
		return queryUser;
	}

	public String getQueryTime() {
		return queryTime;
	}

	public void setQueryContent(String queryContent) {
		this.queryContent = queryContent;
	}


	public void setQueryUser(String queryUser) {
		this.queryUser = queryUser;
	}


	public void setQueryTime(String queryTime) {
		this.queryTime = queryTime;
	}


	//constructor
	public QueryTriple(String queryContent, String queryUser, String queryTime)
	{
		this.queryContent = queryContent;
		this.queryUser = queryUser;
		this.queryTime = queryTime;
	}
	
	/**
	 * 当前三元组中的时间减参数三元组时间的差值，以秒为单位
	 * @param triple
	 * @return
	 */
	public int GetTimeDifference(QueryTriple triple)
	{
		//time format: 00:00:00
		String[] timeSplitThis = queryTime.split(":");
		String[] timeSplitFrom = triple.getQueryTime().split(":");
		int returnValue = 0;
		//compare the first two part of the time
		for(int i = 0; i < timeSplitThis.length; i++)
		{
			int left = Integer.parseInt(timeSplitThis[i]);
			int right = Integer.parseInt(timeSplitFrom[i]);
			//convert to seconds
			returnValue += (left - right) * (int)Math.pow(60, timeSplitThis.length - i + 1);
		}
		
		//compare the last part of the time
		return returnValue;
	}
	
	public void PrintMyself()
	{
		System.out.println("Query: " + queryContent + "User: " + queryUser + "Time: " + queryTime);
	}
	
	//implementation
	public int compareTo(QueryTriple triple)
	{
		/* this - from = ascend */
		
		//time format: 00:00:00
		String[] timeSplitThis = queryTime.split(":");
		String[] timeSplitFrom = triple.getQueryTime().split(":");
		
		int left = 0;
		int right = 0;
		//compare the first two part of the time
		for(int i = 0; i < timeSplitThis.length - 1; i++)
		{
			left = Integer.parseInt(timeSplitThis[i]);
			right = Integer.parseInt(timeSplitFrom[i]);
			if(0 != (left - right))
			{
				return left - right;
			}
		}
		
		//compare the last part of the time
		return left - right;
	}
}
