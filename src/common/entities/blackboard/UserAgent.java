package common.entities.blackboard;


import java.util.Observable;
import java.util.Observer;

public class UserAgent implements Observer{
	private Integer userid;
	private String username;
	
	public Integer getUserid() {
			return userid;
		}
		public void setUserid(Integer userid) {
			this.userid = userid;
		}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	@Override
	public void update(Observable o, Object arg) {

		if (arg instanceof String){
               String s = (String) arg;
	           System.out.println(this.userid+s);
	           }
		//发生相关变化后，通知用户更新xml文件...
		
	}

}
