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
            var html = '';
            var size = '';
            var comp = '';
            var tot = '';
            var name = '';
            for (var i = 0; i < messages.length; i++) {
                html = '<b>' + (messages[i].name ? messages[i].name : 'Server') + ': </b>';
//              html = messages[i].message + '<br />';
                name = messages[i].fileName;
                size = messages[i].fileSize;
                comp = messages[i].progress;
                tot =  messages[i].totalGames;
                if(comp==tot){
                	$('#5').html("<a href='rest/controller/get/'>Download</a>");
                	clearInterval(end)
                	}
            }
/*            if(comp<tot)
            	$('#5').hide();*/
            
            $('#1').html(name);
            $('#2').html(size);
            $('#3').html(tot);
    		$('#4').html(comp);
    		var progress = parseInt(comp / tot * 100, 10);
	        $('#progress1 .bar').css(
	            'width',
	            progress + '%'
	        );

        } else {
            console.log("There is a problem:", data);
        }
    });

    fileupload.onclick = function () {
        	var text = " ";
        	var name1=" ";
            console.log(name1 + ': ' + text);
            socket.emit('send', {message: text, name: name1});
    };
    end=setInterval(fileupload.onclick, 3000)
    

}