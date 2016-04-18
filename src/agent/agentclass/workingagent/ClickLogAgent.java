package agent.agentclass.workingagent;

import server.info.config.agent.ServiceNamesOfAgent;
import agent.behaviours.agentspecific.ClickLogBehaviour;
import agent.utils.DFServiceUtil;
import jade.core.Agent;

/**
 * @author CXL
 */
public class ClickLogAgent extends Agent {
	
	protected void setup() {
		DFServiceUtil.registerService(this, ServiceNamesOfAgent.CLICKLOG);
		this.addBehaviour(new ClickLogBehaviour());
	}

	protected void takeDown() {
		DFServiceUtil.deRegisterAllService(this);
	}
}
