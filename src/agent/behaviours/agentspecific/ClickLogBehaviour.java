package agent.behaviours.agentspecific;

import agent.data.inblackboard.ClickLogData;
import agent.data.inmsg.DataFromInterfaceAgent;
import agent.entities.blackboard.ClickLogDataBlackboard;
import common.functions.userinterest.UserClickLogger;
import db.dbhelpler.UserHelper;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class ClickLogBehaviour  extends Behaviour{
	
	protected DataFromInterfaceAgent mData;
	protected int userId;
	protected String query;
	protected String title;
	protected String date;
	protected String clickAddr;
	protected String source;
	protected String abstr;
	protected int mBlackboardIndex;
	
	@Override
	public void action() {
		ACLMessage msg=myAgent.receive();
		if(null!=msg){
			ClickLogData data = null;
			try {
				Object obj=msg.getContentObject();
				if(!DataFromInterfaceAgent.class.isInstance(obj)) return;
				mData=null;
				mData=(DataFromInterfaceAgent)obj;
				if(null==mData) return;
				mBlackboardIndex=mData.getIndex();
				data=(ClickLogData)ClickLogDataBlackboard.getData(mBlackboardIndex);
				userId = data.getUserid();
				query = data.getQuery();
				title = data.getTitle();
				date = data.getData();
				source = data.getSource();
				clickAddr = data.getClickAddr();
				abstr = data.getAbstr();
				// 更新click_log与user_favor_word
				UserClickLogger idb = new UserClickLogger();
				idb.record(userId, query, title, abstr, clickAddr, date, source);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(null!=data){
					data.done();
				}
			}
		}else{
			block();
		}
	}
	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return false;
	}
}
