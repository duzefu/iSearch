package agent.behaviours.common;

import server.info.config.agent.AgentConfigValue;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import agent.utils.AIDUpdator;
import agent.utils.AgentFactory;

public class AIDPoolUpdateBehaviour extends Behaviour{

	private AIDUpdator mAgentAsUpdator;
	private Agent mAgent;
	boolean status=false;
	
	public AIDPoolUpdateBehaviour(AIDUpdator agent){
		
		mAgentAsUpdator=agent;
		mAgent=(Agent)mAgentAsUpdator;
	}

	@Override
	public void action() {
		
			status=AgentFactory.doneAgentStartedInServerStartup;
			if(status){
				
				mAgent.addBehaviour(new OneShotBehaviour() {
					
					@Override
					public void action() {
						mAgentAsUpdator.updateAIDPool();
						mAgent.addBehaviour(new TickerBehaviour(mAgent, AgentConfigValue.SearchSearchInterval()) {
							
							@Override
							protected void onTick() {
								mAgentAsUpdator.updateAIDPool();
							}
						});
					}
				});
			}
	}

	@Override
	public boolean done() {
		return status;
	}

}
