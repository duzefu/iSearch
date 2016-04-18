<%@page import="server.info.config.SessionAttrNames"%>
<%@ page language="java" import="java.util.*,java.io.Serializable,common.entities.searchresult.PictureResult"
contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	List<String> resultList  = (List<String>)request.getAttribute("results");
	String word=(String)request.getAttribute("query");
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	String usernameinpage = (String) session.getAttribute(SessionAttrNames.USERNAME_ATTR);
	String information = (String) session.getAttribute("information");
	if(null==information) information="欢迎：";
	List <PictureResult> picture_result_list = new ArrayList<PictureResult>();
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
		<script type="text/javascript" src="js/picture.js"></script>
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
	</head>
	<body>
	
		<div id="zhuce"></div>

	<a href="./search.jsp"><div id="ruisou"></div></a> <!--图片链接到主页-->
	<div id="search">
		<form name="myForm" action="picture.action" method="get">
		<input name="query" id="query" type=text size=75 maxlength=65 value="${query}" baiduSug="2" style="height:30px; width:500px" onkeydown="searchButtonClick(event)" />
		<input name="page" id="page" type="hidden" /> 
		<input type=button name=btnG id="searchbutton" value="智  搜"	style="height:30px; width:70px;text-align:center" onclick="PictureAction()" />
		</form>
		<div class="ss_table" style="position:relative; top:10px;">
		<a onclick="FormAction()" >网页</a>
		<a onclick="PictureAction()" >图片</a>
		<a onclick="VideoAction()" >视频</a>
		<a onclick="PictureAction()" >测试</a>
		<a href="//www.baidu.com/more/" onmousedown="return c({'fm':'tab','tab':'more'})">更多»</a>
		</div>
	</div>
	<div id="results_from" style="position:relative; top:10px;">
		<div id="left">结果来自于</div>
		<div id="right"> <img src="images/source.png" width="370" height="30" /></div>
	</div>
	
		
		<%//picture_result_list.add(new Picture_result("标志性建筑_西安电子科技大学校庆80周年...","http://p3.so.qhimg.com/t0167c141b88ab55713.jpg"));%>
		<%//picture_result_list.add(new Picture_result("西安电子科技大学介绍","http://p0.so.qhimg.com/t016834b2cdb0548820.jpg"));%>
		<%//picture_result_list.add(new Picture_result("西安电子科技大学_图片_互动百科","http://p3.so.qhimg.com/t01b468a0b831e14c53.jpg"));%>
	
	<table><tbody><tr>
		<td  width="200" valign="top">
		<div id="left_info">   
				<table width="250" border="0">
				<tbody><tr><td>
				<font size="2 "><b>&nbsp;&nbsp;群组用户也搜索过：(查询人数/组内人数)</b> </font>
				</td></tr></tbody>
				</table>
				<br>
				<table width="200" border="0"><!--显示相关搜索的table-->
				<tbody>
					<tr><td><font size="2"><b>&nbsp;&nbsp;相关搜索：</b></font></td></tr>
					<tr><td><a href="javascript:void(0)" onclick="hotSearch(this)" style="color:blue"><font size="2">&nbsp;&nbsp;恶意做空</font></a></td></tr>
					<tr><td><a href="javascript:void(0)" onclick="hotSearch(this)" style="color:blue"><font size="2">&nbsp;&nbsp;打击恶意做空刻不容缓</font></a></td></tr>
					<tr><td><a href="javascript:void(0)" onclick="hotSearch(this)" style="color:blue"><font size="2">&nbsp;&nbsp;什么叫恶意做空</font></a></td></tr>
					<tr><td><a href="javascript:void(0)" onclick="hotSearch(this)" style="color:blue"><font size="2">&nbsp;&nbsp;恶意做空a股</font></a></td></tr>
					<tr><td><a href="javascript:void(0)" onclick="hotSearch(this)" style="color:blue"><font size="2">&nbsp;&nbsp;恶意做空是什么意思</font></a></td></tr>
					<tr><td><a href="javascript:void(0)" onclick="hotSearch(this)" style="color:blue"><font size="2">&nbsp;&nbsp;期指恶意做空</font></a></td></tr>
					<tr><td><a href="javascript:void(0)" onclick="hotSearch(this)" style="color:blue"><font size="2">&nbsp;&nbsp;摩根士丹利恶意做空</font></a></td></tr>
					<tr><td><a href="javascript:void(0)" onclick="hotSearch(this)" style="color:blue"><font size="2">&nbsp;&nbsp;人民日报 恶意做空</font></a></td></tr>
					<tr><td><a href="javascript:void(0)" onclick="hotSearch(this)" style="color:blue"><font size="2">&nbsp;&nbsp;有人恶意做空a股</font></a></td></tr>
				</tbody>
			    </table>
		
		</div>		
		</td>
		
		<td width="888" valign="top">
	    <div id="picture_results" style="position:relative; left:80px; top:20px;">
	    
			<!-- IMAGE_CONTENT BEGIN -->
			<div id="picture_0"
				style="width: 200px; text-align: center; left: 0px; top: 4px; z-index: 10; visibility: visible; position: absolute; display: block; float: left;">
				<table align="center" border="0" cellpadding="0" cellspacing="0">
					<tbody><tr><td class="topimag" valign="middle"><ul id="imageFrame0" class="thumbul"><li class="imgthumb">
						<span class="thumb_shadow_right"><center>
							<a herf="">
							<img src="<%=resultList.get(0) %>"  title="苹果手机"
					 	 	alt="苹果手机" class="imgthumb" width="150" height="130"  onerror="javascript:this.src='images/error.png';" />
					 	 	</a>
						</center></span>
					</li></ul></td></tr></tbody>
				</table>
			</div>
			
			<div id="picture_1" 
				style="width: 200px; text-align: center; left: 213px; top: 4px; z-index: 10; visibility: visible; position: absolute; display: block; float: left;">
				<table align="center" border="0" cellpadding="0" cellspacing="0">
					<tbody><tr><td class="topimag" valign="middle"><ul id="imageFrame1" class="thumbul"><li class="imgthumb" >
						<span class="thumb_shadow_right"><center>
							<a herf="">
							<img src="<%=resultList.get(1) %>" title="壁纸"
					     		alt="壁纸" class="imgthumb" width="150" height="130" onerror="javascript:this.src='images/error.png';"/>
					     	</a>
						</center></span>
					</li></ul></td></tr></tbody>
			    </table>
			</div>
			
			<div id="picture_2" 
				style="width: 200px; text-align: center; left: 426px; top: 4px; z-index: 10; visibility: visible; position: absolute; display: block; float: left;">
				<table align="center" border="0" cellpadding="0" cellspacing="0">
					<tbody><tr><td class="topimag" valign="middle"><ul id="imageFrame2" class="thumbul"><li class="imgthumb" >
						<span class="thumb_shadow_right"><center>
						    <a herf="">
							<img src="<%=resultList.get(2) %>" title="宠物猫咪"
					 	 		alt="宠物猫咪" class="imgthumb" width="150" height="130" onerror="javascript:this.src='images/error.png';"/> 
					 	 	</a>
						</center></span>
					</li></ul></td></tr></tbody>
			    </table>
			</div>
			
			<div id="picture_3" 
				style="width: 200px; text-align: center; left: 639px; top: 4px; z-index: 10; visibility: visible; position: absolute; display: block; float: left;">
				<table align="center" border="0" cellpadding="0" cellspacing="0">
					<tbody><tr><td class="topimag" valign="middle"><ul id="imageFrame3" class="thumbul"><li class="imgthumb">
						<span class="thumb_shadow_right"><center>
						    <a herf="">
							<img src="<%=resultList.get(3) %>"  title="明星"
					 	 		alt="明星" class="imgthumb" width="150" height="130" onerror="javascript:this.src='images/error.png';"/>
						    </a>
						</center></span>
					</li></ul></td></tr></tbody>
				</table>
			</div>
			
			<div id="picture_4" 
				style="width: 200px; text-align: center; left: 852px; top: 4px; z-index: 10; visibility: visible; position: absolute; display: block; float: left;">
				<table align="center" border="0" cellpadding="0" cellspacing="0">
					<tbody><tr><td class="topimag" valign="middle"><ul id="imageFrame4" class="thumbul"><li class="imgthumb">
						<span class="thumb_shadow_right"><center>
							<a herf="">
							<img src="<%=resultList.get(4) %>" title="摄影"
					        	alt="摄影" class="imgthumb" width="150" height="130" onerror="javascript:this.src='images/error.png';"/>
						    </a>
						</center></span>
					</li></ul></td></tr></tbody>
				</table>
			</div>
			
			<div id="picture_5" 
				style="width: 200px; text-align: center; left: 1065px; top: 4px; z-index: 10; visibility: visible; position: absolute; display: block; float: left;">
			    <table align="center" border="0" cellpadding="0" cellspacing="0">
					<tbody><tr><td class="topimag" valign="middle"><ul id="imageFrame5" class="thumbul"><li class="imgthumb">
						<span class="thumb_shadow_right"><center>
						    <a herf="">
							<img src="<%=resultList.get(5) %>" title="西电校徽"
					     		alt="西电校徽" class="imgthumb" width="150" height="130" onerror="javascript:this.src='images/error.png';"/>
						    </a>
						</center></span>
					</li></ul></td></tr></tbody>     	
				</table>
			</div>
			
			<div id="picture_6" 
				style="width: 200px; text-align: center; left: 0px; top: 180px; z-index: 10; visibility: visible; position: absolute; display: block; float: left;">
			    <table align="center" border="0" cellpadding="0" cellspacing="0">
					<tbody><tr><td class="topimag" valign="middle"><ul id="imageFrame5" class="thumbul"><li class="imgthumb">
						<span class="thumb_shadow_right"><center>
							<img src="<%=resultList.get(6) %>" 
					     		alt="西电" class="imgthumb" width="150" height="130" onerror="javascript:this.src='images/error.png';"/>
						</center></span>
					</li></ul></td></tr></tbody>     	
				</table>
			</div>
			
			<div id="picture_7" 
				style="width: 200px; text-align: center; left: 213px; top: 180px; z-index: 10; visibility: visible; position: absolute; display: block; float: left;">
			    <table align="center" border="0" cellpadding="0" cellspacing="0">
					<tbody><tr><td class="topimag" valign="middle"><ul id="imageFrame5" class="thumbul"><li class="imgthumb">
						<span class="thumb_shadow_right"><center>
							<img src="<%=resultList.get(7) %>"
					     		alt="西电校徽" class="imgthumb" width="150" height="130" onerror="javascript:this.src='images/error.png';"/>
						</center></span>
					</li></ul></td></tr></tbody>     	
				</table>
			</div>
			
			<div id="picture_8" 
				style="width: 200px; text-align: center; left: 426px; top: 180px; z-index: 10; visibility: visible; position: absolute; display: block; float: left;">
			    <table align="center" border="0" cellpadding="0" cellspacing="0">
					<tbody><tr><td class="topimag" valign="middle"><ul id="imageFrame5" class="thumbul"><li class="imgthumb">
						<span class="thumb_shadow_right"><center>
							<img src="<%=resultList.get(8) %>" 
					     		alt="西电校徽" class="imgthumb" width="150" height="130" onerror="javascript:this.src='images/error.png';"/>
						</center></span>
					</li></ul></td></tr></tbody>     	
				</table>
			</div>
			
			<div id="picture_9" 
				style="width: 200px; text-align: center; left: 639px; top: 180px; z-index: 10; visibility: visible; position: absolute; display: block; float: left;">
			    <table align="center" border="0" cellpadding="0" cellspacing="0">
					<tbody><tr><td class="topimag" valign="middle"><ul id="imageFrame5" class="thumbul"><li class="imgthumb">
						<span class="thumb_shadow_right"><center>
							<img src="<%=resultList.get(9) %>" 
					     		alt="西电校徽" class="imgthumb" width="150" height="130" onerror="javascript:this.src='images/error.png';"/>
						</center></span>
					</li></ul></td></tr></tbody>     	
				</table>
			</div>
			
			<div id="picture_10" 
				style="width: 200px; text-align: center; left: 852px; top: 180px; z-index: 10; visibility: visible; position: absolute; display: block; float: left;">
			    <table align="center" border="0" cellpadding="0" cellspacing="0">
					<tbody><tr><td class="topimag" valign="middle"><ul id="imageFrame5" class="thumbul"><li class="imgthumb">
						<span class="thumb_shadow_right"><center>
							<img src="<%=resultList.get(10) %>" 
					     		alt="西电校徽" class="imgthumb" width="150" height="130" onerror="javascript:this.src='images/error.png';"/>
						</center></span>
					</li></ul></td></tr></tbody>     	
				</table>
			</div>
			
			<div id="picture_11" 
				style="width: 200px; text-align: center; left: 1065px; top: 180px; z-index: 10; visibility: visible; position: absolute; display: block; float: left;">
			    <table align="center" border="0" cellpadding="0" cellspacing="0">
					<tbody><tr><td class="topimag" valign="middle"><ul id="imageFrame5" class="thumbul"><li class="imgthumb">
						<span class="thumb_shadow_right"><center>
							<img src="<%=resultList.get(11) %>" 
					     		alt="西电校徽" class="imgthumb" width="150" height="130" onerror="javascript:this.src='images/error.png';"/>
						</center></span>
					</li></ul></td></tr></tbody>     	
				</table>
			</div>
			
			<div id="picture_12" 
				style="width: 200px; text-align: center; left: 0px; top: 360px; z-index: 10; visibility: visible; position: absolute; display: block; float: left;">
			    <table align="center" border="0" cellpadding="0" cellspacing="0">
					<tbody><tr><td class="topimag" valign="middle"><ul id="imageFrame5" class="thumbul"><li class="imgthumb">
						<span class="thumb_shadow_right">
							<img src="<%=resultList.get(12) %>"
					     		alt="西电校徽" class="imgthumb" width="150" height="130" onerror="javascript:this.src='images/error.png';"/>
					    </span>
					</li></ul></td></tr></tbody>     	
				</table>
			</div>
			
			<div id="picture_13" 
				style="width: 200px; text-align: center; left: 213px; top: 360px; z-index: 10; visibility: visible; position: absolute; display: block; float: left;">
			    <table align="center" border="0" cellpadding="0" cellspacing="0">
					<tbody><tr><td class="topimag" valign="middle"><ul id="imageFrame5" class="thumbul"><li class="imgthumb">
						<span class="thumb_shadow_right"><center>
							<img src="<%=resultList.get(13) %>"
					     		alt="西电校徽" class="imgthumb" width="150" height="130" onerror="javascript:this.src='images/error.png';"/>
						</center></span>
					</li></ul></td></tr></tbody>     	
				</table>
			</div>
			
			<div id="picture_14" 
				style="width: 200px; text-align: center; left: 426px; top: 360px; z-index: 10; visibility: visible; position: absolute; display: block; float: left;">
			    <table align="center" border="0" cellpadding="0" cellspacing="0">
					<tbody><tr><td class="topimag" valign="middle"><ul id="imageFrame5" class="thumbul"><li class="imgthumb">
						<span class="thumb_shadow_right"><center>
							<img src="<%=resultList.get(14) %>" 
					     		alt="西电校徽" class="imgthumb" width="150" height="130" onerror="javascript:this.src='images/error.png';" conclick="return false"/>
						</center></span>
					</li></ul></td></tr></tbody>     	
				</table>
			</div>
			
			<div id="picture_15" 
				style="width: 200px; text-align: center; left: 639px; top: 360px; z-index: 10; visibility: visible; position: absolute; display: block; float: left;">
			    <table align="center" border="0" cellpadding="0" cellspacing="0">
					<tbody><tr><td class="topimag" valign="middle"><ul id="imageFrame5" class="thumbul"><li class="imgthumb">
						<span class="thumb_shadow_right"><center>
							<img src="<%=resultList.get(15) %>"
					     		alt="西电校徽" class="imgthumb" width="150" height="130" onerror="javascript:this.src='images/error.png';"/>
						</center></span>
					</li></ul></td></tr></tbody>     	
				</table>
			</div>
			
			<div id="picture_16" 
				style="width: 200px; text-align: center; left: 852px; top: 360px; z-index: 10; visibility: visible; position: absolute; display: block; float: left;">
			    <table align="center" border="0" cellpadding="0" cellspacing="0">
					<tbody><tr><td class="topimag" valign="middle"><ul id="imageFrame5" class="thumbul"><li class="imgthumb">
						<span class="thumb_shadow_right"><center>
							<img src="<%=resultList.get(16) %>"
					     		alt="西电校徽" class="imgthumb" width="150" height="130" onerror="javascript:this.src='images/error.png';"/>
						</center></span>
					</li></ul></td></tr></tbody>     	
				</table>
			</div>
			
			<div id="picture_17" 
				style="width: 200px; text-align: center; left: 1065px; top: 360px; z-index: 10; visibility: visible; position: absolute; display: block; float: left;">
			    <table align="center" border="0" cellpadding="0" cellspacing="0">
					<tbody><tr><td class="topimag" valign="middle"><ul id="imageFrame5" class="thumbul"><li class="imgthumb">
						<span class="thumb_shadow_right"><center>
							<img src="<%=resultList.get(17) %>"
					     		alt="西电校徽" class="imgthumb" width="150" height="130" />
						</center></span>
					</li></ul></td></tr></tbody>     	
				</table>
			</div>
		</div>
		</td>
	</tr></tbody></table>
		
		<div id="pages_pg_2" class="pages" style="position:relative; left:-80px; top:400px;">
			<span class="number">
			<span title="First Page">首页</span>
			<span title="Prev Page">上一页</span>
			<span title="Page 1">[1]</span>
			<span title="Page 2">
				<a href="javascript:pg.toPage(2);">[2]</a>
			</span>
			<span title="Page 3">
				<a href="javascript:pg.toPage(3);">[3]</a>
			</span>
			<span title="Page 4">
				<a href="javascript:pg.toPage(4);">[4]</a>
			</span>
			<span title="Page 5">
				<a href="javascript:pg.toPage(5);">[5]</a>
			</span>
			<span title="Page 6">
				<a href="javascript:pg.toPage(6);">[6]</a>
			</span>
			<span title="Page 7">
				<a href="javascript:pg.toPage(7);">[7]</a>
			</span>
			<span title="Page 8">
				<a href="javascript:pg.toPage(8);">[8]</a>
			</span>
			<span title="Page 9">
				<a href="javascript:pg.toPage(9);">[9]</a>
			</span>
			<span title="Page 10">
				<a href="javascript:pg.toPage(10);">[10]</a>
			</span>
			<span title="Next Page">
				<a href="javascript:pg.toPage(2);">下一页</a>
			</span>
			<span title="Last Page">
				<a href="javascript:pg.toPage(20);">尾页</a>
			</span>
			<span><font size="2">共约10000条结果</span>
			</span><br></div>
		
		<!-- 
		<div id="picture_results" style="position:relative;">
		     <%//int i=0;%>
			 <%//for(i=0;i<=picture_result_list.size();i++)%>
			<div id=<%//=picture_result_list.get(i).getTitle()%> >
				<img src=<%//=picture_result_list.get(i).getLink()%> 
					 alt=<%//=picture_result_list.get(i).getTitle()%>>
			</div>
		</div>
		 -->		    
		 
		<!--  
			<img src="images/sd/bizhi0701.jpg">
			<img src="images/sd/chongwu0701.jpg">
			<img src="images/sd/chongwu0701.jpg">
			<img src="images/sd/mingxing0701.jpg">
			<img src="images/sd/chongwu0701.jpg">
			<img src="images/sd/bizhi0701.jpg">
			<img src="images/sd/chongwu0701.jpg">
			<img src="images/sd/chongwu0701.jpg">
			<img src="images/sd/chongwu0701.jpg">
			<img src="images/sd/mingxing0701.jpg">
			<img src="images/sd/chongwu0701.jpg">
			<img src="images/sd/mingxing0701.jpg">
		-->	
		
	   <div id="box" style="top:450px; left:250px">
			<div class="title">
					实时热点:
					<a href="">更多&gt;&gt;</a>
			</div>
			<div id="hotwords">
			<dl><dd><a href="javascript:void(0)" onclick="hotSearch(this)">公安部排查恶意做空</a></dd>
			<dd><a href="javascript:void(0)" onclick="hotSearch(this)">狱中猎艳犯被判刑</a></dd>
			<dd><a href="javascript:void(0)" onclick="hotSearch(this)">台风莲花登陆广东</a></dd>
			<dd><a href="javascript:void(0)" onclick="hotSearch(this)">沈阳居民楼燃气爆炸</a></dd>
			<dd><a href="javascript:void(0)" onclick="hotSearch(this)">美国捕获双色龙虾</a></dd></dl>
			
			<dl><dd><a href="javascript:void(0)" onclick="hotSearch(this)">男童被曝与猪共处</a></dd>
			<dd><a href="javascript:void(0)" onclick="hotSearch(this)">湖北现蓝白小龙虾</a></dd>
			<dd><a href="javascript:void(0)" onclick="hotSearch(this)">微软裁员7800人</a></dd>
			<dd><a href="javascript:void(0)" onclick="hotSearch(this)">女生酒店实习坠亡</a></dd>
			<dd><a href="javascript:void(0)" onclick="hotSearch(this)">50亿年前银河信号</a></dd></dl>
			</div>
		</div>
            <div id="copyright" style="position:relative; top:400px;">
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