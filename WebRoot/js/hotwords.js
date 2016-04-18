function getHotwords(){
	
	if (getCookieValue() ==""||getCookieValue() ==null) 
	{
		setCookie();
	}
	var xmlhttp;
	if (window.XMLHttpRequest)
	{// code for IE7+, Firefox, Chrome, Opera, Safari
		xmlhttp=new XMLHttpRequest();
	}
	else
	{// code for IE6, IE5
		xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	xmlhttp.open("POST","./realhot.action",true);
	xmlhttp.send();
	xmlhttp.onreadystatechange=function()
	{
		if (xmlhttp.readyState==4 && xmlhttp.status==200)
		{
			var hwjsonarr = eval ("(" + xmlhttp.responseText + ")");
			insertHotwords(hwjsonarr);
		}
	};
}

function insertHotwords(jsonArr){
	
	if(null!=jsonArr){
		var tableobj=document.getElementById("hotwords-table");
		var rowNo=0, colNo=0;
		var row=tableobj.insertRow(tableobj.rows.length);
		row.className="hotword-row";
		for(var i=0;i<jsonArr.length;++i){
			var content=jsonArr[i].hotword;
			if(null==content||content=="") continue;
			var cell=row.insertCell(row.cells.length);
			cell.className="hotword-cell";
			cell.innerHTML="<a href='javascript:void(0)' onclick='hotSearch(this)'>"+content+"</a>";
			if(++colNo>=5){
				if(++rowNo>=2) break;
				colNo=0;
				row=tableobj.insertRow(tableobj.rows.length);
				row.className="hotword-row";
			}
		}
	}
}

//关于热点的连接搜索实现（by许静20121112）
function hotSearch(o) {
	//alert("至该函数！");
	if (getCookieValue() == ""
		|| getCookieValue() == null) {
		setCookie();
	}
	document.getElementById("page").value = 1;//首页提交新请求时需要将page置为1
	// Chrome两种属性都支持, Firefox在某些doctype下无法正确获取innerText，所以Firefox采用textContent,IE没有textContent属性
	var result = o.textContent ? o.textContent : o.innerText;
	//alert(result);
	document.getElementById("query").value = result.toString();
	document.myForm.submit();

}