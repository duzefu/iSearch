<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page language="java" import="java.lang.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">

<title>注册智搜！</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">

<SCRIPT src="js/cookie.js" type=text/javascript></SCRIPT> 
<script type="text/javascript" src="js/XMLHttp.js"></script>
<script type="text/javascript" src="js/userlogin.js"></script>
<script type="text/javascript" src="js/userregister.js"></script>
<link href="css/register.css" rel="stylesheet" type="text/css" />
<link href="css/login.css" rel="stylesheet" type="text/css" />
<style type="text/css">
a:link {
	text-decoration: none;
}

a:visited {
	text-decoration: none;
}

a:active {
	text-decoration: none;
}

a:hover {
	text-decoration: padding-bottom:20px;
	border-bottom: 3 solid #666;
}
</style>
</head>

<body>
        <div id="ruisou"></div>
        <div id="return">
           <div id="zhuce" style="float:left"></div>
           <a href="search.jsp" >返回首页</a>
        </div>
        <div id="welcome">
             <div id="font1">欢迎注册智搜</div>
             <div id="font2">已有账号，马上<a href="javascript:login()">登录</a></div>
        </div>
        <div id="registerfail"></div>
        
 		<form name="myForm" action="" method="post" focus="login" style="margin-left:200px">
		<table height="288" border="0" align="">
			<tr>
				<td>
					<table width="676" height="43" border="0">
					<tr>
					<td>
					</td>
					</tr>
						<tr>
							<td width="90" height="37"><font color="#FF0000">*</font>电子邮件：</td>
							<td width="576">
								<input type="text" style="WIDTH:185px" maxLength=30 name="emailadress"onfocus="getFocus1()"
								 onblur="loseFocus1()" >
								<span id="spanEmail" style="color:red; font-size:12px"></span>
							</td>
							
						</tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td><font size="2" color="#999999">输入一个已拥有的邮件地址,以通过验证完成注册。格式如name@example.com</font></td>
                        </tr>
					</table>
				</td>
			</tr>
			<tr>
			    <td width="678" height="52">
					<table width="674"  height="43" border="0">
						<tr>
							<td width="90" height="37"><font color="#FF0000">*</font>用 户 名：</td>
							<td width="562">
							    
								<input type="text" style="WIDTH: 185px" maxLength=30 name="username" onblur="loseFocus2()">
								
								
								<!--
								<input type="text" style="WIDTH: 185px" maxLength=30 name="username"  Readonly>
                                <font size="2" color="#ff0000">（测试期间用户名默认为电子邮箱,请务必真实填写电子邮箱）</font>
                                -->
								
		                         
								<span id="spanUsername" style="color:red; font-size:12px"></span>
							</td>
						</tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td><font size="2" color="#999999">由字母数字及下划线组成。</font></td>
                        </tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>
					<table width="676"  height="43" border="0">
						<tr>
							<td width="90" height="37"><font color="#FF0000">*</font>密&nbsp;&nbsp;&nbsp;&nbsp;码：</td>
							<td width="574">
							    
								<input type="password" style="WIDTH: 185px" maxLength=30 name="password" onblur="loseFocus3()">
								<span id="spanPassword" style="color:red; font-size:12px"></span>
								
								<!-- 
								<input type="password"  style="WIDTH: 185px" maxLength=30 name="password" onblur="loseFocus3()" disabled>
								<font size="2" color="#ff0000">（测试期间省去密码）</font>
								 -->
							</td>
						</tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td><font size="2" color="#999999">6到16个字符，区分大小写。</font></td>
                        </tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>
					<table width="678"  height="43" border="0">
						<tr>
							<td width="90" height="37"><font color="#FF0000">*</font>确认密码：</td>
							<td width="578">
							    
								<input type="password" style="WIDTH: 185px" maxLength=30 name="usersRepass" onblur="loseFocus4()">
								<span id="spanRepass" style="color:red; font-size:12px"></span>
							
								<!-- 
								<input type="password" style="WIDTH: 185px" maxLength=30 name="usersRepass" onblur="loseFocus4()" disabled>
								<font size="2" color="#ff0000">（测试期间省去确认密码）</font>
								 -->
							</td>
						</tr>
                         <tr>
                            <td>&nbsp;</td>
                            <td><font size="2" color="#999999">再次输入你设置的密码。</font></td>
                        </tr>
					</table>
				</td>
			</tr>
			
			
            <tr>
                <td> 
                <table width="301"><tr>
          <td width="154" align=center>
          <input type="button" name=btnG value="注册"  onclick="Register()" style="width:80px; height:30px;background-color:#00F; color:#FFF; font-weight:bold">
          </td>  
          <td width="151" align=center>     
			<input name="register" type="reset" value="重置" onclick="Reset()" style="width:80px; height:30px;background-color:#00F; color:#FFF; font-weight:bold"> 
		  </td>
         </tr></table>
              </td>
            </tr>
</table>
          

</form>
	<p></p>												
   <div id="copyright">&copy;2012 西安电子科技大学</div>
   
   <!-- 登录框部分（by许静20121113） -->
	<center>
	<div id="loginform" style="display: none">
		<font color="#FF0000" size="+2"
			style="position:absolute;top:6px;right:10px"><b>
			<a href="javascript:close1()" style="color:#F00">&times;</a> </b> </font> <font
			size="+3" color="#999999"
			style="position:absolute;top:20px;left:30px; font-weight:bold">智搜</font>
		<font size="+2" color="#CCCCCC"
			style="position:absolute;top:25px;left:100px; font-weight:bold">寻找您需要的</font>
		<hr style="height:2px; background-color:#F5FFE0;width:100%; position:absolute;top:60px;left:0px" />
		<br> <br>
		<form name="login" action="" method="post" focus="login">
		<div id="inputshow" style="color:#F00"></div>
			<font size="2" style="color:#999999; font-weight:bold">用户账号 </font>
<!-- 	by许静20121112晚 在input框中加入onkeydown="loginbuttonOnClick()"，,在button中加id属性-->
			 <input name="username" id="username" style="width:180px; height:25px; border:1px solid #9F6" type=text onkeydown="loginbuttonOnClick(event)" >
			<p />
			<font size="2" style="color:#999999; font-weight:bold">登录密码 </font>
			
			 <input name="password" id="password" type="password" style="width:180px; height:25px; border:1px solid #9F6" onkeydown="loginbuttonOnClick(event)"/>
			
			 <!--
			 <input name="password" id="password" type="password" style="width:180px; height:25px; border:1px solid #9F6" onkeydown="loginbuttonOnClick(event)" disabled>
			 <br>
			 <font size="2" color="#ff0000">（测试期间省去密码）</font>
			 测试期间使用 -->
			<p />
			<!-- <input name="" type="checkbox" value="" onSelect="" /><font size="2">记住我？</font>
			<a href="" style="font-size:12px">找回密码？</a>  -->
			<input type="button" name=btnG id="loginbutton" value="登&nbsp;录" style="width:50px; height:25px" onclick="tijiao()" />
		</form>
        
	</div>
	</center>
	<div id="mask" style="display: none;">&nbsp;</div>
  </body>
