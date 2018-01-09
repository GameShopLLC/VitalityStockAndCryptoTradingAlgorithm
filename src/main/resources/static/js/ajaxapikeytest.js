/**
 * 
 */
//document.domain='api.gdax.com';
$(document).ready(function(){
	//alert("includes and jquery working");
	setInterval(ajaxCall, 1000);
	//ajaxCall();
});

function ajaxCall() {
	request = $.ajax({
		url: 'https://localhost:8080/testbackendrequest',//'https://api.gdax.com/products',
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
		$('#counter').text('Your current accounts on GDAX are' + JSON.stringify(result));
		
	});
	console.log("In Ajax Call");
}