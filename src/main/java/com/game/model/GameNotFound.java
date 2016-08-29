/*File Name		: GameNotFound.java
 *Created By	: PRATIK RANJANE
 *Purpose		: Handling Exceptions ,storing name of games in other files that 
 *					have some exception.
 * */

package com.game.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class GameNotFound {
	public void addGameNotFoundInFile(String fileName,String temp,String downloadFileName)
	{
		try {
			//adding game name who's data is not fetched
			File notFetched = new File("/home/bridgelabz6/Pictures/files/"+fileName+"StoreNotFetched.csv");
			FileWriter fw = new FileWriter(notFetched.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			
			//adding next line in download file if exception occurs
			File asd = new File("/home/bridgelabz6/Pictures/files/"+downloadFileName);
			FileWriter asdfw = new FileWriter(asd.getAbsoluteFile(), true);
			BufferedWriter asdbw = new BufferedWriter(asdfw);
			
			// if file doesn't exists, then create it
			if (!notFetched.exists()) {
				notFetched.createNewFile();
			}
			if (!asd.exists()) {
				asd.createNewFile();
			}

			//if JSOUP data not found store game name in game not found csv file
			bw.append(temp);
			bw.append("^");
			bw.newLine();
			
			//adding new line in CSV file if data not found for alignment
			asdbw.newLine();
			
			bw.close();
			asdbw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//adding game name if URL or PlayStore data is not fetched
	public void addGameNotFound(String fileName,String temp)
	{
		try {
			File notFetched = new File("/home/bridgelabz6/Pictures/files/"+fileName+"notFetched.csv");

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
			System.out.println("1");
		}
	}
	
}
