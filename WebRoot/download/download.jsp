<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>智搜</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
  </head>
  <% 
     String str="   智搜是一款综合类的搜索引擎，属于元搜索引擎。“智搜”提供了4方面特色服务。可分为两大类，一是充分利用元搜索引擎的特性，根据用户的查询词，自动选择更好的搜索引擎检索；并且通过对检索结果进行统计分析，用户可以对各搜索引擎的检索结果有更好的把握。其二是“智搜”的用户个性化设计。通过学习用户的检索兴趣，“智搜”能够将更满足用户偏好的结果优先展示，并推荐用户可能感兴趣的内容。\n此外，系统还能对成员搜索引擎的结果信息进行统计，并使用图形化的方式展示出来。系统还对那些搜索目标不明确的用户提供推荐词，指引用户的搜索。";
  %>
  <body bgcolor=#C0C0C0>
    <div id="body" align="center">
            <br>
            <br>
            <h1>智能化元搜索引擎之智搜</h1><br>           
            <textarea rows="8" cols="80" readonly="readonly" ><%=str%></textarea><br>
            <br>
            <embed src="iSearch.mp4" width="640" height="400" type="video/mp4"><br>
            <br>
	        <a href="iSearch.mp4"><img src="download_video.png" width="100" height="40" border="0"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;	            
	        <a href="iSearchAndroid.apk"><img src="download_app.png" width="100" height="40" border="0"></a>
            
     </div>
    
  </body>
</html>
