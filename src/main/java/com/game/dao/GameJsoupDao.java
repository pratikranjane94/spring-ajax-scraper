package com.game.dao;

import java.util.ArrayList;

public interface GameJsoupDao {
public void add(ArrayList<String> playStoreDetails,ArrayList<String> apkSiteDetails);
public int check(String fileName,int id);
public void insert(int id,int no,String fileName,ArrayList<String> playStoreDetails,ArrayList<String> apkSiteDetails);
public void update(String fileName,int id);
public int checkId(String fileName);
}
