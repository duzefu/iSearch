package struts.actions.web;

import org.json.JSONException;
import org.json.JSONObject;

import server.info.config.VisibleConstant;
import server.info.config.LangEnvironment.LangEnv;
import server.info.config.VisibleConstant.ContentNames;

public class JsDataBundler {

	public static void getJsonForRegisterJsp(JSONObject ret, LangEnv lang){
		
		if(null==ret) return;
		try{
			ret.put(regprompt_different_repasswd, VisibleConstant.getWebpageContent(ContentNames.regprompt_diff_repasswd, lang));
			ret.put(regprompt_empty_email, VisibleConstant.getWebpageContent(ContentNames.regprompt_email_empty, lang));
			ret.put(regprompt_empty_passwd, VisibleConstant.getWebpageContent(ContentNames.regprompt_empty_passwd, lang));
			ret.put(regprompt_empty_repasswd, VisibleConstant.getWebpageContent(ContentNames.regprompt_repasswd_empty, lang));
			ret.put(regprompt_empty_uname, VisibleConstant.getWebpageContent(ContentNames.regprompt_username_empty, lang));
			ret.put(regprompt_illegal_email, VisibleConstant.getWebpageContent(ContentNames.regprompt_illegal_email, lang));
			ret.put(regprompt_illegal_passwd_len, VisibleConstant.getWebpageContent(ContentNames.regprompt_passwd_length_error, lang));
			ret.put(regprompt_illegal_uname, VisibleConstant.getWebpageContent(ContentNames.regprompt_illegal_username, lang));
			ret.put(regprompt_space_in_passwd, VisibleConstant.getWebpageContent(ContentNames.regprompt_blank_in_passwd, lang));
		}catch(JSONException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * search.jsp中需要的一些字符串信息
	 * @param ret 用于填写信息的json对象，不能是null
	 * @param lang 语言信息，如果是null，等于web
	 */
	public static void getJsonForSearchJsp(JSONObject ret, LangEnv lang){
		
		if(null==ret) return;
		try{
			ret.put(register_entry, VisibleConstant.getWebpageContent(ContentNames.register_entry, lang));
			ret.put(login_entry, VisibleConstant.getWebpageContent(ContentNames.login_entry, lang));
			ret.put(setting_entry, VisibleConstant.getWebpageContent(ContentNames.setting_entry, lang));
			ret.put(logoutentry, VisibleConstant.getWebpageContent(ContentNames.logout_entry, lang));
			ret.put(empty_username_prompt, VisibleConstant.getWebpageContent(ContentNames.empty_username_prompt, lang));
			ret.put(empty_passwd_prmpt, VisibleConstant.getWebpageContent(ContentNames.empty_passwd_prompt, lang));
			ret.put(welcome_prefix, VisibleConstant.getWebpageContent(ContentNames.welcome_prefix, lang));
		}catch(JSONException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 要用在returnpage.jsp相关的js函数中的数据，以json格式传输
	 * @param json用于填写信息的json对象，不能是null
	 * @param lang 语言信息，如果是null，等于web
	 * @return
	 */
	public static void getJsonForResultPageJsp(JSONObject json, LangEnv lang){
		
		if(null==json) return;
		try{
			json.put(register_entry, VisibleConstant.getWebpageContent(ContentNames.register_entry, lang));
			json.put(login_entry, VisibleConstant.getWebpageContent(ContentNames.login_entry, lang));
			json.put(setting_entry, VisibleConstant.getWebpageContent(ContentNames.setting_entry, lang));
			json.put(logoutentry, VisibleConstant.getWebpageContent(ContentNames.logout_entry, lang));
			json.put(empty_username_prompt, VisibleConstant.getWebpageContent(ContentNames.empty_username_prompt, lang));
			json.put(empty_passwd_prmpt, VisibleConstant.getWebpageContent(ContentNames.empty_passwd_prompt, lang));
			json.put(welcome_prefix, VisibleConstant.getWebpageContent(ContentNames.welcome_prefix, lang));
			json.put(first_page, VisibleConstant.getWebpageContent(ContentNames.first_page, lang));
			json.put(last_page, VisibleConstant.getWebpageContent(ContentNames.last_page, lang));
			json.put(next_page, VisibleConstant.getWebpageContent(ContentNames.next_page, lang));
		}catch(JSONException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 在退出登录后，需要刷新登录区的数据，在相应的action返回前调用此函数获得相关的字符串
	 * @param json用于填写信息的json对象，不能是null
	 * @param lang 语言信息，如果是null，等于web
	 * @return
	 */
	public static void getJsonWhileLogout(JSONObject json, LangEnv lang){
		
		if(null==json) return;
		try{
			json.put(register_entry, VisibleConstant.getWebpageContent(ContentNames.register_entry, lang));
			json.put(login_entry, VisibleConstant.getWebpageContent(ContentNames.login_entry, lang));
		}catch(JSONException e){
			e.printStackTrace();
		}
	}

	/**
	 * 获取登录请求后需要返回的数据，用于更新页面上的内容
	 * @param json用于填写信息的json对象，不能是null
	 * @param lang 语言信息，如果是null，等于web
	 */
	public static void getJsonWhileLogin(JSONObject json, LangEnv lang){
		
		if(null==json) return;
		try{
			json.put(welcome_prefix, VisibleConstant.getWebpageContent(ContentNames.welcome_prefix, lang));
			json.put(logoutentry, VisibleConstant.getWebpageContent(ContentNames.logout_entry, lang));
			json.put(setting_entry, VisibleConstant.getWebpageContent(ContentNames.setting_entry, lang));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void getJsonWhileRegister(JSONObject json, LangEnv lang){
		
		if(null==json) return;
		try{
			json.put(login_prompt_after_register, VisibleConstant.getWebpageContent(ContentNames.login_prompt_after_register, lang));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private final static String register_entry="regentry";
	private final static String login_entry="loginentry";
	private final static String setting_entry="settingentry";
	private final static String logoutentry="logoutentry";
	private final static String empty_username_prompt="emptynameprompt";
	private final static String empty_passwd_prmpt="emptypasswdprompt";
	private final static String welcome_prefix="welcomeinfo";
	private final static String last_page="lastpage";
	private final static String next_page="nextpage";
	private final static String first_page="firstpage";
	private final static String login_prompt_after_register="loginprompt";
	private final static String regprompt_empty_email="emptyemail";
	private final static String regprompt_illegal_email="illegalemail";
	private final static String regprompt_empty_uname="emptyusername";
	private final static String regprompt_illegal_uname="illegalusername";
	private final static String regprompt_empty_passwd="emptypasswd";
	private final static String regprompt_illegal_passwd_len="passwdlenerr";
	private final static String regprompt_empty_repasswd="emptyrepasswd";
	private final static String regprompt_different_repasswd="diffrepasswd";
	private final static String regprompt_space_in_passwd="spaceinpasswd";
}
