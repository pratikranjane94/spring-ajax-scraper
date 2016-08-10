/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.game.socket;

public class Message {
	private String fileName;
	private String totalGames;
	private String progress;
    private String name;
    private String message;
    private String fileSize;
    

    public Message() {
    }

    public Message(String fileName, String totalGames, String progress) {
		super();
		this.fileName = fileName;
		this.totalGames = totalGames;
		this.progress = progress;
	}

	public Message(String name, String message) {
        this.name = name;
        this.message = message;
    }
	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getTotalGames() {
		return totalGames;
	}

	public void setTotalGames(String totalGames) {
		this.totalGames = totalGames;
	}

	public String getProgress() {
		return progress;
	}

	public void setProgress(String progress) {
		this.progress = progress;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	@Override
	public String toString() {
		return "Message [fileName=" + fileName + ", totalGames=" + totalGames + ", progress=" + progress + ", name="
				+ name + ", message=" + message + "]";
	}
    
}
