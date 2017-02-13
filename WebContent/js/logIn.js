var logInURL = "../WebProg2015.Project/rest/session/logIn";



$(function(){
	if( $.cookie('ssid') != null ) { 
		$.ajax({
 			type : 'GET',
 			url : logInURL,
 			dataType : "text", 
 			success : function(data) {
				console.log(data);
 				// window.location.replace("../WebProg2015.Project/userHomePage.html");
 			},
 			error : function(XMLHttpRequest, textStatus, errorThrown) {
 				console.log("AJAX ERROR: " + errorThrown);
 			}
 		});
	}
	
    $(".showpassword").each(function(index,input) {
        var $input = $(input);
        $("<p id='checkBoxP' class='opt'/>").append(
        		$("<input type='checkbox' id='rememberMe' />").click(function() {
			            var rememberMe = $(this).is(":checked") ? true : false;
			            if(rememberMe){
			            	$('#firsName').val("rememberMe");
			            }else{
			            	$('#firsName').val("a");
			            }
			         })
			    ).append($("<label for='rememberMe'/>").text("Remember Me")).append(  
			    		
			    $("<input type='checkbox' class='showpasswordcheckbox' id='showPassword' />").click(function() {
                var change = $(this).is(":checked") ? "text" : "password";
                var rep = $("<input placeholder='Password' type='" + change + "' />")
                    .attr("id", $input.attr("id"))
                    .attr("name", $input.attr("name"))
                    .attr('class', $input.attr('class'))
                    .val($input.val())
                    .insertBefore($input);
                $input.remove();
                $input = rep;
             })
        ).append($("<label for='showPassword'/>").text("Show password")).insertAfter($input.parent());
    });
    
    $( "#username" ).focus(function() {
    	  $('#errorMessage').remove();
    	});
    
    $( 'input[name=password]' ).focus(function() {
    	  $('#errorMessage').remove();
    	});
});

$(document).on('submit', '#logInForm', function(e) {
	e.preventDefault();
	console.log("log in attempt");

	var data =$('#logInForm').serializeJSON();
	
	$.ajax({
		type : 'POST',
		url : logInURL,
		contentType : 'application/json',
		data : data,
		success : function(data) {
			console.log(data);
			window.location.replace("../WebProg2015.Project/userHomePage.html");
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			console.log("AJAX ERROR: " + errorThrown);
			$('#checkBoxP').after("<p id='errorMessage' style='color:red; font-weight: bold; margin-bottom:10px;'>Invalid username and/or password!</p>")
		}
	});

});