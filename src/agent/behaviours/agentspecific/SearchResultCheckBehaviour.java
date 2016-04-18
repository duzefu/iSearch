package agent.behaviours.agentspecific;

import java.io.IOException;

import server.commonutils.LogU;
import agent.data.inblackboard.SearchData;
import agent.data.inmsg.DataToSearchEntryAgent;
import agent.data.inmsg.TransactionType.SearchEntryAgentTxType;
import agent.entities.blackboard.SearchDataBlackboard;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

/**
 * SearchEntryAgent的行为之二，负责检查搜索工作是否已经完成。
 * @author zhou
 *
 */
public class SearchResultCheckBehaviour extends Behaviour{

	/*
	 * 如果收到的消息是由MemberSearchAgent或ResultMergeAgent发送的，表示工作完成；
	 * 这个行为会检查本次搜索工作是否结束：
	 * 如果搜索完毕，释放SearchAction的线程锁；
	 * 否则，在自己Agent的消息队列中放一条消息，使得SearchEntryAgent的另外一个行为会再次让成员搜索引擎Agent及结果合成Agent工作
	 * 如果收到的消息不是这两个Agent发送的（是Interface Agent发送的），这是新的搜索请求，
	 * 这个行为不处理这样的消息，会把消息放回队列中，后面会被另外一个行为处理掉
	 */
	
	protected DataToSearchEntryAgent m_sdcDataToMe;
	protected int m_nBlackboardIndex;
	protected SearchEntryAgentTxType m_enuType;
	protected SearchData m_sdcSearchData;
	
	protected ACLMessage m_msgForContinueSearch;
	protected DataToSearchEntryAgent m_sdcDataForContinueSearch;
	
	protected DataToSearchEntryAgent getDataForContinueSearch(){
		if(null==m_sdcDataForContinueSearch){
			m_sdcDataForContinueSearch=new DataToSearchEntryAgent(SearchEntryAgentTxType.searchContinue);
		}
		return m_sdcDataForContinueSearch;
	}
	
	protected ACLMessage getContinuedMsg(){
		
		//使用两个成员变量，避免这个消息和数据对象反复构造
		//使用这个函数实现延迟加载，需要时才构造
		//这两个成员变量不会被多线程访问（只有当前Agent的线程访问），不需要互斥
		if(null==m_msgForContinueSearch){
			m_msgForContinueSearch=new ACLMessage(ACLMessage.INFORM);
		}
		return m_msgForContinueSearch;
	}
	
	@Override
	public void action() {

		ACLMessage msg = myAgent.receive();
		if (null != msg) {
				if (!getDataFromMsg(msg))
					return;
				if (!checkType()) {
					myAgent.postMessage(msg);
					return;
				}
				if (!getDataFromBlackboard())
					return;
				setDoneFlagInBlackboard();
				boolean workIsDone=m_sdcSearchData.memberSearchIsDone()&&m_sdcSearchData.resultMergeIsDone();
				if (workIsDone) {
					if (!m_sdcSearchData.resultEnough()){
						DataToSearchEntryAgent data=getDataForContinueSearch();
						data.setIndex(m_nBlackboardIndex);
						ACLMessage continueMsg=getContinuedMsg();
						try {
							continueMsg.setContentObject(m_sdcDataForContinueSearch);
						} catch (IOException e) {
							e.printStackTrace();
						}
						myAgent.postMessage(continueMsg);
					}else{
						m_sdcSearchData.setResultForAction();
						m_sdcSearchData.done();
					}
				}
		} else {
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

		m_sdcDataToMe = null;
		try {
			m_sdcDataToMe = (DataToSearchEntryAgent) msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
		if (null != m_sdcDataToMe) {
			m_nBlackboardIndex = m_sdcDataToMe.getIndex();
			m_enuType = m_sdcDataToMe.getTransactionType();
			ret = true;
		}

		return ret;
	}
	
	protected boolean checkType() {

		if (!SearchEntryAgentTxType.memberSearchDone.equals(m_enuType)
				&& !SearchEntryAgentTxType.resultMergeDone.equals(m_enuType))
			return false;
		return true;

	}

	protected boolean getDataFromBlackboard(){
		
		m_sdcSearchData=SearchDataBlackboard.getData(m_nBlackboardIndex);
		return null!=m_sdcSearchData;
	}
	

	protected void setDoneFlagInBlackboard(){
		
		/*
		 * 设置结果合成Agent或成员搜索Agent完成工作的标志，
		 * 这项工作不能由结果合成Agent或成员搜索引擎Agent做，
		 * 原因：
		 * 		结果合成Agent及成员搜索Agent都发了工作完成的消息（例如总共6条）之后，
		 * 		当前Agent才开始处理第1条消息；
		 * 		如果不是本Agent来设置黑板中的工作完成标志，
		 * 		这时候就会认为工作已经结束并进行清理工作（例如释放线程锁、Action线程发送响应并清理黑板），
		 * 		而后续的5条消息仍然会被处理，处理时将导致不确定的后果。
		 */
		switch (m_sdcDataToMe.getTransactionType()) {
		case memberSearchDone:
			m_sdcSearchData.memberSearchFinish();
			break;
		case resultMergeDone:
			m_sdcSearchData.resultMergeFinish();
			break;
		default:
			break;
		}
	}
}
