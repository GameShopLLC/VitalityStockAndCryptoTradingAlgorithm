/**
 * 
 */
var i = 0;
$(document).ready(function(){
	//alert("includes and jquery working");
	setInterval(increment, 1000);
});
//$.when( $.ready ).then(function() {
//	setInterval(increment(), 1000);
//	});
function increment() {
	i++;
	$('#counter').text('' + i);
}