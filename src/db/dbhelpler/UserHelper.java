package db.dbhelpler;

import java.util.List;

import server.commonutils.SpringBeanFactoryUtil;
import server.commonutils.MyStringChecker;
import server.info.config.SpringBeanNames;
import server.info.config.MyEnums.UserLoginResult;
import server.info.entites.transactionlevel.UserEntity;
import db.dao.UserDao;

public class UserHelper {

	private UserDao userdao;

	private static UserHelper instance;

	private static UserHelper getInstance() {
		if (null == instance) {
			synchronized (UserHelper.class) {
				if (null == instance) {
					instance = new UserHelper();
				}
			}
		}
		return instance;
	}

	private UserHelper() {
		userdao = (UserDao) SpringBeanFactoryUtil.getBean(SpringBeanNames.USER_DAO_BEAN_NAME);
	}

	private static UserDao getUserDao(){
		
		return getInstance().userdao;
	}
	/**
	 * 验证用户名与密码是否匹配
	 * 
	 * Parameters: username  pwd 
	 * 
	 * @param username 用户名
	 * @param password 密码 
	 * @return 如果匹配，返回这个用户名在数据库中的ID；否则返回0
	 */
	public static int isLegalUserInfo(String username, String password) {

		int ret = 0;
		if (MyStringChecker.isBlank(username) || MyStringChecker.isBlank(password))
			return ret;
		return getUserDao().isLegalUserPassword(username,
				password);
	}
	
	/**
	 * 用户ID合法（正数），但不保证是有效的用户
	 * @param userid
	 * @return
	 */
	public final static boolean isLegalUserID(int userid){
		return userid>0;
	}
	
	/**
	 * 判断该用户名是否已经注册
	 * @param username
	 * @return 存在返回true，不存在返回false
	 */
	public static boolean isExistU(String username){
		
		if(MyStringChecker.isBlank(username)) return false;
		return getUserDao().isExistUnameOrEmail(username, null);
	}
	/**
	 * 判断该邮箱是否已经注册
	 * @param email
	 * @return 存在返回true，不存在返回false
	 */
	public static boolean isExistE(String email){
		
		if(MyStringChecker.isBlank(email)) return false;
		return getUserDao().isExistUnameOrEmail(null, email);
	}
	/**
	 * 检查用户名或邮箱对应的用户是否已经存在
	 * @param username
	 * @param email
	 * @return 
	 */
	public static boolean isExistUE(String username, String email){
		
		if(MyStringChecker.isBlank(username)) return isExistE(email);
		if(MyStringChecker.isBlank(email)) return isExistU(username);
		return getUserDao().isExistUnameOrEmail(username, email);
	}
	
	/**
	 * 查找cookie对应的用户实体
	 * @param cookie
	 * @return
	 */
	public static UserEntity isExistC(String cookie){
		if (MyStringChecker.isBlank(cookie))
			return null;
		List<UserEntity> temp = getUserDao().findUserByCookieid(cookie);
		if (temp.size()!=0) {
			return temp.get(0);
		}else{
			return null;
		}
	}
	/**
	 * 添加一个用户实体
	 * @param UserEntity
	 * @return
	 */
	public static int addUserEntity(UserEntity user){
		int ret = 0;
		ret = getUserDao().add(user);
		return ret;
	}

	
	public final static boolean isLoginUser(int userid){
		
		return userid>1;
	}
	
	public static int getUserIDByUsername(String username){
		
		int ret=1;
		if(null!=username&&!username.isEmpty()){
			ret=getUserDao().getUserIDByUserName(username);
		}
		return ret;
	}
	
	/**
	 * 检查用户登录信息
	 * @param username 用户名
	 * @param passwd 密码，不要用MD5处理
	 * @param ret 数组，长度为1即可，检查完成以后，里面就放了当前这个用户的用户ID；如果是null会引发空指针异常，便于调试查错
	 * @return 枚举变量表示检查结果
	 */
	public static UserLoginResult checkUserInfo(String username, String passwd, int ret[]){
		return getUserDao().checkUserInfo(username, passwd, ret);
	}
	
}
