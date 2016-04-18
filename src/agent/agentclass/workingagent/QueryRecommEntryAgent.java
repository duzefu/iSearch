package agent.agentclass.workingagent;

import server.info.config.agent.ServiceNamesOfAgent;
import agent.behaviours.agentspecific.QueryRecommEntryBehaviour;
import agent.behaviours.agentspecific.QueryRecommMergeBehaviour;
import agent.behaviours.common.AIDPoolUpdateBehaviour;
import agent.utils.AIDPoolUpdateUtil;
import agent.utils.AIDUpdator;
import agent.utils.AgentIDPool;
import agent.utils.DFServiceUtil;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class QueryRecommEntryAgent extends Agent implements AIDUpdator{

	protected AgentIDPool m_sdcGroupAIDPool;
	protected AgentIDPool m_sdcQFGAIDPool;
	
	private DFAgentDescription dfd;
	private ServiceDescription sd;
	
	protected void setup(){
		initAIDPool();
		initServiceSearchData();
		DFServiceUtil.registerService(this, ServiceNamesOfAgent.QUERY_RECOMMENDATION,dfd,sd);
		addBehaviour(new AIDPoolUpdateBehaviour(this));
		addBehaviour(new QueryRecommEntryBehaviour());
		addBehaviour(new QueryRecommMergeBehaviour());
	}
	
	protected void takeDown(){
		DFServiceUtil.deRegisterAllService(this);
	}
	
	protected void initAIDPool(){
		m_sdcGroupAIDPool=new AgentIDPool();
		m_sdcQFGAIDPool=new AgentIDPool();
	}
	
	protected void initServiceSearchData(){
		
		dfd=new DFAgentDescription();
		sd=new ServiceDescription();
	}

	@Override
	public boolean updateAIDPool() {
		
		AIDPoolUpdateUtil.updatePool(ServiceNamesOfAgent.QUERY_GROUP_REOMMENDATION, m_sdcGroupAIDPool,this,dfd,sd);
		AIDPoolUpdateUtil.updatePool(ServiceNamesOfAgent.QUERY_QFG_RECOMMENDATION, m_sdcQFGAIDPool,this,dfd,sd);
		return false;
	}
	
	public AID getGroupRecommReceiver() {

		return m_sdcGroupAIDPool.getNext();
	}
	
	public AID getQFGRecommReceiver(){
		return m_sdcQFGAIDPool.getNext();
	}
	
}
