<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page language="java" import="java.lang.*"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>搜索失败！</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="description" content="搜索失败页面">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<!-- changed by zcl 2014-04-18
	  修改原因：暂时禁止错误界面自动返回，让用户自己选择返回，方便看清楚邮箱。   
	     解除下面一行注释可以使本界面只显示"content"中指定的3秒，后跳转到search.jsp界面-->
	<!--  <meta http-equiv="refresh" content="3;url=search.jsp"> -->
	<!-- end of change by zcl 2014-04-18 -->
  </head>
  
  <body> 
    搜索出现问题！！！ 希望您能将本次查询出现的异常，包括使用的查询词与使用的浏览器反馈到我们的邮箱：<br/> 
    yuansousuotest@sina.com<br/>
    帮助我们改进我们的系统，谢谢。<br> 
    点击此处返回搜索主页：<a href="search.jsp">智搜首页</a>。
    
  </body>
</html>
