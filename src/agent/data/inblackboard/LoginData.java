package agent.data.inblackboard;

import java.util.concurrent.CountDownLatch;

import server.info.config.MyEnums.UserLoginResult;

public class LoginData extends BlackboardBaseData{

	private String m_strUsername;
	private String m_strPasswd;
	private int m_nUserid;
	private UserLoginResult m_enuResult;
	
	public LoginData(CountDownLatch doneSig, String username, String password){
		
		super(doneSig);
		m_strUsername=username;
		m_strPasswd=password;
		m_nUserid=0;
	}
	
	public void setUserid(int userid){
		m_nUserid=userid;
	}
	
	public void setUserid(Integer userid){
		int uid=null==userid?0:userid.intValue();
		setUserid(uid);
	}
	
	public int getUserid(){
		return m_nUserid;
	}
	
	public String getUserName(){
		return m_strUsername;
	}
	
	public String getPassword(){
		return m_strPasswd;
	}
	
	public void setResult(UserLoginResult result){
		m_enuResult=result;
	}
	
	public UserLoginResult getResult(){
		return m_enuResult;
	}
}
