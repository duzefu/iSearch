package agent.agentclass.workingagent;

import server.info.config.agent.ServiceNamesOfAgent;
import agent.behaviours.agentspecific.QueryQFGRecommBehaviour;
import agent.utils.DFServiceUtil;
import jade.core.Agent;

public class QueryQFGRecommAgent extends Agent {

	protected void setup(){
		DFServiceUtil.registerService(this, ServiceNamesOfAgent.QUERY_QFG_RECOMMENDATION);
		this.addBehaviour(new QueryQFGRecommBehaviour());
	}
	
	protected void takeDown(){
		DFServiceUtil.deRegisterAllService(this);
	}
}
