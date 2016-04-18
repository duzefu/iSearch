<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@page import="java.util.HashSet"%>
<%@page import="server.engine.api.EngineFactory.EngineName"%>
<%@page import="server.info.config.LangEnvironment"%>
<%@page import="server.info.config.LangEnvironment.ClientType"%>
<%@page import="server.info.config.SessionAttrNames"%>
<%@page import="org.json.JSONObject"%>
<%@page import="java.util.Set" %>
<%@page import="server.engine.api.EngineFactory" %>
<%@page import="java.util.Iterator"%>

<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%
	String usernameinpage = (String) session
			.getAttribute("usernameinpage");
	String information = (String) session.getAttribute("information");
	information = information + " ";
%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
		<title>智搜，聪明的搜索</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="keywords" content="iSearch,智搜,元搜索">
		<meta http-equiv="description" content="This is my page">
		<!--对布局有影响<link href="css/style.css" rel="stylesheet" type="text/css" />  -->
		<link href="css/login.css" rel="stylesheet" type="text/css" />
		<link rel="stylesheet" type="text/css" href="css/returnpage.css" />
		<link rel="stylesheet" type="text/css" href="css/picture.css" />
		<link rel="stylesheet" type="text/css" href="css/style_bar.css" />
		<link rel="stylesheet" type="text/css" href="css/style_score.css" />

<script type="text/javascript">
	var usernameinpage = null;
	var information = null;
	if ("<%=usernameinpage%>" != "null") {
		usernameinpage = "<%=usernameinpage%>";
		information = "<%=information%>";
	}
</script>

		<script type="text/javascript" src="js/XMLHttp.js">
</script>
		<script type="text/javascript" src="js/cookie.js">
</script>
		<script type="text/javascript" src="js/userlogin.js">
</script>
		<script type="text/javascript" src="js/formAction.js">
</script>
		<script type="text/javascript" src="js/hotwords.js">
</script>
		<script type="text/javascript" src="js/logout.js">
</script>
<script type="text/javascript" src="js/picture.js"></script>
<script type="text/javascript" src="js/video.js"></script>


	</head>
	<body>
	
		<div id="zhuce"></div>
	<div>
	<a href="./search.jsp"><div id="ruisou"></div></a> <!--图片链接到主页-->
	
	<div id="search">
		<form name="myForm" action="search.action" method="get">
		<input name="query" id="query" type=text size=75 maxlength=65 value="${query}" baiduSug="2" style="height:30px; width:500px" onkeydown="searchButtonClick(event)" />
		<input name="page" id="page" type="hidden" /> 
		<input type=button name=btnG id="searchbutton" value="智  搜"	style="height:30px; width:70px;text-align:center" onclick="FormAction()" />
		</form>
		<div class="ss_table">
			<a onclick="FormAction()">网页</a>
			<a onclick="PictureAction()" >图片</a>
			<a onclick="VideoAction()" >视频</a>
			<a onclick="PictureAction()" >测试</a>
			<a href="//www.baidu.com/more/" onmousedown="return c({'fm':'tab','tab':'more'})">更多»</a>
		</div>
	</div>
	
	<div id="results_from">
		<div id="left">结果来自于</div>
		<div id="right"> <img src="images/source.png" width="370" height="30" /></div>
	</div>
		<div id="box">
			<div class="title">
					实时热点:
					<a href="">更多&gt;&gt;</a>
			</div>
			<div id="hotwords"></div>
			<video src="http://www.iqiyi.com/w_19rsbjmhdh.html" controls>浏览器不支持</video>
			<video src="http://my.tv.sohu.com/us/63368152/65328498.shtml" controls>浏览器不支持</source>
			<video src="https://www.duba.com/?f=liebaont">浏览器不支持</video>
			<img src="images/sd/mingxing0701.jpg">
			<img src="images/sd/chongwu0701.jpg">
			<img src="images/sd/bizhi0701.jpg">
			<img src="images/sd/chongwu0701.jpg">
			<img src="images/sd/chongwu0701.jpg">
			<img src="images/sd/chongwu0701.jpg">
			<img src="images/sd/mingxing0701.jpg">
			<img src="images/sd/chongwu0701.jpg">
			<img src="images/sd/mingxing0701.jpg">
		</div>
	</div>
			<div id="copyright">
				&copy;2012 西安电子科技大学
			</div>
			<div id="loginform" style="display: none">
				<font color="#FF0000" size="+2"
					style="position: absolute; top: 6px; right: 10px"><b><a
						href="javascript:close1()" style="color: #F00">&times;</a> </b> </font>
				<font size="+3" color="#999999"
					style="position: absolute; top: 20px; left: 30px; font-weight: bold">智搜</font>
				<font size="+2" color="#CCCCCC"
					style="position: absolute; top: 25px; left: 100px; font-weight: bold">寻找您需要的</font>
				<hr
					style="height: 2px; background-color: #F5FFE0; width: 100%; position: absolute; top: 60px; left: 0px" />
				<br>
				<br>
				<form name="login" action="" method="post" focus="login">
					<div id="inputshow" style="color: #F00"></div>
					<font size="2" style="color: #999999; font-weight: bold">用户账号
					</font>
					<input name="username" id="username"
						style="width: 180px; height: 25px; border: 1px solid #9F6"
						type=text onkeydown="loginbuttonOnClick(event)">
					<p />
						<font size="2" style="color: #999999; font-weight: bold">登录密码
						</font>
						
			<input name="password" id="password" type="password" style="width:180px; height:25px; border:1px solid #9F6" onkeydown="loginbuttonOnClick(event)"/>

						<p />
							<input type="button" name=btnG id="loginbutton" value="登&nbsp;录"
								style="width: 50px; height: 25px" onclick="tijiao()" />
				</form>

			</div>
			<div id="mask" style="display: none;">
				&nbsp;
			</div>

	</body>
	<!-- 百度搜索框提示 -->
	<script charset="gbk" src="http://www.baidu.com/js/opensug.js">
</script>
</html>