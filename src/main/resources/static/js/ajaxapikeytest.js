/** 
 * 
 */
//document.domain='api.gdax.com';
//var isAlgRunning = false;
$(document).ready(function(){
	//alert("includes and jquery working");
	$('.detailsTab').hide();
	$.ajax({
		url: 'https://ancient-crag-48261.herokuapp.com/isAlgorithmRunning',
		type: 'GET',
		success: function(response) {
			//$('#counter').text(response);
			//$('#is').text(response);
			if (response === 'false') {
			$('#counter').text('Algorithm not running');
			}
			else if (response === 'true') {
				$('#counter').text('Algorithm running');
			}
			console.log(response);
			//console.log("SHOWING TICKER DATA")
		}
		
	})
	
	$('#mainButton').click(function(){
		$('.mainTab').show();
		$('.detailsTab').hide();
	})
	
	$('#detailsButton').click(function(){
		$('.mainTab').hide();
		$('.detailsTab').show();
	})
	
	
	
	$('#startAlgorithm').click(function(){
		//e.preventDefault();
		$.ajax({
			url: 'https://ancient-crag-48261.herokuapp.com/startAlgorithm',
			type: 'POST',
			success: function(response) {
				$('#counter').text('Algorithm running');
				console.log(response);
				console.log("INSIDE AJAX BUTTON");
			}
			
		})
	})
	
	$('#rallyAlgorithm').click(function(){
		//e.preventDefault();
		$.ajax({
			url: 'https://ancient-crag-48261.herokuapp.com/rallyAlgorithm',
			type: 'POST',
			success: function(response) {
				$('#counter').text('Algorithm running');
				console.log(response);
				console.log("INSIDE AJAX BUTTON");
			}
			
		})
	})
	
	
	setInterval(updateDetails, 1000);
	setInterval(updateAlgStatus, 1000);
	//Decouple business logic from view logic.
	//setInterval(ajaxCall, 1000);
	setInterval(statusCall, 1000);
	
	
	
	//ajaxCall();
});

function updateDetails(){
	$.ajax({
		url: 'https://ancient-crag-48261.herokuapp.com/showThreads',
		type: 'GET',
		contentType: 'application/json',
		success: function(response) {
			//var result = $.parseJSON(response);
			$('#threadList').empty();
			console.log(response);
			for (var i = 0; i < response.length; i++){
				$('#threadList').append('<div id=\"thread-' + i + '\">' 
						+ i + ' ' + response[i].lifeTimeState + ' ' 
						+ response[i].buyProcessState
						+ ' Group ' + response[i].name
						+ ' USD: $' + response[i].usd 
						+ ' LTC: ' + response[i].ltc 
						+ ' Profit: $' + response[i].profit
						+ ' RequestBuyPrice: $' + response[i].requestBuyPrice 
						+ ' RequestSellPrice: $'+ response[i].requestSellPrice +'</div>');
			}
		}
	})
}
function updateAlgStatus() {
	//$('#counter').text(running);
	var counterText = '';
	 $.ajax({
		url: 'https://ancient-crag-48261.herokuapp.com/showTickerData',
		type: 'GET',
		success: function(response) {
			//$('#counter').text(response);
			$('#tickerData').text(response);
			console.log(response);
			console.log("SHOWING TICKER DATA")
		}
		
	})
	
	 $.ajax({
		url: 'https://ancient-crag-48261.herokuapp.com/showPrice',
		type: 'GET',
		success: function(response) {
			//counterText += '\n' + response;
			$('#currentPrice').text(response);
			console.log(response);
			console.log("SHOWING TICKER DATA")
		}
		
	})
	
	 $.ajax({
		url: 'https://ancient-crag-48261.herokuapp.com/showCarrotData',
		type: 'GET',
		success: function(response) {
			//counterText += '\n' + response;
			$('#priceDataList').text(response);
			console.log(response);
			console.log("SHOWING TICKER DATA")
		}
		
	})
	
	
}

