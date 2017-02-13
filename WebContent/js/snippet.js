var logOutURL = "../WebProg2015.Project/rest/session/logOut";
var me;
var comments;
var snippet;

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
	snippet = JSON.parse(localStorage.getItem('snippetView')|| '{}');
	if (me) {
		me = JSON.parse(me);
		if (me.role==='admin') {
			if (snippet.commentsEnabled) {
				$("#actions").append('<button onclick="disableComments()">Disable comments</button>');
			} else {
				$("#actions").append('<button onclick="enableComments()">Enable comments</button>');
			}
		}
	}

$("#createComment").click(function(){

      snippet = JSON.parse(localStorage.getItem('snippetView')|| '{}');
      //grab all form data  
	  var formData = new FormData();
      formData.append('text', $('#text').val());
      var url = "rest/comments/" + snippet.id;
	  $.ajax({
	    url: url,
	    type: 'POST',
	    data: formData,
		headers: {
			Accept: "application/json"
		},
		async: false,
		cache: false,
		contentType: false,
		processData: false,    
	    success: function (returndata) {
			$('#commentNumContainer').empty();
			comments = comments + 1;
			$('#commentNumContainer').append(
				'<h1>' + comments + ' comments</h1'
			);
			showCommentOnPage(0, returndata);
            $('#text').val("");
	    },
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			console.log("AJAX ERROR: " + errorThrown);
			swal("Error", "There was an error while trying to reach server. Please, try again.", "error");
		}
	  });
	 
	  return false;
});

    snippet = JSON.parse(localStorage.getItem('snippetView')|| '{}');
    $('#snippetContainer').append(
		'<div>'+
			'<section class="dataPart1">'+	
				'<p class="nameP"><b>Description: </b>' +  snippet.description + '</a></p>'+
			'</section>'+
			'<section class="dataPart2">'+
				'<p><b>Code: </b>' +  snippet.code + '</p>'+			
				'<p><b>Programming language: </b>' +  snippet.language.name + '</p>'+
				'<p><b>Repository: </b>' +  snippet.repository + '</p>'+
				'<p><b>Creator: </b>' +  
				(snippet.user && snippet.user.username || 'Anonymous') + '</p>'+
			'</section>'+	
		'</div>'
		);
    
	if (snippet.commentsEnabled) {
		loadComments();
		$("#commentBox").append(
			'<textarea id="text" placeholder="Type a comment"></textarea>' +
            '<button id="createComment">Post</button>'
			)
	} else {
		$('#commentNumContainer').append('<h1>Commenting disabled</h1>');
	}
    
    
});

function disableComments() {
	$.ajax({
 			type : 'PUT',
 			url : 'rest/snippets/block/'+snippet.id,
 			dataType : "text",
			headers: {
				Accept: "application/json"
			},
			async: false,
			cache: false,
			contentType: false,
			processData: false,    
 			success : function(data) {
				 snippet.commentsEnabled = false;
				 localStorage.setItem('snippetView', JSON.stringify(snippet));
				 if (me.role==='admin') {
					$("#actions").empty();
					$("#actions").append('<button onclick="enableComments()">Enable comments</button>');
				}
				$('#commentNumContainer').empty();
				$('#commentsContainer').empty(); 
				$('#commentNumContainer').append('<h1>Commenting disabled</h1>');
				$("#commentBox").empty();
            },
			error: function(error) {
				swal("Error", "There was an error while trying to reach server. Please, try again.", "error");
			}
 		});
}

function enableComments() {
	$.ajax({
 			type : 'PUT',
 			url : 'rest/snippets/unblock/'+snippet.id,
 			dataType : "text",
			headers: {
				Accept: "application/json"
			},
			async: false,
			cache: false,
			contentType: false,
			processData: false,    
 			success : function(data) {
				if (me.role==='admin') {
					$("#actions").empty();
					$("#actions").append('<button onclick="disableComments()">Disable comments</button>');
				}
				snippet.commentsEnabled = true;
				localStorage.setItem('snippetView', JSON.stringify(snippet));
				loadComments();
				$("#commentBox").append(
				'<textarea id="text" placeholder="Type a comment"></textarea>' +
				'<button id="createComment">Post</button>'
				)
            },
			error: function(error) {
				swal("Error", "There was an error while trying to reach server. Please, try again.", "error");
			}
 		});
}

function loadComments() {
	$.ajax({
 			type : 'GET',
 			url : 'rest/comments/'+snippet.id,
 			dataType : "text",
			headers: {
				Accept: "application/json"
			},
			async: false,
			cache: false,
			contentType: false,
			processData: false,    
 			success : function(data) {
				$('#commentNumContainer').empty();
				$('#commentsContainer').empty();
                data = JSON.parse(data);
				comments = data.length;
                if (data.length === 0) {
                    $('#commentNumContainer').append('<h1>No comments</h1>');
                    return;
			    }
                $('#commentNumContainer').append(
                    '<h1>' + data.length + ' comments</h1'
                );
                $.each(data, function(index, element) {
                    showCommentOnPage(index, element);
                });
            },
			error: function(error) {
				swal("Error", "There was an error while trying to reach server. Please, try again.", "error");
			}
 		});
}

function deleteComment(element) {
	console.log(element)
	var url = "rest/comments/" + element.id;
	
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
			$('#commentNumContainer').empty();
			comments = comments - 1;
			$('#commentNumContainer').append(
				'<h1>' + comments + ' comments</h1'
			);
			$('#comment'+data.id).remove();
			
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

function ratePositive(element) {
	console.log(element)
	var url = "rest/ratings/positive/" + element.id;
	
	$.ajax({ 
	    type: 'POST', 
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
			loadComments(); 
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

function rateNegative(element) {
	console.log(element)
	var url = "rest/ratings/negative/" + element.id;
	
	$.ajax({ 
	    type: 'POST', 
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
			loadComments();
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

function showCommentOnPage(index, element){
	let isRated;
	if (me) {
		isRated = _.find(element.rating.positive, function(o) { 
			return o.username === me.username
		});
		if (!isRated) {
			isRated = _.find(element.rating.negative, function(o) { 
				return o.username === me.username
			});
		}
	}
	$('#commentsContainer').append(
		'<div class="oneEventDiv" id="comment'+ element.id +'">'+
            // '<img src="./Data/Images/Users/' + ((element.user && element.user.imageName) || "images/RateItMin.jpg") + '" />' +
            '<p><small>' + ((element.user && element.user.username) || "Anonymous") + '</small></p>' +
			'<p>' +  element.text + '</p>'+	
			'<div class="ratings">' +
				'<p><bold>Positive</bold>: ' + element.rating.positive.length + '</p>' +
				(
					me && !isRated ?
						'<button id="ratepos'+ element.id + '">rate positive</button>'
						:
						''
				) +
				'<p><bold>Negative</bold>: ' + element.rating.negative.length + '</p>' +
				(
					me && !isRated ?
						'<button id="rateneg'+ element.id + '">rate negative</button>'
						:
						''
				)+
			'</div>' + 
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
	$('#ratepos'+element.id).click(ratePositive.bind(this,element))
	$('#rateneg'+element.id).click(rateNegative.bind(this,element))
	$('#'+element.id).click(deleteComment.bind(this,element))
}


