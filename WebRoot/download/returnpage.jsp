<%@ page language="java"
	import="java.util.*,common.entities.searchresult.Result,common.entities.blackboard.Interest"
	pageEncoding="UTF-8"%>
<%-- <%@taglib prefix="s" uri="/struts-tags"%> --%>
<%@page import="common.functions.webpagediagram.PieChartPainter"%>
<%@page import="common.functions.webpagediagram.BarChartPainter"%>
<%@page import="common.functions.webpagediagram.SearchEngineScore"%>
<%@page import="common.functions.webpagediagram.CoolDynamicBar"%>
<%@page import="common.functions.webpagediagram.CoolDynamicEngineScore"%>
<%@page import="common.functions.webpagediagram.VennDiagram"%>
<%@page import="struts.actions.web.SearchAction"%>
<%@page import="org.jfree.chart.servlet.ServletUtilities"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="java.awt.Color"%>
<%@page import="server.info.entities.communication.RecommQueryAndPercent" %>

<%
	//step1: 获取各种结果列表等信息==============================================================
	int a=1;
	List<Result> otherRecomList  = (List<Result>)request.getAttribute("clickRecomResult");
	List<Result> list=(List<Result>)request.getAttribute("results");
	List<Result> allResult=(List<Result>)request.getAttribute("allResult");
	String word=(String)request.getAttribute("query");
	Integer pages=Integer.valueOf((String)request.getAttribute("page"));
	Integer lastPage = (Integer)request.getAttribute("lastPage");
	List<RecommQueryAndPercent> queryRecomm=(List<RecommQueryAndPercent>)request.getAttribute("queryRecomResult");
	List<String> relatedSearch = (List<String>)request.getAttribute("relatedSearch");
	String chosenEngine=(String)request.getAttribute("selectedEngineName");
	if(null==chosenEngine) chosenEngine="";
	Set<String> mEngineOfResult=(Set<String>)request.getAttribute("mEngineOfResult");//获取搜索结果所覆盖的所有搜索引擎 by LiuJiawei
	String currentPage=(String)request.getAttribute("page");
	 int userid=(Integer)request.getAttribute("userid");
	//end of step 1===============================================================================
	
	//获取饼状图（信息检索覆盖率）所需要的数据
	String urlPie = "";//空字符串为异常
	if(allResult != null && !allResult.isEmpty())
	{
		/* String fileNamePie = ServletUtilities.saveChartAsPNG(PieChartPainter.GetPieChart(allResult),440,360,session); //第二张饼状图的
		//ServletUtilities是面向web开发的工具类，返回一个字符串文件名,文件名自动生成，生成好的图片会自动放在服务器（tomcat）的临时文件下（temp）
		urlPie = request.getContextPath() + "/DisplayChart?filename=" + fileNamePie;
		//根据文件名去临时目录下寻找该图片，这里的/DisplayChart路径要与配置文件里用户自定义的<url-pattern>一致 */
	}
	
	//用户名
	String usernameinpage = (String)session.getAttribute("usernameinpage");
	String information = (String)session.getAttribute("information"); 
	information=information+" ";
	List<Entry<String, Double>> scoreBar = CoolDynamicBar.GetDataset(userid,usernameinpage);//兴趣评价图的数据来源

%>

<html>
<head>
<title>智搜，聪明的搜索</title>
<link rel="stylesheet" type="text/css" href="css/returnpage.css" />
<link rel="stylesheet" type="text/css" href="css/login.css" />
<link rel="stylesheet" type="text/css" href="css/style_bar.css" />
<link rel="stylesheet" type="text/css" href="css/style_score.css" />
<script type="text/javascript" src="js/jquery-1.8.3.min.js"></script>
<script type="text/javascript" src="js/returnpage.js"></script>
<script type="text/javascript" src="js/XMLHttp.js"></script>
<script type="text/javascript" src="js/cookie.js"></script>
<script type="text/javascript" src="js/userlogin.js"></script>
<script type="text/javascript" src="js/formAction.js"></script>
<script type="text/javascript" src="js/userclick.js"></script>
<script type="text/javascript" src="js/hotwords.js"></script>
<script type="text/javascript" src="js/logout.js"></script>
<script type="text/javascript" src="js/canvasXpress.min.js"></script>
<script type="text/javascript" src="js/jquery-1.4.4.min.js"></script>
<script type="text/javascript" src="js/min.js"></script>
<script type="text/javascript" src="js/venn.js"></script>
<script type="text/javascript" src="js/picture.js"></script>
<script type="text/javascript" src="js/video.js"></script>
<script type="text/javascript"> <!--用户名，登陆等-->
	var usernameinpage=null;
	var information=null;
	if("<%=usernameinpage%>" != "null")
	{
		usernameinpage="<%=usernameinpage%>";
		information="<%=information%>";   
	}
