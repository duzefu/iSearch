package agent.behaviours.agentspecific;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import server.commonutils.MyStringChecker;
import server.info.entities.communication.RecommQueryAndPercent;
import agent.data.inblackboard.SearchData;
import agent.data.inmsg.DataToQueryRecommEntryAgent;
import agent.data.inmsg.TransactionType.QueryRecommEntryAgentTxType;
import agent.entities.blackboard.SearchDataBlackboard;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class QueryRecommMergeBehaviour extends Behaviour{

	private static final int RESULT_QUERY_AMOUNT=15;//需要的查询词数量
	
	//从消息中取到的数据
	protected DataToQueryRecommEntryAgent m_sdcDataToMe;
	protected int m_nBlackboardIndex;

	
	//从黑板中取到的数据
	protected SearchData m_sdcSearchData;
	protected String m_strQuery;
	
	protected List<RecommQueryAndPercent> m_lsMergedResult=new LinkedList<RecommQueryAndPercent>();
	
	@Override
	public void action() {
		
		ACLMessage msg=myAgent.receive();
		if(null!=msg){
			if(!getDataFromMsg(msg)) return;
			if(!checkType()){
				myAgent.postMessage(msg);
				return;
			}
			if(!getBlackboardData()) return;
			setQueryRecommDoneStatus();
			if(m_sdcSearchData.queryGroupRecommIsDone()&&m_sdcSearchData.queryQfgRecommIsDone()){
				mergeRecommResult();
				m_sdcSearchData.done();
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
			m_sdcDataToMe = (DataToQueryRecommEntryAgent) msg.getContentObject();
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

		QueryRecommEntryAgentTxType type = m_sdcDataToMe.getTransactionType();
		boolean flag = QueryRecommEntryAgentTxType.groupRecommDone.equals(type)
				|| QueryRecommEntryAgentTxType.qfgRecommDone.equals(type);
		return flag;
	}

	protected boolean getBlackboardData(){
		
		boolean ret=false;
		m_sdcSearchData=SearchDataBlackboard.getData(m_nBlackboardIndex);
		if(null!=m_sdcSearchData){
			m_strQuery=m_sdcSearchData.getQuery();
			ret=null!=m_strQuery;
		}
		return ret;
	}
	
	protected void setQueryRecommDoneStatus() {

		QueryRecommEntryAgentTxType type = m_sdcDataToMe.getTransactionType();
		switch (type) {
		case groupRecommDone:
			m_sdcSearchData.groupQueryRecommFinish();
			break;
		case qfgRecommDone:
			m_sdcSearchData.qfgQueryRecommFinish();
			break;
		default:
			break;
		}
	}
	
	protected void mergeRecommResult(){
		
		//这里直接从黑板中读取缓存出来用于合成，合成时，这两个列表不能再被其他线程（Agent）修改
		//因此，多线程冲突会导致在这里出现异常（目前还不会）
		List<RecommQueryAndPercent> lsGroup=m_sdcSearchData.getGroupRecommBuffer(), lsQfg=m_sdcSearchData.getQFGRecommBuffer();
		if(null==lsGroup||lsGroup.isEmpty()) return;//临时措施，如果群组没能推荐结果，也不用查询流图推荐
		m_lsMergedResult.clear();
		if(null!=lsGroup&&!lsGroup.isEmpty()) Collections.sort(lsGroup, Collections.reverseOrder());
		if(null!=lsQfg&&!lsQfg.isEmpty()) Collections.sort(lsQfg,Collections.reverseOrder());
		if(null==lsGroup||null==lsQfg) mergeWithNullList(lsGroup,lsQfg,m_lsMergedResult);//保证取消某一个机制也还能正常
		else mergeWithNotNullList(lsGroup,lsQfg,m_lsMergedResult);
		m_sdcSearchData.saveQueryRecommResult(m_lsMergedResult);
	}
	
	/**
	 * 两个列表有一个是null（目前的实现是不可能出现的，防止以后出现）
	 * @param lsGroup
	 * @param lsQfg
	 * @param ret
	 */
	private void mergeWithNullList(List<RecommQueryAndPercent> lsGroup,
			List<RecommQueryAndPercent> lsQfg, List<RecommQueryAndPercent> ret){
		
		if(null==lsGroup&&null==lsQfg) return;
		List<RecommQueryAndPercent> lstmp;
		if(null!=lsGroup) lstmp=lsGroup;
		else lstmp=lsQfg;
		if(lstmp.isEmpty()) return;
		
		Iterator<RecommQueryAndPercent> it=lstmp.iterator();
		int count=lstmp.size();
		count=count<RESULT_QUERY_AMOUNT?count:RESULT_QUERY_AMOUNT;
		while(it.hasNext()){
			RecommQueryAndPercent next=it.next();
			if(null==next||next.getQuery().equals(m_strQuery)) continue;
			ret.add(next);
			if(--count==0) break;
		}
	}
	
	/**
	 * 两个列表都不是null时调用（但可能为空）
	 * @param lsGroup
	 * @param lsQfg
	 * @param ret
	 */
	private void mergeWithNotNullList(List<RecommQueryAndPercent> lsGroup,
			List<RecommQueryAndPercent> lsQfg, List<RecommQueryAndPercent> ret) {

		if(lsGroup.isEmpty()&&lsQfg.isEmpty()) return;
		
		Iterator<RecommQueryAndPercent> itg = lsGroup.iterator(), itqfg = lsQfg.iterator();
		RecommQueryAndPercent resg = null, resqfg = null;
		while (itg.hasNext() && itqfg.hasNext()&&ret.size() < RESULT_QUERY_AMOUNT) {
			if(null==resg||resg.getQuery().equals(m_strQuery)){
				resg=itg.next();
				continue;
			}
			if(null==resqfg||resg.getQuery().equals(m_strQuery)){
				resqfg=itqfg.next();
				continue;
			}
			if (resg.getPercent() > resqfg.getPercent()) {
				ret.add(resg);
				resg = null;
				while (itg.hasNext() && null == resg) resg = itg.next();
			} else {
				ret.add(resqfg);
				resqfg = null;
				while (itqfg.hasNext() && null == resqfg) resqfg = itqfg.next();
			}
		}
		
		if(ret.size()<RESULT_QUERY_AMOUNT){
			
			//到这里时，两个列表中某一个列表为空了，而resg与resqfg有可能指向了一条有效结果（还没有空的那个列表的）
			if(null!=resg) ret.add(resg);
			if(ret.size()>=RESULT_QUERY_AMOUNT) return;
			if(null!=resqfg) ret.add(resqfg);
			if(ret.size()>=RESULT_QUERY_AMOUNT) return;
			
			//有可能有一个列表还有剩余的元素
			Iterator<RecommQueryAndPercent> it;
			if(itg.hasNext()) it=itg;
			else it=itqfg;
			
			while(it.hasNext()&&ret.size() < RESULT_QUERY_AMOUNT){
				RecommQueryAndPercent next=it.next();
				if(null==next||next.getQuery().equals(m_strQuery)) continue;
				ret.add(next);
			}
		}
	}
}
