package agent.behaviours.agentspecific;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import agent.data.inblackboard.SearchData;
import agent.data.inmsg.DataForClickRecomm;
import agent.entities.blackboard.SearchDataBlackboard;
import common.entities.searchresult.Result;
import common.functions.recommendation.click.ClickRecommendation;
import db.dbhelpler.UserGroupHelper;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

/**
 * 结果推荐（点击推荐）行为
 * @author zhou
 *
 */
public class ClickRecommBehaviour extends Behaviour{

	protected DataForClickRecomm m_sdcDataToMe;
	protected int m_nBlackboardIndex;
	
	protected String m_strQuery;
	protected int m_nUserid;
	protected Set<Integer> m_setGroupId;

	protected SearchData m_sdcBlackboardData;
	
	protected List<Result> m_lsRecommRes=new LinkedList<Result>();
	
	@Override
	public void action() {
		
		ACLMessage msg=myAgent.receive();
		if(null!=msg){
			if(!getData(msg)) return;
			if(!extractBlackboardData()) return;
			if(checkGroupUserID()){
				m_lsRecommRes.clear();
				ClickRecommendation.getClickRecommendation(m_lsRecommRes, m_strQuery, m_setGroupId);
				m_sdcBlackboardData.saveClickRecommResult(m_lsRecommRes);
			}
			m_sdcBlackboardData.done();
		}else{
			block();
		}
	}

	@Override
	public boolean done() {
		return false;
	}

	/**
	 * 提取消息中的数据，数据设置在相关的成员变量中
	 * @param msg 消息
	 * @return 无误返回true
	 */
	protected boolean getData(ACLMessage msg){
		
		boolean ret=false;
		if(null==msg) return ret;
		
		m_sdcDataToMe = null;
		try {
			m_sdcDataToMe = (DataForClickRecomm) msg.getContentObject();
		} catch (Exception e) {
			
		}
		if (null != m_sdcDataToMe) {
			m_nBlackboardIndex = m_sdcDataToMe.getIndex();
			ret = true;
		}
		
		return ret;
	}
	
	/**
	 * 从数据黑板中取数据，设置相关成员变量
	 * @return 无误返回true
	 */
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
	
	/**
	 * 确定群组用户ID数组，如果成员变量是null，则获取用户群组信息
	 * @return 如果用户没有群组用户，返回false
	 */
	private boolean checkGroupUserID() {

		if (null == m_setGroupId) {
			m_setGroupId = new HashSet<Integer>();
			UserGroupHelper.getGroupUserID(m_nUserid, m_setGroupId);
		}
		return !m_setGroupId.isEmpty();
	}
	
}
