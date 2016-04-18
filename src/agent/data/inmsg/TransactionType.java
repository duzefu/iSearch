package agent.data.inmsg;

public class TransactionType {

	public static enum InterfaceAgentTxType {login,regist,search,clicklog,queryRecomm,clickRecomm,groupDivide};
	public static enum SearchEntryAgentTxType {taskDispatch, memberSearchDone, resultMergeDone,searchContinue};
	public static enum QueryRecommEntryAgentTxType {taskDispatch, groupRecommDone, qfgRecommDone};
	
}
