package server.info.config;

import server.engine.api.EngineFactory;
import server.engine.api.EngineFactory.EngineName;
import server.info.config.CategoryInfo.Category;
import server.info.config.LangEnvironment.ClientType;
import server.info.config.LangEnvironment.LangEnv;

/**
 * 定义在页面上要显示的内容
 * @author zhou
 */
public class VisibleConstant {

	/*
	 * 注：这个类不捕捉异常，不检查参数，参数出错时，是其他部分编程有错，应该及时纠正
	 */
	public final static String getWebpageContent(ContentNames content, LangEnv lang){
		return (null==lang?LangEnvironment.currentEnv(ClientType.web):lang).equals(LangEnv.cn)?CONTENT_CN[content.ordinal()]:CONTENT_EN[content.ordinal()];
	}
	
	public final static String getWebpageContent(ContentNames content){
		return getWebpageContent(content,LangEnvironment.currentEnv(ClientType.web));
	}
	
	public final static String getStrEngNameWeb(EngineName name, LangEnv lang){
		return (null==lang?LangEnvironment.currentEnv(ClientType.web):lang).equals(LangEnv.cn)?EngineFactory.getVisibleCnName(name):EngineFactory.getVisibleEnName(name);
	}
	
	/**
	 * 获得页面上显示的搜索引擎名字，根据当前系统语言环境返回中或英文名称
	 * @param name
	 * @return
	 */
	public final static String getStrEngNameWeb(EngineName name){
		return getStrEngNameWeb(name, LangEnvironment.currentEnv(ClientType.web));
	}
	
	/**
	 * 获得可显示显示的类别名字
	 * @param name
	 * @return
	 */
	public final static String getStrCategoryNameWeb(Category cat, LangEnv lang){
		return (null==lang?LangEnvironment.currentEnv(ClientType.web):lang).equals(LangEnv.cn)?CategoryInfo.getVisibleCnString(cat):CategoryInfo.getVisibleEnString(cat);
	}
	
	/**
	 * 获得网页上显示的类别名字，根据当前系统语言环境返回中或英文名称
	 * @param name
	 * @return
	 */
	public final static String getStrCategoryNameWeb(Category cat){
		return getStrCategoryNameWeb(cat, LangEnvironment.currentEnv(ClientType.web));
	}
	
