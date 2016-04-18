package server.info.config.agent;

public class AgentConfigValue {

	private static int smServiceSearchInterval=DefaultAgentConfigValue.ServiceSearchInterval;
	
	public static  int SearchSearchInterval(){
		return smServiceSearchInterval;
	}
	
}
