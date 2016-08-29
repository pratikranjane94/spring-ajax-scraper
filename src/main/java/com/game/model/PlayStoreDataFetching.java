/*File Name		: PlayStoreDataFetching.java
 *Created By	: PRATIK RANJANE
 *Purpose		: Getting game information from PlayStore such as Game name, Version, Size,
 *				  Publish date, Package name and storing it into CSV file.
 * */

package com.game.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class PlayStoreDataFetching {
	
			/*-----------------------Scraping PlayStore site data---------------------------*/
	
	public ArrayList<String> getPlayStoreData(String url)
	{

		ArrayList<String> playStoreDetails = new ArrayList<String>();
		// ArrayList<String> err=new ArrayList<String>();
		try {
			// fetching the document over HTTP
			Document doc = Jsoup.connect(url).userAgent("Chrome/47.0.2526.80").timeout(10000).get();

			// getting game title class to fetch title
			Elements t = doc.getElementsByClass("document-title");

			// getting game info class to fetch version size publish date
			Elements g = doc.getElementsByClass("document-subtitle");
			Elements info = doc.getElementsByClass("meta-info");

			// getting game package name
			String pack = url.substring(url.indexOf("id=") + 3);

			// getting game info
			String title = t.select("[class=id-app-title]").text();
			String genre = g.select("[itemprop=genre]").text();
			String version = info.select("[itemprop=softwareVersion]").text();
			String size = info.select("[itemprop=fileSize]").text();
			String pDate = info.select("[itemprop=datePublished]").text();
			
			/*--------------LOGO and is game JSOUP---------*/
			/*			String[] cate=null;
			boolean found = false;
			String imageUrl=doc.getElementsByClass("cover-container").select("[itemprop=image]").attr("src");
			if(!imageurl.contains("http"))
				imageUrl=("http:").concat(imageUrl);
			System.out.println("image url"+imageUrl);
			String category=g.select("[class=document-subtitle category]").attr("href").toLowerCase();
			System.out.println("Category:"+category);
				if(category.contains("game"))
					found=true;
			System.out.println("cateogry link:"+category+" ,found :"+found);*/
			/*---------------------end-----------------*/
			
			/*if version with varies find version from description
			 * System.out.println("Checking:"+version.contains("Varies"));
			  if(version.equals("") || version.contains("Varies")==true){
					String newVer=doc.getElementsByClass("recent-change").text();
					System.err.println("old new version:"+newVer);
					newVer=newVer.substring(newVer.indexOf(".")-1, newVer.indexOf(".")+5).trim();
					version = newVer.replaceAll("[^0-9.]", ""); 
					System.out.println("new version:"+version);
			}*/

			//if no data fetched return null
			if (title.equals("") && genre.equals("") && version.equals("") && size.equals("") && pDate.equals("")
					&& pack.equals("")) {
				return null;
			} else {
				playStoreDetails.add(title);
				playStoreDetails.add(genre);
				playStoreDetails.add(size);
				playStoreDetails.add(version);
				playStoreDetails.add(pDate);
				playStoreDetails.add(pack);
				
				System.out.println("----------Play Store Data--------------");
				
				// showing game details
				System.out.println("Title of Game: " + title);
				System.out.println("Genre:" + genre);
				System.out.println("Version: " + version);
				System.out.println("File Size: " + size);
				System.out.println("Update date: " + pDate);
				System.out.println("Package Name:" + pack);
			}

		} catch (UnknownHostException u) {
			try {
				Thread.sleep(1000);	//wait for a second
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//if unknown host exception occurs call the same method again
			PlayStoreDataFetching asdf = new PlayStoreDataFetching();	
			asdf.getPlayStoreData(url);

		}

		catch (Exception e) {

			return null;
		}
		return playStoreDetails;
	}

	public boolean createCsv(ArrayList<String> playStoreDetails,String downloadFileName) // creating CSV file for play store data
	{
		String title = playStoreDetails.get(0);
		String genre = playStoreDetails.get(1);
		String size = playStoreDetails.get(2);
		String version = playStoreDetails.get(3);
		String pDate = playStoreDetails.get(4);
		String pack = playStoreDetails.get(5);
		try {
			File file = new File("/home/bridgelabz6/Pictures/files/"+downloadFileName); //adding data to CSV
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			System.out.println("file exists:"+file.exists());
			
			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
				bw.append("PlayStore Title");
				bw.append("^");
				bw.append("Genre");
				bw.append("^");
				bw.append("Size");
				bw.append("^");
				bw.append("Version");
				bw.append("^");
				bw.append("Publish Date");
				bw.append("^");
				bw.append("Package");
				bw.append("^");
				bw.append("^");
				bw.append("Apk Title");
				bw.append("^");
				bw.append("Genre");
				bw.append("^");
				bw.append("Size");
				bw.append("^");
				bw.append("Version");
				bw.append("^");
				bw.append("Publish Date");
				bw.append("^");
				bw.append("Download Link");
				bw.newLine();
			}

			//adding data to file
			bw.append(title);
			bw.append("^");
			bw.append(genre);
			bw.append("^");
			bw.append(size);
			bw.append("^");
			bw.append(version);
			bw.append("^");
			bw.append(pDate);
			bw.append("^");
			bw.append(pack);
			bw.append("^");
			bw.close();
			System.out.println("Done");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	// getting package name from PlayStore URL
	public String getPackage(ArrayList<String> playStoreDetails) {
		String pack = playStoreDetails.get(5); 							
		return pack;
	}

}
