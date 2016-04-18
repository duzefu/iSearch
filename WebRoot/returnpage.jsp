<%@page import="server.info.config.LangEnvironment.ClientType"%>
<%@page import="server.info.config.SessionAttrNames"%>
<%@page import="org.json.JSONObject"%>
<%@ page language="java"
	import="java.util.*,common.entities.searchresult.Result"
	pageEncoding="UTF-8"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="server.info.config.LangEnvironment.LangEnv"%>
<%@page import="server.info.config.PicturePath.PictureType"%>
<%@page import="server.info.config.PicturePath"%>
<%@page import="db.dbhelpler.UserInterestHelper"%>
<%@page import="server.info.entites.transactionlevel.UserInterestEntity"%>
<%@page import="server.info.config.VisibleConstant.ContentNames"%>
<%@page import="server.info.config.VisibleConstant"%>
<%@page import="db.dbhelpler.InterestVo"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@page import="common.functions.webpagediagram.PieChartPainter"%>
<%@page import="common.functions.webpagediagram.MySpriderWebPlotCall"%>
<%@page import="common.functions.webpagediagram.BarChartPainter"%>
<%@page import="org.jfree.chart.servlet.ServletUtilities"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="java.awt.Color"%>
<%@page import="server.info.entities.communication.RecommQueryAndPercent" %>
<%@page import="server.info.entites.transactionlevel.UserInterestValueEntity" %>
<%@page import="server.engine.api.EngineFactory"%>
<%@page import="server.engine.api.EngineFactory.EngineName"%>
<%@page import="java.lang.StringBuilder" %>
<%@page import="struts.actions.web.JsDataBundler" %>
<%@page import="server.info.config.LangEnvironment"%>
<%@page import="server.info.config.LangEnvironment.ClientType"%>


<%!
	private final static int MAX_INTEREST_AMOUNT=4;//显示的兴趣类别时允许的最大数目
	private final static int MAX_ABSTR_LENGTH=100;//最大显示摘要的长度
	private final static int PIE_WIDTH=550;//生成的饼图的宽
	private final static int PIE_HEIGHT=450;//生成的饼图的高
	private final static double PIE_IMG_FACTOR=0.6;//实际上在页面显示时，img元素的宽高与饼图宽高的倍数
	
	private final String limitAbstr(String orgin){
		if(null==orgin||orgin.length()<=MAX_ABSTR_LENGTH) return orgin;
		return orgin.substring(0, MAX_ABSTR_LENGTH)+"...";
	}
%>
<%
	String word=(String)request.getAttribute("query");
	if(null==word) word="";
	String encodedQuery=URLEncoder.encode(word);//由于查询词会用于拼接在成员搜索引擎结果页面的URL然后放在html元素中，查询词如果带有引号会导致错误，所以要处理
	Integer curPage=(Integer)request.getAttribute("page");
	LangEnv lang=(LangEnv) session.getAttribute(SessionAttrNames.LANG_ATTR);
	if(null==lang) lang=LangEnvironment.currentEnv(ClientType.web);
	
	List<Result> shownResult=(List<Result>)request.getAttribute("results");
	List<RecommQueryAndPercent> queryRecomm=(List<RecommQueryAndPercent>)request.getAttribute("queryRecomResult");
	List<String> relatedSearch = (List<String>)request.getAttribute("relatedSearch");

	Set<EngineName> engOfAllResult=(Set<EngineName>)request.getAttribute("engineOfResult");
	Set<EngineName> filterEng=(Set<EngineName>)request.getAttribute("filterEngines");
	if(null==filterEng){
		filterEng=new HashSet<EngineName>();
		EngineFactory.getAllEngineNames(filterEng);
	}
	
	//用户兴趣信息
	String username = (String)session.getAttribute(SessionAttrNames.USERNAME_ATTR);
	Integer userid= (Integer)request.getAttribute("userid");
	List<UserInterestEntity> interestInfo=new LinkedList<UserInterestEntity>();
	if(null==userid) UserInterestHelper.getDescSortedInterestPercent(interestInfo, username, MAX_INTEREST_AMOUNT);
	else UserInterestHelper.getDescSortedInterestPercent(interestInfo, userid, MAX_INTEREST_AMOUNT);
	
	//获取饼状图（信息检索覆盖率）所需要的数据
	List<Result> allResult=(List<Result>)request.getAttribute("allResult");
	String urlPie = "";//空字符串为异常
	String interestUrls[] = new String[3];
	List<InterestVo> intereststemp = null;
	if(allResult != null && !allResult.isEmpty())
	{
		String fileNamePie = ServletUtilities.saveChartAsPNG(PieChartPainter.GetPieChart(allResult, lang),PIE_WIDTH,PIE_HEIGHT,session);
		//ServletUtilities是面向web开发的工具类，返回一个字符串文件名,文件名自动生成，生成好的图片会自动放在服务器（tomcat）的临时文件下（temp）
		urlPie = request.getContextPath() + "/DisplayChart?filename=" + fileNamePie;
		//根据文件名去临时目录下寻找该图片，这里的/DisplayChart路径要与配置文件里用户自定义的<url-pattern>一致
		
		for(int i=1;i<4;i++){
			interestUrls[i-1] = "";	
			if(username!=null && !username.equals("")){
				intereststemp = UserInterestHelper.getUserInterestValIns(userid, i);
				if(intereststemp!=null && intereststemp.size()>0){
					String tempurl = ServletUtilities.saveChartAsPNG(MySpriderWebPlotCall.createChart(userid,i,lang),PIE_WIDTH,PIE_HEIGHT,session);
					interestUrls[i-1] = request.getContextPath() + "/DisplayChart?filename=" + tempurl;
					intereststemp = null;
				}
			}
		}
	}
	
	//获得一些常量信息，以JSON对象封装好并赋值给一个js变量，
	//用于浏览器中的脚本文件，使脚本修改页面时，能保持语言的正确性
	JSONObject jsdata=new JSONObject();
	JsDataBundler.getJsonForResultPageJsp(jsdata, lang);
