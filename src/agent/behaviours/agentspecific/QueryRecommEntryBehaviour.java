package agent.behaviours.agentspecific;

import java.io.IOException;
import java.util.Set;

import agent.agentclass.workingagent.QueryRecommEntryAgent;
import agent.data.inblackboard.SearchData;
import agent.data.inmsg.DataForQueryGroupRecomm;
import agent.data.inmsg.DataForQueryQFGRecomm;
import agent.data.inmsg.DataToQueryRecommEntryAgent;
import agent.data.inmsg.TransactionType.QueryRecommEntryAgentTxType;
import agent.entities.blackboard.SearchDataBlackboard;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

/**
 * 目前查询词推荐同时使用了群组推荐及查询流图推荐（并列关系），
 * 两种推荐都需要查询数据库，所以用两个Agent（线程）来并行完成。
 * 这个行为负责将任务分发出去。
 * @author zhou
 *
 */
public class QueryRecommEntryBehaviour extends Behaviour{

	//从消息中取到的数据
	protected DataToQueryRecommEntryAgent m_sdcDataToMe;
	protected int m_nBlackboardIndex;
	
	//从黑板中取到的数据
	protected SearchData m_sdcSearchData;
	protected String m_strQuery;
	protected int m_nUserid;
	protected Set<Integer> m_setGroupId;
	
	//工作数据
	protected DataForQueryGroupRecomm m_sdcDataToGRA;
	protected DataForQueryQFGRecomm m_sdcDataToQfgRA;
	
	protected ACLMessage m_msgToGRA;
	protected ACLMessage m_msgToQfgRA;
	
	/**
	 * 获得要发送给群组推荐（查询词）Agent的消息
	 * @return 消息对象
	 */
	protected ACLMessage getMsgToGRA(){
		
		if(null==m_msgToGRA){
			AID myAID=myAgent.getAID();
			m_msgToGRA=new ACLMessage(ACLMessage.INFORM);
			m_msgToGRA.setSender(myAID);
			m_msgToGRA.addReplyTo(myAID);
		}
		
		m_msgToGRA.clearAllReceiver();
		return m_msgToGRA;
	}
	
	protected ACLMessage getMsgToQfgRA(){
		
		if(null==m_msgToQfgRA){
			AID myAID=myAgent.getAID();
			m_msgToQfgRA=new ACLMessage(ACLMessage.INFORM);
			m_msgToQfgRA.setSender(myAID);
			m_msgToQfgRA.addReplyTo(myAID);
		}
		
		m_msgToQfgRA.clearAllReceiver();
		return m_msgToQfgRA;
	}
	
	protected DataForQueryGroupRecomm getDataForGRA(){
		
		if(null==m_sdcDataToGRA){
			m_sdcDataToGRA=new DataForQueryGroupRecomm();
		}
		return m_sdcDataToGRA;
	}
	
	protected DataForQueryQFGRecomm getDataForQfgRA(){
		
		if(null==m_sdcDataToQfgRA){
			m_sdcDataToQfgRA=new DataForQueryQFGRecomm();
		}
		return m_sdcDataToQfgRA;
	}
	
	@Override
	public void action() {
		
		ACLMessage msg=myAgent.receive();
		if(null!=msg){
			if(!getDataFromMsg(msg)) return;
			if(!checkType()){
				myAgent.postMessage(msg);
				return;
			}
			if(!extractBlackboardData()) return;
			m_sdcSearchData.resetQueryRecommDoneStatus();
			switch (m_sdcDataToMe.getTransactionType()) {
			case taskDispatch:
				doDispatch();
				break;
			default:
				break;
			}
		}else{
			block();
		}
	}

	@Override
	public boolean done() {
		return false;
	}
	
	protected boolean getDataFromMsg(ACLMessage msg) {

		boolean ret = false;
		if (null == msg) return ret;

		m_sdcDataToMe=null;
		try {
			m_sdcDataToMe = (DataToQueryRecommEntryAgent) msg.getContentObject();
		} catch (Exception e) {
			
		}
		if (null != m_sdcDataToMe) {
			m_nBlackboardIndex = m_sdcDataToMe.getIndex();
			ret = true;
		}

		return ret;
	}
	
	protected boolean checkType() {

		QueryRecommEntryAgentTxType type = m_sdcDataToMe.getTransactionType();
		return QueryRecommEntryAgentTxType.taskDispatch.equals(type);
	}

	protected boolean extractBlackboardData(){
		
		boolean ret=false;
		
		m_sdcSearchData=SearchDataBlackboard.getData(m_nBlackboardIndex);
		if(null!=m_sdcSearchData){
			m_strQuery=m_sdcSearchData.getQuery();
			m_setGroupId=m_sdcSearchData.getGroupUserID();
			m_nUserid=m_sdcSearchData.getUserid();
			ret=true;
		}
		
		return ret;
	}
	
	protected void doDispatch(){
		
		sendMsgToGRA();
		sendMsgToQfgRA();
	}
	
	protected void sendMsgToGRA() {

		DataForQueryGroupRecomm data = getDataForGRA();
		data.setIndex(m_nBlackboardIndex);
		ACLMessage msg = getMsgToGRA();
		AID receiver=((QueryRecommEntryAgent) myAgent).getGroupRecommReceiver();
		msg.addReceiver(receiver);
		try {
			msg.setContentObject(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		myAgent.send(msg);
	}

	protected void sendMsgToQfgRA() {

		DataForQueryQFGRecomm data = getDataForQfgRA();
		data.setIndex(m_nBlackboardIndex);
		ACLMessage msg = getMsgToQfgRA();
		AID receiver=((QueryRecommEntryAgent) myAgent).getQFGRecommReceiver();
		msg.addReceiver(receiver);
		try {
			msg.setContentObject(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		myAgent.send(msg);
	}
	
}
