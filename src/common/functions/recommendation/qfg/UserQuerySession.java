package common.functions.recommendation.qfg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import server.commonutils.SpringBeanFactoryUtil;
import server.info.config.SpringBeanNames;
import common.textprocess.similarity.EditFeatures;
import common.textprocess.similarity.EditFeatures.QueryType;
import db.dao.QFGGenDao;

public class UserQuerySession {
	//data
	private List<QueryTriple> userQuerySession = new ArrayList<QueryTriple>();
	private int timeoutThreshold = 30 * 60;//30m
	private List<Integer> breakPoints = new ArrayList<Integer>();
	private List<EditFeatures> textualFeatures = new ArrayList<EditFeatures>();
	
	//getter and setter
	public void SetTimeOutThreshold(int timeoutThreshold)
	{
		this.timeoutThreshold = timeoutThreshold;
	}
	
	//public method
	public void AddQueryTriple(QueryTriple sample)
	{
		userQuerySession.add(sample);
	}
	
	public void SortedSession()
	{
		Collections.sort(userQuerySession);
	}
	
	public void CalBreakPoints()
	{
		int length = userQuerySession.size();
		if(0 == length) return;

		QueryTriple preNode = userQuerySession.get(0);
		for(int i = 1; i < length; i++)
		{
			QueryTriple curNode = userQuerySession.get(i);
			if(curNode.GetTimeDifference(preNode) >= timeoutThreshold)
			{
				//means at index i is the start of a new session
				breakPoints.add(i);
			}
		}
	}

	public void PrintMyself()
	{
//		for(int i = 0; i < userQuerySession.size(); i++)
//		{
//			userQuerySession.get(i).PrintMyself();
//		}
		for(int i=0;i<textualFeatures.size();++i){
			textualFeatures.get(i).print();
		}
	}

	public void CalWeights()
	{
		int uid=0;
		if(null==userQuerySession&&userQuerySession.isEmpty()) return;
		String cookie = userQuerySession.get(0).getQueryUser();
		QFGGenDao qfgdao=(QFGGenDao) SpringBeanFactoryUtil.getBean(SpringBeanNames.QFG_DAO_BEAN_NAME);
		for(int i = 0; i < userQuerySession.size() - 1; i++)
		{
			//get the current and the next query
			QueryTriple qtriple1=userQuerySession.get(i), qtriple2=userQuerySession.get(i+1);
			if(null==qtriple1||null==qtriple2) continue;
			String query1=qtriple1.getQueryContent(), query2=qtriple2.getQueryContent();
			if(null==query1||null==query2||query1.isEmpty()||query2.isEmpty()||query1.equals(query2)) continue;
			EditFeatures tempWeight = new EditFeatures(userQuerySession.get(i), userQuerySession.get(i + 1), QueryType.CHINESE_CHAR);
			//calculate weights
			tempWeight.CalculateAll();
			textualFeatures.add(tempWeight);
			qfgdao.add(tempWeight, cookie);
		}
	}
}
