package agent.behaviours.agentspecific;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import server.info.entities.communication.RecommQueryAndPercent;
import agent.data.inblackboard.SearchData;
import agent.data.inmsg.DataForQueryGroupRecomm;
import agent.data.inmsg.DataToQueryRecommEntryAgent;
import agent.data.inmsg.TransactionType.QueryRecommEntryAgentTxType;
import agent.entities.blackboard.SearchDataBlackboard;
import common.functions.recommendation.group.QueryGroupRecommendation;
import db.dbhelpler.UserGroupHelper;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

/**
 * 查询词的群组推荐行为
 * @author zhou
 *
 */
public class QueryGroupRecommBehaviour extends Behaviour{

	protected DataForQueryGroupRecomm m_sdcDataToMe;
	protected int m_nBlackboardIndex;
	
	protected String m_strQuery;
	protected int m_nUserid;
	protected Set<Integer> m_setGroupId;

	protected SearchData m_sdcBlackboardData;
	
	protected List<RecommQueryAndPercent> m_lsRecommRes=new LinkedList<>();
	
	//用于发送响应
	protected ACLMessage m_msgReply;
	protected DataToQueryRecommEntryAgent m_sdcDataReply;
	
	protected DataToQueryRecommEntryAgent getReplyData(){
		if(null==m_sdcDataReply){
			m_sdcDataReply=new DataToQueryRecommEntryAgent(QueryRecommEntryAgentTxType.groupRecommDone);
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
		
		ACLMessage msg=myAgent.receive();
		if(null!=msg){
			if(getData(msg)&&extractBlackboardData()&&checkGroupUserID()){
				m_lsRecommRes.clear();
				QueryGroupRecommendation.getQueryReommendation(m_lsRecommRes, m_setGroupId, m_strQuery);
				m_sdcBlackboardData.saveGroupQueryRecommResult(m_lsRecommRes);
			}
			reply(msg);
		}else{
			block();
		}
	}

	@Override
	public boolean done() {
		return false;
	}

	protected boolean getData(ACLMessage msg){
		
		boolean ret=false;
		if(null==msg) return ret;
		
		m_sdcDataToMe=null;
		try {
			m_sdcDataToMe=(DataForQueryGroupRecomm)msg.getContentObject();
		} catch (UnreadableException e) {
			
		}
		if(null!=m_sdcDataToMe){
			m_nBlackboardIndex=m_sdcDataToMe.getIndex();
			ret=true;
		}
					
		return ret;
	}
	
	protected boolean extractBlackboardData(){
		
		boolean ret=false;
		
		m_sdcBlackboardData=SearchDataBlackboard.getData(m_nBlackboardIndex);
		if(null!=m_sdcBlackboardData){
			m_strQuery=m_sdcBlackboardData.getQuery();
			m_setGroupId=m_sdcBlackboardData.getGroupUserID();
			m_nUserid=m_sdcBlackboardData.getUserid();
			ret=true;
		}

		return ret;
	}
	
	private boolean checkGroupUserID() {

		if (null == m_setGroupId) {
			m_setGroupId = new HashSet<Integer>();
			UserGroupHelper.getGroupUserID(m_nUserid, m_setGroupId);
		}
		return !m_setGroupId.isEmpty();
	}
	
	protected void reply(ACLMessage msg){
		
		if(null==msg) return;
		Iterator<AID> it=msg.getAllReplyTo();
		if(!it.hasNext()) return;
		
		ACLMessage msgReply=getReplyMsg();
		while(it.hasNext()) msgReply.addReceiver(it.next());
		
		DataToQueryRecommEntryAgent data=getReplyData();
		data.setIndex(m_nBlackboardIndex);
		try {
			msgReply.setContentObject(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		myAgent.send(msgReply);
	}
	
}
