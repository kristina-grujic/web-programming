package webProg2015.project.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import webProg2015.project.model.Comment;
import webProg2015.project.model.Rating;

public class RatingDao {
	
	private HashMap<String, Rating> allRatings = new HashMap<String, Rating>();
	private static RatingDao instance = new RatingDao();
	
	private RatingDao() {
		BufferedReader in = null;
		try {
			File theDir = new File("./Data");

			if (!theDir.exists()) {
				 theDir.mkdir();
			}
			
			readRatingsFromFile(in);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
				}
			}
		}
	}
	
	public static RatingDao getInstance(){
		return instance;
	}
	
	private void readRatingsFromFile(BufferedReader in) {
		try (ObjectInputStream ois
			= new ObjectInputStream(new FileInputStream("./Data/ratings.ser"))) {

			allRatings = (HashMap<String, Rating>) ois.readObject();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private boolean saveRatingsToFile(){
		try (ObjectOutputStream oos =
				new ObjectOutputStream(new FileOutputStream("./Data/ratings.ser"))) {
			oos.writeObject(allRatings);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public synchronized boolean addRating(Rating r){
//		allRatings.put(r, r);			
		if(saveRatingsToFile()){
			return true;
		}else{
			return false;
		}
		
	}

	public synchronized Rating getCommentRating(String commentId){;
		return allRatings.get(commentId);
	}
	 
}
