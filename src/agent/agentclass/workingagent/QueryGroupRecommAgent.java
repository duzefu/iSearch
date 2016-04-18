package agent.agentclass.workingagent;

import server.info.config.agent.ServiceNamesOfAgent;
import agent.behaviours.agentspecific.QueryGroupRecommBehaviour;
import agent.utils.DFServiceUtil;
import jade.core.Agent;

public class QueryGroupRecommAgent extends Agent {

	protected void setup(){
		DFServiceUtil.registerService(this, ServiceNamesOfAgent.QUERY_GROUP_REOMMENDATION);
		this.addBehaviour(new QueryGroupRecommBehaviour());
	}
	
	protected void takeDown(){
		DFServiceUtil.deRegisterAllService(this);
	}
}
