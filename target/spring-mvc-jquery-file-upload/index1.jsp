<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8">
<title>jQuery File Upload Example</title>
<script src="js/jquery.1.9.1.min.js"></script>

<script src="js/vendor/jquery.ui.widget.js"></script>
<script src="js/jquery.iframe-transport.js"></script>
<script src="js/jquery.fileupload.js"></script>

<script src="./js/socket.io/socket.io.js"></script>
<script src="./js/socketdemo.js"></script>

<!-- bootstrap just to have good looking page -->
<script src="bootstrap/js/bootstrap.min.js"></script>
<link href="bootstrap/css/bootstrap.css" type="text/css" rel="stylesheet" />

<!-- we code these -->
<link href="css/dropzone.css" type="text/css" rel="stylesheet" />
<script src="js/myuploadfunction.js"></script>
</head>

<body>
<h1>File Upload</h1>
<div style="width:500px;padding:20px">

	<input id="fileupload" type="file" name="files" data-url="http://localhost:8080/spring-mvc-jquery-file-upload/rest/controller/upload">
	
	<div id="dropzone" class="fade well">Drop files here</div>
	
	<div id="progress" class="progress">
    	<div class="bar" style="width: 0%;"></div>
	</div>
	<div id="output" style="display:none">File Uploaded </div>
	
	<div id="content"></div>
	<div id="completed"></div>
	<div id="total"></div>
	
	
</div>
	<table id="uploaded-files" class="table">
		<tr>
			<th>File Name</th>
			<th>File Size</th>
			<th>No of game in files</th>
			<th>Progress</th>
			<th>Download</th>
		</tr>
		<td><div id="1"></div></td>
		<td><div id="2"></div></td>
		<td><div id="3"></div></td>
		<td><div id="4"></div></td>
		<td><div id="5"></div></td>
	</table>
	    
</body> 
</html>
