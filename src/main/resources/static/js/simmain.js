$(document).ready(function(){

	setInterval(updateStatus, 1000);
	$('#getEpochTimeResult').click(function(){
		$.ajax({
			url:'https://ancient-crag-48261.herokuapp.com/runSimulation',
			type: 'POST',
			success: function(response){
				console.log(response);
			}
		})
//		$.ajax({
//			contentType:'application/json',
//			dataType:'json',
//			url:'https://ancient-crag-48261.herokuapp.com/showEpochTimeCandle',
//			type: 'POST',
//			data: JSON.stringify({"from":"2018-03-25T11:07:04Z",
//					"to":"2018-03-25T11:09:04Z"
//			}),
//		success: function(response) {
//			$('#epochTimeResult').text(JSON.stringify(response));
//			console.log(response);
////			$('#epochTimeResult').text(JSON.stringify(response));
////			console.log(JSON.stringify(response));
//		}
//		})
		//$('#getEpochTimeResult').text($('#testEpochTime').val());
	});
	
	
});

function updateStatus(){
	$.ajax({
		url:'https://ancient-crag-48261.herokuapp.com/simulationStatusReport',
		//url:'https://ancient-crag-48261.herokuapp.com/gdaxData',
		
		type: 'GET',
		success: function(response){
			console.log(response);
			$('#epochTimeResult').text(response);
		}
	})
}