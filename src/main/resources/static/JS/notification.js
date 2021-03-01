$(document).ready(function(){
	$('bodynotification hr').last().remove();
})
function showNoti(){
	if(document.getElementById("notification").style.visibility == "visible"){
		document.getElementById("notification").style.visibility = "hidden";
	}
	else{
		document.getElementById("notification").style.visibility = "visible";
	}
}