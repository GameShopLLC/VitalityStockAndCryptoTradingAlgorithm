$(document).ready(function(){

	$('#getEpochTimeResult').click(function(){
		$.ajax({
			contentType:'application/json',
			dataType:'application/json',
			url:'https://ancient-crag-48261.herokuapp.com/showEpochTimeCandle',
			type: 'POST',
			data: {"from":"2018-03-25T11:07:04Z",
					"to":"2018-03-25T11:09:04Z"
			},
		success: function(response) {
			$('#epochTimeResult').text(JSON.stringify(response));
			console.log(response);
//			$('#epochTimeResult').text(JSON.stringify(response));
//			console.log(JSON.stringify(response));
		}
		})
		//$('#getEpochTimeResult').text($('#testEpochTime').val());
	});
	
	
});