package agent.data.inmsg;

import jade.util.leap.Serializable;

public class DataForClickRecomm implements Serializable {

	private static final long serialVersionUID = 5775460080965067845L;
	
	private int mBlackboardIndex;
	
	public DataForClickRecomm(){
	}
	
	public DataForClickRecomm(int index){
		mBlackboardIndex=index;
	}
	
	public int getIndex(){
		return mBlackboardIndex;
	}
	
	public void setIndex(int index){
		mBlackboardIndex=index;
	}
	
}
