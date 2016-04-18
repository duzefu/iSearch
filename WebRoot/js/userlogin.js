function rightTitle() {
	
	if (usernameinpage == null) {
		document.getElementById("zhuce").innerHTML = "<a href='./register.jsp'>"+jsonBundle.regentry+"</a> <a href='javascript:login()'>"+jsonBundle.loginentry+"</a>";
	} else {
		document.getElementById("zhuce").innerHTML= "<span id=\"userinfo\" style=\"color=#333;\">"+jsonBundle.welcomeinfo+usernameinpage+"</span>"
		+ "&nbsp;&nbsp;&nbsp;&nbsp;" /*+ "<a href='./settings.jsp'>"+jsonBundle.settingentry+"</a>" */
		+ "<a href='javascript:logout("+"\""+usernameinpage+"\""+")'>"+jsonBundle.logoutentry+"</a>";
	}
}

function tijiao(){

	var flag = true;
	var strUserName = document.login.username.value;	
	if(strUserName == "")	{		
		document.getElementById("result-hint").innerHTML=jsonBundle.emptynameprompt;
		flag = false;
		document.login.username.focus();	
		return false;	
	}	
    
	var strUserpassword = document.login.password.value;	
	if(strUserpassword == ""){		
		document.getElementById("result-hint").innerHTML=jsonBundle.emptypasswdprompt;
		flag = false;	
		document.login.password.focus();		
		return false;	
	}
	
	if(flag){
		var xhr = createXMLHttpRequest();
		xhr.onreadystatechange=function(){
			if(xhr.readyState == 4 && (xhr.status==200 || xhr.status==304)){
				var ret = xhr.responseText;
				ret=eval("("+ret+")");
				if(ret.result=="success"){
					var zhucheDiv=document.getElementById("zhuce");
					if(null==zhucheDiv) window.location.href="./search.action";
					else{ 
						zhucheDiv.innerHTML="<span id=\"userinfo\" style=\"color=#333;\">"+ret.welcomeinfo+strUserName+"</span>"
															+ "<a href='javascript:logout("+"\""+strUserName+"\""+")'>"+ret.logoutentry+"</a>";
						document.getElementById("logindiv").style.display = 'none';
						document.getElementById("mask").style.display = 'none';
					}
				}
				else{
					document.getElementById("result-hint").innerHTML=ret.reason;
					document.getElementById("username").value="";
					document.getElementById("password").value="";
					document.login.username.focus();
				}
			}
		};
		xhr.open("post","userlogin.action",false);
		xhr.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
		xhr.send("username="+strUserName+"&password="+strUserpassword);
	}
}

function login() {
	
	var mask=document.getElementById("mask");
	mask.style.height = window.screen.height  + 'px';
	mask.style.width = window.screen.width  + 'px';
	mask.style.display = 'block';
	var logindiv=document.getElementById("logindiv");
	logindiv.style.display = 'block';
}


function closeLoginForm() {
	document.getElementById("logindiv").style.display = 'none';
	document.getElementById("mask").style.display = 'none';
	document.getElementById("result-hint").innerHTML='&nbsp;';
}

function loginbuttonOnClick(evt)   {   
	evt = evt||window.event; //兼容IE和Firefox获得keyBoardEvent对象
	 var key = evt.keyCode || evt.charCode;//兼容IE和Firefox获得keyBoardEvent对象的键值 
	 
	     if(key == 13){   
	    	 var button =document.getElementById("loginbutton");  
	 		button.click();   	 
	}   
	 
}

