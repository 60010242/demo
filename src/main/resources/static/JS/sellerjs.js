$(document).ready(function(){
	if(document.getElementById("overdetail") != null){
		var count = document.getElementById("overdetail").childElementCount;
		if (count <2) document.getElementById("overdetail").style.height="60vh";
		console.log(count);
	}
	
})


/* search */
function searchFor(select){
	console.log(select.options[select.selectedIndex].value);
	document.getElementById("search-box").placeholder = "search for " + select.options[select.selectedIndex].value;
}


/* edit product */
function onaddproduct() {
	document.getElementById("overlayaddproduct").style.visibility = "visible";
}
function oneditproduct(idproduct, idcategory) {	
	console.log(idproduct +" "+ idcategory);
	$.ajax({url: "/editoneproduct/"+idproduct+"/"+idcategory, success: function(result){
	      $("#editproductpage").html(result);
	}});
	
	document.getElementById("overlayeditproduct").style.visibility = "visible";
}
function oncategory() {
	document.getElementById("overlayaddcat").style.visibility = "visible";
}

function off() {
	var overlay = document.getElementById("overlay");
	var overlaydetail = document.getElementById("overlaydetail");
	var overlayaddcat = document.getElementById("overlayaddcat");
	var overlayaddproduct = document.getElementById("overlayaddproduct");
	var overlayeditproduct = document.getElementById("overlayeditproduct");

	if(overlay != null){ overlay.style.visibility = "hidden";}
  	if(overlaydetail != null){ overlaydetail.style.visibility = "hidden";}
	if(overlayaddcat != null){ overlayaddcat.style.visibility = "hidden";}
	if(overlayaddproduct != null){ overlayaddproduct.style.visibility = "hidden";}
	if(overlayeditproduct != null){ overlayeditproduct.style.visibility = "hidden";}
}