package agent.data.inmsg;

import agent.data.inmsg.TransactionType.QueryRecommEntryAgentTxType;
import jade.util.leap.Serializable;

public class DataToQueryRecommEntryAgent implements Serializable{

	private static final long serialVersionUID = 1604669455920615057L;
	
	//实际数据在黑板中的索引（编号）
	private int mBlackboardIndex;
	private QueryRecommEntryAgentTxType mTranType;
	
	/**
	 * 构造数据对象
	 * @param blackboardIndex 真实数据在黑板中的索引
	 * @param type 事务类型
	 */
	public DataToQueryRecommEntryAgent(int blackboardIndex, QueryRecommEntryAgentTxType type){
		mBlackboardIndex=blackboardIndex;
		mTranType=type;
	}
	
	public DataToQueryRecommEntryAgent(QueryRecommEntryAgentTxType type){
		this(0, type);
	}
	
	public int getIndex(){
		return mBlackboardIndex;
	}
	
	public QueryRecommEntryAgentTxType getTransactionType(){
		return mTranType;
	}
	
	public void setIndex(int index){
		mBlackboardIndex=index;
	}
}
