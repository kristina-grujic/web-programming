$("#createForm").submit(function(event){
	 
	  //disable the default form submission
	  event.preventDefault();
	  
	  //grab all form data  
	  var formData = new FormData();
      formData.append('name', $('#name').val());
	  var url = "rest/programming_languages";
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
            $.modal.close();
	    },
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			$.modal.close();
			if (errorThrown==='Unauthorized') {
			  	swal("Error", "You have no permission to add a programming language.", "error");	
			}
			swal("Error", "There was an error while trying to reach server. Please, try again.", "error");
		
		}
	  });
	 
	  return false;
});
