package agent.agentclass.workingagent;

import server.info.config.agent.ServiceNamesOfAgent;
import agent.behaviours.agentspecific.ResultMergeBehaviour;
import agent.utils.DFServiceUtil;
import jade.core.Agent;

public class ResultMergeAgent extends Agent {

	protected void setup(){
		DFServiceUtil.registerService(this, ServiceNamesOfAgent.RESULT_MREGE);
		this.addBehaviour(new ResultMergeBehaviour());
	}
	
	protected void takeDown(){
		DFServiceUtil.deRegisterAllService(this);
	}
}
