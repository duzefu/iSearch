package agent.behaviours.agentspecific;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import server.info.entities.communication.RecommQueryAndPercent;
import agent.data.inblackboard.SearchData;
import agent.data.inmsg.DataForQueryQFGRecomm;
import agent.data.inmsg.DataToQueryRecommEntryAgent;
import agent.data.inmsg.TransactionType.QueryRecommEntryAgentTxType;
import agent.entities.blackboard.SearchDataBlackboard;
import common.functions.recommendation.qfg.QueryQFGRecommendation;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

/**
 * 查询词的查询流图推荐行为
 * @author zhou
 *
 */
public class QueryQFGRecommBehaviour extends Behaviour{

	protected DataForQueryQFGRecomm m_sdcDataToMe;
	protected int m_nBlackboardIndex;
	
	protected String m_strQuery;

	protected SearchData m_sdcBlackboardData;
	
	protected List<RecommQueryAndPercent> m_lsRecommRes = new LinkedList<RecommQueryAndPercent>();

	//用于发送响应
	protected ACLMessage m_msgReply;
	protected DataToQueryRecommEntryAgent m_sdcDataReply;
	
	protected DataToQueryRecommEntryAgent getReplyData(){
		if(null==m_sdcDataReply){
			m_sdcDataReply=new DataToQueryRecommEntryAgent(QueryRecommEntryAgentTxType.qfgRecommDone);
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
			if (getData(msg) && extractBlackboardData()) {
				m_lsRecommRes.clear();
				QueryQFGRecommendation.getQueryReommendation(m_lsRecommRes, m_strQuery);
				m_sdcBlackboardData.saveQueryRecommResult(m_lsRecommRes);
			}
			reply(msg);
		} else {
			block();
		}
	}

	@Override
	public boolean done() {
		return false;
	}

	protected boolean getData(ACLMessage msg) {

		boolean ret = false;
		if (null == msg) return ret;

		m_sdcDataToMe = null;
		try {
			m_sdcDataToMe = (DataForQueryQFGRecomm) msg.getContentObject();
		} catch (Exception e) {

		}
		if (null != m_sdcDataToMe) {
			m_nBlackboardIndex = m_sdcDataToMe.getIndex();
			ret = true;
		}
		return ret;
	}
	
	protected boolean extractBlackboardData(){
		
		boolean ret=false;
		
		m_sdcBlackboardData=null;
		m_sdcBlackboardData=SearchDataBlackboard.getData(m_nBlackboardIndex);
		if(null!=m_sdcBlackboardData){
			m_strQuery=m_sdcBlackboardData.getQuery();
			ret=true;
		}
		
		return ret;
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
