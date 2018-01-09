/**
 * 
 */
//var i = 0;
$(document).ready(function(){
	//alert("includes and jquery working");
	setInterval(ajaxCall, 1000);
});
//$.when( $.ready ).then(function() {
//	setInterval(increment(), 1000);
//	});

function ajaxCall() {
	request = $.ajax({
		url: 'http://localhost:8080/testajax',
		method: 'GET',
		success: function(data) {
			console.log("W00T");
			$('#counter').text(data);
		},
		error: function(data){
		console.log("FUCK");	
		}
	});
	console.log("In Ajax Call");
}
//function increment() {
//	i++;
//	$('#counter').text('' + i);
//}