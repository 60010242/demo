$(document).ready(function(){

	if($('#loading')!="" && $('#loading')!=null) { $('#loading').hide(); }
	if($('#resultmsg')!="" && $('#resultmsg')!=null) { $('#resultmsg').fadeOut(5000); }
})

function wait(){
	$('#loading').show();
}

function confirmandwait(confirmtxt){
	if(!confirm(confirmtxt)) return false
	$('#loading').show();
}