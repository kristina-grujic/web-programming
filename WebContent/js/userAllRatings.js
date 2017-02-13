var logOutURL = "../WebProg2015.Project/rest/session/logOut";
var searchDone = false;

// LogOut Button in navigation bar
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

$(function(){	

	loadRatings();
	
	
	$('.closeModImg').click(function() {
		$('#myModalImage').hide();
	});	
	
	$('#btnSearch').click(function() {
		if(!searchDone){
			var placeName = $("#placeSearchField").val();
			var rating = $("#rating").val();
			
			if(placeName==="" && rating===""){
				swal("Error", "You must fill either Place or Rating field.", "error");
				return false;
			}

		   var data ={
				"placeName" :  placeName,
				"rating"    :  rating					
		   };
			
		   var url ="rest/ratings/filtered"; 
			
		   $.ajax({
			    url: url,
			    type: 'POST',
			    data: JSON.stringify(data),
			    contentType : 'application/json',
			    success: function (data) {
			    	$('#ratingsContainer').empty();
			    	$('#btnSearch').html('Cancel Search');
			    	$.each(data, function(index, element) {
			    		showRatingOnPage(index, element);
			        });
			    	searchDone = true;
			    },
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					if(XMLHttpRequest.status == 400){
						swal("Error", "Wrong parameters for search.", "error");
					}else if(XMLHttpRequest.status == 401){
						window.location.replace("../WebProg2015.Project/logIn.html");
					}else if(XMLHttpRequest.status == 500){
						swal("Error", "There was an error while trying to reach server. Please, try again.", "error");
					}
				}
			  });			
		}else{
			$('#ratingsContainer').empty();
	    	$('#btnSearch').html('Search');
	    	loadRatings();
	    	searchDone = false;
		}
	});
	
});


//Load all ratings on page load
function loadRatings(){
	
	var url = "rest/ratings/all";
	
	$.ajax({ 
	    type: 'GET', 
	    url: url, 
	    data: { get_param: 'value' }, 
	    dataType: 'json',
	    success: function (data) { 
	        $.each(data, function(index, element) {
	        	showRatingOnPage(index, element);
	        });
	    },
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			if(XMLHttpRequest.status == 401){
				window.location.replace("../WebProg2015.Project/logIn.html");
			}
		}
	});	
}

function showRatingOnPage(index, element){
	$('#ratingsContainer').append(			
			'<div class="oneRatingDiv">'+
				'<div class="userImagePart">'+
					'<img class="thumbnailImg" src="rest/users/image-by-username/' + element.userId  + '/thumbnail?t=' + new Date().getTime() + '" alt="' + element.userId + '">'+'</img>'+
					'<p>' + element.userId + '</p>'+
				'</div>'+
				'<section class="reviewSection">'+				
					'<p id="placeData-' + element.id + '"></p>'+			
				'</section>'+
				'<section class="ratingSection">'+					
					'Rating: <div style="display:inline-block;" id="mark-' + element.id + '"></div>'+	
					'<p style="font-size:12px; float:right; margin-right: 10px;">' + element.ratedOn + '</p>'+
				'</section>'+				
				'<section class="reviewSection">'+				
					'<p>Review: ' +  element.review + '</p>'+			
				'</section>'+
			'</div>'
			);	
	allRatingsInitRateStars(element);
	addPlaceData(element);
}

function addPlaceData(element){
	var url = "rest/places/" + element.placeId;

	$.ajax({ 
	    type: 'GET', 
	    url: url, 
	    data: { get_param: 'value' }, 
	    dataType: 'json',
	    success: function (data) { 
	    	$("#placeData-"+element.id).text("For place: "+data.name);	    	
	    },
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			if(XMLHttpRequest.status == 401){
				window.location.replace("../WebProg2015.Project/logIn.html");
			}
		}
	});	
}

function allRatingsInitRateStars(element){
	$("#mark-" + element.id).rateYo({		 
	    rating    : element.mark,
	    readOnly  : true,
	    starWidth: "18px",
	    multiColor: {	 
	      "startColor": "#FF0000", //RED
	      "endColor"  : "#f1c40f", //Yellow
	    }
	  });	
}

//show full size image in modal on click
$(document).on('click','.thumbnailImg', function() {
	 var imgName = $(this).attr("alt");
	 var url = "rest/users/image-by-username/"+imgName;
	 $('#myModalImage').show();
	 $("#img01").attr("src", url); 
	 var caption = imgName;
	 $('#caption').text(caption);

});