</script>
</head>

<body>
	<div id="zhuce"></div>
	
	<div id="selectEngine" style="display:none"><%=chosenEngine%></div>
	<div id="pageno" style="display:none"><%=currentPage%></div>
	
	<a href="./search.jsp"><div id="ruisou"></div></a> <!--图片链接到主页-->
	<div id="search">
		<form name="myForm" action="search.action" method="get">
		<input name="query" id="query" type=text size=75 maxlength=65 value="${query}" baiduSug="2" style="height:30px; width:500px" onkeydown="searchButtonClick(event)" />
		<input name="page" id="page" type="hidden" /> 
		<input type=button name=btnG id="searchbutton" value="智  搜"	style="height:30px; width:70px;text-align:center" onclick="FormAction()" />
		<input name="selectedEngineName" id="selectedEngineName" type="hidden">
		</form>
		<div class="ss_table">
		<a onclick="FormAction()">网页</a>
		<a onclick="PictureAction()" >图片</a>
		<a onclick="VideoAction()" >视频</a>
		<a onclick="PictureAction()" >测试</a>
		<a href="//www.baidu.com/more/" onmousedown="return c({'fm':'tab','tab':'more'})">更多»</a>
		</div>
	</div>


	<div id="results_from" style="position:relative; top:2px">
		<div id="left">结果来自于</div>
		<div id="right"> <img src="images/source.png" width="370" height="30" /></div>
	</div>

	<hr /><!--分割线-->
	<!--以上为整体界面的上部-->

	<table><!--整个界面的body-->
		<tr>
			<td width="330" valign="top" >
				<div id="leftRelated">
				<table width="330" border="0"> <!--显示群组用户也搜索过的table-->
					<tr><td><font size="2 "><b>群组用户也搜索过：</b> </font></td></tr>
					<%
					if(queryRecomm!=null&&!queryRecomm.isEmpty())
						for(Iterator<RecommQueryAndPercent> it=queryRecomm.iterator();it.hasNext();)
						{
							String keyword=it.next().getQuery();
					%>
						<tr>
							<td>
								<a href="javascript:void(0)"  onclick="hotSearch(this)" style="color:blue">
									<font size="2">
										<%=keyword%>
									</font>
								</a>
							</td>
						</tr>
						<%}%>
				</table>
				<br />
				
				<table width="250" border="0"><!--显示相关搜索的table-->
					<tr><td><font size="2"><b>相关搜索：</b></font></td></tr>
					<%if(relatedSearch!=null&&!relatedSearch.isEmpty()) 
						for(int i=0;i<relatedSearch.size();i++)
						{%><tr><td><a href="javascript:void(0)"  onclick="hotSearch(this)" style="color:blue"><font size="2"><%=relatedSearch.get(i)%></font></a></td></tr><%}%>
				</table>
				<br/>
				<input type ="button"id="display_table" onclick="changeVisibility()" value="显示"></input>
				<!-- <a id="display_table" onclick="changeVisibility()">显示</a> -->
				<div id ="fugai" hidden="ture">
				<span><font size="2"><b></>检索信息覆盖率：</b></font></span>
				<% if(!urlPie.equals("")) 
				   {%><img src="<%= urlPie %>" width="220" height="180"><%}%>  
				<br></br>
				</div>
