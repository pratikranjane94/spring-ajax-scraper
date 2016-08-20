package com.game.dao;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import com.game.dto.Count;
import com.game.dto.JsoupData;

@Repository("gameJsoupDao")
public class GameJsoupDaoImp implements GameJsoupDao {
	@Resource(name = "sessionFactory")
	SessionFactory sessionFactory;
	Session session;

	public void add(ArrayList<String> playStoreDetails, ArrayList<String> apkSiteDetails) {

		JsoupData jsoupData = new JsoupData();
		session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();

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

	public void insert(int id, int no, String fileName, ArrayList<String> playStoreDetails,
			ArrayList<String> apkSiteDetails) {
		session = sessionFactory.openSession();
		Transaction tr = session.beginTransaction();
		Count count = new Count();
		count.setId(id);
		count.setNo(no);
		count.setFileName(fileName);
		count.setTitle(playStoreDetails.get(0));
		count.setGenre(playStoreDetails.get(1));
		count.setSize(playStoreDetails.get(2));
		count.setVersion(playStoreDetails.get(3));
		count.setPublishDate(playStoreDetails.get(4));
		count.setPackageName(playStoreDetails.get(5));
		if (apkSiteDetails != null) {
			try{
			count.setDlTitle(apkSiteDetails.get(0));
			count.setDlGenre(apkSiteDetails.get(1));
			count.setDlSize(apkSiteDetails.get(2));
			count.setDlVersion(apkSiteDetails.get(3));
			count.setDlPublishDate(apkSiteDetails.get(4));
			count.setDownloadLink(apkSiteDetails.get(5));
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		session.save(count);
		tr.commit();
		System.out.println("Jsoup data stored in database");
	}

	// gives last progress of given file
	@SuppressWarnings("rawtypes")
	public int check(String fileName, int id) {
		Count count = new Count();
		int tot = 0;
		session = sessionFactory.openSession();
		Query query = session.createQuery("from Count where fileName=:fileName and id=:id order by no DESC");
		query.setString("fileName", fileName);
		query.setInteger("id", id);
		query.setMaxResults(1);
		// System.out.println("Last progress:"+query);
		List list = query.list();
		for (int i = 0; i < list.size(); i++) {
			count = (Count) list.get(i);
		}
		tot = count.getNo();
		System.out.println("total Progress(db):" + tot);
		return tot;
	}

	// gives last id of given file
	@SuppressWarnings("rawtypes")
	public int checkId(String fileName) {
		Count count = new Count();
		int id = 0;
		session = sessionFactory.openSession();
		Query query = session.createQuery("from Count where fileName=:fileName order by no DESC");
		query.setString("fileName", fileName);
		query.setMaxResults(1);
		// System.out.println("Last progress:"+query);
		List list = query.list();
		for (int i = 0; i < list.size(); i++) {
			count = (Count) list.get(i);
		}
		id = count.getId();
		System.out.println("Last id(db):" + id);
		return id;
	}
	//checks last id in database
	@SuppressWarnings("rawtypes")
	public int checkLastId() {
		Count count = new Count();
		int id = 0;
		session = sessionFactory.openSession();
		Query query = session.createQuery("from Count order by no DESC");
		query.setMaxResults(1);
		// System.out.println("Last progress:"+query);
		List list = query.list();
		for (int i = 0; i < list.size(); i++) {
			count = (Count) list.get(i);
		}
		id = count.getId();
		System.out.println("last id in database regardless of filename:" + id);
		return id;
	}

	// gives last file name from database
	@SuppressWarnings("rawtypes")
	public String checkLastFileName() {
		Count count = new Count();
		String lastFileName;
		session = sessionFactory.openSession();
		Query query = session.createQuery("from Count order by no DESC");
		query.setMaxResults(1);
		List list = query.list();
		for (int i = 0; i < list.size(); i++) {
			count = (Count) list.get(i);
		}
		lastFileName = count.getFileName();
		return lastFileName;
	}

	//returns whether database is empty or not
	@SuppressWarnings("rawtypes")
	public boolean isEmpty() {
		session = sessionFactory.openSession();
		Query query = session.createQuery("from Count");
		List list = query.list();
		System.out.println("Is empty:" + list.size());
		if (list.size() <= 0)
			return true;
		else
			return false;
	}

	public void update(String fileName,int id) {
		session = sessionFactory.openSession();
		Query update = session.createQuery("update Count set filename=:filename where id=:id");
		update.setString("filename", fileName);
		update.setInteger("id", id);
		update.executeUpdate();
		System.out.println("Updated:");
	}
}
