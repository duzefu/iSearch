package agent.behaviours.agentspecific;

import java.io.IOException;
import java.io.Serializable;

import agent.agentclass.entryagent.InterfaceAgent;
import agent.data.inmsg.DataForClickRecomm;
import agent.data.inmsg.DataFromInterfaceAgent;
import agent.data.inmsg.DataToInterfaceAgent;
import agent.data.inmsg.DataToQueryRecommEntryAgent;
import agent.data.inmsg.DataToSearchEntryAgent;
import agent.data.inmsg.TransactionType.InterfaceAgentTxType;
import agent.data.inmsg.TransactionType.QueryRecommEntryAgentTxType;
import agent.data.inmsg.TransactionType.SearchEntryAgentTxType;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

/**
 * 接口Agent的特有行为，不要把它添加到其他Agent中
 * 这个行为对象会通过myAgent调用InterfaceAgent中定义的函数，
 * 如果是其他类型的Agent，会导致类型转换异常
 * 
 * @author zhou
 *
 */
public class InterfaceAgentBehaviour extends Behaviour {

	private static final long serialVersionUID = 6838084987528020136L;
	
	protected DataToInterfaceAgent m_sdcDataToMe;
	
	// 接收Agent
	protected AID mReceiver;
	protected int mBlackboardIndex;
	protected Object mDataToSend;
	
	/**
	 * 必须传InterfaceAgent类的对象，否则后续这个行为将无用
	 * @param a
	 */
	public InterfaceAgentBehaviour(Agent a) {
		if(InterfaceAgent.class.isInstance(a)) myAgent=a;
		else myAgent=null;
	}
	
	//转发时使用的消息
	protected ACLMessage msgForSend;
	
	//各类数据对象，填充在消息体中
	protected DataFromInterfaceAgent m_sdcForCommon;
	protected DataToSearchEntryAgent m_sdcForSearch;
	protected DataForClickRecomm m_sdcForClickRecomm;
	protected DataToQueryRecommEntryAgent m_sdcForQueryRecomm;
	
	/**
	 * 获取并设置要转发的消息对象
	 * @return
	 */
	private ACLMessage getMsgForSend(){
		
		//实现延迟加载方式，同时该成员变量反复被重用
		if(null==msgForSend){
			msgForSend=new ACLMessage(ACLMessage.INFORM);
			AID myAID=myAgent.getAID();
			msgForSend.addReplyTo(myAID);
			msgForSend.setSender(myAID);
		}
		//重用时，上一轮发消息时的接收者还在，需要清空
		msgForSend.clearAllReceiver();
		return msgForSend;
	}
	
	/**
	 * 获取转发消息中的数据对象
	 * @return
	 */
	private DataFromInterfaceAgent getCommonData(){
		if(null==m_sdcForCommon){
			m_sdcForCommon=new DataFromInterfaceAgent();
		}
		return m_sdcForCommon;
	}
	
	/**
	 * 获取转发给搜索入口Agent的数据对象
	 * @return
	 */
	private DataToSearchEntryAgent getDataForSearch(){
		if(null==m_sdcForSearch){
			m_sdcForSearch=new DataToSearchEntryAgent(SearchEntryAgentTxType.taskDispatch);
		}
		return m_sdcForSearch;
	}
	
	/**
	 * 获取转发给查询推荐Agent的数据对象
	 * @return
	 */
	private DataToQueryRecommEntryAgent getDataForQueryRecomm(){
		if(null==m_sdcForQueryRecomm){
			m_sdcForQueryRecomm=new DataToQueryRecommEntryAgent(QueryRecommEntryAgentTxType.taskDispatch);
		}
		return m_sdcForQueryRecomm;
	}
	
	/**
	 * 获取转发给结果推荐Agent的数据对象
	 * @return
	 */
	private DataForClickRecomm getDataForClickRecomm(){
		if(null==m_sdcForClickRecomm){
			m_sdcForClickRecomm=new DataForClickRecomm();
		}
		return m_sdcForClickRecomm;
	}
	
	@Override
	public void action() {

			ACLMessage msg = myAgent.receive();
			if (null != msg) {
				if(!getDataFromMsg(msg)) return;
				InterfaceAgentTxType type = m_sdcDataToMe.getTransactionType();
				mBlackboardIndex = m_sdcDataToMe.getIndex();
				ACLMessage oMsg=getSentMsg(type);
				if(null!=oMsg) myAgent.send(oMsg);
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
			m_sdcDataToMe = (DataToInterfaceAgent) msg.getContentObject();
			ret = true;
		} catch (Exception e) {

		}
		
		return ret;
	}
	
	/**
	 * 准备接口Agent要转发的消息
	 * @return
	 */
	public ACLMessage getSentMsg(InterfaceAgentTxType type){

		Serializable data=getSendData(type);
		mReceiver=((InterfaceAgent)myAgent).getReceiver(type);
		if(null==data||null==mReceiver) return null;
		
		ACLMessage ret = getMsgForSend();
		ret.addReceiver(mReceiver);
		try {
			ret.setContentObject(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * 获取接口Agent转发消息中的数据对象
	 * @param type 事务类型
	 * @return
	 */
	private Serializable getSendData(InterfaceAgentTxType type){
		
		Serializable ret=null;
		switch (type) {
		case login:
		case regist:
		case clicklog:
		case groupDivide:
			DataFromInterfaceAgent cd=getCommonData();
			cd.setIndex(mBlackboardIndex);
			ret=cd;
			break;
		case search:
			DataToSearchEntryAgent sd=getDataForSearch();
			sd.setIndex(mBlackboardIndex);
			ret=sd;
			break;
		case queryRecomm:
			DataToQueryRecommEntryAgent qd=getDataForQueryRecomm();
			qd.setIndex(mBlackboardIndex);
			ret=qd;
			break;
		case clickRecomm:
			DataForClickRecomm crd=getDataForClickRecomm();
			crd.setIndex(mBlackboardIndex);
			ret=crd;
			break;
		default:
			break;
		}
		return ret;
	}
}
