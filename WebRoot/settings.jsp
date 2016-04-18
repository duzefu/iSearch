<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page language="java" import="java.lang.*"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<base href="<%=basePath%>">

		<title>My JSP 'settings.jsp' starting page</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		<link href="css/settings.css" rel="stylesheet" type="text/css" />

		<script type="text/javascript" src="js/usersetting.js">
</script>

	</head>

	<body>
		<div id="return">
			<a href="search.jsp">返回首页</a>
		</div>
		<div id="ruisou"></div>
		<div id="setting">
			智搜搜索设置
		</div>
		<div class="set">
			<form action="./usersetting.action" method="post"
				style="margin-left: 200px">
				<table border="0">
					<tbody>
						<tr>
							<th width="155">
								<font size="2">搜索引擎选择</font>
							</th>
							<td width="505">
								<font size="2">选择喜欢的搜索引擎</font>
							</td>
						</tr>
						<tr>
							<td height="35">
								&nbsp;
							</td>
							<td id="SearchEngine">
							</td>
						</tr>
						<tr>
							<th>
								<font size="2">搜索框提示</font>
							</th>
							<td width="505">
								<font size="2">是否希望在搜索时显示搜索框提示</font>
							</td>
						</tr>
						<tr>
							<td height="35">
								&nbsp;
							</td>
							<td>
								<input type="radio" checked="" name="s1" id="s1_1">
								<label for="s1_1">
									<font size="2">显示</font>
								</label>

								<input type="radio" name="s1" id="s1_2">
								<label for="s1_2">
									<font size="2">不显示</font>
								</label>
							</td>
						</tr>
						<tr>
							<th>
								<font size="2">搜索语言范围</font>
							</th>
							<td>
								<font size="2">设定您所要搜索的网页内容的语言</font>
							</td>

						</tr>
						<tr>
							<td height="35">
								&nbsp;
							</td>
							<td>
								<input type="radio" checked="" name="s2" id="s2_1">
								<label for="s2_1">
									<font size="2">显示</font>
								</label>

								<input type="radio" name="s2" id="s2_2">
								<label for="s2_2">
									<font size="2">不显示</font>
								</label>
							</td>
						</tr>
						<tr>
							<th>
								<font size="2">搜索结果显示条数</font>
							</th>
							<td>
								<font size="2">设定您希望搜索结果显示的条数</font>
							</td>
						</tr>
						<tr>
							<td height="35">
								&nbsp;
							</td>
							<td>
								<input type="radio" name="SL" id="SL_0" value="0" checked="">
								<label for="SL_0">
									<font size="2">全部 语言</font>
								</label>
								<input type="radio" name="SL" id="SL_1" value="1">
								<label for="SL_1">
									<font size="2">仅简体中文</font>

								</label>
								<input type="radio" name="SL" id="SL_2" value="2">
								<label for="SL_2">
									<font size="2">仅繁体中文</font>

								</label>
							</td>
						</tr>
						<tr>
							<th>
								<font size="2">输入法</font>
							</th>
							<td>
								<font size="2">设定在睿搜搜索页面的输入法</font>
							</td>
						</tr>
						<tr>
							<td height="35">
								&nbsp;
							</td>
							<td>
								<select name="ime">
									<option value="1">
										<font size="2">手写</font>
									</option>
									<option value="2">
										<font size="2">拼音</font>

									</option>
									<option value="0" selected="">
										<font size="2">关闭</font>
									</option>
								</select>
							</td>
						</tr>
						<tr>
							<td width="350" align=center colspan="2">
								<input type="submit" value="保存设置"
									style="width: 80px; height: 30px; background-color: #00F; color: #FFF; font-weight: bold; margin-left: 80px; margin-right: 50px; float: left">
								<input type="reset" value="恢复默认"
									style="width: 80px; height: 30px; background-color: #00F; color: #FFF; font-weight: bold">
							</td>
						</tr>
					</tbody>
				</table>
				<!--  <hr style="border:solid #FFC" border-width:3px"> -->
				<input type="hidden" name="SU">
			</form>

		</div>
		<div id="copyright">
			&copy;2012 西安电子科技大学
		</div>
	</body>
</html>
