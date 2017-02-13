var autocomplete;

$("#msform").submit(function(event){
	 
	  //disable the default form submission
	  event.preventDefault();
	  
	  if(!validateThirdStep()){
			return false;
	  }
	 
	  //grab all form data  
	  var formData = new FormData($(this)[0]);
	  var url = "";
	  var redirectUrl = "";
	  url = "rest/users/signup/" + $('#username').val();
	  redirectUrl = "../WebProg2015.Project/userHomePage.html"
	  formData.delete('address');
      formData.append('address', JSON.stringify(autocomplete.getPlace()));
	  
	  $.ajax({
	    url: url,
	    type: 'POST',
	    data: formData,
	    async: false,
	    cache: false,
	    contentType: false,
	    processData: false,
	    success: function (returndata) {
	      window.location.replace(redirectUrl);
	    },
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			console.log("AJAX ERROR: " + errorThrown);
			swal("Error", "There was an error while trying to reach server. Please, try again.", "error");
		}
	  });
	 
	  return false;
});




$('#username').focusout(function(){
	var username = $('#username').val();
	if(username===""){
		markInvalidUsername("empty");
	}else{
		var checkUserAvailabilityURL = "../WebProg2015.Project/rest/users/signup/" +username;
		$.ajax({
			type : 'get',
			url : checkUserAvailabilityURL,
			dataType : "text",
			success : function(data) {
				if(data==="available"){
					markUsernameValid();
				}else if(data==="taken"){
					markInvalidUsername("taken");
				}
			},
		});
	}
});

function markInvalidUsername(reason){
	$("#username").css("border", "5px solid red");
	if(reason==="empty"){
		$("#username").after("<p id=\"usernameMessage\" style=\"color:red; margin-bottom: 10px;\">Username field can not be empty.<i class=\"fa fa-times\" style=\"color:red;\"></i></p>");
	}else if(reason==="taken"){
		$("#username").after("<p id=\"usernameMessage\" style=\"color:red; margin-bottom: 10px;\">This username is not available.<i class=\"fa fa-times\" style=\"color:red;\"></i></p>");
	}
}

function markUsernameValid(){
	$("#username").css("border", "5px solid green");
	$("#username").after("<p id=\"usernameMessage\" style=\"color:green; margin-bottom: 10px;\" >This username is available. <i class=\"fa fa-check\" style=\"color:green;\"></i></p>");
}

$('#username').focusin(function(){
	$("#usernameMessage").remove();
	$("#username").css("border", "1px solid #ccc");
});


$('#cpass').focusout(function(){
	var cpass = $('#cpass').val();
	if(cpass===""){
		markInvalidCpass("empty");
	}else{
		var password = $('#password').val();
		if(password!=cpass){
			markInvalidCpass("different");
		}else{
			markCpassValid();
		}
	}
});

function markInvalidCpass(reason){
	$("#cpass").css("border", "5px solid red");
	if(reason==="empty"){
		$("#cpass").after("<p id=\"cpassMessage\" style=\"color:red; margin-bottom: 10px;\">Password must be confirmed.<i class=\"fa fa-times\" style=\"color:red;\"></i></p>");
	}else if(reason==="different"){
		$("#cpass").after("<p id=\"cpassMessage\" style=\"color:red; margin-bottom: 10px;\">Value doesn't match the entered password.<i class=\"fa fa-times\" style=\"color:red;\"></i></p>");
	}
}

function markCpassValid(){
	$("#cpass").css("border", "5px solid green");
	$("#cpass").after("<p id=\"cpassMessage\" style=\"color:green; margin-bottom: 10px;\" >Value matches the entered password. <i class=\"fa fa-check\" style=\"color:green;\"></i></p>");
}

$('#cpass').focusin(function(){
	$("#cpassMessage").remove();
	$("#cpass").css("border", "1px solid #ccc");
});

// Validation

function validateInput(element){
	var fs = $(element).parent();
	var id = fs.attr('id');
	if(id==="fs1"){
		return validateFirstStep();
	}else if(id==="fs2"){
		return validateSecondStep();
	}
}

