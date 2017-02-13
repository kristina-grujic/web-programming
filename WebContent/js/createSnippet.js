
var me;
$(function(){	

	loadLanguages();
})

//Load programming languages
function loadLanguages(){
	
	me = localStorage.getItem('myself');
	var url = "rest/programming_languages";
	
	$.ajax({ 
	    type: 'GET', 
	    url: url, 
	    data: { get_param: 'value' }, 
	    dataType: 'json',
	    success: function (data) { 
			if (data.length === 0) {
				$.modal.close();
				return;
			}
	        $.each(data, function(index, element) {
	        	$('#languageName').append(
					'<option value="'+ element.name +
					'">' + element.name + '</option>'
				);
	        });
			
	    },
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			swal("Error", "There was an error while trying to reach server. Please, try again.", "error");
		}
	});	
}

$("#createForm").submit(function(event){
	 
	  //disable the default form submission
	  event.preventDefault();
	  
	  //grab all form data  
	  var formData = new FormData();
      formData.append('description', $('#description').val());
      formData.append('code', $('#code').val());
      formData.append('repository', $('#repository').val());
      formData.append('languageName', $('#languageName').val());
	  var url = "rest/snippets";
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
	    success: function (element) {
			$.modal.close();
            $('#snippetsContainer').append(
				'<div class="oneEventDiv">'+
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
						me ?
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
	    },
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			swal("Error", "There was an error while trying to reach server. Please, try again.", "error");
		}
	  });
	 
	  return false;
});
