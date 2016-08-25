var req;

/*$(function () {
    $('#file').fileupload({
        dataType: 'json',                
        progressall: function (e, data) {
	        var progress = parseInt(data.loaded / data.total * 100, 10);
	        $('#progress .bar').css(
	            'width',
	            progress + '%'
	        );
   		},
        done: function (e, data) {
        	alert("done")
        },
		dropZone: $('#dropzone')
    });
});*/

function callAjax() {
    	req=$.ajax({
    	type:"POST",
    	url:'http://localhost:8080/spring-mvc-jquery-file-upload/rest/ajaxcontroller/upload',
    	data: new FormData(document.getElementById("form1")),
        enctype: 'multipart/form-data',
        processData: false,
        contentType: false,
        
        //success function
        success: function (data) {
        	$('#info').html("File Uploaded ! Work is in Progress.!");
        	console.log("data",data)
        	
        	//displaying file details
            $.each(data, function (index, file) {
            	if(data!=null){
            	$("#1").html(file.fileName);
            	$("#2").html(file.totalGames);
            	$("#3").html(file.progress);
            	$("#4").html("<a href='rest/ajaxcontroller/get/"+index+"'>Download</a>");
            	
            	//storing total games and progress
            	var totl = parseInt(file.totalGames);
            	var prog = parseInt(file.progress);
            	
            	//Displaying JSOUP Progress
            	var progress = parseInt(prog / totl * 100,10);
            	console.log("progress done percentage:",progress)
            	//alert("progress",progress);
    	        $('#progress .bar').css(
    	            'width',
    	            progress + '%'
    	        );
            	}//end of if
            	
            	//if progress is less than total games call the controller again
        		if(prog<=totl)
        			{
        			alert("1 loop");
        			callAjax();
        			}
        		else
        			{
        			//completed the JSOUP process
        			$('#info').html("JSOUP Completed");
        			//$('#button').prop('disabled', false);
        			return false;
        			}
            });//end of each loop
            
        },
		dropZone: $('#dropzone')
    });
}
