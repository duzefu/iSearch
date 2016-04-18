<%@page import="server.video.engine.api.VideoInfo"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="server.info.config.PicturePath.PictureType"%>
<%@page import="server.info.config.PicturePath"%>
<%@page import="server.info.config.VisibleConstant.ContentNames"%>
<%@page import="server.info.config.VisibleConstant"%>
<%@page import="server.info.config.LangEnvironment.LangEnv"%>
<%@page import="server.info.config.SessionAttrNames"%>
<%@page import="java.util.List" %>
<%@page import="java.util.Iterator" %>
<%@page import="org.json.JSONObject"%>
<%@page import="struts.actions.web.JsDataBundler" %>
<%
	Integer curPage=(Integer)request.getAttribute("page");
	LangEnv lang=(LangEnv) session.getAttribute(SessionAttrNames.LANG_ATTR);
	List<VideoInfo> results = (List<VideoInfo>) request.getAttribute("results");
	
	boolean stopPage = false;
	//获得一些常量信息，以JSON对象封装好并赋值给一个js变量，
	//用于浏览器中的脚本文件，使脚本修改页面时，能保持语言的正确性
	JSONObject jsdata=new JSONObject();
	JsDataBundler.getJsonForResultPageJsp(jsdata, lang);
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale">

<title><%=VisibleConstant.getWebpageContent(ContentNames.page_title, lang) %></title>
<link rel="stylesheet" type="text/css" href="css/returnpage.css" />
<link rel="stylesheet" type="text/css" href="css/rpage-loginentry.css" />
<link rel="stylesheet" type="text/css" href="css/login.css" />
<link rel="stylesheet" type="text/css" href="css/style_bar.css" />
<link rel="stylesheet" type="text/css" href="css/style_score.css" />
<link rel="stylesheet" type="text/css" href="css/copyright.css" />
<link href="css/bootstrap.min.css" rel="stylesheet" type="text/css">
<style type="text/css">

#main-body #result-area .container .col-sm-12 .row .col-sm-3 .result {
	float: left;
  	height: 153px;
    line-height: 148%;
    margin: 0 20px 0 0;
    overflow: hidden;
    text-align: center;
    width: 210px;
}

#main-body #result-area .container .col-sm-12 .row .col-sm-3 .result .view-box {
	cursor: pointer;
    display: block;
    height: 144px;
}

#main-body #result-area .container .col-sm-12 .row .col-sm-3 .result .view-box .video {
	display: block;
    height: 98px;
    overflow: hidden;
    position: relative;
    width: 100%;
}

#main-body #result-area .container .col-sm-12 .row .col-sm-3 .result .view-box .video .s-mask {
	background: #000 none repeat scroll 0 0;
    height: 100%;
    left: 0;
    opacity: 0;
    position: absolute;
    top: 0;
    visibility: hidden;
    width: 100%;
}
#main-body #result-area .container .col-sm-12 .row .col-sm-3 .result .view-box .video .s-play {
	background: rgba(0, 0, 0, 0) url("download/play_231cc54.png") no-repeat scroll 0 0;
    height: 42px;
    left: 50%;
    margin: -21px 0 0 -21px;
    position: absolute;
    visibility: hidden;
    top: 50%;
    width: 42px;
}

#main-body #result-area .container .col-sm-12 .row .col-sm-3 .result .view-box:hover .video .s-mask, #main-body #result-area .container .col-sm-12 .row .col-sm-3 .result .view-box:hover .video .s-play {
    visibility: visible;
}

#main-body #result-area .container .col-sm-12 .row .col-sm-3 .result .view-box .video .info-bg {
	background: rgba(0, 0, 0, 0) url("download/bg_shadow_39ec89c.png") repeat-x scroll left bottom;
    bottom: 0;
    display: block;
    height: 50px;
    left: 0;
    line-height: 18px;
    position: absolute;
    width: 100%;
}

#main-body #result-area .container .col-sm-12 .row .col-sm-3 .result .view-box .video .info {
	bottom: 2px;
    color: #fff;
    font-size: 12px;
    position: absolute;
    right: 6px;
}

#main-body #result-area .container .col-sm-12 .row .col-sm-3 .result .view-box .video .imgs {
	height: 100px;
    width: 100%;
}

#main-body #result-area .container .col-sm-12 .row .col-sm-3 .result .view-box .video .site {
	bottom: 3px;
    color: #fff;
    left: 6px;
    position: absolute;
}
#main-body #result-area .container .col-sm-12 .row .col-sm-3 .result .view-box .title {
	overflow: hidden;
}
</style>

