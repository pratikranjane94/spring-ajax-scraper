/*File Name		: ApkSiteDataFetching.java
 *Created By	: PRATIK RANJANE
 *Purpose		: Getting game information from APK-DL.com such as Game name, Version, Size,
 *				  Publish date, APK Download Link and storing it into CSV file.
 * */

package com.game.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class ApkSiteDataFetching {
	
	public ArrayList<String> createApkSiteDetails(String pack) {
		
		ArrayList<String> s = new ArrayList<String>();
		ArrayList<String> apkSiteDetails = new ArrayList<String>();
		
		String downUrl;
		String apk = "https://apk-dl.com/";
		
		//appending package name with APK-DL site
		String apkSite = apk.concat(pack);

		try {
			// fetch the document over HTTP
			Document doc = Jsoup.connect(apkSite).userAgent("Chrome/47.0.2526.80").timeout(10000).get();

			// getting info class to fetch genre title version and publish date
			Elements info = doc.getElementsByClass("info");

			// getting meta data to fetch file size
			String info1 = doc.getElementsByClass("info").select("div:has(span:contains(File Size:))").text();

			// getting data from info class
			String genre = info.select("[itemprop=applicationCategory]").attr("content"); 	// getting genre
			String title = info.select("[itemprop=name]").attr("content"); 					// getting title
			String version = info.select("[itemprop=softwareVersion]").attr("content"); 	// getting version
			String pDate = info.select("[itemprop=datePublished]").attr("content"); 		// getting publish date

			pDate = pDate.replace(",", " "); // replacing comma in date with space

			//getting game size
			StringTokenizer st = new StringTokenizer(info1, ": "); // TOKENIZER to find size
			while (st.hasMoreTokens()) {
				s.add(st.nextToken()); // adding words from meta data to array list
			}
			int i = s.indexOf("Size");
			String size = s.get(i + 1); // end of getting size

			//getting download link
			String downLink = doc.getElementsByClass("btn-md").attr("href");
			if (downLink.contains("http") == false) // checking whether download URL/link contains "HTTP"
				downLink = ("http:").concat(downLink.trim());

			//scraping download URL to get download link
			Document doc1 = Jsoup.connect(downLink).userAgent("Chrome/47.0.2526.80").timeout(10000).get();
			downUrl = doc1.getElementsByTag("p").select("a[href]").attr("href");
			if (downUrl != "") {
				if (downUrl.contains("http") == false) // adding "HTTP" to link if not present
				{
					downUrl = ("http:").concat(downUrl);
				}
			} else {
				downUrl = downUrl.replaceAll(downUrl, "No download Link or paid app"); // no download link present
			} //end of getting download link
			
			//if no data fetched
			if (title.equals("") && genre.equals("") && version.equals("") && size.equals("") && pDate.equals("")) {
				return null;
			} else {
				apkSiteDetails.add(title);
				apkSiteDetails.add(genre);
				apkSiteDetails.add(size);
				apkSiteDetails.add(version);
				apkSiteDetails.add(pDate);
				apkSiteDetails.add(downUrl);
				
				System.out.println("----------Dl-apk site data--------------");
				
				// displaying game info
				System.out.println("Title: " + title);
				System.out.println("Apk Site genre: " + genre);
				System.out.println("Version: " + version);
				System.out.println("Published Date: " + pDate);
				System.out.println("Size: " + size);
				System.out.println("Download Link:" + downUrl);
			}
		} catch (UnknownHostException u) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			ApkSiteDataFetching as = new ApkSiteDataFetching();
			as.createApkSiteDetails(pack);
		} catch (Exception e) {

			return null;
		}
		return apkSiteDetails;
	}

					// Creating JSON file of fetched info
	public boolean createCsv(ArrayList<String> apkSiteDetails,String downloadFileName) 	
	{
			try {
			String title = apkSiteDetails.get(0);
			String genre = apkSiteDetails.get(1);
			String size = apkSiteDetails.get(2);
			String version = apkSiteDetails.get(3);
			String pDate = apkSiteDetails.get(4);
			String downUrl = apkSiteDetails.get(5);

			File file = new File("/home/bridgelabz6/Pictures/files/"+downloadFileName);
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			
			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			} else {
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
				bw.append(downUrl);
				bw.append("^");
				if (downUrl.contains("http://dl3.apk-dl.com/store/download?id")) {
					System.out.println("inside if");
					bw.append("Broken Link");
				}
				bw.newLine();
				bw.close();

				System.out.println("Done");
			}
		}

		catch (Exception e) {
			return false;
		}
		return true;
	}
	/*
	 * public String getDownloadLink(ArrayList<String> apkSiteDetails) { String
	 * url=""; String downLink; try { downLink=apkSiteDetails.get(5); Document
	 * doc1 =
	 * Jsoup.connect(downLink).userAgent("Chrome/47.0.2526.80").timeout(10000).
	 * get();
	 * 
	 * url=doc1.getElementsByTag("p").select("a[href]").attr("href"); //
	 * System.out.println("url:"+url); } catch(Exception e) {
	 * e.printStackTrace(); } return url; }
	 */

}
