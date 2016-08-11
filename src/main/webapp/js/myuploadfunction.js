$(function () {
    $('#fileupload').fileupload({
        dataType: 'json',                
        progressall: function (e, data) {
	        var progress = parseInt(data.loaded / data.total * 100, 10);
	        $('#progress .bar').css(
	            'width',
	            progress + '%'
	        );
	        //$("#output").html("Completed").show();
	        //$("#output").show();
   		},
        done: function (e, data) {
        	//$("tr:has(td)").remove();
            $.each(data.result, function (index, file) {
            	$('#5').html("<a href='rest/controller/get/"+index+"'>click</a>");
            }); 
        },
		dropZone: $('#dropzone')
    });
});