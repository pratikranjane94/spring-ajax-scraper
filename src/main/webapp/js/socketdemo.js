var end;
window.onload = function () {

    var messages = [];
    var message = document.getElementById("message");
    var sendButton = document.getElementById("send");
    var content = document.getElementById("content");
    var name = document.getElementById("name");
    var fileupload=document.getElementById("fileupload");
    var socket = io.connect('http://localhost:3400', {
        'reconnection delay': 1000,
        'force new connection': true
    });

    socket.on('connect', function () {
        console.log('connected');
    });

    socket.on('message', function (data) {
        if (data.message) {
            messages.push(data);
            var size = '';
            var completed = '';
            var total = '';
            var name = '';
            for (var i = 0; i < messages.length; i++) {
                name = messages[i].fileName;
                size = messages[i].fileSize;
                completed = messages[i].progress;
                total =  messages[i].totalGames;
                
                if(completed==total){
                	$('#5').html("<a href='rest/controller/get/'>Download</a>");
                	clearInterval(end)
                	}
            }//end of for
            
            $('#1').html(name);	//displaying file name
            $('#2').html(size);	//displaying file size
            $('#3').html(total);	//displaying total games
    		$('#4').html(completed);	//displaying progress
    		
    		//updating progress bar
    		var progress = parseInt(completed / total * 100, 10);
	        $('#progress1 .bar').css(
	            'width',
	            progress + '%'
	        );//end of progress bar

        } else {
            console.log("There is a problem:", data);
        }
    });//end of socket.on('message') function

    //on file uploaded function
    fileupload.onclick = function () {
        	var text = " ";
        	var name1=" ";
            console.log(name1 + ': ' + text);
            socket.emit('send', {message: text, name: name1});
    };
    end=setInterval(fileupload.onclick, 3000)
    

}