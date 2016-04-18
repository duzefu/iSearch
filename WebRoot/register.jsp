<%@page import="org.json.JSONObject"%>
<%@page import="struts.actions.web.JsDataBundler"%>
<%@page import="server.info.config.SessionAttrNames"%>
<%@page import="server.info.config.LangEnvironment.ClientType"%>
<%@page import="server.info.config.LangEnvironment.LangEnv"%>
<%@page import="server.info.config.LangEnvironment"%>
<%@page import="server.info.config.VisibleConstant.ContentNames"%>
<%@page import="server.info.config.VisibleConstant"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page language="java" import="java.lang.*" %>

<%
	LangEnv lang=(LangEnv)session.getAttribute(SessionAttrNames.LANG_ATTR);
	if(null==lang) lang=LangEnvironment.currentEnv(ClientType.web);
	JSONObject json=new JSONObject();
	JsDataBundler.getJsonForRegisterJsp(json, lang);
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
	<head>
	<title><%=VisibleConstant.getWebpageContent(ContentNames.register_page_title)%></title>

	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">

	<link href="css/register.css" rel="stylesheet" type="text/css" />
	<link href="css/login.css" rel="stylesheet" type="text/css" />
	<link href="css/copyright.css" rel="stylesheet" type="text/css"/>
	
	<script src="js/cookie.js" type=text/javascript></script> 
	<script type="text/javascript" src="js/XMLHttp.js"></script>
	<script type="text/javascript" src="js/userlogin.js"></script>
	<script type="text/javascript" src="js/userregister.js"></script>
	<script type="text/javascript">
		var jsonBundle = null;
		jsonBundle=JSON.parse('<%=json.toString()%>');
	</script>
</head>

<body>

	<div id="main-container">
		<div id="page-head">
			<div id="regpage-logo">
				<a href="./search.action"><img src="images/IMsearch100x100.png" width="100px" height="100px" ></a>
			</div>
			<div id="return-home">
	           <a href="./search.action" ><%=VisibleConstant.getWebpageContent(ContentNames.return_home_page, lang) %></a>
	        </div>

		</div>
		<div id="welcome">
			<div id="welcome-register"><%=VisibleConstant.getWebpageContent(ContentNames.welcome_register, lang) %></div>
			<div id="has-account"><%=VisibleConstant.getWebpageContent(ContentNames.has_account, lang) %><a href="javascript:login()"><%=VisibleConstant.getWebpageContent(ContentNames.login_entry, lang) %></a></div>
		</div>
		<form name="myForm" action="" method="post" focus="login">
			<div class="row" id="registerfail">&nbsp;</div>
			<div class="row" id="email-row">
				<div class="left-col" name="left-col">
					<%=VisibleConstant.getWebpageContent(ContentNames.email_address, lang) %>
				</div>
				<div class="right-col">
					<div class="right-input">
						<input id="email-input" type="text" maxLength=30 name="emailaddress"onfocus="getFocusEmail()" onblur="loseFocusEmail()" >
						<span id="spanEmail" class="input-promt">&nbsp;</span>
					</div>
					<div class="right-prompt">
						<%=VisibleConstant.getWebpageContent(ContentNames.email_prompt, lang) %>
					</div>
				</div>
			</div>
			<div style="clear:both; visibility:hidden;"></div> 
			<div class="row" id="account-row">
				<div class="left-col" name="left-col">
					<%=VisibleConstant.getWebpageContent(ContentNames.user_name, lang) %>
				</div>
				<div class="right-col">
					<div class="right-input">
						<input id="account-input" type="text" maxLength=30 name="username" onfocus="getFocusUsername()" onblur="loseFocusUsername()">
						<span id="spanUsername" class="input-promt">&nbsp;</span>
					</div>
					<div class="right-prompt">
						<%=VisibleConstant.getWebpageContent(ContentNames.account_prompt, lang) %>
					</div>
				</div>
			</div>
			<div style="clear:both; visibility:hidden;"></div> 
			<div  class="row" id="passwd-row">
				<div class="left-col" name="left-col">
					<%=VisibleConstant.getWebpageContent(ContentNames.passwd, lang) %>
				</div>
				<div class="right-col">
					<div class="right-input">
						<input id="passwd-input" type="password" maxLength=30 name="password" onfocus="getFocusPasswd()" onblur="loseFocusPasswd()">
						<span id="spanPasswd" class="input-promt">&nbsp;</span>
					</div>
					<div class="right-prompt">
						<%=VisibleConstant.getWebpageContent(ContentNames.passwd_prompt, lang) %>
					</div>
				</div>
			</div>
			<div style="clear:both; visibility:hidden;"></div> 
			<div class="row" id="paswd-repeat-row">
				<div class="left-col" name="left-col">
					<%=VisibleConstant.getWebpageContent(ContentNames.passwd_confirm, lang) %>
				</div>
				<div class="right-col">
					<div class="right-input">
						<input id="passwd-confirm-input" type="password" maxLength=30 name="usersRepass" onfocus="getFocusPasswdConfirm()" onblur="loseFocusPasswdConfirm()">
						<span id="spanRePasswd" class="input-promt">&nbsp;</span>
					</div>
					<div class="right-prompt">
						<%=VisibleConstant.getWebpageContent(ContentNames.confirm_passwd_prompt, lang) %>
					</div>
				</div>
			</div>
			<div style="clear:both; visibility:hidden;"></div>  
			<div class="row" id="button-row">
				<div class="left-col" name="left-col">
					&nbsp;
				</div>
				<div class="right-col">
					<input type="button" name=btnG value="<%=VisibleConstant.getWebpageContent(ContentNames.register_button, lang) %>"  onclick="Register()">
					<input name="register" type="reset" value="<%=VisibleConstant.getWebpageContent(ContentNames.reset_button, lang) %>" onclick="Reset()"> 
				</div>
			</div>
		</form>
		<div id="copyright">
			&copy;<%=VisibleConstant.getWebpageContent(ContentNames.copyright_info, lang) %>
		</div>
	</div>
	<div id="logindiv">
		<div id="loginform">
			<div id="loginform-title">
				<div id="loginform-title-text"><%=VisibleConstant.getWebpageContent(ContentNames.login_form_title, lang) %></div>
				<div id="loginform-title-quit"><span id="loginform-quit-icon" onclick="closeLoginForm()">&times;</span></div>
			</div>
			<div id="result-hint">&nbsp;</div>
			<div id="loginform-body">
				<form name="login" action="" method="post" focus="login">
					<div id="loginform-username" class="loginform-input-row">
						<span class="lf-input-left"><%=VisibleConstant.getWebpageContent(ContentNames.user_name, lang) %></span>
						<input name="username" class="loginform-input" type=text onkeydown="loginbuttonOnClick(event)" />
					</div>
					<div id="loginform-password" class="loginform-input-row">
						<span class="lf-input-left"><%=VisibleConstant.getWebpageContent(ContentNames.passwd, lang) %></span>
						<input name="password" type="password" class="loginform-input" type=text onkeydown="loginbuttonOnClick(event)" />
					</div>
					<div id="loginform-button" class="loginform-input-row">
						<input type="button" id="loginbutton" value="<%=VisibleConstant.getWebpageContent(ContentNames.login_button, lang) %>" onclick="tijiao()" />
					</div>
				</form>
			</div>
		</div>
	</div>
	<div id="mask">&nbsp;</div>
  </body>
