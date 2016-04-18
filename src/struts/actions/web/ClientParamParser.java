package struts.actions.web;

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import server.commonutils.MyStringChecker;
import server.info.config.LangEnvironment;
import server.info.config.SessionAttrNames;
import server.info.config.LangEnvironment.ClientType;
import server.info.config.LangEnvironment.LangEnv;

public class ClientParamParser {

	/**
	 * 解析URL中传递的语言变量。
	 * 如果参数中的strLang无效（错误或为空），则根据当前的http session取语言信息；
	 * 如果session中也没有设置相应的值，则以服务器默认的为准；
	 * 如果strLang是有效的，会同时把这个值设置到session当中。
	 * @param strLang 
	 * 		客户端传入的字符串，是action直接拿到的值；
	 * 		如果URL中有多个lang=xxx形式，strutt2会产生：xx1,xx2格式，
	 * 		这里支持这种格式，并且第一个有效的xxx就是最终结果；
	 * @param type 客户端类型，最好不要传null，如果strLang无效，session中没有设置值，这里传null将会导致找不到默认的结果而返回null
	 * @param session 当前的httpsession，如果传入null，该函数将自己获取；但前提是这个函数必须是在servlet相关函数直接或间接调用，否则将导致异常
	 * @return 解析后的语言信息
	 */
	public static final LangEnv parseLang(String strLang, ClientType type, HttpSession session){
		
		LangEnv ret=null;
		if(null==session) session=ServletActionContext.getRequest().getSession();
		if(null!=strLang){
			String arr[]=strLang.split(",");
			if(null!=arr&&arr.length>=1){
				for(int i=0;i<arr.length;++i){
					String curStr=arr[i].trim();
					if(MyStringChecker.isBlank(curStr)) continue;
					ret=LangEnvironment.tryParseLang(curStr);
					if(null!=ret) break;
				}
			}
		}
		
		if(null!=ret){
			session.setAttribute(SessionAttrNames.LANG_ATTR, ret);//如果传递的URL中有语言参数，以此为准，session中相应的值也修改掉
		}else{
			//没有传入语言参数，首先以session中设置的属性为准
			ret=(LangEnv) session.getAttribute(SessionAttrNames.LANG_ATTR);
			if(null==ret&&null!=type){
				//session中也没有相应的值（刚打开浏览器，第一次访问，也没有写lang=?），以服务器默认的环境为准
				ret=LangEnvironment.currentEnv(type);
				session.setAttribute(SessionAttrNames.LANG_ATTR, ret);
			}
		}
		
		return ret;
	}
}