<script type="text/javascript" src="js/jquery-2.1.4.min.js"></script>
<script type="text/javascript" src="js/bootstrap.min.js"></script>
<script type="text/javascript" src="js/video.js"></script>
<script type="text/javascript" src="js/XMLHttp.js"></script>
<script type="text/javascript" src="js/cookie.js"></script>
<script type="text/javascript" src="js/formAction.js"></script>
<script type="text/javascript" src="js/picture.js"></script>
<script type="text/javascript"> 
	var jsonBundle=null;
	jsonBundle=JSON.parse('<%=jsdata.toString()%>');
	
	function login() {
		
		var mask=document.getElementById("mask");
		mask.style.height = window.screen.height  + 'px';
		mask.style.width = window.screen.width  + 'px';
		mask.style.display = 'block';
		var logindiv=document.getElementById("logindiv");
		logindiv.style.display = 'block';
	}
</script>

</head>
	<body>
		<div id="returnpage-head">
			<div id="returnpage-logo" class="rpage-fleft-div">
				<a href="./search.action"><img src="images/IMsearch100x100.png" width="100px" height="100px" ></a>
			</div>
			<div id="returnpage-head-middle" class="rpage-fleft-div">
				<div class="ss_table">
					<span onclick="FormAction()"><%=VisibleConstant.getWebpageContent(ContentNames.tab_web, lang) %></span>
					<span onclick="PictureAction()" ><%=VisibleConstant.getWebpageContent(ContentNames.tab_picture, lang) %></span>
					<span onclick="VideoAction()" ><%=VisibleConstant.getWebpageContent(ContentNames.tab_video, lang) %></span>
				</div>
				<div id="search">
					<form name="myForm" action="video.action" method="get">
						<input name="query" id="query" type=text size=75 maxlength=65 value="${query}" baiduSug="2" onkeydown="searchButtonClick(event)" />
						<input type=button name=btnG id="searchbutton" value="<%=VisibleConstant.getWebpageContent(ContentNames.search_button, lang) %>" onclick="VideoAction()" />
						<input name="page" id="page" type="hidden" />
						<input name="lang" id="lang" type="hidden" value="<%=lang %>">
						<input name="filterengine" id="filterEng" type="hidden"/ >
					</form>
				</div>
				<div id="result-from-pic">
					<table>
						<tr>
							<td class="engSelCol1 engSelRow"><%=VisibleConstant.getWebpageContent(ContentNames.result_from_prompt, lang) %></td>
							<td class="engSelCol2 engSelRow"><img src="<%=PicturePath.getWebpageContent(PictureType.result_from_picture, lang) %>" width="390" height="30"></td>
						</tr>
					</table>
				</div>
			</div>
			<div id="zhuce">
				<a href="./register.jsp">注册</a>
				<a href="javascript:login()">登陆</a>
			</div>
		</div>
		<div style="clear:both; visibility:hidden;"></div> 
		<hr style="margin-left: 5px; border-top:3px ridge;"/>
		<div id="main-body">
			<div id="main-left" class="rpage-fleft-div">
				<div id="query-recomm" class="left-div">
					<table> 
						<th class="title-hint"><%=VisibleConstant.getWebpageContent(ContentNames.query_recomm_title, lang) %></th>
					</table>
				</div>
				<div id="relate-search" class="left-div">
					<table>
						<th class="title-hint"><%=VisibleConstant.getWebpageContent(ContentNames.relate_search_title, lang) %></th>
					</table>
				</div>
			</div>
			<div id="result-area"  class="rpage-fleft-div">
				<div class="container">
					<%
					if(null!=results&&!results.isEmpty()){
						int status = -1;
						for (Iterator<VideoInfo> it=results.iterator();it.hasNext();){
							VideoInfo curRes=it.next();
							if(null==curRes) continue;
							status ++;
					%>
						<% if(status % 4 == 0) { %>
							<div class="col-sm-12">
							<div class="row">
						<% }%>
							<div class="col-sm-3">
								<div class="result" >
									<a href="<%=curRes.getVideoUrl()%>" class="view-box" target="_blank">
										<span class="video">
											<img class="img-responsive imgs" title="<%=curRes.getTitle()%>" src="<%=curRes.getImageUrl()%>">
											<span class="s-mask"></span>
											<span class="s-play"></span>
											<span class="info-bg"></span>
											<span class="site"> <%=curRes.getSite() %> </span>
											<span class="info"> <%=curRes.getDuration() %></span>
										</span>
										<span class="title"> <%=curRes.getTitle()%> </span>
									</a>
								</div>
							</div>
						<% if(status % 4 == 3) { %>
							</div>
							<br>
							</div>
						<% } %>
					<%} 
					}else{
						stopPage = true;
					%>
						<div class="result-no-found"><%=VisibleConstant.getWebpageContent(ContentNames.no_result_found, lang)%></div>
					<%}%>
					</div>
				<div id="page-area">
					<script language="JavaScript">
						var pg = new showVideoPages('pg', <%=stopPage%>);
						pg.page = <%=curPage%>;
						pg.printHtml();
					</script>
				</div>
			</div>

			<div style=" clear:both; visibility:hidden;"> 
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