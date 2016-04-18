package agent.behaviours.agentspecific;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import server.commonutils.LogU;
import server.engine.api.EngineFactory;
import agent.agentclass.workingagent.SearchEntryAgent;
import agent.data.inblackboard.SearchData;
import agent.data.inmsg.DataToMemberSearchAgent;
import agent.data.inmsg.DataToResultMergeAgent;
import agent.data.inmsg.DataToSearchEntryAgent;
import agent.data.inmsg.TransactionType.SearchEntryAgentTxType;
import agent.entities.blackboard.SearchDataBlackboard;
import common.entities.searchresult.ResultPool;
import common.entities.searchresult.ResultPoolItem;
import common.functions.resultmerge.ResultCounter;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

/**
 * SearchEntryAgent的行为之一，负责进一步向成员搜索Agent及结果合成Agent分发任务
 * @author zhou
 *
 */
public class SearchTaskDispatchBehaviour extends Behaviour{

	//从消息中取到的数据
	protected DataToSearchEntryAgent m_sdcDataToMe;
	protected int m_nBlackboardIndex;
	
	//从黑板中取到的数据
	protected SearchData m_sdcSearchData;
	protected ResultPoolItem m_sdcResItem;
	
	//工作数据
	protected DataToMemberSearchAgent m_sdcDataToMSA;
	protected DataToResultMergeAgent m_sdcDataToRMA;
	protected ACLMessage m_msgToMSA;
	protected ACLMessage m_msgToRMA;
	
	public SearchTaskDispatchBehaviour() {
		m_sdcDataToMSA=new DataToMemberSearchAgent();
		m_sdcDataToRMA=new DataToResultMergeAgent();
		m_msgToMSA=new ACLMessage(ACLMessage.INFORM);
		m_msgToRMA=new ACLMessage(ACLMessage.INFORM);
	}
	
