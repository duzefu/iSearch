function FormAction(){
	if (getCookieValue() ==""||getCookieValue() ==null) 
	{
        setCookie();
	}
	
	document.forms.myForm.action="search.action";
	var elem= document.getElementById("page");
	if(null!=elem) elem.value = 1;//首页提交新请求时需要将page置为1
	elem=document.getElementById("filterEng")
	if(null!=elem) elem.value="";
	document.forms.myForm.submit();
}

function changeInterestPic(fileName){
	
	var elem= document.getElementById("interestShowPic");
	elem.src = fileName;
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

function SelectAction(){
	
	var filterRes="";
	var filterInputs=document.getElementsByName("filterinput");
	if(null!=filterInputs){
		for(var i=0;i<filterInputs.length;++i){
			var input=filterInputs[i];
			if(null==input||input.value==""||!input.checked) continue;
			filterRes+=input.value+",";
		}
	}
	if (getCookieValue() ==""||getCookieValue() ==null) 
	{
        setCookie();
	}
	
	var elem= document.getElementById("filterEng");
	if(null!=elem) elem.value=filterRes;
	elem=document.getElementById("page");
	if(null!=elem) elem.value = 1;
	document.myForm.submit();
}

function pageAction(pageNo){
	
	if (isNaN(parseInt(pageNo))) pageNo = 1;
	if (getCookieValue() ==""||getCookieValue() ==null) 
	{
        setCookie();
	}
	document.forms.myForm.action="search.action";
	var elem= document.getElementById("page");
	if(null!=elem) elem.value = pageNo;
	document.forms.myForm.submit();
}