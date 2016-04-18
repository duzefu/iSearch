package agent.agentclass.workingagent;

import server.info.config.agent.ServiceNamesOfAgent;
import agent.behaviours.agentspecific.ClickRecommBehaviour;
import agent.utils.DFServiceUtil;
import jade.core.Agent;

public class ResultRecommAgent extends Agent {

	protected void setup(){
		DFServiceUtil.registerService(this, ServiceNamesOfAgent.RESULT_RECOMMENDATION);
		this.addBehaviour(new ClickRecommBehaviour());
	}
	
	protected void takeDown(){
		DFServiceUtil.deRegisterAllService(this);
	}
}
