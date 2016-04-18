package struts.actions.web;

import javax.servlet.http.HttpSession;

import server.info.config.LangEnvironment.ClientType;
import server.info.config.LangEnvironment.LangEnv;

public class WebParamParser {

	/**
	 * 解析语言参数（网页版）
	 * @param strLang 语言字符串；如果有逗号（struts2碰到url带有多个同名参数时的处理方式），逗号被作为分隔符，
	 * 		分隔后的多个字符串，第一个有效的就是最终使用的；这个值可以是无效的或null
	 * @param session Http session对象，可以为null；为null时这个函数必须由servlet直接或间接调用）
	 * @return
	 */
	public final static LangEnv parseLang(String strLang, HttpSession session){
		return ClientParamParser.parseLang(strLang, ClientType.web, session);
	}
	
}
