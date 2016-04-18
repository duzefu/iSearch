package agent.data.inmsg;

import jade.util.leap.Serializable;

public class DataForQueryGroupRecomm implements Serializable {

	private static final long serialVersionUID = 5775460080965067845L;
	
	private int mBlackboardIndex;
	
	public DataForQueryGroupRecomm(){
	}
	
	public DataForQueryGroupRecomm(int index){
		mBlackboardIndex=index;
	}
	
	public int getIndex(){
		return mBlackboardIndex;
	}
	
	public void setIndex(int index){
		mBlackboardIndex=index;
	}
	
}
