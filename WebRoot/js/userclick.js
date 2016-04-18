//利用AJAX记录用户点击，并将用户点击过的部分变红(待实现) By邹延鑫20130306
function userclick(title, abstr, address, sources) {
	if (getCookieValue("Cookieid") == "" || getCookieValue("Cookieid") == null) {
		setCookie();
	}

	var xhr;
	if (window.XMLHttpRequest)
	  {// code for IE7+, Firefox, Chrome, Opera, Safari
	  xhr=new XMLHttpRequest();
	  }
	else
	  {// code for IE6, IE5
	  xhr=new ActiveXObject("Microsoft.XMLHTTP");
	  }

	//var xhr = createXMLHttpRequest();

	xhr.onreadystatechange = function() {
		if (xhr.readyState == 4 && (xhr.status == 200 || xhr.status == 304)) {
			var ret = xhr.responseText;
			console.log(ret);
			ret = eval("(" + ret + ")");
			console.log(ret.flag);
			if (ret.flag == "true") {
				console.log(ret.url);
				//window.open(ret.url);
			}

		}
	};
	var url="cookieid=" + getCookieValue("Cookieid") + "&query="
	+ document.getElementById("query").value + "&title=" + arguments[0]
	+ "&abstr=" + arguments[1] + "&clickaddress=" + encodeURI(address)
	+ "&sources=" + arguments[3];
	xhr.open("post", "./userclick.action", false);
	xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	xhr.send(url);
}

// var xmlhttp;
// if (window.XMLHttpRequest)
// {// code for IE7+, Firefox, Chrome, Opera, Safari
// xmlhttp=new XMLHttpRequest();
// }
// else
// {// code for IE6, IE5
// xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
// }
// var url= "./userclick.action?query=".concat(query)
// .concat("&title=").concat(arguments[0])
// .concat("&abstr=").concat(arguments[1])
// .concat("&clickaddress=").concat(arguments[2])
// .concat("&cookieid=".concat(cookieid));
//	
// xmlhttp.open("POST",url,false);
//	
// xmlhttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
//
// xmlhttp.send();

// xmlhttp.onreadystatechange=function()
// {
// if (xmlhttp.readyState==4 && xmlhttp.status==200)
// {
// //重写方法，将点击过的部分变成红色
// //document.getElementById("hotwords").innerHTML=xmlhttp.responseText;
//		}
//	};
//}
