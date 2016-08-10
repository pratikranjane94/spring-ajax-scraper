package com.game.dao;

import java.util.ArrayList;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import com.game.dto.JsoupData;

@Repository("gameJsoupDao")
public class GameJsoupDaoImp implements GameJsoupDao{
	@Resource(name="sessionFactory")
	SessionFactory sessionFactory;
	Session session;
	public void add(ArrayList<String> playStoreDetails,ArrayList<String> apkSiteDetails) {
		
		JsoupData jsoupData=new JsoupData();
		session=sessionFactory.openSession();
		Transaction transaction=session.beginTransaction();
		
		jsoupData.setTitle(playStoreDetails.get(0));
		jsoupData.setGenre(playStoreDetails.get(1));
		jsoupData.setSize(playStoreDetails.get(2));
		jsoupData.setVersion(playStoreDetails.get(3));
		jsoupData.setPublishDate(playStoreDetails.get(4));
		jsoupData.setPackageName(playStoreDetails.get(5));
		
		jsoupData.setDlTitle(apkSiteDetails.get(0));
		jsoupData.setDlGenre(apkSiteDetails.get(1));
		jsoupData.setDlSize(apkSiteDetails.get(2));
		jsoupData.setDlVersion(apkSiteDetails.get(3));
		jsoupData.setDlPublishDate(apkSiteDetails.get(4));
		jsoupData.setDownloadLink(apkSiteDetails.get(5));
		session.save(jsoupData);
		transaction.commit();
		
		
	}

}
