package agent.behaviours.agentspecific;

import server.info.config.MyEnums.RegisterResult;
import server.info.entites.transactionlevel.UserEntity;
import agent.data.inblackboard.RegistData;
import agent.data.inmsg.DataFromInterfaceAgent;
import agent.entities.blackboard.RegistDataBlackboard;
import common.textprocess.userXMLfilehelpler.UserXMLHelper;
import db.dbhelpler.UserHelper;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class RegistProcessBehaviour extends Behaviour{

	protected DataFromInterfaceAgent mData;
	protected String mUsername;
	protected String mPasswd;
	protected String mEmail;
	protected String mCookieid;
	protected int mUserid;
	protected int mBlackboardIndex;
	
	@Override
	public void action() {
		
		ACLMessage msg=myAgent.receive();
		if(null!=msg){
			RegistData data = null;
			try {
				mData=null;
				Object obj=msg.getContentObject();
				if(!DataFromInterfaceAgent.class.isInstance(obj)) return;
				mData=(DataFromInterfaceAgent)obj;
				if(null==mData) return;
				mBlackboardIndex=mData.getIndex();
				data=(RegistData)RegistDataBlackboard.getData(mBlackboardIndex);
				mUsername=data.getUsername();
				mPasswd=data.getPassword();
				mEmail = data.getEmailadress();
				boolean isExistu = UserHelper.isExistU(mUsername);
				boolean isExiste = UserHelper.isExistE(mEmail);
				if(isExistu) data.setRegistState(RegisterResult.username_exit);
				else if(isExiste) data.setRegistState(RegisterResult.email_exist);
				else{
					UserEntity user = new UserEntity();
					user.setUsername(mUsername);
					user.setPassword(mPasswd);
					user.setEmail(mEmail);
					int uid = UserHelper.addUserEntity(user);
//					String userId = String.valueOf(uid);
//					UserXMLHelper.getInstance().createUserXMLFile(userId);// 生成本次注册用户的兴趣XML文件（by许静
																			// 20121105)
					if(UserHelper.isLegalUserID(uid)) data.setRegistState(RegisterResult.success);
					else data.setRegistState(RegisterResult.illegal_info);//这里认为没有把信息放入数据库是因为注册信息有错
				}
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
		return false;
	}

}
