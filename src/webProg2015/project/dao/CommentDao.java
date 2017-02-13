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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

import webProg2015.project.model.Comment;
import webProg2015.project.model.Person;
import webProg2015.project.model.ProgrammingLanguage;
import webProg2015.project.model.Snippet;

public class CommentDao {
	private HashMap<String, Comment> allComments = new HashMap<String, Comment>();
	private static CommentDao instance = new CommentDao();

	private CommentDao() {
		BufferedReader in = null;
		try {
			File theDir = new File("./Data");

			if (!theDir.exists()) {
				 theDir.mkdir();
			}
			
			File file = new File("./Data/comments.txt");
			
			if(file.exists()){
				in = new BufferedReader(new InputStreamReader(
	                      new FileInputStream(file), "UTF-8"));
	
				readCommentsFromFile(in);
			}
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
	
	public static CommentDao getInstance(){
		return instance;
	}

	private void readCommentsFromFile(BufferedReader in) {
		try (ObjectInputStream ois
				= new ObjectInputStream(new FileInputStream("./Data/comments.ser"))) {

				allComments = (HashMap<String, Comment>) ois.readObject();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
	}

	private boolean saveCommentsToFile(){
		try (ObjectOutputStream oos =
				new ObjectOutputStream(new FileOutputStream("./Data/comments.ser"))) {
			oos.writeObject(allComments);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	public synchronized boolean addComment(Comment u){
		allComments.put(u.getId(), u);
		if(saveCommentsToFile()){
			return true;
		}else{
			return false;
		}
	}
	
	public synchronized boolean removeComment(String id){
		allComments.remove(id);
		if (saveCommentsToFile()) {
			return true;
		} else {
			return false;
		}
	}
	
	public synchronized Comment getComment(String id){
		return allComments.get(id);
	}
	
	
	public synchronized Collection<Comment>getAllComments(){
		return allComments.values();
	}
	
	public synchronized Collection<Comment>getSnippetComments(String snippetId){
		HashMap<String, Comment> result = new HashMap<String, Comment>();
		for (Comment c : allComments.values()) {
			if (c.getSnippet().getId().equals(snippetId)) {
				result.put(c.getId(), c);
			}
		}
		return result.values();
	}
	
	public synchronized boolean updateComment(Comment c){
		if (allComments.containsKey(c.getId())) {
			allComments.remove(c.getId());
			allComments.put(c.getId(), c);
			if(saveCommentsToFile()){
				return true;
			}else{
				return false;
			}
		}else {
			return false;
		}	
	}
}
