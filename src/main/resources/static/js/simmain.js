$(document).ready(function(){

	$('#getEpochTimeResult').click(function(){
		$.ajax({
			contentType:'application/json',
			//dataType:'',
			url:'https://ancient-crag-48261.herokuapp.com/showEpochTimeCandle',
			type: 'POST',
			//data: {"from":$('#testEpochTime').val(),
			//		"to": "" + (parseInt($('#testEpochTime').val()) + 59)
			//},
		success: function(response) {
			//$('#epochTimeResult').text(response);
			//console.log(response);
			$('#epochTimeResult').text(JSON.stringify(response));
			console.log(JSON.stringify(response));
		}
		})
		//$('#getEpochTimeResult').text($('#testEpochTime').val());
	});
	
	
});