$(document).ready(function(){

	$('#getEpochTimeResult').click(function(){
		$.ajax({
			contentType:'application/json',
			dataType:'application/json',
			url:'https://ancient-crag-48261.herokuapp.com/showEpochTimeCandle',
			type: 'POST',
			data: {"from":$('#testEpochTime').val(),
					"to": "" + (parseInt($('#testEpochTime').val()) + 1)
			},
		success: function(response) {
			$('#epochTimeResult').text(JSON.stringify(response));
			console.log(JSON.stringify(response));
		}
		})
		//$('#getEpochTimeResult').text($('#testEpochTime').val());
	});
	
	
});