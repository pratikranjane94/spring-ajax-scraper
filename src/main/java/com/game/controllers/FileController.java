/*File Name	: FileController.java
 *Created By: PRATIK RANJANE
 *Purpose	: Storing the uploaded file, creating JOUSP of games presents in files,
 *			  creating CSV file of details of games using and displaying on web page using Socket,
 *			  Downloading the file. 
 * */
package com.game.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.game.dao.GameJsoupDaoImp;
import com.game.dto.FileMeta;
import com.game.dto.Message;
import com.game.model.ApkSiteDataFetching;
import com.game.model.GameNotFound;
import com.game.model.PlayStoreDataFetching;
import com.game.model.PlayStoreUrlFetching;

@RestController
@EnableWebMvc
@RequestMapping("/controller")
public class FileController {
	
	//Class objects are created
	PlayStoreUrlFetching purl = new PlayStoreUrlFetching();
	PlayStoreDataFetching psdf = new PlayStoreDataFetching();
	ApkSiteDataFetching asdf = new ApkSiteDataFetching();
	GameNotFound gameNotFound = new GameNotFound();
	FileMeta fileMeta = null;
	MultipartFile mpf = null;
	SocketIOServer server;
	Configuration config;

	@Resource(name = "gameJsoupDao")
	private GameJsoupDaoImp gameJsoupDao;

	LinkedList<FileMeta> files = new LinkedList<FileMeta>();
	ArrayList<String> playStoreDetails = new ArrayList<String>();
	ArrayList<String> apkSiteDetails = new ArrayList<String>();
 
