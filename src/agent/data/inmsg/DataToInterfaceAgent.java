package agent.data.inmsg;

import agent.data.inmsg.TransactionType.InterfaceAgentTxType;
import jade.util.leap.Serializable;
/**
 * 封装的一个传递给InterfaceAgent的消息结构
 * 包括黑板的索引编号，事务类型
 * @author ZCL
 *
 */
public class DataToInterfaceAgent implements Serializable{

	private static final long serialVersionUID = 1604669455920615057L;
	
	//实际数据在黑板中的索引（编号）
	private int mBlackboardIndex;
	//事务类型，如搜索、登录等
	private InterfaceAgentTxType mTranType;
	
	/**
	 * 构造数据对象
	 * @param blackboardIndex 真实数据在黑板中的索引
	 * @param type 事务类型
	 */
	public DataToInterfaceAgent(int blackboardIndex, InterfaceAgentTxType type){
		mBlackboardIndex=blackboardIndex;
		mTranType=type;
	}
	
	public DataToInterfaceAgent(int blackboardIndex){
		mBlackboardIndex=blackboardIndex;
	}
	public int getIndex(){
		return mBlackboardIndex;
	}
	
	public InterfaceAgentTxType getTransactionType(){
		return mTranType;
	}
	
	public void setTransactionType(InterfaceAgentTxType type){
		mTranType=type;
	}
}