function validateFirstStep(){
	if($("#usernameMessage").css("color")!="rgb(0, 128, 0)"){
		swal("Error", "You must choose available username.", "error");
		return false;
	}
	if($("#password").val()==="" || $("#cpass").val()===""){
		swal("Error", "You must fill and confirm password.", "error");
		return false;
	}
	if($("#cpassMessage").css("color")!="rgb(0, 128, 0)"){
		swal("Error", "You must confirm password correctly.", "error");
		return false;
	}
	return true;
}

function validateEmail(email) {
    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}

function validateSecondStep(){
	if($("#firstName").val()===""){
		swal("Error", "You must fill first name.", "error");
		return false;
	}
	if($("#lastName").val()===""){
		swal("Error", "You must fill last name.", "error");
		return false;
	}
	if($("#email").val()===""){
		swal("Error", "You must fill email.", "error");
		return false;
	}
	if(!validateEmail($("#email").val())){
		swal("Error", "You must enter a valid email.", "error");
		return false;
	}
	return true;
}

function validateThirdStep(){
	var val =$('input[type=file]').val().toLowerCase();
	var regex = new RegExp("(.*?)\.(jpg|png|jpeg|bmp|ico)$");
	if(!(regex.test(val))) {
		$('input[type=file]').val('');
		swal("Error", "Please select image file.", "error");
		return false;
	} 
	return true;
}

function initAutocomplete() {
	autocomplete = new google.maps.places.Autocomplete(
		/** @type {!HTMLInputElement} */(document.getElementById('address')),
		{types: ['geocode']});
}


// Animation

var current_fs, next_fs, previous_fs; //fieldsets
var left, opacity, scale; //fieldset properties which we will animate
var animating; //flag to prevent quick multi-click glitches


$(".next").click(function(){	
	if(animating) return false;
	animating = true;
	
	if(!validateInput(this)){
		animating = false;
		return false;
	}
	
	current_fs = $(this).parent();
	next_fs = $(this).parent().next();
	
	//activate next step on progressbar using the index of next_fs
	$("#progressbar li").eq($("fieldset").index(next_fs)).addClass("active");
	
	//show the next fieldset
	next_fs.show(); 
	//hide the current fieldset with style
	current_fs.animate({opacity: 0}, {
		step: function(now, mx) {
			//as the opacity of current_fs reduces to 0 - stored in "now"
			//1. scale current_fs down to 80%
			scale = 1 - (1 - now) * 0.2;
			//2. bring next_fs from the right(50%)
			left = (now * 50)+"%";
			//3. increase opacity of next_fs to 1 as it moves in
			opacity = 1 - now;
			current_fs.css({
        'transform': 'scale('+scale+')',
        'position': 'absolute'
      });
			next_fs.css({'left': left, 'opacity': opacity});
		}, 
		duration: 800, 
		complete: function(){
			current_fs.hide();
			animating = false;
		}, 
		//this comes from the custom easing plugin
		easing: 'easeInOutBack'
	});
});

$(".previous").click(function(){
	if(animating) return false;
	animating = true;
	
	current_fs = $(this).parent();
	previous_fs = $(this).parent().prev();
	
	//de-activate current step on progressbar
	$("#progressbar li").eq($("fieldset").index(current_fs)).removeClass("active");
	
	//show the previous fieldset
	previous_fs.show(); 
	//hide the current fieldset with style
	current_fs.animate({opacity: 0}, {
		step: function(now, mx) {
			//as the opacity of current_fs reduces to 0 - stored in "now"
			//1. scale previous_fs from 80% to 100%
			scale = 0.8 + (1 - now) * 0.2;
			//2. take current_fs to the right(50%) - from 0%
			left = ((1-now) * 50)+"%";
			//3. increase opacity of previous_fs to 1 as it moves in
			opacity = 1 - now;
			current_fs.css({'left': left});
			previous_fs.css({'transform': 'scale('+scale+')', 'opacity': opacity});
		}, 
		duration: 800, 
		complete: function(){
			current_fs.hide();
			animating = false;
		}, 
		//this comes from the custom easing plugin
		easing: 'easeInOutBack'
	});
});