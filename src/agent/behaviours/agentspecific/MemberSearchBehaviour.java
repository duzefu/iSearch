package agent.behaviours.agentspecific;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import server.commonutils.LogU;
import server.commonutils.MyStringChecker;
import server.engine.api.AbstractEngine;
import server.engine.api.EngineFactory;
import agent.data.inblackboard.SearchData;
import agent.data.inmsg.DataToMemberSearchAgent;
import agent.data.inmsg.DataToSearchEntryAgent;
import agent.data.inmsg.TransactionType.SearchEntryAgentTxType;
import agent.entities.blackboard.SearchDataBlackboard;
import common.entities.searchresult.Result;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

/**
 * 成员搜索引擎Agent的行为，完成一页的搜索
 * 
 * @author zhou
 *
 */
public class MemberSearchBehaviour extends Behaviour {

	// 通过消息获得
	protected DataToMemberSearchAgent m_sdcDataToMe;
	protected int m_nBlackboardIndex;
	protected String m_strEngineName;
	protected int m_nLastAmount;
	protected int m_nPage;

	// 通过黑板得到
	protected SearchData m_sdcSearchData;
	protected String m_strQuery;

	// 用于发送响应
	protected ACLMessage m_msgReply;
	protected DataToSearchEntryAgent m_sdcDataReply;

	// 搜索结果临时存储位置，每次搜索前要清空
	protected List<Result> m_lsResult = new LinkedList<Result>();

	/**
	 * 获得成员搜索Agent向SearchEntryAgent回复消息时使用的数据对象
	 * 
	 * @return
	 */
	protected DataToSearchEntryAgent getReplyData() {
		if (null == m_sdcDataReply) {
			m_sdcDataReply = new DataToSearchEntryAgent(
					SearchEntryAgentTxType.memberSearchDone);
		}
		return m_sdcDataReply;
	}

	/**
	 * 获得成员搜索Agent向SearchEntryAgent回复消息时使用的消息对象
	 * 
	 * @return
	 */
	protected ACLMessage getReplyMsg() {

		if (null == m_msgReply) {
			m_msgReply = new ACLMessage(ACLMessage.INFORM);
			AID myAID = myAgent.getAID();
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
			if (getDataFromMessage(msg) && getDataFromBlackboard() && !MyStringChecker.isBlank(m_strQuery)) {
				AbstractEngine e = EngineFactory.engineFactory(m_strEngineName);
				m_lsResult.clear();
				e.getResults(m_lsResult, m_strQuery, m_nPage, -1, m_nLastAmount);
				if (!m_lsResult.isEmpty()) {
					SearchDataBlackboard.addMemberSearchResult(m_lsResult, m_nBlackboardIndex);
				}
			}
			reply(msg);
		} else {
			block();
		}
	}

	protected boolean getDataFromMessage(ACLMessage msg) {

		boolean ret = false;
		if (null == msg) return ret;

		m_sdcDataToMe=null;
		try {
			m_sdcDataToMe = (DataToMemberSearchAgent) msg.getContentObject();
		} catch (Exception e) {
			
		}
		if (null != m_sdcDataToMe){
			m_nBlackboardIndex = m_sdcDataToMe.getIndex();
			m_strEngineName = m_sdcDataToMe.getEngineName();
			m_nLastAmount = m_sdcDataToMe.getLastAmount();
			m_nPage = m_sdcDataToMe.getTargetPage();
			ret = true;
		}

		return ret;
	}

	protected boolean getDataFromBlackboard(){
		
		boolean ret=false;
		m_sdcSearchData=SearchDataBlackboard.getData(m_nBlackboardIndex);
		if(null!=m_sdcSearchData){
			m_strQuery=m_sdcSearchData.getQuery();
			ret=true;
		}
		return ret;
	}
	

	protected void reply(ACLMessage msg) {

		if (null == msg) return;
		Iterator<AID> it = msg.getAllReplyTo();
		if (!it.hasNext()) return;

		ACLMessage msgReply = getReplyMsg();
		while (it.hasNext()) msgReply.addReceiver(it.next());

		DataToSearchEntryAgent data = getReplyData();
		data.setIndex(m_nBlackboardIndex);
		try {
			msgReply.setContentObject(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		myAgent.send(msgReply);
	}
	
	@Override
	public boolean done() {
		return false;
	}

}
