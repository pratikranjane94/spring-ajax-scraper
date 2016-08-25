package com.game.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import com.game.dao.GameJsoupDaoImp;
import com.game.dto.FileMeta;
import com.game.model.ApkSiteDataFetching;
import com.game.model.GameNotFound;
import com.game.model.PlayStoreDataFetching;
import com.game.model.PlayStoreUrlFetching;


@RestController
@EnableWebMvc
@RequestMapping("/ajaxcontroller")
@Controller
public class AjaxFileController {

	FileMeta fileMeta = null;
	ArrayList<FileMeta> files = new ArrayList<FileMeta>();
	PlayStoreUrlFetching purl = new PlayStoreUrlFetching();
	PlayStoreDataFetching psdf = new PlayStoreDataFetching();
	ApkSiteDataFetching asdf = new ApkSiteDataFetching();
	GameNotFound gameNotFound=new GameNotFound();
	
	@Resource(name="gameJsoupDao")
	private GameJsoupDaoImp gameJsoupDao;

	ArrayList<String> playStoreDetails = new ArrayList<String>();
	ArrayList<String> apkSiteDetails = new ArrayList<String>();
	int id=0;
	int no=0;
	String url = "";
	String line;
	String temp = "";
	int count;
	int totoalGames=0;
	boolean status = true;
	boolean psStatus = true;
	String fileName;
	String downloadFileName;
		
												//Creating JSOUP of Uploaded File
	@RequestMapping(value="/upload", method = RequestMethod.POST)
	public @ResponseBody ArrayList<FileMeta> upload(MultipartHttpServletRequest request, HttpServletResponse response) throws IOException {
		System.out.println("new ajax file controller");

		try {
			if(gameJsoupDao.checkProgress(fileName,id)!=totoalGames)
			files.remove(0);
		} catch (Exception e) {
			System.out.println("File not found");
		}
		//---------------Iterator for multiple files-----------------
		
		 Iterator<String> itr =  request.getFileNames();
		 MultipartFile mpf;
			
		 // get each file
		 while(itr.hasNext()){
			 
			 //get next multipart file and its information
			 
			 mpf = request.getFile(itr.next()); 
			 //System.out.println("file:"+request.getFile("files"));
			 //System.out.println("file Name"+request.getFile("files").getOriginalFilename());
			 System.out.println(mpf.getOriginalFilename() +" uploaded! "+files.size());	

			 //if files > 10 remove the first from the list
			 //if(files.size() >= 10)
				 //files.pop();
			 
			//---------------end of iterator for multiple files-----------------
			 
			 //Storing data in file meta
			 
			 fileMeta = new FileMeta();
			 fileMeta.setFileName(mpf.getOriginalFilename());
			 fileMeta.setFileSize(mpf.getSize()/1024+" Kb");
			 fileMeta.setFileType(mpf.getContentType());
			 fileMeta.setDownloadFileName(mpf.getOriginalFilename().replace(".", "Download."));
			 try {
				fileMeta.setBytes(mpf.getBytes());
				
				//Copy file to local disk
				FileCopyUtils.copy(mpf.getBytes(), new FileOutputStream("/home/bridgelabz6/Pictures/files/"+mpf.getOriginalFilename()));
				
				
				//Counting no of games in files
				 FileReader frCount = new FileReader("/home/bridgelabz6/Pictures/files/"+mpf.getOriginalFilename());
				 BufferedReader brCount = new BufferedReader(frCount);
					while (brCount.readLine()!=null) {
						count++;
						}
					totoalGames=count-1;
					count=0;
					fileMeta.setTotalGames(totoalGames);
					System.out.println("totoalGames:"+totoalGames);
					brCount.close();
				//end of counting game
					
				//----------------------------JSOUP Started--------------------------------
				
				FileReader fr = new FileReader("/home/bridgelabz6/Pictures/files/"+mpf.getOriginalFilename());
				BufferedReader br = new BufferedReader(fr);
				
				//storing filename and download filename
				fileName=mpf.getOriginalFilename();
				downloadFileName=mpf.getOriginalFilename().replace(".", "Download.");
				System.out.println("filname:"+fileName);
				
				//if database is empty assign id to zero for first file  
				if(gameJsoupDao.isEmpty())
					id=0;
				else
				{
					//if last filename is same as current filename assign same id to game data
					if(gameJsoupDao.checkLastFileName().equals(fileName))
						id=gameJsoupDao.checkId(fileName);
					else{
						//if different(i.e new) file is uploaded assign new id(i.e increase id)
						id=gameJsoupDao.checkLastId();
						id=id+1;
					}
				}
				
				if (br.readLine() == null) {
					System.out.println("file is empty");
				} else {
					
					//Until no of game completed are less than total games create JSOUP 
				    if(gameJsoupDao.checkProgress(fileName,id)<totoalGames){
				    	
				    	//checking last game completed and read next game name
				    	for (int i = 0; i < gameJsoupDao.checkProgress(fileName,id)+1; i++) {
							line=br.readLine();
						}
						temp = line;
						String[] gname = line.split("\\^");
						
						//Separates the game name from line read from file
						line=gname[1];			
						System.out.println("Game Name= " + line);	
						
						url = purl.findUrl(line); //getting URL for game
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
						psStatus = psdf.createCsv(playStoreDetails,downloadFileName);
						
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
						status = asdf.createCsv(apkSiteDetails,downloadFileName);
						
						// handling exception in APK site details
						if (status == false) {
							gameNotFound.addGameNotFoundInFile("DlApk", temp, downloadFileName);
						}
					} // end of if
				    
					no=gameJsoupDao.checkProgress(fileName,id);
					System.out.println("current progress:"+gameJsoupDao.checkProgress(fileName,id));
					//fileMeta.setProgress(no);
					//System.out.println("no increment:"+(no+1));
					
				} // end of else
				br.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			 
			 if(gameJsoupDao.checkProgress(fileName,id)>=totoalGames)
			 {
				 String fileNameID=fileName.concat(Integer.toString(id));
				 System.out.println("updated filename with id:"+fileNameID);
				 gameJsoupDao.update(fileNameID, id);
				//break;
				 return null;
			 }
			 else{
				 if(id==0){
					 id=gameJsoupDao.checkId(fileName);
					 id=id+1;
				 }
				 
				 gameJsoupDao.insert(id,no+1,fileName,playStoreDetails,apkSiteDetails);
			 }
			 fileMeta.setProgress(gameJsoupDao.checkProgress(fileName,id));
			 //add to files
			 files.add(fileMeta);
			 System.out.println("comparison"+gameJsoupDao.checkProgress(fileName,id)+"total games"+totoalGames);
			 System.out.println("End");
			 System.out.println("----------------------------------------End Of Program-------------------------------------------------");
		 }
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
	
	@RequestMapping(value="done",method=RequestMethod.POST)
	public void done(){
		System.out.println("done");
	}
 
}