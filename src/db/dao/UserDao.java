package db.dao;

import java.util.List;

import server.info.config.MyEnums.UserLoginResult;
import server.info.entites.transactionlevel.UserEntity;
import db.hibernate.tables.isearch.User;

public interface UserDao {

	public int add(UserEntity user);
	
	public void delete(int uid);
	
	public int update(UserEntity user);
	
	public UserEntity get(int uid);
	
	public List<UserEntity> findUserByUsername(String username);
	
	public List<UserEntity> findUserByCookieid(String cookieid);
	
	public List<UserEntity> findUserByEmailAdress(String emaildress);
	
	public List<UserEntity> findUserByImei(String imei);
	
	public int getUserIdByCookieid(String cookieid);
	
	public int isLegalUserPassword(String username, String pwd);
	
	public List<Integer> getAllUserID();
	
	public boolean setPasswd(int uid, String passwd);
	
	public int getUserIDByUserName(String username);
	
	/**
	 * 检查用户名或邮箱是否已经存在
	 * 
	 * @param username 用户名，可以为null或空字符串
	 * @param email 邮箱，可以为null或空字符串
	 * @return 
	 *         当username及email都传了null或空字符串时，返回false；
	 *         两个参数任意一个有效，就查找数据库，只要数据库中有相应的用户名
	 *         或邮箱，返回true；否则返回false
	 */
	public boolean isExistUnameOrEmail(String username, String email);
	
	/**
	 * 检查用户名及密码，返回检查结果
	 * @param username 用户名
	 * @param passwd 密码（不要用MD5处理）
	 * @param ret 数组，长度为1即可，用来返回这个用户的ID
	 * @return 用户名不存在时，返回枚举变量no_exist_user；否则检查密码，密码错误返回error_passwd；正确则返回correct
	 */
	public UserLoginResult checkUserInfo(String username, String passwd, int ret[]);
}
