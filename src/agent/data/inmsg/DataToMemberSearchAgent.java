package agent.data.inmsg;

import jade.util.leap.Serializable;

public class DataToMemberSearchAgent implements Serializable {

	private static final long serialVersionUID = 5775460080965067845L;
	
	private int mBlackboardIndex;
	private String mEngineName;//服务的搜索引擎
	private int mLastAmount;//上一次调用者已经获得的数据条目
	private int mTargetPage;
	
	public DataToMemberSearchAgent(){}
	
	public DataToMemberSearchAgent(int index, String engineName){
		mBlackboardIndex=index;
	}
	
	public int getIndex(){
		return mBlackboardIndex;
	}
	
	public int getLastAmount(){
		return mLastAmount;
	}
	
	public int getTargetPage(){
		return mTargetPage;
	}
	
	public String getEngineName(){
		return mEngineName;
	}
	
	public void setIndex(int index){
		mBlackboardIndex=index;
	}
	
	public void setEngineName(String name){
		mEngineName=name;
	}
	
	public void setLastAmount(int amount){
		mLastAmount=amount;
	}
	
	public void setSearchPage(int page){
		mTargetPage=page;
	}
}
