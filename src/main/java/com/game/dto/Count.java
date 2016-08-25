package com.game.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class Count {

@Id
private int id;
@Column
private int no;
@Column
private String fileName;
@Column
private String title;
@Column
private String genre;
@Column
private String size;
@Column
private String version;
@Column
private String publishDate;
@Column
private String packageName;
@Column
private String dlTitle;
@Column
private String dlGenre;
@Column
private String dlSize;
@Column
private String dlVersion;
@Column
private String dlPublishDate;
@Column
private String downloadLink;

public String getFileName() {
	return fileName;
}

public void setFileName(String fileName) {
	this.fileName = fileName;
}

public int getNo() {
	return no;
}

public void setNo(int no) {
	this.no = no;
}

public int getId() {
	return id;
}

public void setId(int id) {
	this.id = id;
}

public String getTitle() {
	return title;
}
public void setTitle(String title) {
	this.title = title;
}
public String getGenre() {
	return genre;
}
public void setGenre(String genre) {
	this.genre = genre;
}
public String getSize() {
	return size;
}
public void setSize(String size) {
	this.size = size;
}
public String getVersion() {
	return version;
}
public void setVersion(String version) {
	this.version = version;
}
public String getPublishDate() {
	return publishDate;
}
public void setPublishDate(String publishDate) {
	this.publishDate = publishDate;
}
public String getPackageName() {
	return packageName;
}
public void setPackageName(String packageName) {
	this.packageName = packageName;
}
public String getDlTitle() {
	return dlTitle;
}
public void setDlTitle(String dlTitle) {
	this.dlTitle = dlTitle;
}
public String getDlGenre() {
	return dlGenre;
}
public void setDlGenre(String dlGenre) {
	this.dlGenre = dlGenre;
}
public String getDlSize() {
	return dlSize;
}
public void setDlSize(String dlSize) {
	this.dlSize = dlSize;
}
public String getDlVersion() {
	return dlVersion;
}
public void setDlVersion(String dlVersion) {
	this.dlVersion = dlVersion;
}
public String getDlPublishDate() {
	return dlPublishDate;
}
public void setDlPublishDate(String dlPublishDate) {
	this.dlPublishDate = dlPublishDate;
}
public String getDownloadLink() {
	return downloadLink;
}
public void setDownloadLink(String downloadLink) {
	this.downloadLink = downloadLink;
}

@Override
public String toString() {
	return "Count [id=" + id + ", no=" + no + ", fileName=" + fileName + ", title=" + title + "]";
}

}
