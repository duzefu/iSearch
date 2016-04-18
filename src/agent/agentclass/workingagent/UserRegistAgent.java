package agent.agentclass.workingagent;

import server.info.config.agent.ServiceNamesOfAgent;
import agent.behaviours.agentspecific.RegistProcessBehaviour;
import agent.utils.DFServiceUtil;
import jade.core.Agent;

/**
 * 用户注册Agent的定义
 * @author CXL
 *
 */
public class UserRegistAgent extends Agent {
	
	protected void setup(){
		DFServiceUtil.registerService(this, ServiceNamesOfAgent.REGISTER);
		this.addBehaviour(new RegistProcessBehaviour());
	}
	
	protected void takeDown(){
		
		DFServiceUtil.deRegisterAllService(this);
	}
}
