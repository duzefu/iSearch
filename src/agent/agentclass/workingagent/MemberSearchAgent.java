package agent.agentclass.workingagent;

import server.info.config.agent.ServiceNamesOfAgent;
import agent.behaviours.agentspecific.MemberSearchBehaviour;
import agent.utils.DFServiceUtil;
import jade.core.Agent;

/**
 * 成员搜索Agent，具有一个行为，根据收到的消息中指定的搜索引擎名称，查询搜索结果
 * @author zhou
 */
public class MemberSearchAgent extends Agent {

	protected void setup(){
		DFServiceUtil.registerService(this, ServiceNamesOfAgent.MEMBER_SERACH);
		this.addBehaviour(new MemberSearchBehaviour());
	}
	
	protected void takeDown(){
		DFServiceUtil.deRegisterAllService(this);
	}
	
}
