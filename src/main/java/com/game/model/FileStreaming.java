package com.game.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;


public class FileStreaming 
{
	final int BUFFER_SIZE = 4096;
    
    public boolean downloadFile(String fileURL, String saveDir)
            throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestProperty("User-Agent", "Chrome/47.0.2526.80");
        int responseCode = httpConn.getResponseCode();
 
        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();
 
            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10,
                            disposition.length() - 1);
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                        fileURL.length());
            }
 
            System.out.println("Content-Type = " + contentType);
            System.out.println("Content-Disposition = " + disposition);
            System.out.println("Content-Length = " + contentLength);
            System.out.println("fileName = " + fileName);
            System.out.println("Downloading..");
            // opens input stream from the HTTP connection
            java.io.InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = saveDir + File.separator + fileName;
             
            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);
 
            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            
            outputStream.close();
            inputStream.close();
            httpConn.disconnect();
            System.out.println("File downloaded");
            return true;
        } else {
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
            return false;
        }
        
    }
    
    public boolean matchScrapers(JSONObject psObj,JSONObject apkObj)
    {
    int flag=0;
    try 
    {
		String title=psObj.getString("title");
		String genre=psObj.getString("genre");
		String version=psObj.getString("version");
		String size=psObj.getString("size");
		String date=psObj.getString("date");
		
		String dlTitle=apkObj.getString("dlTitle");
		String dlGenre=apkObj.getString("dlGenre");
		String dlVersion=apkObj.getString("dlVersion");
		String dlSize=apkObj.getString("dlSize");
		String dlDate=apkObj.getString("dlDate");
		
		if(title.equalsIgnoreCase(dlTitle) && genre.equalsIgnoreCase(dlGenre) && version.equalsIgnoreCase(dlVersion) && size.equalsIgnoreCase(dlSize) && date.equalsIgnoreCase(dlDate))
		flag=1;
	} catch (Exception e) {
		e.printStackTrace();
	} 
      if(flag==1)
    	  return true;
      else
		return false;
    	
    }
    
    
}
