var logOutURL = "../WebProg2015.Project/rest/session/logOut";
var searchDone = false;
var me;
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
	me = localStorage.getItem('myself');
	if (me) {
		me = JSON.parse(me);
		if (me.role==='admin') {
			$("#actions").append('<a href="addLanguage.html" rel="modal:open">Add programming language</a>');
			$("#actions").append('<a href="createSnippet.html" rel="modal:open">Create snippet</a>');	
		} else {
			$("#actions").append('<a href="createSnippet.html" rel="modal:open">Create snippet</a>');		
		}
	} else {
		$("#actions").append('<a href="createSnippet.html" rel="modal:open">Create snippet</a>');			
	}
	
	loadSnippets();
	
});


//Load all events on page load
function loadSnippets(){
	
	var url = "rest/snippets";
	
	$.ajax({ 
	    type: 'GET', 
	    url: url, 
	    data: { get_param: 'value' }, 
	    dataType: 'json',
	    success: function (data) { 
			if (data.length === 0) {
				$('#snippetsContainer').append('<h1>No snippets available</h1>');
				return;
			}
	        $.each(data, function(index, element) {
	        	showSnippetOnPage(index, element);
	        });
			
	    },
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			console.log(textStatus);
		}
	});	
}

function goToSnippet(element) {
	localStorage.setItem('snippetView', JSON.stringify(element));
}

function deleteSnippet(element) {
	var url = "rest/snippets/" + element.id;
	
	$.ajax({ 
	    type: 'DELETE', 
	    url: url, 
	    dataType: 'json',
		headers: {
			Accept: "application/json"
		},
	    async: false,
	    cache: false,
	    contentType: false,
	    processData: false,    
	    success: function (data) { 
			$('#snippet'+data.id).remove();
	    },
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			if(XMLHttpRequest.status == 401){
				localStorage.removeItem('myself')
				window.location.replace("../WebProg2015.Project/logIn.html");
				return;
			}
			swal("Error", "There was an error while trying to reach server. Please, try again.", "error");
		}
	});	
}

function showSnippetOnPage(index, element){
	$('#snippetsContainer').append(
		'<div class="oneEventDiv" id="snippet'+ element.id + '">'+
			'<section class="dataPart1">'+	
				'<p class="nameP"><b>Description: </b><a href="snippet.html" id="element'+element.id+'">' +  element.description + '</a></p>'+
			'</section>'+
			'<section class="dataPart2">'+
				'<p><b>Code: </b>' +  element.code + '</p>'+			
				'<p><b>Programming language: </b>' +  element.language.name + '</p>'+
				'<p><b>Repository: </b>' +  element.repository + '</p>'+
				'<p><b>Creator: </b>' +  
				(element.user && element.user.username || 'Anonymous') + '</p>'+
			'</section>'+	
			(
				me && me.role==='admin' || (element.user && me && element.user.username === me.username) ?
					'<section>' +
						'<button id="' + element.id + '" >Delete</button>' +
					'</section>'
					:
					''
			)
			+
		'</div>'
		);
	$('#element'+element.id).click(goToSnippet.bind(this,element))

	$('#'+element.id).click(deleteSnippet.bind(this,element))
}
