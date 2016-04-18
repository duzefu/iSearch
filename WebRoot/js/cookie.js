//设置cookie，cookie由getTime()+5位随机数字组成 By邹延鑫20130306
function setCookie() 
{ 
	number = Math.random()*(100000-10000)+10000;
	number=Math.round(number);
	date= new Date();
	Cookieid = date.getTime();
	Cookieid = Cookieid.toString().concat(number);
	
	expireDate = new Date();
	expireDate.setMonth(expireDate.getMonth()+12*10);
	
	document.cookie = "iSearchCookie="+Cookieid+";expires="+expireDate.toUTCString();
}

//获取cookie(借鉴了Discuz!的写法) By邹延鑫20130306
function getCookieValue()
{
	if(document.cookie.length == 0)
	{
		setCookie();
	}
	var cookie_start = document.cookie.indexOf("iSearchCookie"); 
	var cookie_end = document.cookie.indexOf(";", cookie_start); 
	return cookie_start == -1 ? '' : unescape(document.cookie.substring(cookie_start + name.length + 1, (cookie_end > cookie_start ? cookie_end : document.cookie.length))); 
}