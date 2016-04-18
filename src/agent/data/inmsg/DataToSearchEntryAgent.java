package agent.data.inmsg;

import agent.data.inmsg.TransactionType.SearchEntryAgentTxType;
import jade.util.leap.Serializable;

public class DataToSearchEntryAgent implements Serializable{
	
	private static final long serialVersionUID = 5537374920007057612L;
	//实际数据在黑板中的索引（编号）
	private int mBlackboardIndex;
	private SearchEntryAgentTxType mTranType;
	
	/**
	 * 构造数据对象
	 * @param blackboardIndex 真实数据在黑板中的索引
	 * @param type 事务类型
	 */
	public DataToSearchEntryAgent(int blackboardIndex, SearchEntryAgentTxType type){
		mBlackboardIndex=blackboardIndex;
		mTranType=type;
	}
	
	public DataToSearchEntryAgent(SearchEntryAgentTxType type){
		this(0, type);
	}
	
	public int getIndex(){
		return mBlackboardIndex;
	}
	
	public SearchEntryAgentTxType getTransactionType(){
		return mTranType;
	}
	
	public void setIndex(int index){
		mBlackboardIndex=index;
	}
}
