//该函数是只要涉及Ajax就必须要用到的，目的是为了获得XMLHttpRequest对象

function createXMLHttpRequest(){
	var xmlHttp = null;
	try{
		//Firefox, Opera 8.0+, Safari
		xmlHttp = new XMLHttpRequest();
	}
	catch(e){
		//IE7.0以下的浏览器以ActiveX组件的方式来创建XMLHttpRequest
		var MSXML = ['MSXML2.XMLHTTP.6.0','MSXML2.XMLHTTP.5.0',
		             'MSXML2.XMLHTTP.4.0','MSXML2.XMLHTTP.3.0',
		             'MSXML2.XMLHTTP','Microsoft.XMLHTTP'];
		for(var i=0;i<MSXML.length;i++){
			try{
				xmlHttp = new ActiveXObject(MSXML[i]);
				break;
			}catch(e){}
		}
	}
	return xmlHttp;
}
