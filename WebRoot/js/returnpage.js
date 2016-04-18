function showPages(name) { // 初始化属性
	this.name = name; // 对象名称
	this.page = 1; // 当前页数
	this.argName = 'page'; // 参数名
	this.showTimes = 1; // 打印次数
	this.cookieid = getCookieValue("Cookieid");
	this.query = document.getElementById("query").value;
	this.isFirstPage=false;
	this.firstShown=1;//第一个要显示的页号
	this.lastShown=10;//最后一个要显示的页号
}

showPages.prototype.printHtml = function() { // 显示html代码
	this.checkPages();
	this.showTimes += 1;
	document.write('<div id="pages_' + this.name + '_' + this.showTimes	+ '" class="pages"></div>');
	document.getElementById('pages_' + this.name + '_' + this.showTimes).innerHTML = this.createPageHtml();
}

showPages.prototype.checkPages = function() { // 进行当前页数和总页数的验证
	if (isNaN(parseInt(this.page))) this.page = 1;
	if (this.page < 1) this.page = 1;
	this.page = parseInt(this.page);
	if(this.page==1) this.isFirstPage=true;
	if(this.page>6){
		this.firstShown=this.page-4;
		this.lastShown=this.firstShown+9;
	}
}

showPages.prototype.createPageHtml = function() {
	var ret="";
	if(!this.isFirstPage){
		ret+="<a class=\"page-jump\" href=\"javascript:void(0)\" onclick=\"pageAction(\'"+(this.page-1)+"\')\">"+jsonBundle.lastpage+"</a>";
	}
	for(var i=this.firstShown;i<=this.lastShown;++i){
		if(i==this.page){
			ret+="<span class=\"page-elem\">"+i+"</span>";
		}else{
			ret+="<a class=\"page-elem\" href=\"javascript:void(0)\" onclick=\"pageAction(\'"+i+"\')\">"+i+"</a>";
		}
	}
	ret+="<a class=\"page-jump\" href=\"javascript:void(0)\" onclick=\"pageAction(\'"+(this.page+1)+"\')\">"+jsonBundle.nextpage+"</a>";
	return ret;
}

showPages.prototype.createHtml = function(mode) { // 生成html代码
	
	var strHtml = '', prevPage = this.page - 1, nextPage = this.page + 1;
	if (mode == '' || typeof (mode) == 'undefined')
		mode = 1;
	switch (mode) {
	case 1: // 模式1 (10页缩略,首页,前页,后页,尾页)
		// strHtml += '<span class="count">Pages: ' + this.page + ' / ' +
		// this.pageCount + '</span>';
		strHtml += '<span class="number">';
		if (prevPage < 1) {
			strHtml += '<span title="First Page">&#171;</span>';
			strHtml += '<span title="Prev Page">&#139;</span>';
		} else {
			strHtml += '<span title="First Page"><a href="javascript:'
					+ this.name + '.toPage(1);">&#171;</a></span>';
			strHtml += '<span title="Prev Page"><a href="javascript:'
					+ this.name + '.toPage(' + prevPage
					+ ');">&#139;</a></span>';
		}
		if (this.page % 10 == 0) {
			var startPage = this.page - 9;
		} else {
			var startPage = this.page - this.page % 10 + 1;
		}
		if (startPage > 10)
			strHtml += '<span title="Prev 10 Pages"><a href="javascript:'
					+ this.name + '.toPage(' + (startPage - 1)
					+ ');">...</a></span>';
		for (var i = startPage; i < startPage + 10; i++) {
			if (i > this.pageCount)
				break;
			if (i == this.page) {
				strHtml += '<span title="Page ' + i + '">[' + i + ']</span>';
			} else {
				strHtml += '<span title="Page ' + i + '"><a href="javascript:'
						+ this.name + '.toPage(' + i + ');">[' + i
						+ ']</a></span>';
			}
		}
		if (this.pageCount >= startPage + 10)
			strHtml += '<span title="Next 10 Pages"><a href="javascript:'
					+ this.name + '.toPage(' + (startPage + 10)
					+ ');">...</a></span>';
		if (nextPage > this.pageCount) {
			strHtml += '<span title="Next Page">&#155;</span>';
			strHtml += '<span title="Last Page">&#187;</span>';
		} else {
			strHtml += '<span title="Next Page"><a href="javascript:'
					+ this.name + '.toPage(' + nextPage
					+ ');">&#155;</a></span>';
			strHtml += '<span title="Last Page"><a href="javascript:'
					+ this.name + '.toPage(' + this.pageCount
					+ ');">&#187;</a></span>';
		}
		strHtml += '</span><br />';
		break;
	}
	return strHtml;
}

showPages.prototype.createUrl = function(page) { // 生成页面跳转url
	if (isNaN(parseInt(page)))
		page = 1;
	if (page < 1)
		page = 1;
	if (page > this.pageCount)
		page = this.pageCount;
	var url = location.protocol + '//' + location.host + location.pathname;
	// alert('url:'+url);
	var args = location.search;
	// alert('args:'+args);
	var reg = new RegExp('([\?&]?)' + this.argName + '=[^&]*[&$]?', 'gi');
	args = args.replace(reg, '$1');
	// alert('new args:'+args);
	if (args == '' || args == null) {
		args += '?' + this.argName + '=' + page;
	} else if (args.substr(args.length - 1, 1) == '?'
			|| args.substr(args.length - 1, 1) == '&') {
		args += this.argName + '=' + page;
	} else {
		args += '&' + this.argName + '=' + page;
	}

	var selectEngine = document.getElementById("selectEngine");
	var text = selectEngine.innerHTML;
	if (text == null)
		text = "all";
	// if(args.match(/cookieid/)){
	if (args.match(/cookieid/) && args.match(/query/)) {
		url = url + args;
		// alert(url);
	} else {
		url = url + args;
		// url = url + args + '&' + 'cookieid' + '=' + this.cookieid + '&'+
		// 'query' + '=' + this.query;
		// alert(url);
	}
	if (url.indexOf("&selectedEngineName=") < 0) {
		url += "&selectedEngineName=" + text;
	}
	else {
		var checked="&selectedEngineName=(all)|(百度)|(有道)|(搜搜)|(搜狗)|(必应)|(即刻)";
		var exp=new RegExp(checked);
		url.replace(exp,"&selectedEngineName="+text);
	}
	return url;
	}

showPages.prototype.toPage = function(page) { // 页面跳转
	var turnTo = 1;
	if (typeof (page) == 'object') {
		turnTo = page.options[page.selectedIndex].value;
	} else {
		turnTo = page;
	}
	self.location.href = this.createUrl(turnTo);
}

showPages.prototype.formatInputPage = function(e) { // 限定输入页数格式
	var ie = navigator.appName == "Microsoft Internet Explorer" ? true : false;
	if (!ie)
		var key = e.which;
	else
		var key = event.keyCode;
	if (key == 8 || key == 46 || (key >= 48 && key <= 57))
		return true;
	return false;
}

function clickQuery(o) {
	
	if (getCookieValue() == "" || getCookieValue() == null) {
		setCookie();
	}
	document.getElementById("page").value = 1;
	// Chrome两种属性都支持, Firefox在某些doctype下无法正确获取innerText，所以Firefox采用textContent,IE没有textContent属性
	var result = o.textContent ? o.textContent : o.innerText;
	document.getElementById("query").value = result.toString();
	document.getElementById("filterEng").value = "";
	document.myForm.submit();
}