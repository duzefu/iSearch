package agent.agentclass.workingagent;

import server.info.config.agent.ServiceNamesOfAgent;
import agent.behaviours.agentspecific.LoginProcessBehaviour;
import agent.utils.DFServiceUtil;
import jade.core.Agent;

public class UserLoginAgent extends Agent {

	protected void setup(){
		DFServiceUtil.registerService(this, ServiceNamesOfAgent.LOGIN);
		this.addBehaviour(new LoginProcessBehaviour());
	}
	
	protected void takeDown(){
		
		DFServiceUtil.deRegisterAllService(this);
	}
	
}