	@Override
	public void setAgent(Agent a) {
		
		super.setAgent(a);
		if(null==a) return;
		AID myAID=a.getAID();
		m_msgToMSA.setSender(myAID);
		m_msgToMSA.addReplyTo(myAID);
		m_msgToRMA.setSender(myAID);
		m_msgToRMA.addReplyTo(myAID);
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
			if(!getDataFromBlackboard()){
				return;
			}
			switch (m_sdcDataToMe.getTransactionType()) {
			case searchContinue:
				continueSearch();
				break;
			case taskDispatch:
				newSearch();
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

		m_sdcDataToMe = null;
		try {
			m_sdcDataToMe = (DataToSearchEntryAgent) msg.getContentObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null != m_sdcDataToMe) {
			m_nBlackboardIndex = m_sdcDataToMe.getIndex();
			ret = true;
		}

		return ret;
	}
	
	protected boolean checkType() {

		SearchEntryAgentTxType type = m_sdcDataToMe.getTransactionType();
		if (!SearchEntryAgentTxType.searchContinue.equals(type)
				&& !SearchEntryAgentTxType.taskDispatch.equals(type))
			return false;
		return true;

	}

	protected boolean getDataFromBlackboard(){
		
		m_sdcSearchData=SearchDataBlackboard.getData(m_nBlackboardIndex);
		return null!=m_sdcSearchData;
	}
	
	/**
	 * 当前行为收到的请求是来自于InterfaceAgent，开启一次新的查询任务
	 */
	protected void newSearch(){
		
		setResultPoolItem();//取结果缓存
		setSchedule(m_sdcSearchData.getResultListItem());//设置调度
		m_sdcSearchData.resetSearchDoneStatus();
		doDispatch();
	}
	
	/**
	 * 当前行为收到的请求来自于另外一个行为：SearchResultCheckBehaviour
	 * 目前的搜索结果数量还不够，要再一次向成员搜索引擎Agent及结果合成Agent分发任务
	 */
	protected void continueSearch(){
		m_sdcSearchData.swapBuffer();//交换搜索结果回填缓冲区及待合成结果缓冲区
		m_sdcSearchData.clearSearchBuffer();//此时的搜索结果回填缓冲区实际上是上一轮已经合并过的，要清空
		m_sdcSearchData.getResultListItem().pageIncrease();//成员搜索引擎要搜索的目标页码加1
		m_sdcSearchData.resetSearchDoneStatus();//清除上一轮已经设置的完成状态标志
		doDispatch();//再次分配任务
	}
	
	protected void setResultPoolItem() {
		if (!m_sdcSearchData.hasSearchResultCache()) {
			ResultPoolItem item = ResultPool.getResultListItem(
					m_sdcSearchData.getUserid(), m_sdcSearchData.getQuery());
			m_sdcSearchData.setResultListItem(item);
		}
	}

	/**
	 * 在缓存对象（rpItem）中设置调度策略
	 * @param rpItem
	 */
	private void setSchedule(ResultPoolItem rpItem) {

		/*
		 * 设置搜索引擎的逻辑：
		 * 		1. 如果用户指定了成员搜索引擎（this.schedule不空），则把缓存列表中的调度结果设置为用户指定的
		 *		2. 如果用户没有指定搜索引擎，采用缓存中已经存在的
		 *		3. 如果缓存中也没有调度结果（这个词是新搜索的，用户也没有指定搜索引擎），所有搜索引擎都调用
		 *		其中，setSchedule函数如果发现调度结果发生了变化，会清空搜索结果列表
		 *		注：这里用户指定搜索引擎是指用户只希望搜索结果来自这些搜索引擎；该功能目前在网页版还没有
		 */
		if (m_sdcSearchData.hasSelectEngine())
			//用户指定了搜索引擎（或者是有调度结果），总是把缓存列表中的调度值设置为新的
			rpItem.setSchedule(m_sdcSearchData.getSchedule());
		else if (!rpItem.hasScheduleResult()) {
			//用户没有指定搜索引擎，而且缓存中也没有调度结果，所有的搜索引擎都被选
			Map<String, Double> schedule=new HashMap<String, Double>();
			Iterator<String> itEname=EngineFactory.getAllEngineEnName().iterator();
			while(itEname.hasNext()) schedule.put(itEname.next(), 1.0);
			rpItem.setSchedule(schedule);
		}
		//else{}用户没的指定搜索引擎，但是缓存中有搜索引擎（第二次搜索），保持缓存中的调度结果即可
	}
	
	protected void doDispatch(){
		
		ResultPoolItem item=m_sdcSearchData.getResultListItem();
		Map<String, Double> schedule=item.getSchedule();
		Iterator<String> it=schedule.keySet().iterator();
		int mseCount=0;
		while(it.hasNext()){
			String engName=it.next();
			m_sdcDataToMSA.setEngineName(engName);
			m_sdcDataToMSA.setIndex(m_nBlackboardIndex);
			m_sdcDataToMSA.setLastAmount(m_sdcSearchData.getResultAmount(engName));
			m_sdcDataToMSA.setSearchPage(item.getPageForMemberSearchEngine());
			try {
				m_msgToMSA.setContentObject(m_sdcDataToMSA);
				m_msgToMSA.clearAllReceiver();
				m_msgToMSA.addReceiver(((SearchEntryAgent)myAgent).getMemberSearchReceiver());
				myAgent.send(m_msgToMSA);
				++mseCount;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		m_sdcSearchData.setMemberSearchEngineAmount(mseCount);
		m_sdcDataToRMA.setIndex(m_nBlackboardIndex);
		try {
			m_msgToRMA.setContentObject(m_sdcDataToRMA);
		} catch (IOException e) {
			e.printStackTrace();
		}
		m_msgToRMA.clearAllReceiver();
		m_msgToRMA.addReceiver(((SearchEntryAgent)myAgent).getResultMergeReceiver());
		myAgent.send(m_msgToRMA);
		
	}
}
