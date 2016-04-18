package struts.actions.android;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import common.functions.webpagediagram.CoolDynamicBar;
import db.dao.UserDao;

public class AndroidUserInterstAction {
	private HttpServletResponse response;
	private String username ;
	private String userid;
	private String imei;
	private List<Entry<String, Double>> userInterest;
	private UserDao userDao;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	
	public String execute() throws Exception{
		if(username != null && !username.equals("")){
			//判断用户是否为登陆用户
			if(userDao.findUserByUsername(username) != null ){
				if(userid == String.valueOf(userDao.findUserByUsername(username).get(0).getUid())
						&& imei == userDao.findUserByUsername(username).get(0).getImei()){
						//获取用户的兴趣变迁 
						userInterest = CoolDynamicBar.GetDataset(Integer.parseInt(userid),username);
						//将结果传输给客户端
						response = ServletActionContext.getResponse();
						OutputStream outs = response.getOutputStream();
						ObjectOutputStream oos = new ObjectOutputStream(outs);
						oos.writeObject(userInterest);
						//关闭资源
						oos.flush();
						oos.close();
						outs.flush();
						outs.close();
					}
				}
			}
		return null;
	}
}
	