function ajaxCall() {
	request = $.ajax({
		//url: 'https://localhost:8080/testbackendrequest',//'https://api.gdax.com/products',
		url: 'https://ancient-crag-48261.herokuapp.com/testbackendrequest',//'https://api.gdax.com/products',
		
		//dataType: 'application/json',
		//format:'json',
		method: 'GET',
//		beforeSend : function(req){
//			req.setRequestHeader("Access-Control-Request-Method", "GET");
//			//req.setRequestHeader("Origin", "107.204.62.253");
//			req.setRequestHeader("Access-Control-Allow-Headers", "CB-ACCESS-KEY, CB-ACCESS-SIGN, CB-ACCESS-TIMESTAMP,CB-ACCESS-PASSPHRASE");
//		       
//		},
//		a
//		headers: {
//			
//			//DO INLINE TEST FIRST
//			//'CB-ACCESS-KEY': bde7c18ff32b42bcff6d27afae438b3b,
//			//'CB-ACCESS-SIGN':
//			//'Access-Control-Request-Method': 'GET',
//			//'Access-Control-Allow-Headers': 'CB-ACCESS-KEY, CB-ACCESS-SIGN, CB-ACCESS-TIMESTAMP,CB-ACCESS-PASSPHRASE',
//			'CB-ACCESS-KEY' : key, //REPLACE WITH INLINE
//			'CB-ACCESS-SIGN' : signature, //gethash()
//			'CB-ACCESS-TIMESTAMP': timestamp,
//			'CB-ACCESS-PASSPHRASE': passphrase
//		},
		contentType: 'application/json',
//		xhrFields: {
//            'withCredentials': true
//    },
		//crossDomain: true,
		success: function(data) {
			console.log("W00T");
			console.log("THE FUCK");
			console.log(data);
//	        var result = $.parseJSON(data);
//			$('#counter').text('The current price of bitcoin is $' + result.price);
		},
		error: function(){
		console.log("FUCK THIS");	
		}
	}).then(function(data){
		var result = $.parseJSON(data);
		//$('#counter').text('The current price of bitcoin is $' + result.price);
		//$('#counter').text('Your current accounts locally are' + JSON.stringify(result));
		//$('#counter').text('Your current accounts locally are' + result[0].profile_id);
		//$('#counter').text('Your current accounts on GDAX are' + JSON.stringify(result.price));
		$('#counter').text('The current price of Litecoin is' + JSON.stringify(result));
		priceReadResultCall(JSON.stringify(result));
	});
	console.log("In Ajax Call");
}

function priceReadResultCall(priceData) {
	request = $.ajax({
		url: 'https://ancient-crag-48261.herokuapp.com/priceReadResult',
		method: 'POST',
		data: priceData,
		contentType: 'application/json',//'application/x-www-form-urlencoded; charset=UTF-8',//'application/json',
		dataType: 'text',
		success: function(response) {
			console.log("W00T IN RESPONSE");
			//console.log("THE FUCK");
			console.log(response);
		},
		error: function(){
		console.log("FUCK THIS RESPONSE");	
		}
	}).then(function(response){
		//var result = $.parseJSON(response);
		//its showing same times, need boolean in 
		//response to show if there is a change,
		//and then if so append text
//		if (response.includes('true')) {
//		$('#priceDataList').text($('#priceDataList').text() + "," + response.replace(' true',''));//JSON.stringify(result));
//		}
		//$('#priceDataList').text($('#priceDataList').text() + "," + response);
		$('#priceDataList').text(response);
	});
	console.log("In Ajax Call");
}

function statusCall() {
	request = $.ajax({
		//url: 'https://localhost:8080/testbackendrequest',//'https://api.gdax.com/products',
		url: 'https://ancient-crag-48261.herokuapp.com/vitalityInstanceStatus',//'https://api.gdax.com/products',
		
		//dataType: 'application/json',
		//format:'json',
		method: 'GET',

//		},
		//contentType: 'application/json', //text???
//		xhrFields: {
//            'withCredentials': true
//    },
		//crossDomain: true,
		success: function(data) {
			console.log("W00T");
			console.log("THE FUCK");
			console.log(data);
			},
		error: function(){
		console.log("FUCK THIS");	
		}
	}).then(function(data){
		//var result = $.parseJSON(data);
		//$('#counter').text('The current price of Litecoin is' + JSON.stringify(result));
		//priceReadResultCall(JSON.stringify(result));
		//RESULT IN STATUS ID
		$('#algStatus').text(data);
	});
}