<script type="text/javascript">
function changeVisibility()
{
//document.getElementById("display_table").innerHTML="隐藏";
var state=!document.getElementById("fugai").hidden;
if(state)
	{
	document.getElementById("fugai").hidden=state;
	document.getElementById("merge-table").hidden=state;
	//document.getElementById("merge-table").stlye.display="block";
	document.getElementById("display_table").value="显示";
	}
else
{
	document.getElementById("fugai").hidden=state;
	document.getElementById("merge-table").hidden=state;
	//document.getElementById("merge-table").stlye.display="block";
	document.getElementById("display_table").value="隐藏";
	}
}
</script>	

				</div>
			</td>
			
			<td width="600" valign="top">
			<div id="content_right"></div>
			<br />
				<script language="JavaScript">
					window.onload = myFunction
					function myFunction()
					{
						if (usernameinpage != null) 
						{
							document.getElementById("zhuce").innerHTML = "<font size='2'>"
								+ information + usernameinpage + "&nbsp;&nbsp;&nbsp;&nbsp;"
								+ "<a href='./settings.jsp'>设置</a>"
								+ "<a href='javascript:logout(" + "\"" + usernameinpage + "\""
								+ ")'>退出</a>";
						}
						else 
						{
							document.getElementById("zhuce").innerHTML = "<a href='./register.jsp'>注册</a> <a href='javascript:login()'>登录</a>";
						}
				</script>
                
				
				<table width="600" border="0" style="margin-top:60px"> <!-- 群组推荐 -->
				<%if(otherRecomList.size()>0 && pages==1)
				  {
				      for(int j=0;j<otherRecomList.size();j++)
				      {
					  	%>
						
						<tr><td width="590" style="height:auto"><a href="<%=otherRecomList.get(j).getLink()%>" target="_blank" onclick="userclick('<%=otherRecomList.get(j).getTitle()%>','<%=otherRecomList.get(j).getAbstr()%>',' <%=otherRecomList.get(j).getLink()%>','群组推荐')">
						<font size="3" color="#FF0000"><%=otherRecomList.get(j).getTitle()%></font></a></td></tr>
						
						<tr><td width="590" style="line-height:1.5; height:auto"><font color="black" size="2"><%=otherRecomList.get(j).getAbstr()%></font></td></tr>
						
						<tr><td width="590" style="height:auto">
							<div style="width: 590px; word-break : break-all">
								<font color="#336600" size="2" face="宋体">
									<script type="text/javascript">
										function getLink(link)
											{
												var shortlink='';
												if(link.length > 50)
												{
													shortlink=link.substring(0,50);
													shortlink+='...';
												}
												else
													shortlink=link;
												return(shortlink);
											}
										document.write(getLink("<%=otherRecomList.get(j).getLink()%>"));
									</script>
								</font>
							</div>
							</td></tr>
						
						<tr>
						<td width="590" style="height:auto"><font size="2">来自：<font color="blue">群组点击推荐</font></font>
						</td>
						</tr>
				<% } } %>		
					
				<%if(list != null){
					%>
					<!-- 2015/07/13 LiuJiawei加入复选框 -->
					
					<tr>	
			          <td id="TargetEngine" width="590" style="height:auto" >
							
							<div style="position:absolute; width:590px; left:258px; top:150px;">
								  	<font size="2"><b>选择特定的搜索引擎：</b></font>	
								  	<div style="position:relative;top:8px">
								    <% if(mEngineOfResult.contains("百度")) %>
										<label style="width:50px;"><input id="Baidu" name="engine" type="checkbox" value="baidu" style="width:25px"<%if(chosenEngine.contains("baidu")){ %>checked=true<%}%>>百度</label>
									<% if(mEngineOfResult.contains("搜狗")) %>
										<label style="width:50px"><input id="Sougou" name="engine" type="checkbox" value="sougou" style="width:25px"<%if(chosenEngine.contains("sougou")){ %>checked=true<%}%>>搜狗</label>
									<% if(mEngineOfResult.contains("有道")) %>	
										<label style="width:50px"><input id="Youdao" name="engine" type="checkbox" value="youdao" style="width:25px"<%if(chosenEngine.contains("youdao")){ %>checked=true<%}%>>有道</label>
									<% if(mEngineOfResult.contains("雅虎")) %>
										<label style="width:50px"><input id="Yahoo" name="engine" type="checkbox" value="yahoo" style="width:25px"<%if(chosenEngine.contains("yahoo")){ %>checked=true<%}%>>雅虎</label>
									<% if(mEngineOfResult.contains("必应")) %>
										<label style="width:50px"><input id="Bing" name="engine" type="checkbox" value="bing" style="width:25px"<%if(chosenEngine.contains("bing")){ %>checked=true<%}%>>必应</label>
									    <input type="button" name=SelectButton id="selectbutton" value="确定" 
											style="height: 30px; width: 60px; text-align: center"
											onclick="SelectAction()">
									</div>
							        <% if(chosenEngine=="all"||chosenEngine=="")  
							          { %> 
							        	<label><div style="position:relative; top:10px">
							          
													<font size="2"><b>以下搜索结果来自：</b>百度、搜狗、有道、雅虎、必应
										      					   
													</font>	
											   </div>	
										</label>
								     <%} 
								    else {%>
								    	<label><div style="position:relative; top:14px">
													<font size="2"><b>以下搜索结果来自：</b>
																   <%if(chosenEngine.contains("baidu"))  
						   		   										{ %>百度  <%}
						   	   										 if(chosenEngine.contains("sougou")) 
						   											    {  %> 搜狗  <%}
						   	                                       	 if(chosenEngine.contains("youdao"))  
						   	                                       	    {%>  有道 <%}
						   		                                     if(chosenEngine.contains("yahoo")) 
						   		                                    	{%>    雅虎 <%  }
															   		 if(chosenEngine.contains("bing")) 
															   			{%> 必应  <% }%> 
										      					   
													</font>	
											   </div>	
										</label>
								     <%} %>  
								   
							</div>
				   </td></tr>
				<% 	for (int j=0;j<list.size();j++){
				%>
				<tr>
					<td id="FinalResult"  width="590" style="height:auto; position:relative; top:75px; left:-80px" ><a href="<%=list.get(j).getLink() %>" target="_blank" onclick="userclick('<%=list.get(j).getTitle()%>','<%=list.get(j).getAbstr()%>',' <%=list.get(j).getLink()%>','<%=list.get(j).getSource() %>')" >
					<font size="3" color="#FF0000"><%=list.get(j).getTitle()%></font></a></td>
				</tr>
				
				<tr><td id="FinalResult"  width="590" style="height:auto; position:relative; top:75px; left:-80px"><font color="black" size="2"><%=list.get(j).getAbstr()%></font></td></tr>
				
				<tr>
					<td id="FinalResult"  width="590" style="height:auto; position:relative; top:75px; left:-80px">
						<div style="width: 590px; word-break : break-all">
							<font color="#336600" size="2" face="宋体">
							<script type="text/javascript">
								function getLink(link)
									{
										var shortlink='';
										if(link.length>50)
										{
											shortlink=link.substring(0,50);
											shortlink+='...';
										}
										else
											shortlink=link;
										return(shortlink);
									}
									document.write(getLink("<%=list.get(j).getLink()%>"));
							</script>
							</font>
						</div>
					</td>
				</tr>
				
				<tr>
					<td id="FinalResult"  width="590" style="height:auto;position:relative;top:75px; left:-80px">
					<font color="" size="2">来自：
					<% 	String[] array=list.get(j).getArray();
						for(String item :array)
						{
							if(item.indexOf("百度") >= 0)
							{
								String URL="http://www.baidu.com/s?wd="+word;%> 
								<a href="<%=URL%>"><%=item%>&nbsp</a> <%
							}
  							else if(item.indexOf("搜搜") >= 0)
							{
  							 	String URL="http://www.soso.com/q?w="+word; %> 
								<a href="<%=URL%>"><%=item%>&nbsp</a> <%
							}
  							else if(item.indexOf("搜狗") >= 0)
  							{
  							 	String URL="http://www.sogou.com/web?query="+word; %> 
								<a href="<%=URL%>"><%=item%>&nbsp;</a> <%
							}	
 	                        else if(item.indexOf("盘古") >= 0)
  							{
  							 	String URL="http://search.panguso.com/pagesearch.htm?q="+word; %> 
								<a href="<%=URL%>"><%=item%>&nbsp;</a> <%
							}
 	                        else if(item.indexOf("必应")>=0)
  							{
  							 	String URL="http://cn.bing.com/search?query="+word; %> 
								<a href="<%=URL%>"><%=item%>&nbsp;</a> <%
							}
 	                       else if(item.indexOf("雅虎")>=0)
 	          				{
 	          					String URL="https://search.yahoo.com/search?toggle=1&cop=mss&ei=UTF-8&fr=yfp-t-308&fp=1&p="+word; %> 
 	        					<a href="<%=URL%>"><%=item%>&nbsp;</a> <%
 	        				}
  							else if(item.indexOf("有道")>=0)
  							{
  							 	String URL="http://www.youdao.com/search?q="+word; %> 
								<a href="<%=URL%>"><%=item%>&nbsp;</a> <%
							}else{
								String URL=list.get(j).getSource(); %> 
								<a href="<%=URL%>"><font color="blue">群组点击推荐</font></font></a>
							<%}
							%> 
					<%  } %> 
					</font>
					</td>
				</tr>
				<%}
				}
				else
				{%>
					<table width="600" border="0"><tr><td><font size="3"><b>很抱歉，没有您要查找的内容！</b></font></td></tr></table>
				<%} %>
				</table>  
				
				<td width="180" valign="top"><div id="content_left"></div></td>
				<td width="180" valign="top" id="rightRelated">
				
				<div>
					<% if(scoreBar != null && !scoreBar.isEmpty()){%>
						<div id="content_right">	
						<span><font size="2"><b>用户近三天的兴趣变迁：</b></font></span><!--第一幅图-->
						<% if(scoreBar.size() >= 1) {%>
							<div class="progressbar" data-perc="<%=(int)(scoreBar.get(0).getValue() * 100) %>"><!--第一个兴趣-->
								<font face="微软雅黑" size="2"><%= scoreBar.get(0).getKey() %></font>
								<div class="bar"><span></span></div>
								<div class="label"><span></span></div>
							</div>
						<%}%>
						
						<% if(scoreBar.size() >= 2) {%>
							<div class="progressbar" data-perc="<%= (int)(scoreBar.get(1).getValue() * 100) %>"><!--第二个兴趣-->
								<font face="微软雅黑" size="2"><%= scoreBar.get(1).getKey() %></font>
								<div class="bar color2"><span></span></div>
								<div class="label"><span></span></div>
							</div>
						<%}%>
						
						<% if(scoreBar.size() >= 3) {%>
							<div class="progressbar" data-perc="<%= (int)(scoreBar.get(2).getValue() * 100) %>"><!--第三个兴趣-->
								<font face="微软雅黑" size="2"><%= scoreBar.get(2).getKey() %></font>
								<div class="bar color3"><span></span></div>
								<div class="label"><span></span></div>
							</div>	
						<%}%>
						
						<% if(scoreBar.size() >= 4) {%>
							<div class="progressbar" data-perc="<%= (int)(scoreBar.get(3).getValue() * 100) %>"><!--第四个兴趣-->
								<font face="微软雅黑" size="2"><%= scoreBar.get(3).getKey() %></font>
								<div class="bar color4"><span></span></div>
								<div class="label"><span></span></div>
							</div>
						<%}%>	
					<%}%>
				</div>			
				
				<script type="text/javascript">
				$(function() 
				{
					$('.progressbar').each(function(){var t = $(this),
					dataperc = t.attr('data-perc'),
					barperc = Math.round(dataperc*2.65);
					t.find('.bar').animate({width:barperc}, dataperc*25);
					t.find('.label').append('<div class="perc"></div>');
			
					function perc(){
						var length = t.find('.bar').css('width'),
						perc = Math.round(parseInt(length)/2.65),
						labelpos = (parseInt(length) + 35 - 2);
						t.find('.label').css('left', labelpos);
						t.find('.perc').text(perc+'%');
					}
					perc();
					setInterval(perc, 0); 
					});
		
				});
				</script>
				</div>
				</td>
		
				<!--<td width="180" valign="top"><div id="xuanze"></div></td>-->
		
		</tr>
	</table>
		

<div id="PAGE" style="position:relative;top:100px">
<script language="JavaScript">
	var pg = new showPages('pg');
	pg.pageCount = <%=lastPage %>; // 定义总页数
	pg.printHtml();
</script>
</div>
	
	<div id="copyright" style="position:relative;top:100px">&copy;2015 西安电子科技大学</div>
	
	<!-- 登录实现部分（by许静20121113） -->
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
<!-- 百度搜索框提示 -->
<script charset="gbk" src="http://www.baidu.com/js/opensug.js"></script>
<!--</html>-->
</html>