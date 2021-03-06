<!DOCTYPE html>
<html>
<head>
<script>
    var totalFileLength, totalUploaded, fileCount, filesUploaded;
 
    //To log everything on console
    function debug(s) {
        var debug = document.getElementById('debug');
        if (debug) {
            debug.innerHTML = debug.innerHTML + '<br/>' + s;
        }
    }
 
    //Will be called when upload is completed
    function onUploadComplete(e,data) {
    	 $.each(data.result, function (index, file) {
         	$("#uploaded-files").append(
             		$('<tr/>')
             		.append($('<td/>').text("hello"))
             		.append($('<td/>').text(file.fileName))
             		.append($('<td/>').text(file.totalGames))
             		.append($('<td/>').text(file.progress))
             		.append($('<td/>').html("<a href='rest/controller/get/"+index+"'>Click</a>"))
             		)//end $("#uploaded-files").append()
         });
         alert("not done");
         setInterval(next(), 10000);
         alert("not done 1");
    }
 
    //Will be called when user select the files in file control
    function onFileSelect(e) {
        var files = e.target.files; // FileList object
        var output = [];
        fileCount = files.length;
        totalFileLength = 0;
        for (var i = 0; i < fileCount; i++) {
            var file = files[i];
            output.push(file.name);
            output.push('<br/>');
            debug('add ' + file.size);
            totalFileLength += file.size;
        }
        document.getElementById('selectedFiles').innerHTML = output.join('');
        debug('totalFileLength:' + totalFileLength);
    }
 
    //This will continueously update the progress bar
    function onUploadProgress(e) {
        if (e.lengthComputable) {
            var percentComplete = parseInt((e.loaded + totalUploaded) * 100 / totalFileLength);
            var bar = document.getElementById('bar');
            bar.style.width = percentComplete + '%';
            bar.innerHTML = percentComplete + ' % complete';
        } else {
            debug('unable to compute');
        }
        done();
    }
 
    //the Ouchhh !! moments will be captured here
    function onUploadFailed(e) {
        alert("Error uploading file");
    }
 
    //Pick the next file in queue and upload it to remote server
    function uploadNext() {
        var xhr = new XMLHttpRequest();
        var fd = new FormData();
        var file = document.getElementById('files').files[filesUploaded];
        fd.append("multipartFile", file);
        xhr.upload.addEventListener("progress", onUploadProgress, false);
        xhr.addEventListener("load", onUploadComplete, true);
        xhr.addEventListener("error", onUploadFailed, false);
        xhr.open("POST", "http://localhost:8080/spring-mvc-jquery-file-upload/rest/ajaxcontroller/upload",true);
        debug('uploading ' + file.name);
        xhr.send(fd);
    }
 
    //Let's begin the upload process
    function startUpload() {
        totalUploaded = filesUploaded = 0;
        uploadNext();
    }
 
    //Event listeners for button clicks
    window.onload = function() {
        document.getElementById('files').addEventListener('change', onFileSelect, false);
        document.getElementById('uploadButton').addEventListener('click', startUpload, false);
    }
</script>
</head>
<body>
    <div style="width:55%">  
        <h1>HTML5 Ajax Multi-file Upload With Progress Bar</h1>
        <div id='progressBar' style='height: 20px; border: 2px solid green; margin-bottom: 20px'>
            <div id='bar' style='height: 100%; background: #33dd33; width: 0%'>
            </div>
        </div>
        <form style="margin-bottom: 20px">
            <input type="file" id="files" multiple style="margin-bottom: 20px"/><br/>
            <output id="selectedFiles"></output>
            <input id="uploadButton" type="button" value="Upload" style="margin-top: 20px"/>
        </form>
        <div id='debug' style='height: 100px; border: 2px solid #ccc; overflow: auto'></div>
        <ul id="ulList"></ul>
    </div>
    <div id="uploaded-files"></div>
</body>
</html>