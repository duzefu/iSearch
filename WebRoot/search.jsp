<%@page import="server.info.config.SessionAttrNames"%>
<%@page import="org.json.JSONObject"%>
<%@page import="server.info.config.LangEnvironment.LangEnv"%>
<%@page import="struts.actions.web.JsDataBundler"%>
<%@page import="server.info.config.PicturePath.PictureType"%>
<%@page import="server.info.config.PicturePath"%>
<%@page import="java.util.HashSet"%>
<%@page import="server.engine.api.EngineFactory.EngineName"%>
<%@page import="server.info.config.LangEnvironment"%>
<%@page import="server.info.config.LangEnvironment.ClientType"%>
<%@page import="server.info.config.VisibleConstant.ContentNames"%>
<%@page import="server.info.config.VisibleConstant"%>
<%@page import="server.engine.api.EngineFactory"%>
<%@page import="java.util.Iterator"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.Set" %>
<%@page import="server.engine.api.EngineFactory" %>
<%@page import="server.info.config.VisibleConstant" %>

<%
	String usernameinpage = (String) session.getAttribute(SessionAttrNames.USERNAME_ATTR);
	LangEnv lang=(LangEnv) session.getAttribute(SessionAttrNames.LANG_ATTR);
	if(null==lang) request.getAttribute("langEnv");
	if(null==lang) lang=LangEnvironment.currentEnv(ClientType.web);
	JSONObject jsondata=new JSONObject();
	JsDataBundler.getJsonForSearchJsp(jsondata, lang);
%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
		<title><%=VisibleConstant.getWebpageContent(ContentNames.page_title, lang) %></title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache, must-revalidate">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="keywords" content="<%=VisibleConstant.getWebpageContent(ContentNames.page_keywords, lang)%>">
		<meta http-equiv="description" content="This is my page">
		<link href="css/homepage.css" rel="stylesheet" type="text/css" />
		<link rel="stylesheet" type="text/css" href="css/home-loginentry.css" />
		<link rel="stylesheet" type="text/css" href="css/login.css" />
		<link href="css/copyright.css" rel="stylesheet" type="text/css" />
		
		<script type="text/javascript">
			var usernameinpage = null;
			var jsonBundle = null;
			if ("<%=usernameinpage%>" != "null") {
				usernameinpage = "<%=usernameinpage%>";
			}
			jsonBundle=JSON.parse('<%=jsondata.toString()%>');
		</script>
		<script type="text/javascript" src="js/XMLHttp.js"></script>
		<script type="text/javascript" src="js/cookie.js"></script>
		<script type="text/javascript" src="js/userlogin.js"></script>
		<script type="text/javascript" src="js/formAction.js"></script>
		<script type="text/javascript" src="js/logout.js"></script>
		<script type="text/javascript" src="js/hotwords.js" charset="utf-8"></script>
		<script type="text/javascript">
		window.onload=function()
		{
			rightTitle();
			getHotwords();
		};
		</script>
	</head>
	<body>
		<div id=homepate-head>
			<div id="zhuce"></div>
		</div>
		<div id="logo"><img src="images/IMsearch250x250.png" width="250px" height="250px" ></div>
		<div id="navigation">
			<form name="myForm" action="search.action" method="get">
				<input name="query" id="query" type=text size=75 baiduSug="2"
						maxlength=65 onkeydown="searchButtonClick(event)">
				<input type="button" name=btnG id="searchbutton" value="<%=VisibleConstant.getWebpageContent(ContentNames.search_button, lang) %>"
						onclick="FormAction()">
				<table id="below_input" align="center">
					<tr valign="middle">
						<td class="engSelCol1 engSelRow"><%=VisibleConstant.getWebpageContent(ContentNames.engine_select_prompt, lang) %></td>
						<td class="engSelCol2 engSelRow">
						<% 
							for(Iterator<EngineName> it=EngineFactory.getAllEngineIterator();it.hasNext();){
								EngineName enumName=it.next();
								String strName=VisibleConstant.getStrEngNameWeb(enumName, lang);
								String strEnName=EngineFactory.getEnNameString(enumName);
						%>
							<label id=engSelector ><input type="checkbox" name="schedule" value="<%=strEnName %>"><%=strName%></label>
						<%}%>
						</td>
					</tr>
					<tr valign="middle">
						<td class="engSelCol1 engSelRow"><%=VisibleConstant.getWebpageContent(ContentNames.result_from_prompt, lang) %></td>
						<td class="engSelCol2 engSelRow"><img src="<%=PicturePath.getWebpageContent(PictureType.result_from_picture, lang) %>" width="390" height="30"></td>
					</tr>
				</table>
				<input name="page" id="page" type="hidden">
				<input name="lang" id="lang" type="hidden" value="<%=lang%>">
			</form>
		</div>
		<div id="hotwords">
			<table id="hotwords-table" align="center" cellspacing="15">
				<tr id="hotwords-title">
					<td id="hwtitle-left"><%=VisibleConstant.getWebpageContent(ContentNames.real_hot_title, lang) %></td>
					<!--<td id="hwtitle-more"><a href=""><%=VisibleConstant.getWebpageContent(ContentNames.more_prompt, lang) %>&gt;&gt;</a></td>-->
				</tr>
			</table>
		</div>
		<div id="copyright">
			&copy;<%=VisibleConstant.getWebpageContent(ContentNames.copyright_info, lang) %>
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
	<!-- 百度搜索框提示 -->
	<script charset="gbk" src="http://www.baidu.com/js/opensug.js"></script>
</html>