	String url = "";			//Play Store URL
	String line;				//line read from file and stores game name
	String temp = "";			//game name temporary
	String fileSize;			//size of uploaded file
	String fileName;			//name of uploaded file
	String downloadFileName;	//downloading name for file
	int progress = 0;			//no of game's JSOUP completed
	int count;					//temporary stores total no of game in file
	int id;						//unique id for each uploaded file
	int totoalGames = 0;		//total games in file
	boolean status = true;		//status of APK-DL CSV created or not
	boolean psStatus = true;	//status of PlayStore CSV created or not

	
	/*-------------------------------------------Creating JSOUP of Uploaded File-------------------------------------------*/
	
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody LinkedList<FileMeta> upload(MultipartHttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		System.out.println("Ajax socket file controller");

							/*---------------Web Socket Configuration----------*/
		config = new Configuration();
		config.setHostname("localhost");
		config.setPort(3400);
		
		server = new SocketIOServer(config);
		
		server.addConnectListener(new ConnectListener() {
			@Override
			public void onConnect(SocketIOClient client) {
				System.out.println("Connected");
			}
		});
		
		System.out.println("Starting server...");
		server.start();
		System.out.println("Server started");
							/*---------------end of web socket connection----------*/

							/*---------------iterator for getting file-------------*/

		Iterator<String> itr = request.getFileNames();
		
		// get each file
		while (itr.hasNext()) {

			// get uploaded MULTIPART file and its information

			mpf = request.getFile(itr.next());
			System.out.println(mpf.getOriginalFilename() + " uploaded! ");

			fileName = mpf.getOriginalFilename();
			downloadFileName = mpf.getOriginalFilename().replace(".", "Download.");

					/*---------------end of iterator for getting files-----------------*/

			fileSize = mpf.getSize() / 1024 + " Kb";

			// storing data in file meta class
			fileMeta = new FileMeta();
			fileMeta.setFileName(fileName);
			fileMeta.setFileSize(mpf.getSize() / 1024 + " Kb");
			fileMeta.setFileType(mpf.getContentType());

			try {
				fileMeta.setBytes(mpf.getBytes());

				// copy uploaded file to local disk
				FileCopyUtils.copy(mpf.getBytes(),
						new FileOutputStream("/home/bridgelabz6/Pictures/files/" + fileName));

				// counting no of games in file
				FileReader frCount = new FileReader("/home/bridgelabz6/Pictures/files/" + fileName);
				BufferedReader brCount = new BufferedReader(frCount);
				while (brCount.readLine() != null) {
					count++;
				}
				totoalGames = count - 1;
				
				//reseting count to zero
				count = 0;
				
				fileMeta.setTotalGames(totoalGames);
				System.out.println("totoalGames:" + totoalGames);
				
				brCount.close();
				// end of counting game

				/*----------------------------JSOUP started--------------------------------*/

				FileReader fr = new FileReader("/home/bridgelabz6/Pictures/files/" + fileName);
				BufferedReader br = new BufferedReader(fr);

				line = br.readLine();
				if (line == null) {
					System.out.println("file is empty");
				} else {
					line = br.readLine();
					
					for (progress = 0; progress < totoalGames; progress++) {

						fileMeta.setProgress(progress);

						temp = line;
						String[] gname = line.split("\\^");
						line = gname[1];
						System.out.println("Game Name= " + line); // read games name

						// Separates the game name from line read from file
						line = gname[1];
						System.out.println("Game Name= " + line);

						// getting URL for game
						url = purl.findUrl(line);

						line = br.readLine();

						// exception handling if URL not found
						if (url == null) {
							gameNotFound.addGameNotFound("Url", temp);
							continue;
						} // end of handling in URL fetching

						// getting play store site data
						playStoreDetails = psdf.getPlayStoreData(url);

						// handling exception in play store details
						if (playStoreDetails.equals(null)) {
							gameNotFound.addGameNotFound("PlayStore", temp);
						} // end of handling in play store details

						// creating CSV file of play store data
						psStatus = psdf.createCsv(playStoreDetails, downloadFileName);

						// handling exception in creating play store data CSV file
						if (psStatus == false) {
							gameNotFound.addGameNotFoundInFile("PlayStore", temp, downloadFileName);
						}

						// getting play store package name
						String pack = psdf.getPackage(playStoreDetails);

						// getting APK-DL site data
						apkSiteDetails = asdf.createApkSiteDetails(pack);

						// handling exception in APK site details
						if (apkSiteDetails == null) {
							gameNotFound.addGameNotFoundInFile("DlApk", temp, downloadFileName);
						} // end of handling in APK-DL

						// creating CSV file of APK-DL site details
						status = asdf.createCsv(apkSiteDetails, downloadFileName);

						// handling exception in creating APK-DL data CSV file
						if (status == false) {
							gameNotFound.addGameNotFoundInFile("DlApk", temp, downloadFileName);
						}

						// Database entry
						if (gameJsoupDao.isEmpty())
							id = 0;
						else {
							// if last filename is same as current filename
							// assign same id to game data
							if (gameJsoupDao.checkLastFileName().equals(fileName))
								id = gameJsoupDao.checkId(fileName);
							else {
								// if filename not matched, different(i.e new)
								// file is uploaded assign new id(i.e increase
								// id)
								id = gameJsoupDao.checkLastId();
								id = id + 1;
							}
						}
						
						//data is inserted into database
						gameJsoupDao.insert(id, progress + 1, fileName, playStoreDetails, apkSiteDetails);

						// end of database entry

						//socket listener which send file info to AJAX 
						server.addEventListener("send", Message.class, new DataListener<Message>() {
							@Override
							public void onData(SocketIOClient client, Message data, AckRequest ackSender)
									throws Exception {
								System.out.println("onSend: " + data.toString());
								data.setFileName(fileName);
								data.setFileSize(fileSize);
								System.out.println("progress:" + progress);
								data.setProgress(Integer.toString(progress));
								data.setTotalGames(Integer.toString(totoalGames));
								server.getBroadcastOperations().sendEvent("message", data);
							}
						});//end of socket listener
						
						System.out.println("For progress:" + progress);
					} // end of for
					
					String fileNameID = fileName.replace(".", Integer.toString(id) + ".");
					gameJsoupDao.update(fileNameID, id);
				} // end of else
				br.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
			// adding metaFile info to ArrayList files
			files.add(fileMeta);

			// concatenating ID with filename 
			String fileNameID = fileName.replace(".", Integer.toString(id) + ".");
			String downloadFileNameID = downloadFileName.replace(".", Integer.toString(id) + ".");

			File oldFile = new File("/home/bridgelabz6/Pictures/files/" + fileName);
			File newFile = new File("/home/bridgelabz6/Pictures/files/" + fileNameID);

			File oldDownloadfile = new File("/home/bridgelabz6/Pictures/files/" + downloadFileName);
			File newDownloadfile = new File("/home/bridgelabz6/Pictures/files/" + downloadFileNameID);

			// renaming old file name to new
			if (oldFile.renameTo(newFile))
				System.out.println("File renamed");
			else
				System.out.println("Sorry! File can't be renamed");

			// renaming old download file name to new
			fileMeta.setDownloadFileName(downloadFileNameID);
			if (oldDownloadfile.renameTo(newDownloadfile))
				System.out.println("File renamed");
			else
				System.out.println("Sorry! File can't be renamed");

			System.out.println("-----------End Of Program-----------");
		}

		//socket disconnect listener
		server.addDisconnectListener(new DisconnectListener() {
			@Override
			public void onDisconnect(SocketIOClient client) {
				server.stop();
				client.disconnect();
				System.out.println("onDisconnected");
			}
		});
		return files;
	}//End of creating JSOUP function

	/*-------------------------------------------Download file to local storage-------------------------------------------*/
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public void get(HttpServletResponse response) throws FileNotFoundException {
		try {

			// writing download file in byte
			System.out.println("after download file name:" + fileMeta.getDownloadFileName());
			File file = new File("/home/bridgelabz6/Pictures/files/" + fileMeta.getDownloadFileName());
			byte[] byteArray = new byte[(int) file.length()];
			byteArray = FileUtils.readFileToByteArray(file);
			// end of writing to bytes

			response.setContentType(fileMeta.getFileType());
			response.setHeader("Content-disposition",
					"attachment; filename=\"" + fileMeta.getDownloadFileName() + "\"");
			FileCopyUtils.copy(byteArray, response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//End of Downloading file function

}//End of class