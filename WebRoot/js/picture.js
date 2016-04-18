function PictureAction(){
	if (getCookieValue() ==""||getCookieValue() ==null) 
	{
        setCookie();
	}
	
	document.forms.myForm.action="pic.action";
	document.getElementById("page").value = 1;//首页提交新请求时需要将page置为1
	document.myForm.submit();
}

function addpictureURL(picture_id,picture_url){
	
	document.getElementById(picture_id).src = picture_url;//将图片的的地址加入
	
} 

function searchButtonClick(evt){
	/*if (event.keyCode == 13) {
			var button =document.getElementById("searchbutton"); 
			button.click();
		}*/
	evt = evt||window.event; //兼容IE和Firefox获得keyBoardEvent对象
	 var key = evt.keyCode || evt.charCode;//兼容IE和Firefox获得keyBoardEvent对象的键值 
	 
	     if(key == 13){   
	    	 var button =document.getElementById("searchbutton");  
	 		button.click();  
	}   
}