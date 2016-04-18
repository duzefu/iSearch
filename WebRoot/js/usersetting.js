function go()
{
	var check="";
	var checkbox = document.getElementsByName("check");
	alert(checkbox.length);
	for(var i=0;i<checkbox.length;i++)
	{
        if(checkbox[i].checked == true)
        {
        	check=check.contact("&").contact(checkbox[i].value);
        	
        }
	}
	alert(check);
	var xmlhttp;
	if (window.XMLHttpRequest)
	{// code for IE7+, Firefox, Chrome, Opera, Safari
		xmlhttp=new XMLHttpRequest();
	}
	else
	{// code for IE6, IE5
		xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	var url = "./usersetting.action";
	xmlhttp.open("POST",url,true);
	xmlhttp.send();
	xmlhttp.onreadystatechange=function()
	{
		if (xmlhttp.readyState==4 && xmlhttp.status==200)
		{
//			document.getElementById("hotwords").innerHTML=xmlhttp.responseText;
			//alert("ok");
		}
	};
}



window.onload=function()
{
	var xmlhttp;
	if (window.XMLHttpRequest)
	{// code for IE7+, Firefox, Chrome, Opera, Safari
		xmlhttp=new XMLHttpRequest();
	}
	else
	{// code for IE6, IE5
		xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	var url = "./getsearchengine.action";
	xmlhttp.open("POST",url,true);
	xmlhttp.send();
	xmlhttp.onreadystatechange=function()
	{
		if (xmlhttp.readyState==4 && xmlhttp.status==200)
		{
			document.getElementById("SearchEngine").innerHTML=xmlhttp.responseText;
			//alert("ok");
		}
	};
}