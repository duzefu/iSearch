package server.info.config.agent;

public class PathOfAgentsClass {

	private static String msInterfaceAgentPath="agent.agentclass.entryagent.InterfaceAgent";
	private static String msGatewayAgentPath="jade.wrapper.gateway.GatewayAgent";
	
	private static String msLoginProcessAgentPath="agent.agentclass.workingagent.UserLoginAgent";
	private static String msRegistProcessAgentPath="agent.agentclass.workingagent.UserRegistAgent";
	private static String msSearchEntryAgentPath="agent.agentclass.workingagent.SearchEntryAgent";
	private static String msMemberSearchAgentPath="agent.agentclass.workingagent.MemberSearchAgent";
	private static String msResultMergeAgentPath="agent.agentclass.workingagent.ResultMergeAgent";
	private static String msClickLogAgentPath="agent.agentclass.workingagent.ClickLogAgent";
	private static String msQueryRecommAgentPath="agent.agentclass.workingagent.QueryRecommEntryAgent";
	private static String msQueryGroupRecommAgentPath="agent.agentclass.workingagent.QueryGroupRecommAgent";
	private static String msQueryQfgRecommAgentPath="agent.agentclass.workingagent.QueryQFGRecommAgent";
	private static String msClickRecommAgentPath="agent.agentclass.workingagent.ResultRecommAgent";
	private static String msGroupDivideAgentPathString="agent.agentclass.workingagent.GroupDivideAgent";
	
	public static String InterfaceAgent(){
		return msInterfaceAgentPath;
	}
	public static String GatewayAgent(){
		return msGatewayAgentPath;
	}
	
	public static String UserLoginAgent(){
		return msLoginProcessAgentPath;
	}
	public static String UserClickLogAgent(){
		return msClickLogAgentPath;
	}
	public static String UserRegistAgent(){
		return msRegistProcessAgentPath;
	}
	public static String SearchEntryAgent(){
		return msSearchEntryAgentPath;
	}
	public static String MemberSearchAgent(){
		return msMemberSearchAgentPath;
	}
	public static String ResultMergeAgent(){
		return msResultMergeAgentPath;
	}
	public static String QueryRecommEntryAgent(){
		return msQueryRecommAgentPath;
	}
	public static String QueryGroupRecommAgent(){
		return msQueryGroupRecommAgentPath;
	}
	public static String QueryQFGRecommAgent(){
		return msQueryQfgRecommAgentPath;
	}
	public static String ClickRecommAgent(){
		return msClickRecommAgentPath;
	}
	public static String GroupDivideAgent(){
		return msGroupDivideAgentPathString;
	}
}
