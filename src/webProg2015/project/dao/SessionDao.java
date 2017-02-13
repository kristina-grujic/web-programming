package webProg2015.project.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

import webProg2015.project.model.Session;

public class SessionDao {
	
	private HashMap<String, Session> allSessions = new HashMap<String, Session>();
	private static SessionDao instance = new SessionDao();
	
	private SessionDao() {
		BufferedReader in = null;
		try {
			File theDir = new File("./Data");

			if (!theDir.exists()) {
				 theDir.mkdir();
			}
			
			File file = new File("./Data/sessions.txt");
			
			if(file.exists()){
				in = new BufferedReader(new InputStreamReader(
	                      new FileInputStream(file), "UTF-8"));
	
				readSessionsFromFile(in);
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
	
	public static SessionDao getInstance(){
		return instance;
	}
	
	private void readSessionsFromFile(BufferedReader in) {
		String line, ssid = "", username = "", dateString = "";
		boolean persistent = false;
		SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy HH:mm:ss");
		Date lastLogInTime = null;
		Session s = null;
		StringTokenizer st;
		try {
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.equals(""))
					continue;
				st = new StringTokenizer(line, "~");
				while (st.hasMoreTokens()) {
					ssid = st.nextToken().trim();
					username = st.nextToken().trim();
					dateString = st.nextToken();
					lastLogInTime = sdf.parse(dateString);
					persistent = Boolean.parseBoolean(st.nextToken().trim());
				}
				s = new Session(ssid, username, lastLogInTime, persistent);
				allSessions.put(ssid, s);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	private boolean saveSessionsToFile(){
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("./Data/sessions.txt"), "UTF-8"));
			String line;
			SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy HH:mm:ss");
			for (Session s : allSessions.values()) {				
				line = s.getSessionId() + " ~ " + s.getUsername() + " ~ " + sdf.format(s.getLastLogInTime()) + " ~ " + s.isPersistent();
				out.write(line);
				out.newLine();
			}
			out.flush();
			out.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
		
	public synchronized boolean addSession(String ssid, String username, boolean remeberMe){
		if (!allSessions.containsKey(ssid)) {
			
			Session s = new Session(ssid, username, new Date(), remeberMe);

			allSessions.put(ssid, s);
			
			if(saveSessionsToFile()){
				return true;
			}else{
				return false;
			}
		}else {
			return false;
		}
	}
	
	public synchronized String getUsernameBySessionId(String ssid){
		if(allSessions.get(ssid)==null){
			return null;
		}
		allSessions.get(ssid).setLastLogInTime(new Date());
		saveSessionsToFile();
		return allSessions.get(ssid).getUsername();
	}

	public synchronized boolean removeSession(String ssid){
		if (allSessions.containsKey(ssid)) {
			allSessions.remove(ssid);
			if(saveSessionsToFile()){
				return true;
			}else{
				return false;
			}
		}else {
			return false;
		}		
	}
	
	public synchronized Collection<Session> readAllSesions(){
		return allSessions.values();
	}
}
