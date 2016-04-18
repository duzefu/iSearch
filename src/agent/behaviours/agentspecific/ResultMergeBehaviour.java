package agent.behaviours.agentspecific;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import agent.data.inblackboard.SearchData;
import agent.data.inmsg.DataToResultMergeAgent;
import agent.data.inmsg.DataToSearchEntryAgent;
import agent.data.inmsg.TransactionType.SearchEntryAgentTxType;
import agent.entities.blackboard.SearchDataBlackboard;
import common.entities.searchresult.Result;
import common.functions.resultmerge.Merge;
import db.dbhelpler.UserHelper;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class ResultMergeBehaviour extends Behaviour {

	//从消息中收到的数据
	protected DataToResultMergeAgent m_sdcDataToMe;
	protected int m_nBlackboardIndex;

	//从黑板中取到的数据
	protected SearchData m_sdcSearchData;
	protected String m_strQuery;
	protected List<Result> m_lsForMerge;
	protected List<Result> m_lsTargetResult;
	protected int m_nUserid;
	
	//用于发送响应
	protected ACLMessage m_msgReply;
	protected DataToSearchEntryAgent m_sdcDataReply;
	
	protected DataToSearchEntryAgent getReplyData(){
		if(null==m_sdcDataReply){
			m_sdcDataReply=new DataToSearchEntryAgent(SearchEntryAgentTxType.resultMergeDone);
		}
		return m_sdcDataReply;
	}
	
	protected ACLMessage getReplyMsg(){
		
		if(null==m_msgReply){
			m_msgReply=new ACLMessage(ACLMessage.INFORM);
			AID myAID=myAgent.getAID();
			m_msgReply.setSender(myAID);
			m_msgReply.addReplyTo(myAID);
		}
		m_msgReply.clearAllReceiver();
		return m_msgReply;
	}
	
	@Override
	public void action() {

		ACLMessage msg = myAgent.receive();
		if (null != msg) {
			if (
					getDataFromMessage(msg) 
					&& getDataFromBlackboard() 
					&& null != m_lsTargetResult 
					&& null != m_lsForMerge
					&& !m_lsForMerge.isEmpty()
				){
				boolean isLogin = UserHelper.isLoginUser(m_nUserid);
				Merge.resultMerge(m_lsTargetResult, m_lsForMerge, m_nUserid, m_strQuery, isLogin);
			}
			reply(msg);
		} else {
			block();
		}
	}

	protected void reply(ACLMessage msg){
		
			Iterator<AID> it=msg.getAllReplyTo();
			if(!it.hasNext()) return;
			
			ACLMessage msgReply=getReplyMsg();
			while(it.hasNext()) msgReply.addReceiver(it.next());
			
			DataToSearchEntryAgent data=getReplyData();
			data.setIndex(m_nBlackboardIndex);
			try {
				msgReply.setContentObject(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
			myAgent.send(msgReply);
	}
	
	protected boolean getDataFromMessage(ACLMessage msg){

		boolean ret=false;
		if (null == msg) return ret;

		m_sdcDataToMe = null;
		try {
			m_sdcDataToMe = (DataToResultMergeAgent) msg.getContentObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null != m_sdcDataToMe) {
			m_nBlackboardIndex = m_sdcDataToMe.getIndex();
			ret = true;
		}
			
		return ret;
	}
	
	protected boolean getDataFromBlackboard(){
		
		boolean ret=false;
		m_sdcSearchData=SearchDataBlackboard.getData(m_nBlackboardIndex);
		if(null!=m_sdcSearchData){
			m_strQuery=m_sdcSearchData.getQuery();
			m_lsForMerge=m_sdcSearchData.getMergeResultBuffer();
			m_lsTargetResult=m_sdcSearchData.getTargetListForMerge();
			m_nUserid=m_sdcSearchData.getUserid();
			ret=true;
		}
		return ret;
	}

	@Override
	public boolean done() {
		return false;
	}

}
