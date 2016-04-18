package agent.behaviours.agentspecific;

import server.info.config.MyEnums.UserLoginResult;
import agent.data.inblackboard.LoginData;
import agent.data.inmsg.DataFromInterfaceAgent;
import agent.entities.blackboard.LoginDataBlackboard;
import db.dbhelpler.UserHelper;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class LoginProcessBehaviour extends Behaviour{

	protected DataFromInterfaceAgent m_sdcDataToMe;
	protected int m_nBlackboardIndex;
	
	protected LoginData m_sdcBlackboardData;
	protected String m_strUsername;
	protected String m_strPasswd;
	
	protected UserLoginResult m_enuResult;
	protected int m_arrId[] = new int[1];
	
	@Override
	public void action() {
		
		ACLMessage msg=myAgent.receive();
		if(null!=msg){
			if(!getDataFromMsg(msg)) return;
			if(!getDataFromBlackboard()) return;
			m_arrId[0]=-1;
			m_enuResult=UserHelper.checkUserInfo(m_strUsername, m_strPasswd, m_arrId);
			setResultToBlackboard();
			m_sdcBlackboardData.done();
		}else{
			block();
		}
	}

	protected boolean getDataFromMsg(ACLMessage msg) {

		boolean ret = false;
		if (null != msg) {
			m_sdcDataToMe = null;
			try {
				m_sdcDataToMe = (DataFromInterfaceAgent) msg.getContentObject();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (null != m_sdcDataToMe) {
				m_nBlackboardIndex = m_sdcDataToMe.getIndex();
				ret = true;
			}
		}
		return ret;
	}
	
	protected boolean getDataFromBlackboard(){
		
		boolean ret=false;
		m_sdcBlackboardData=LoginDataBlackboard.getData(m_nBlackboardIndex);
		if(null!=m_sdcBlackboardData){
			m_strUsername=m_sdcBlackboardData.getUserName();
			m_strPasswd=m_sdcBlackboardData.getPassword();
			ret=true;
		}
		return ret;
	}
	
	protected void setResultToBlackboard(){
		m_sdcBlackboardData.setUserid(m_arrId[0]);
		m_sdcBlackboardData.setResult(m_enuResult);
	}
	
	@Override
	public boolean done() {
		return false;
	}

}
