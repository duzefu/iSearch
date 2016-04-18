package agent.agentclass.workingagent;

import server.info.config.agent.ServiceNamesOfAgent;
import agent.behaviours.agentspecific.GroupDivideBehaviour;
import agent.utils.DFServiceUtil;
import jade.core.Agent;

public class GroupDivideAgent extends Agent {
	
	protected void setup() {
		DFServiceUtil.registerService(this, ServiceNamesOfAgent.GROUP_DIVIDE);
		this.addBehaviour(new GroupDivideBehaviour());
	}

	protected void takeDown() {
		DFServiceUtil.deRegisterAllService(this);
	}
}
