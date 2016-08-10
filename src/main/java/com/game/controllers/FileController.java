package com.game.controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
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
/*import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;*/
import com.game.dao.GameJsoupDaoImp;
import com.game.dto.FileMeta;
import com.game.model.ApkSiteDataFetching;
import com.game.model.PlayStoreDataFetching;
import com.game.model.PlayStoreUrlFetching;

import com.game.socket.Message;


@RestController
@EnableWebMvc
@RequestMapping("/controller")
public class FileController {

	LinkedList<FileMeta> files = new LinkedList<FileMeta>();
	
	FileMeta fileMeta = null;
	PlayStoreUrlFetching purl = new PlayStoreUrlFetching();
	PlayStoreDataFetching psdf = new PlayStoreDataFetching();
	ApkSiteDataFetching asdf = new ApkSiteDataFetching();
	
	@Autowired
	GameJsoupDaoImp gameJsoupDaoImp=new GameJsoupDaoImp();

	ArrayList<String> playStoreDetails = new ArrayList<String>();
	ArrayList<String> apkSiteDetails = new ArrayList<String>();

	String url = "";
	String line;
	String temp = "";
	String msg="started";
	int progress = 0;
	int totoalGames=0;
	boolean status = true;
	boolean psStatus = true;
		
	//uploading file to local disk

