package server.info.config;

/**
 * 有一些变量值（用户名、用户指定的语言环境）可能在很多地方涉及，特别是同时涉及java代码与jsp页面，
 * 可以设置在http session中，避免在jsp设计时，总要考虑在url后附加这些数据；在session中getAttribute及setAttribute都需要一个字符串名字，放在这个类里统一管理
 * 存放需要放在session中的值对应的属性名
 * @author zhou
 *
 */
public class SessionAttrNames {

	public final static String LANG_ATTR="langenv";//当前session的语言环境
	public final static String USERNAME_ATTR="usernameinpage";//用户名属性
}
