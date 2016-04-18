package agent.agentclass.workingagent;

import server.info.config.agent.ServiceNamesOfAgent;
import agent.behaviours.agentspecific.SearchResultCheckBehaviour;
import agent.behaviours.agentspecific.SearchTaskDispatchBehaviour;
import agent.behaviours.common.AIDPoolUpdateBehaviour;
import agent.utils.AIDPoolUpdateUtil;
import agent.utils.AIDUpdator;
import agent.utils.AgentIDPool;
import agent.utils.DFServiceUtil;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class SearchEntryAgent extends Agent implements AIDUpdator{

	protected AgentIDPool mMemSEAIDPool;
	protected AgentIDPool mResMergeAIDPool;
	
	private DFAgentDescription dfd;
	private ServiceDescription sd;
	
	protected void setup(){
		initAIDPool();
		initServiceSearchData();
		DFServiceUtil.registerService(this, ServiceNamesOfAgent.SEARCH_ENTRY,dfd,sd);
		addBehaviour(new AIDPoolUpdateBehaviour(this));
		addBehaviour(new SearchTaskDispatchBehaviour());
		addBehaviour(new SearchResultCheckBehaviour());
	}
	
	protected void takeDown(){
		DFServiceUtil.deRegisterAllService(this);
	}
	
	protected void initAIDPool(){
		mMemSEAIDPool=new AgentIDPool();
		mResMergeAIDPool=new AgentIDPool();
	}
	
	protected void initServiceSearchData(){
		
		dfd=new DFAgentDescription();
		sd=new ServiceDescription();
	}

	@Override
	public boolean updateAIDPool() {
		
		AIDPoolUpdateUtil.updatePool(ServiceNamesOfAgent.MEMBER_SERACH, mMemSEAIDPool,this,dfd,sd);
		AIDPoolUpdateUtil.updatePool(ServiceNamesOfAgent.RESULT_MREGE, mResMergeAIDPool,this,dfd,sd);
		return false;
	}
	
	public AID getMemberSearchReceiver() {

		return mMemSEAIDPool.getNext();
	}
	
	public AID getResultMergeReceiver(){
		return mResMergeAIDPool.getNext();
	}
	
}
