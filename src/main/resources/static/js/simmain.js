$(document).ready(function(){

	$('#getEpochTimeResult').click(function(){
		$.ajax({
			url:'https://ancient-crag-48261.herokuapp.com/showEpochTimeCandle',
			type: 'POST',
			data: {"from":$('#testEpochTime').val(),
					"to":$('#testEpochTime').val()
			},
		success: function(response) {
			$('#epochTimeResult').text(response.open);
			console.log(response);
		}
		})
		//$('#getEpochTimeResult').text($('#testEpochTime').val());
	});
	
	
});