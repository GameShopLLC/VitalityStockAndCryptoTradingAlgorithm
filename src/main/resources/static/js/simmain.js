$(document).ready(function(){

	$('#getEpochTimeResult').click(function(){
		$.ajax1({
			url:'https://ancient-crag-48261.herokuapp.com/startAlgorithm',
			type: 'POST',
			data: {"from":$('#testEpochTime').val(),
					"to":$('#testEpochTime').val()
			},
		success: function(response) {
			$('#epochTimeResult').text(response.open);
		}
		})
		//$('#getEpochTimeResult').text($('#testEpochTime').val());
	});
	
	
});