%>

<html>
	<head>
		<title><%=VisibleConstant.getWebpageContent(ContentNames.page_title, lang) %></title>
		<link rel="stylesheet" type="text/css" href="css/returnpage.css" />
		<link rel="stylesheet" type="text/css" href="css/rpage-loginentry.css" />
		<link rel="stylesheet" type="text/css" href="css/login.css" />
		<link rel="stylesheet" type="text/css" href="css/style_bar.css" />
		<link rel="stylesheet" type="text/css" href="css/style_score.css" />
		<link rel="stylesheet" type="text/css" href="css/copyright.css" />
		<script type="text/javascript" src="js/jquery-1.8.3.min.js"></script>
		<script type="text/javascript" src="js/returnpage.js"></script>
		<script type="text/javascript" src="js/XMLHttp.js"></script>
		<script type="text/javascript" src="js/cookie.js"></script>
		<script type="text/javascript" src="js/userlogin.js"></script>
		<script type="text/javascript" src="js/formAction.js"></script>
<%-- 		<script type="text/javascript" src="js/userclick.js"></script>
 --%>		<script type="text/javascript" src="js/logout.js"></script>
		<script type="text/javascript" src="js/picture.js"></script>
		<script type="text/javascript" src="js/video.js"></script>
		<script type="text/javascript">
			function changeVisibility()
			{
				var state=!document.getElementById("fugai").hidden;
				if(state)
				{
					document.getElementById("fugai").hidden=state;
					document.getElementById("merge-table").hidden=state;
					document.getElementById("display_table").value="<%=VisibleConstant.getWebpageContent(ContentNames.show_button, lang)%>";
				}
				else
				{
					document.getElementById("fugai").hidden=state;
					document.getElementById("merge-table").hidden=state;
					document.getElementById("display_table").value="<%=VisibleConstant.getWebpageContent(ContentNames.hide_button, lang)%>";
				}
			}
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
		</script>
		<script type="text/javascript"> 
			var usernameinpage=null;
			if("<%=username%>" != "null")
			{
				usernameinpage="<%=username%>";
			}
			var jsonBundle=null;
			jsonBundle=JSON.parse('<%=jsdata.toString()%>');
			window.onload=function()
			{
				rightTitle();
			};
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
					<form name="myForm" action="search.action" method="get">
						<input name="query" id="query" type=text size=75 maxlength=65 value="${query}" baiduSug="2" onkeydown="searchButtonClick(event)" />
						<input type=button name=btnG id="searchbutton" value="<%=VisibleConstant.getWebpageContent(ContentNames.search_button, lang) %>" onclick="FormAction()" />
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
			<div id="zhuce"></div>
		</div>
		<div style="clear:both; visibility:hidden;"></div> 
		<hr />
		<div id="main-body">
			<div id="main-left" class="rpage-fleft-div">
				<div id="query-recomm" class="left-div">
					<table> 
						<th class="title-hint"><%=VisibleConstant.getWebpageContent(ContentNames.query_recomm_title, lang) %></th>
						<%
							if(queryRecomm!=null&&!queryRecomm.isEmpty())
							for(Iterator<RecommQueryAndPercent> it=queryRecomm.iterator();it.hasNext();)
							{
								String keyword=it.next().getQuery();
						%>
						<tr><td class="left-words">
							<a href="javascript:void(0)"  onclick="clickQuery(this)">
								<%=keyword%>
							</a>
						</td></tr>
						<%}%>
					</table>
				</div>
				<div id="relate-search" class="left-div">
					<table>
						<th class="title-hint"><%=VisibleConstant.getWebpageContent(ContentNames.relate_search_title, lang) %></th>
							<%
								if(relatedSearch!=null&&!relatedSearch.isEmpty()) 
								for(Iterator<String> it=relatedSearch.iterator();it.hasNext();)
								{
									String relWord=it.next();
							%>
							<tr><td class="left-words">
								<a href="javascript:void(0)"  onclick="clickQuery(this)"><%=relWord%></a>
							</td></tr>
							<%}%>
						</table>
				</div>
			</div>
			<div id="result-area"  class="rpage-fleft-div">
				<div id="filter-result-area">
					<%if(null!=shownResult&&!shownResult.isEmpty()){ %>
						<table>
							<th class="title-hint"><%=VisibleConstant.getWebpageContent(ContentNames.filter_title, lang) %></th>
							<tr><td>
							<% 
								List<EngineName> engForShown=new ArrayList<EngineName>(engOfAllResult);
								Collections.sort(engForShown);
								for(Iterator<EngineName> it=engForShown.iterator();it.hasNext();){
									EngineName curEng=it.next();
									if(null==curEng) continue;
									String visibleName=VisibleConstant.getStrEngNameWeb(curEng, lang);
									String engName=EngineFactory.getEnNameString(curEng);
							%>
								<label>
									<input  name="filterinput" type="checkbox" value="<%=engName%>" <%if(filterEng.contains(curEng)){%>checked="true"<%}%>><%=visibleName %>
								</label>
							<%}%>
							</td><td>
								<input type="button" name=SelectButton id="selectbutton" value="<%=VisibleConstant.getWebpageContent(ContentNames.confirm_button, lang) %>" style="height: 30px; width: 60px; text-align: center" onclick="SelectAction()" />
							</td></tr>
						</table>
					<%} %>
				</div>
				<div id="search-result">
				<%
				if(null!=shownResult&&!shownResult.isEmpty()){
					for (Iterator<Result> it=shownResult.iterator();it.hasNext();){
						Result curRes=it.next();
						if(null==curRes) continue;
				%>
					<div class="result-container">
						<h3 class="result-title">
							<a href="<%=curRes.getLink()%>" target="_blank" onclick="userclick('<%=curRes.getTitle()%>','<%=curRes.getAbstr()%>',' <%=curRes.getLink()%>','<%=curRes.getSource() %>')" ><%=curRes.getTitle()%></a>
						</h3>
						<div class="result-abstract">
							<%=limitAbstr(curRes.getAbstr())%>
						</div>
						<div class="result-url">
							<script type="text/javascript">
								document.write(getLink("<%=curRes.getLink()%>"));
							</script>
						</div>
						<div class="result-from">
							<span><%=VisibleConstant.getWebpageContent(ContentNames.result_src_prefix, lang) %>&nbsp;&nbsp;</span>
						<% 
							boolean isRecommResult=curRes.isRecommendation();
							String srcEleClass=isRecommResult?"result-from-recomm":"result-from-eng"; %>
							<span class="<%=srcEleClass%>">
								<%if(isRecommResult){ %>
									<%=VisibleConstant.getWebpageContent(ContentNames.result_recomm_src, lang) %>
								<%}else{
										Iterator<Entry<EngineName, Integer>> itSrcToPos=curRes.getOrderedSrcToPosIterator();
										if(null!=itSrcToPos){
											for(;itSrcToPos.hasNext();){
												Entry<EngineName, Integer> next=itSrcToPos.next();
												EngineName enuEng=next.getKey();
												Integer pos=next.getValue();
												if(null==pos) continue;
								%>
								<span onclick='window.open("<%=EngineFactory.getResultPageUrl(enuEng, encodedQuery)%>");'><%=VisibleConstant.getStrEngNameWeb(enuEng, lang)+"("+pos+")"%>&nbsp;</span>
								<%
											}
										}
									}
								%>
							</span>
						</div>
					</div>
				<%} 
				}else{%>
					<div class="result-no-found"><%=VisibleConstant.getWebpageContent(ContentNames.no_result_found, lang)%></div>
				<%}%>
				</div>
				<div id="page-area">
					<script language="JavaScript">
						var pg = new showPages('pg');
						pg.page = <%=curPage %>;
						pg.printHtml();
					</script>
				</div>
			</div>
			<div id="main-right" class="rpage-fleft-div">
				<div id="result-proportion" class="left-div">
					<div id ="fugai">
						<table class="pic-tab">
							<th class="title-hint"><%=VisibleConstant.getWebpageContent(ContentNames.result_distribution, lang) %></th>
							<% 
								if(!urlPie.equals("")) 
		   						{%>
		   					<tr><td>
		   						<img src="<%= urlPie %>" width="<%=(int)PIE_WIDTH*PIE_IMG_FACTOR %>" height="<%=(int)PIE_HEIGHT*PIE_IMG_FACTOR%>">
		   					</td></tr>
			   				<%}%>
			   			</table>
				   	</div>
				</div>
			<% 
				if(interestInfo != null && !interestInfo.isEmpty())
				{%>
				<div id="content_right">
					<span class="title-hint"><%=VisibleConstant.getWebpageContent(ContentNames.user_interest_title, lang) %></span>
					<%
						int barOrder=1;	
						for(Iterator<UserInterestEntity> it=interestInfo.iterator();it.hasNext();){
							UserInterestEntity entity=it.next();
							int num = interestInfo.size();
							/* if(barOrder==num)
								break; */
							if(null==entity) continue;
					%>
						<div class="progressbar" data-perc="<%=(int)(entity.getValue()* 100) %>">
							<font face="微软雅黑" size="2"><%= VisibleConstant.getStrCategoryNameWeb(entity.getCategory(), lang) %></font>
							<div class="<%="bar color"+barOrder%>"><span></span></div>
							<div class="label"><span></span></div>
						</div>
						<%
							++barOrder;
						}
				}%>
				<div id="interest-show" class="left-div">
					<div id ="interest">
						<table class="pic-tab">
						<%
						if(!interestUrls[0].equals("")){
						%>
							<th class="title-hint"><%=VisibleConstant.getWebpageContent(ContentNames.interest_distribution, lang) %>
							</th>
							<% 
								if(!interestUrls[0].equals("")) 
		   						{%>
		   					<tr><td>
		   						<img id="interestShowPic" src="<%= interestUrls[0] %>" width="<%=(int)PIE_WIDTH*PIE_IMG_FACTOR %>" height="<%=(int)PIE_HEIGHT*PIE_IMG_FACTOR%>">
		   					</td></tr>
		   					<tr></div><td>
		   					<input type=button name=btnDay1 id="interestday1button" value="<%=VisibleConstant.getWebpageContent(ContentNames.interest_day_one, lang) %>" onclick="changeInterestPic('<%=interestUrls[0] %>')" />
		   					<%
		   					if(!interestUrls[1].equals(""))
		   					{%>
		   					<input type=button name=btnDay2 id="interestday2button" value="<%=VisibleConstant.getWebpageContent(ContentNames.interest_day_two, lang) %>" onclick="changeInterestPic('<%=interestUrls[1] %>')" />
		   					<%}
		   					if(!interestUrls[2].equals(""))
		   					{%>
		   					<input type=button name=btnDay3 id="interestday3button" value="<%=VisibleConstant.getWebpageContent(ContentNames.interest_day_three, lang) %>" onclick="changeInterestPic('<%=interestUrls[2] %>')" />
		   					<%}%>
		   					</td>
		   					</tr>
			   				<%}%>
			   				<%} %>
			   			</table>
				   	</div>
				</div>
				</div>
				<script type="text/javascript">
					$(function() 
					{
						$('.progressbar').each(function(){var t = $(this),
						dataperc = t.attr('data-perc'),
						barperc = Math.round(dataperc*2.96);
						t.find('.bar').animate({width:barperc}, dataperc*25);
						t.find('.label').append('<div class="perc"></div>');
				
						function perc(){
							var length = t.find('.bar').css('width'),
							perc = Math.round(parseInt(length)/2.96),
							labelpos = (parseInt(length) + 90 - 15 - 4 - 2);
							t.find('.label').css('left', labelpos);
							t.find('.perc').text(perc+'%');
						}
						perc();
						setInterval(perc, 0); 
						});
					});
				</script>
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