	@RequestMapping(value="/upload", method = RequestMethod.POST)
	public @ResponseBody LinkedList<FileMeta> upload(MultipartHttpServletRequest request, HttpServletResponse response) throws IOException {
		/*---------------web socket connection----------*/
		Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(3400);
        final SocketIOServer server = new SocketIOServer(config);
        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient client) {
                System.out.println("onConnected");
                client.sendEvent("message", new Message("hello", "Welcome to the chat!","0"));
            }
        });
        System.out.println("Starting server...");
        server.start();
        System.out.println("Server started");
        /*---------------end of web socket connection----------*/
        
        
		/*---------------iterator for multiple files-----------------*/
		
		 Iterator<String> itr =  request.getFileNames();
		 MultipartFile mpf = null;
			
		 // get each file
		 while(itr.hasNext()){
			 
			 //get next multipart file and its information
			 
			 mpf = request.getFile(itr.next()); 
			 //System.out.println("file:"+request.getFile("files"));
			// System.out.println("file Name"+request.getFile("files").getOriginalFilename());
			 System.out.println(mpf.getOriginalFilename() +" uploaded! "+files.size());	

			 //if files > 10 remove the first from the list
			 if(files.size() >= 10)
				 files.pop();
			 
			/*---------------end of iterator for multiple files-----------------*/
			 final String fileSize=mpf.getSize()/1024+" Kb";
			 //storing data in file meta
			 fileMeta = new FileMeta();
			 fileMeta.setFileName(mpf.getOriginalFilename());
			 fileMeta.setFileSize(mpf.getSize()/1024+" Kb");
			 fileMeta.setFileType(mpf.getContentType());
			 fileMeta.setDownloadFileName(mpf.getOriginalFilename().replace(".", "Download."));
			 try {
				fileMeta.setBytes(mpf.getBytes());
				
				// copy file to local disk
				FileCopyUtils.copy(mpf.getBytes(), new FileOutputStream("/home/bridgelabz6/Pictures/files/"+mpf.getOriginalFilename()));
				
				
				//counting no of games
				 FileReader frCount = new FileReader("/home/bridgelabz6/Pictures/files/"+mpf.getOriginalFilename());
				 BufferedReader brCount = new BufferedReader(frCount);
					while (brCount.readLine()!=null) {
						totoalGames++;
						}
					totoalGames=totoalGames-1;
					fileMeta.setTotalGames(totoalGames);
					System.out.println("totoalGames:"+totoalGames);
					brCount.close();
				//end of counting game
					
				/*----------------------------jsoup started--------------------------------*/
				
				FileReader fr = new FileReader("/home/bridgelabz6/Pictures/files/"+mpf.getOriginalFilename());
				BufferedReader br = new BufferedReader(fr);
				final String fileName=mpf.getOriginalFilename();
				
				line = br.readLine();
				if (line == null) {
					System.out.println("file is empty");
				} else {
					line=br.readLine();					
					while (line != null) {
						server.addEventListener("send", Message.class, new DataListener<Message>() {
				            @Override
				            public void onData(SocketIOClient client, Message data, AckRequest ackSender) throws Exception {
				                System.out.println("onSend: " + data.toString());
				                data.setFileName(fileName);
				                data.setFileSize(fileSize);
				                data.setName("File Progress");
				                System.out.println("Socket progress:"+Integer.toString(progress));
				                data.setProgress(Integer.toString(progress));
				                data.setTotalGames(Integer.toString(totoalGames));
				                server.getBroadcastOperations().sendEvent("message", data);
				            }
				        });
				        
						fileMeta.setProgress(progress);
						
						temp = line;
						String[] gname = line.split("\\^");					
						line=gname[1];			
						System.out.println("Game Name= " + line);	//read games name
						
						url = purl.findUrl(line); //getting url for game
						line = br.readLine();

						// exception handling if url not found
						if (url == null) {
							try {
								File notFetched = new File("/home/bridgelabz6/Pictures/files/UrlnotFetched.csv");

								// if file doesn't exists, then create it
								if (!notFetched.exists()) {
									notFetched.createNewFile();
								}

								FileWriter fw = new FileWriter(notFetched.getAbsoluteFile(), true);
								BufferedWriter bw = new BufferedWriter(fw);
								bw.append(temp);
								bw.append("^");
								bw.newLine();
								bw.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
							continue;
						} // end of handling in url fetching

						// getting play store site data
						playStoreDetails = psdf.getPlayStoreData(url);

						// handling exception in playstore details
						if (playStoreDetails.equals(null)) {
							try {
								File notFetched = new File("/home/bridgelabz6/Pictures/files/PlayStoreNotFetched.csv");

								// if file doesn't exists, then create it
								if (!notFetched.exists()) {
									notFetched.createNewFile();
								}

								FileWriter fw = new FileWriter(notFetched.getAbsoluteFile(), true);
								BufferedWriter bw = new BufferedWriter(fw);
								bw.append(temp);
								bw.append("^");
								bw.newLine();
								bw.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
							continue;
						} // end of handling in play store details

						// creating csv file of play store data
						psStatus = psdf.createCsv(playStoreDetails,mpf.getOriginalFilename());
						
						if (psStatus == false) {
							try {
								File notFetched = new File("/home/bridgelabz6/Pictures/files/PlayStoreNotFetched.csv");
								
								//adding next line in download file if exception occurs
								File asd = new File("/home/bridgelabz6/Pictures/files/"+mpf.getOriginalFilename().replace(".", "Download."));
								
								// if file doesn't exists, then create it
								if (!notFetched.exists()) {
									notFetched.createNewFile();
								}
								if (!asd.exists()) {
									asd.createNewFile();
								}
								FileWriter asdfw = new FileWriter(asd.getAbsoluteFile(), true);
								BufferedWriter asdbw = new BufferedWriter(asdfw);
								FileWriter fw = new FileWriter(notFetched.getAbsoluteFile(), true);
								BufferedWriter bw = new BufferedWriter(fw);
								
								//if play store data not found append new line in csv file
								bw.append(temp);
								bw.append("^");
								bw.newLine();
								asdbw.newLine();
								bw.close();
								asdbw.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
							continue;
						}

						// getting play store package name
						String pack = psdf.getPackage(playStoreDetails);

						// getting apk-dl site data
						apkSiteDetails = asdf.createApkSiteDetails(pack);

						// handling exception in apk site details
						if (apkSiteDetails == null) {
							try {
								File notFetched = new File("/home/bridgelabz6/Pictures/files/apkDlNotFetched.csv");
								File asd = new File("/home/bridgelabz6/Pictures/files/"+mpf.getOriginalFilename().replace(".", "Download."));
								// if file doesn't exists, then create it
								if (!notFetched.exists()) {
									notFetched.createNewFile();
								}
								if (!asd.exists()) {
									asd.createNewFile();
								}
								FileWriter asdfw = new FileWriter(asd.getAbsoluteFile(), true);
								BufferedWriter asdbw = new BufferedWriter(asdfw);
								FileWriter fw = new FileWriter(notFetched.getAbsoluteFile(), true);
								BufferedWriter bw = new BufferedWriter(fw);
								bw.append(temp);
								bw.append("^");
								bw.newLine();
								asdbw.newLine();
								bw.close();
								asdbw.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
							continue;
						} // end of handling in apk-dl

						
						// creating csv file of apk-dl site details
						status = asdf.createCsv(apkSiteDetails,mpf.getOriginalFilename());
						
						if (status == false) {
							try {
								File notFetched = new File("/home/bridgelabz6/Pictures/files/apkDlNotFetched.csv");
								File asd = new File("/home/bridgelabz6/Pictures/files/"+mpf.getOriginalFilename().replace(".", "Download."));
								// if file doesn't exists, then create it
								if (!notFetched.exists()) {
									notFetched.createNewFile();
								}
								if (!asd.exists()) {
									asd.createNewFile();
								}
								FileWriter asdfw = new FileWriter(asd.getAbsoluteFile(), true);
								BufferedWriter asdbw = new BufferedWriter(asdfw);
								FileWriter fw = new FileWriter(notFetched.getAbsoluteFile(), true);
								BufferedWriter bw = new BufferedWriter(fw);
								bw.append(temp);
								bw.append("^");
								bw.newLine();
								asdbw.newLine();
								bw.close();
								asdbw.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
							continue;
						}
						gameJsoupDaoImp.add(playStoreDetails, apkSiteDetails);//storing jsoup data in database
						progress++;
						System.out.println("Main progress:"+Integer.toString(progress));
					} // end of while
				} // end of else
				br.close();
		
			} catch (IOException e) {
				e.printStackTrace();
			}
			 //add to files
			 files.add(fileMeta);
			//totoalGames=0; 
			//progress=0;
			 System.out.println("End");
		 }
		 server.stop();
	        server.addDisconnectListener(new DisconnectListener() {
	            @Override
	            public void onDisconnect(SocketIOClient client) {
	                System.out.println("onDisconnected");
	            }
	        });
		return files;	
	}

	//displaying file to download
	@RequestMapping(value = "/get/{value}", method = RequestMethod.GET)
	 public void get(HttpServletResponse response,@PathVariable String value) throws FileNotFoundException{
		 FileMeta getFile = files.get(Integer.parseInt(value));
		 
		 try {		
			 //writing download file in byte
			 	File file = new File("/home/bridgelabz6/Pictures/files/"+getFile.getDownloadFileName());
			    byte[] byteArray = new byte[(int) file.length()];
			    byteArray = FileUtils.readFileToByteArray(file);
			    //end of writing to bytes  
			    
			 	response.setContentType(getFile.getFileType());
			 	response.setHeader("Content-disposition", "attachment; filename=\""+getFile.getDownloadFileName()+"\"");
		        FileCopyUtils.copy(byteArray, response.getOutputStream());
		 }catch (IOException e) {
				e.printStackTrace();
		 }
	 }
 
}
