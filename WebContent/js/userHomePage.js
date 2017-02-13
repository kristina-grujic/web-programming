var logOutURL = "../WebProg2015.Project/rest/session/logOut";
var map = null;
var markersData = [];
var newRatingVal = 0;
var ratedPlaceId = 0;
var myLocation = {};

//LogOut Button in navigation bar
$(function(){
 	$('#logOut').click(function(){
		 localStorage.removeItem('myself');
 		$.ajax({
 			type : 'GET',
 			url : logOutURL,
 			dataType : "text", 
 			success : function(data) {
 				window.location.replace("../WebProg2015.Project/logIn.html");
 			}
 		});
 	});
});

//Load all places for logged manager on page load
function loadSelfData(){
	
	var url = "rest/users/self-data";

	$.ajax({ 
	    type: 'GET', 
	    url: url, 
	    data: { get_param: 'value' }, 
	    dataType: 'json',
	    success: function (data) { 
			console.log(data);
				 me = data;
	       localStorage.setItem('myself', JSON.stringify(data));
	    },
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			if(XMLHttpRequest.status == 401){
				window.location.replace("../WebProg2015.Project/logIn.html");
			}
		}
	});	
}

$(function(){
	let me = localStorage.getItem('myself');
	if (!me) {
		loadSelfData();
	}
	else {
		me = JSON.parse(me);
	}
	myLocation = me.address ? JSON.parse(me.address) : {};
	console.log(myLocation);
});


function initMap() {
	
	map = new google.maps.Map($('#map').get(0), {
	    center: myLocation.geometry ? myLocation.geometry.location : {lat: 45.251249, lng: 19.83839},
	    zoom: 14,
	    mapTypeId: google.maps.MapTypeId.ROADMAP
	  });	

		if (myLocation.geometry) {
			var marker = new google.maps.Marker({
					position: myLocation.geometry.location,
					label: 'YOU',
					map: map,
					title: "You're here!"
				});
		}
}
