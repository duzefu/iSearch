<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=utf-8"  
    pageEncoding="UTF-8"%> 
    <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">

		<title>${title}</title>
		<link rel="stylesheet" type="text/css" href="css/returnpage.css" />
		<link rel="stylesheet" type="text/css" href="css/rpage-loginentry.css" />
		<link rel="stylesheet" type="text/css" href="css/login.css" />
		<link rel="stylesheet" type="text/css" href="css/style_bar.css" />
		<link rel="stylesheet" type="text/css" href="css/style_score.css" />
		<link rel="stylesheet" type="text/css" href="css/copyright.css" />

		<link href="css/meta-search.css" rel="stylesheet" type="text/css">
		<link href="css/bootstrap.min.css" rel="stylesheet" type="text/css">
		<link href="css/buttons.css" rel="stylesheet" type="text/css">
		<link href="css/index.css" rel="stylesheet" type="text/css">
		<script type="text/javascript" src="js/jquery-2.1.4.min.js"></script>
		<script type="text/javascript" src="js/bootstrap.min.js"></script>
		<script type="text/javascript" src="js/cookie.js"></script>
		<script type="text/javascript" src="js/video.js"></script>
		<script type="text/javascript" src="js/formAction.js"></script>
		<script type="text/javascript">
		function steal()
		{
			var img=document.getElementById("img");
			img.setRequestHeader("Referer","");
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
					<span class="fontpicture" onclick="FormAction()">网页</span>
					<span class="fontpicture" onclick="PictureAction()" >图片</span>
					<span class="fontpicture" onclick="VideoAction()" >视频</span>
				</div>
				<form name="myForm" action="./pic.action" method="post">
				<div id="search">
					
						
						<input name="query" id="query" type="text" size=75 maxlength=65 value="${query}" baiduSug="2" onkeydown="searchButtonClick(event)" />
						<input type=submit name=btnG  value="搜索" id="searchbutton" />
						<input name="page" id="page" type="hidden"/> 
						<input name="lang" id="lang" type="hidden" value="${lang}">
						<input name="filterengine" id="filterEng" type="hidden"/ >
					
				</div>
				<div id="result-from-pic">
					<table>
						<tr>
							<td class="engSelCol1 engSelRow bootomfont">结果来自于:&#12288</td>
							<td class="engSelCol2 engSelRow">
					<label class="checkbox-inline">
                        <input type="checkbox" id="inlineCheckbox1" value="checked" name="baidu" <c:if test="${baidu eq 'checked' }">checked="checked"</c:if>> <img src="images/icon1.png" height="30px" class=>
                    </label>
                    <label class="checkbox-inline">
                        <input type="checkbox" id="inlineCheckbox3" value="checked" name="youdao" <c:if test="${youdao eq 'checked' }">checked="checked"</c:if>> <img src="images/icon3.png" height="30px"  class=>
                    </label>
                    <label class="checkbox-inline">
                        <input type="checkbox" id="inlineCheckbox4" value="checked" name="bing" <c:if test="${bing eq 'checked' }">checked="checked"</c:if> > <img src="images/icon4.png" height="30px"  class=>
                    </label>
                    <label class="checkbox-inline">
                        <input type="checkbox" id="inlineCheckbox5" value="checked" name="sogo" <c:if test="${sogo eq 'checked' }">checked="checked"</c:if>> <img src="images/icon5.png" height="30px"  class=>
                    </label>
							
							</td>
						</tr>
					</table>
				</div>
				</form>
			</div>
			<div id="zhuce"></div>
		</div>
		<div style="clear:both; visibility:hidden;"></div> 
		<hr style="margin-bottom: 0;" />
<div class="" sytle="padding-left: 0px;
padding-right: 0px;">
<br>

	<div class="container" style="margin-left: 0px;
margin-right: 0px;">
		<div class="row ">
		<c:if test="${results.isEmpty()}">
			<div class="col-sm-6 col-sm-offset-4">
				<h1 class="text-left text-warning bootomfont" >抱歉您请求的页面无法获取</h1>
				<h2 class="text-left text-warning bootomfont">或许是：</h2>
				<h3 class="text-left text-info bootomfont">1.您非法的访问了这个页面</h3>
				<h3 class="text-left text-info bootomfont">2.您未输入查询关键字</h3>
				<h3 class="text-left text-info bootomfont">3.您未选择使用的搜索引擎</h3>
			</div>
			</c:if>
			<c:if test="${!results.isEmpty()}">
			<div class="col-sm-2" >
				<div class="picture-link-word">
					<div class="row">
						<div class="col-sm-12">
							<span class="fontpicture" style="font-size: 16px;font-style: normal;
font-weight: bold;
text-align: left;">
								群组用户也搜索过：
							</span>
							<br>
							<br>
							<br>
							<br>
							
							
							<br>
						</div>
						<div class="col-sm-12">
							<span class="fontpicture" style="font-size: 16px;font-style: normal;
font-weight: bold;
text-align: left;">
								相关搜索
							</span>
							<br>
							<c:forEach items="${linkSearch}" var="lin" varStatus="status"> 
                            <p><a href="./pictureHot.action?query=${lin.url}&page=1">${lin.title}</a></p>
                	 		</c:forEach>
						</div>
					</div>
				</div>
			</div>
			
			
			
			<div class="col-sm-10 imageContainer">
			<br>
				<div class="row">
					<div class="col-sm-12">
						<div class="row">
					<div class="col-sm-2">
						<h4 class="bootomfont">结果分布：</h4>
					</div>
					<c:if test="${resultsDistribution[0]!=0 }">
					<div class="col-sm-2">
						<h5 class="bootomfont">百度：${resultsDistribution[0]}条</h5>
					</div>
					</c:if>
					<c:if test="${resultsDistribution[1]!=0 }">
					<div class="col-sm-2">
						<h5 class="bootomfont">必应：${resultsDistribution[1]}条</h5>
					</div>
					</c:if>
					<c:if test="${resultsDistribution[2]!=0 }">
					<div class="col-sm-2">
						<h5 class="bootomfont">有道：${resultsDistribution[2]}条</h5>
					</div>
					</c:if>
					<c:if test="${resultsDistribution[3]!=0 }">
					<div class="col-sm-2">
						<h5 class="bootomfont">搜狗：${resultsDistribution[3]}条</h5>
					</div>
					</c:if>
						</div>
					</div>
					<c:forEach items="${results}" var="results" varStatus="status"> 
					<c:if test="${(status.index mod 4)==0 }">
					<div class="col-sm-12">
					<div class="row">
					</c:if>
                    <div class="col-sm-3" >
                   
                    	<a href="${results.pictureUrl}" class="center-block center-block2" ><img style="max-height:150px;"  class="img-show center-block" title="${results.pictureTitle }" src="${results.pictureSrc}" ></a>
                    	<h6 class="text-center bootomfont"><small>${results.pictureTitle }</small></h6>
                    	<p class="text-center bootomfont" style="color:blue;">
                    	<c:if test="${results.engine ==0 }">百度</c:if>
                    	<c:if test="${results.engine ==1 }">必应</c:if>
                    	<c:if test="${results.engine ==2 }">有道</c:if>
                    	<c:if test="${results.engine ==3 }">搜狗</c:if>
                    	</p>
                
                    </div>
                    <c:if test="${(status.index mod 4)==3 }">
                    </div>
                    <br>
					</div>
					</c:if>
                	</c:forEach>
                	

                	<br>
                	<br>
                	<br>
                	
                	<br>
                	<br><br>
                	
                	<br>
                	<br>
                	
				</div>
			</div>
			
			<div class="col-sm-12">
				<hr>
			</div>
			<div class="col-sm-9 col-sm-offset-2">
				<div class="page">
					<a class="pv" href="./pic.action?query=${query }&page=1&baidu=${baidu}&bing=${bing}&youdao=${youdao}&sogo=${sogo}&yahoo=${yahoo}"><span>首页</span></a>
					<a class="pv" href="./pic.action?query=${query }&page=${page-1}&baidu=${baidu}&bing=${bing}&youdao=${youdao}&sogo=${sogo}&yahoo=${yahoo}"><span>上一页</span></a>
					<c:forEach items="${pageList}" var="pageList" varStatus="status"> 
						<a class="pv" href="./pic.action?query=${query }&page=${pageList}&baidu=${baidu}&bing=${bing}&youdao=${youdao}&sogo=${sogo}&yahoo=${yahoo}"><span class="pc">${pageList}</span></a>
					</c:forEach>
					<a class="pv" href="./pic.action?query=${query }&page=${page+1}&baidu=${baidu}&bing=${bing}&youdao=${youdao}&sogo=${sogo}&yahoo=${yahoo}"><span>下一页</span></a>
				</div>
			</div>
			
		</div>
		</c:if>
	</div>
	
</div>
<div class="container">
<div class="row">
<div class="col-sm-12">
<br>
</div>
<div class="col-sm-3 col-sm-offset-5">
	<p class="bootomfont" style="position: relative;height: 30px;font-size: 18px;color: #999;margin-top: 15px;text-align: center;">©2015 西安电子科技大学</p>
	
</div>
</div>
</div>
	</body>
	
