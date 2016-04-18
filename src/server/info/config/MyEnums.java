package server.info.config;

public class MyEnums {

	public enum RegisterResult {
		/**
		 * 注册成功
		 */
		success, 
		/**
		 * 邮箱已被注册
		 */
		email_exist,
		/**
		 * 用户名已被注册
		 */
		username_exit, 
		/**
		 * 信息有错，
		 */
		illegal_info,
	};
	
	/**
	 * 用于登录的时候，标志搜索用户信息的结果
	 * @author zhou
	 *
	 */
	public enum UserLoginResult {
		/**
		 * 能够正确搜索到结果
		 */
		success,
		/**
		 * 用户名不存在
		 */
		no_exist_user,
		/**
		 * 用户名存在，但密码错误
		 */
		error_passwd
	};
	
}
