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
        	//console.log("Success Request:",req)
        	$('#button').prop('disabled', true);
        	var file=new FormData(document.getElementById("form1"));
        	$('#info').html("File Uploaded ! Work is in Progress.!");
        	
        	//console.log("done");
        	console.log("data",data)
        	//alert("inside success")
            $.each(data, function (index, file) {
            	//console.log("progress")
            	//alert("inside each")
            	if(data!=null){
            	$("#1").html(file.fileName);
            	$("#2").html(file.totalGames);
            	$("#3").html(file.progress);
            	$("#4").html("<a href='rest/ajaxcontroller/get/"+index+"'>Download</a>");
            	var totl = parseInt(file.totalGames);
            	var prog = parseInt(file.progress);
            	console.log("tot:",totl);
            	console.log("prog:",prog);
            	
            	var progress = parseInt(prog / totl * 100,10);
            	console.log("progress done percentage:",progress)
            	//alert("progress",progress);
    	        $('#progress .bar').css(
    	            'width',
    	            progress + '%'
    	        );
            	}
            	if(progress==100)
            		{
            		$('#info').html("JSOUP Completed");
            		}
                		if(prog<totl)
                			{
                			alert("1 loop");
                			callAjax();
                			}
                		else
                			{
                			return false;
                			}
            });
            
        },
		dropZone: $('#dropzone')
    });
}

