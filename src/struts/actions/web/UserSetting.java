package struts.actions.web;

//import java.io.IOException;
//import java.io.PrintWriter;
import java.util.List;
















//import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import server.info.entites.transactionlevel.UserEntity;
import db.dao.UserDao;
import db.dao.UserSeSettingDao;
import db.hibernate.tables.isearch.User;
import db.hibernate.tables.isearch.UserSeSetting;

public class UserSetting {
	
	private UserDao userDao;
	
	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public List<String> getCheck() {
		return check;
	}

	public void setCheck(List<String> check) {
		this.check = check;
	}

	public UserSeSettingDao getUserSeSettingDao() {
		return userSeSettingDao;
	}

	public void setUserSeSettingDao(UserSeSettingDao userSeSettingDao) {
		this.userSeSettingDao = userSeSettingDao;
	}

	private List<String> check;
	
	private UserSeSettingDao userSeSettingDao;
	
	public String execute() throws Exception
	{
		/**
		 * 这个类有错，暂时不用
		 */
//		String userSettings = null;
//		if(check.isEmpty()) return userSettings;
//		for(String s:check){
//			if(userSettings==null)
//			{
//				userSettings = s;
//			}
//			else
//			{
//				userSettings = userSettings+"&"+s;
//			}
//		}
//		System.out.println(userSettings);
//		HttpSession session = ServletActionContext.getRequest().getSession();
//		String usernameinpage = (String)session.getAttribute("usernameinpage");
//		System.out.println(usernameinpage);
//		List<UserEntity> ul = userDao.findUserByUsername(usernameinpage);
//		List<UserSeSetting> us = userSeSettingDao.findUserSeSettingByUsername(usernameinpage);
//		UserSeSetting uss = new UserSeSetting();
//		User user=new User();
//		user.setUserid(ul.get(0).getUid());
//		uss.setUser(user);
//		uss.setUsername(usernameinpage);
//		uss.setSeSetting(userSettings);
//		if(us.isEmpty())
//		{
//			userSeSettingDao.add(uss);
//		}
//		else
//		{
//			userSeSettingDao.update(uss);
//		}
//		System.out.println(userSettings);
		
		return "saveSettingsSuccess";
		
		
		
//		HttpServletResponse res = ServletActionContext.getResponse();  
//		res.reset();  
//		res.setContentType("text/html;charset=utf-8");  
//		PrintWriter pw = null;
//		try {
//			pw = res.getWriter();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}  
//		  
//		pw.print("<dl>");
//		pw.flush();  
//		pw.close();
//		return null;
	}
}
