package agent.agentclass.entryagent;

import server.info.config.agent.ServiceNamesOfAgent;
import agent.behaviours.agentspecific.InterfaceAgentBehaviour;
import agent.behaviours.common.AIDPoolUpdateBehaviour;
import agent.data.inmsg.TransactionType.InterfaceAgentTxType;
import agent.utils.AIDPoolUpdateUtil;
import agent.utils.AIDUpdator;
import agent.utils.AgentIDPool;
import agent.utils.DFServiceUtil;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class InterfaceAgent extends Agent implements AIDUpdator{
	
	private AgentIDPool mLoginProcessAgentPool;
	private AgentIDPool mRegistProcessAgentPool;
	private AgentIDPool mSearchEntryAgentPool;
	private AgentIDPool mClickLogAgentPool;
	private AgentIDPool mQueryRecommAgentPool;
	private AgentIDPool mClickRecommAgentPool;
	private AgentIDPool mGroupDivideAgentPool;
	
	//服务注册/搜索时使用的对象，可重用，不必每次都重新new
	//但在重用的时候要留意其中的一些服务名称、类型是否被正确设置、清空等
	private DFAgentDescription dfd;
	private ServiceDescription sd;
	
	protected void setup() {
		initReceiverAIDPool();
		initServiceSearchData();
		DFServiceUtil.registerService(this, ServiceNamesOfAgent.INTERFACE,dfd,sd);
		addBehaviour(new InterfaceAgentBehaviour(this));
		addBehaviour(new AIDPoolUpdateBehaviour(this));
	}

	/**
	 * 在Agent结束时向DF撤销注册的服务
	 */
	protected void takeDown(){
		DFServiceUtil.deRegisterAllService(this);
	}
	
	/**
	 * 初始化Agent池（为空，具体内容在AID池更新的行为中设置
	 */
	protected void initReceiverAIDPool() {
		mLoginProcessAgentPool = new AgentIDPool();
		mRegistProcessAgentPool = new AgentIDPool();
		mSearchEntryAgentPool=new AgentIDPool();
		mClickLogAgentPool = new AgentIDPool();
		mQueryRecommAgentPool=new AgentIDPool();
		mClickRecommAgentPool=new AgentIDPool();
		mGroupDivideAgentPool = new AgentIDPool();
	}
	
	/**
	 * 初始化Agent服务注册与搜索时所需要的数据
	 */
	protected void initServiceSearchData(){
		
		dfd=new DFAgentDescription();
		sd=new ServiceDescription();
	}

	/**
	 * 根据业务类型的不同，选择特定的Agent作为接收者
	 * 
	 * @param type 业务类型
	 * @return Agent ID
	 */
	public AID getReceiver(InterfaceAgentTxType type) {

		AID ret = null;

		switch (type) {
		case login:
			ret = mLoginProcessAgentPool.getNext();
			break;
		case clicklog:
			ret = mClickLogAgentPool.getNext();
			break;
		case regist:
			ret = mRegistProcessAgentPool.getNext();
			break;
		case search:
			ret=mSearchEntryAgentPool.getNext();
			break;
		case queryRecomm:
			ret=mQueryRecommAgentPool.getNext();
			break;
		case clickRecomm:
			ret=mClickRecommAgentPool.getNext();
			break;
		case groupDivide:
			ret = mGroupDivideAgentPool.getNext();
			break;
		default:
			break;
		}

		return ret;
	}

	
	@Override
	public boolean updateAIDPool() {
		AIDPoolUpdateUtil.updatePool(ServiceNamesOfAgent.CLICKLOG, mClickLogAgentPool,this,dfd,sd);
		AIDPoolUpdateUtil.updatePool(ServiceNamesOfAgent.LOGIN, mLoginProcessAgentPool,this,dfd,sd);
		AIDPoolUpdateUtil.updatePool(ServiceNamesOfAgent.REGISTER, mRegistProcessAgentPool,this,dfd,sd);
		AIDPoolUpdateUtil.updatePool(ServiceNamesOfAgent.SEARCH_ENTRY, mSearchEntryAgentPool,this,dfd,sd);
		AIDPoolUpdateUtil.updatePool(ServiceNamesOfAgent.QUERY_RECOMMENDATION, mQueryRecommAgentPool, this, dfd, sd);
		AIDPoolUpdateUtil.updatePool(ServiceNamesOfAgent.RESULT_RECOMMENDATION, mClickRecommAgentPool, this, dfd, sd);
		AIDPoolUpdateUtil.updatePool(ServiceNamesOfAgent.GROUP_DIVIDE, mGroupDivideAgentPool, this, dfd, sd);
		return true;
	}
	
}