	/*
	 * 页面上每需要多一种要显示的内容，就在这加一个枚举变量，
	 * 同时在下面的数组也对应分别增加各种语言环境下应该显示的字符串，
	 * 然后在页面上（或在获得要向安卓端发送的数据之前）之前调用getContent函数就可以。
	 */
	public enum ContentNames{
			/*1*/search_button,					//搜索按钮里面的文字
			/*2*/page_title,							//标题（会显示在浏览器标题栏）
			/*3*/page_keywords,					//在search.jsp的<head>中有一个关键字标识
			/*4*/engine_select_prompt,		//选择指定的搜索引擎那里的提示语
			/*5*/result_from_prompt,			//"结果来自于"提示语
			/*6*/real_hot_title,					//实时热点词栏标题
			/*7*/more_prompt,					//“更多”提示
			/*8*/copyright_info,					//页面底部的版权信息
			/*9*/sw_name,							//智搜软件名字
			/*10*/login_form_title,				//登录框提示信息
			/*11*/login_button,					//登录按钮文字
			/*12*/login_link,						//界面上的登录入口
			/*13*/user_name,						//登录、注册界面的用户账号
			/*14*/passwd,							//密码
			/*15*/login_entry,						//登录入口按钮
			/*16*/register_entry,					//注册入口按钮
			/*17*/setting_entry,					//
			/*18*/logout_entry,					//
			/*19*/tab_web,							//
			/*20*/tab_picture,						//
			/*21*/tab_video,						//
			/*22*/query_recomm_title,		//
			/*23*/relate_search_title,			//
			/*24*/filter_title,						//
			/*25*/confirm_button,				//
			/*26*/result_eng_prompt,			//
			/*27*/result_src_prefix,				//
			/*28*/show_button,					//
			/*29*/hide_button,					//
			/*30*/result_recomm_src,			//
			/*31*/result_distribution,			//
			/*32*/no_result_found,				//
			/*33*/user_interest_title,			//
			/*34*/empty_username_prompt,//
			/*35*/empty_passwd_prompt,	//
			/*36*/no_such_username,			//
			/*37*/passwd_error,					//
			/*38*/login_response,				//
			/*39*/welcome_prefix,				//
			/*40*/last_page,						//
			/*41*/next_page,						//
			/*42*/first_page,						//
			/*43*/loginfail_error_username,			//
			/*44*/register_page_title,			//
			/*45*/interest_day_one,
			/*46*/interest_day_two,
			/*47*/interest_day_three,
			/*48*/interest_distribution,
			/*49*/return_home_page,
			/*50*/welcome_register,
			/*51*/has_account,
			/*52*/email_address,
			/*53*/passwd_confirm,
			/*54*/register_button,
			/*55*/reset_button,
			/*56*/email_prompt,
			/*57*/account_prompt,
			/*58*/passwd_prompt,
			/*59*/confirm_passwd_prompt,
			/*60*/regfail_info_error,
			/*61*/regfail_exist_email,
			/*62*/regfail_exist_name,
			/*63*/login_prompt_after_register,
			/*64*/regprompt_email_empty,
			/*65*/regprompt_illegal_email,
			/*66*/regprompt_username_empty,
			/*67*/regprompt_illegal_username,
			/*68*/regprompt_empty_passwd,
			/*69*/regprompt_passwd_length_error,
			/*70*/regprompt_repasswd_empty,
			/*71*/regprompt_diff_repasswd,
			/*72*/regprompt_blank_in_passwd,
			/*73*/loginfail_error_passwd,
	};
	private final static String CONTENT_CN[]={
			/*1*/"智搜",
			/*2*/"智搜，聪明的搜索",
			/*3*/"imSearch,智搜,元搜索",
			/*4*/"选择特定的搜索引擎：",
			/*5*/"结果来自于：",
			/*6*/"实时热点",
			/*7*/"更多...",
			/*8*/"2015 西安电子科技大学",
			/*9*/"智搜",
			/*10*/"登录智搜账号",
			/*11*/"登&nbsp;录",
			/*12*/"登录",
			/*13*/"用户账号",
			/*14*/"登录密码",
			/*15*/"登录",
			/*16*/"注册",
			/*17*/"设置",
			/*18*/"退出",
			/*19*/"网页",
			/*20*/"图片",
			/*21*/"视频",
			/*22*/"群组用户也搜索过：",
			/*23*/"相关搜索",
			/*24*/"按来源的成员搜索引擎筛选搜索结果：",
			/*25*/"确定",
			/*26*/"以下搜索结果来自：",
			/*27*/"来自：",
			/*28*/"显示",
			/*29*/"隐藏",
			/*30*/"系统推荐",
			/*31*/"检索结果来源分布：",
			/*32*/"很抱歉，没有您要查找的内容！",
			/*33*/"您的搜索兴趣：",
			/*34*/"请输入您的用户名",
			/*35*/"请输入您的密码",
			/*36*/"该账号不存在",
			/*37*/"密码错误，请重试",
			/*38*/"登录失败，请重试。",
			/*39*/"欢迎，",
			/*40*/"上一页",
			/*41*/"下一页",
			/*42*/"第一页",
			/*43*/"用户名不存在",
			/*44*/"注册智搜",
			/*45*/"第一天",
			/*46*/"第二天",
			/*47*/"第三天",
			/*48*/"最近兴趣变化:",
			/*49*/"回到主页",
			/*50*/"欢迎注册智搜",
			/*51*/"已有账号, ",
			/*52*/"电子邮件",
			/*53*/"确认密码",
			/*54*/"注册",
			/*55*/"重置",
			/*56*/"输入真实的邮箱地址以用于验证，格式如name@example.com",
			/*57*/"由字母，下划线或数字组成",
			/*58*/"长度6-16位，区分大小写",
			/*59*/"请再次输入您的密码",
			/*60*/"信息有误，请检查",
			/*61*/"该邮箱已被注册",
			/*62*/"该用户名已被注册",
			/*63*/"注册成功，现在用注册的账号登录",
			/*64*/"请输入您的邮件地址",
			/*65*/"邮件地址格式有误，请检查",
			/*66*/"请输入您的用户名",
			/*67*/"用户名有非法字符，请检查",
			/*68*/"请输入您的密码",
			/*69*/"密码长度有误，请检查",
			/*70*/"请确认您的密码",
			/*71*/"两次输入的密码不一致",
			/*72*/"密码中不能有空格",
			/*73*/"密码错误"
	};
	private final static String CONTENT_EN[]={
			/*1*/ "Search",
			/*2*/"IM Search, a smart metasearch engine",
			/*3*/"IM Search, metasearch",
			/*4*/"Preperences:",
			/*5*/"Results provided by: ",
			/*6*/"Favorite Fetches: ",
			/*7*/"More",
			/*8*/"2015 Xidian University, Xian, China",
			/*9*/"IM Search",
			/*10*/"Sign In, Have Fun",
			/*11*/"Log in now",
			/*12*/"Log In",
			/*13*/"Username",
			/*14*/"Password",
			/*15*/"SignIn",
			/*16*/"SignUp",
			/*17*/"Settings",
			/*18*/"SignOut",
			/*19*/"Webs",
			/*20*/"Images",
			/*21*/"Videos",
			/*22*/"Recommended words: ",
			/*23*/"Related searches: ",
			/*24*/"Filter results of certain search engines：",
			/*25*/"OK",
			/*26*/"The following results are provided by: ",
			/*27*/"Retrieved by：",
			/*28*/"Show",
			/*29*/"Hide",
			/*30*/"Recommended Result",
			/*31*/"Distribution of results: ",
			/*32*/"Sorry, no results are found.",
			/*33*/"Your favorite subjects:",
			/*34*/"Please enter in your username",
			/*35*/"Please enter in your password",
			/*36*/"No such an account exists",
			/*37*/"Your password is error, try again, please",
			/*38*/"Sign in failed, please try again",
			/*39*/"Welcome, ",
			/*40*/"Previous",
			/*41*/"Next",
			/*42*/"First page",
			/*43*/"Your username is not exist",
			/*44*/"Sign up",
			/*45*/"Day one",
			/*46*/"Day two",
			/*47*/"Day three",
			/*48*/"Distribution of interest:",
			/*49*/"Homepage",
			/*50*/"Welcome to sign up your account",
			/*51*/"Have a account yet? ",
			/*52*/"Email",
			/*53*/"Repeat password",
			/*54*/"Submit",
			/*55*/"Clear",
			/*56*/"Please enter your real email address used for identify; for example, name@example.com",
			/*57*/"Combination of alphabet, digit and underline",
			/*58*/"Consist of 6-16 characters, and case-sensitive",
			/*59*/"Input your password again",
			/*60*/"Register information is incorrect",
			/*61*/"The email has been registered",
			/*62*/"The username has been registered",
			/*63*/"Sign up successfully, sign in now?",
			/*64*/"Please enter your email",
			/*65*/"Please check your email format",
			/*66*/"Please enter your account name",
			/*67*/"Some illegal characters exist in your account name",
			/*68*/"Please enter your password",
			/*69*/"Length of password is illegal",
			/*70*/"Please confirm your password",
			/*71*/"The two passwords are mismatch",
			/*72*/"Space is illegal for a password, try again",
			/*73*/"Password incorrect"
	};
	
}
