package webProg2015.project.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

import webProg2015.project.model.Person;

public class PersonDao {
	private HashMap<String, Person> allUsers = new HashMap<String, Person>();
	private HashMap<String, Person> allAdmins = new HashMap<String, Person>();
	private static PersonDao instance = new PersonDao();

	private PersonDao() {
		BufferedReader in = null;
		try {
			File theDir = new File("./Data");

			if (!theDir.exists()) {
				 theDir.mkdir();
			}
			
			File file = new File("./Data/persons.txt");
			
			if(file.exists()){
				in = new BufferedReader(new InputStreamReader(
	                      new FileInputStream(file), "UTF-8"));
	
				readUsersFromFile(in);
			}
			
			File adminFile = new File("./Data/admins.txt");
			
			if(file.exists()){
				in = new BufferedReader(new InputStreamReader(
	                      new FileInputStream(adminFile), "UTF-8"));
	
				readAdminsFromFile(in);
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
	
	public static PersonDao getInstance(){
		return instance;
	}

	private void readUsersFromFile(BufferedReader in) {
		String line, username = "", password = "", firstName = "", lastName = "", telephone = "", email = "",
				imageName = "", role = "", address = "";
		boolean blocked = false;
		StringTokenizer st;
		try {
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.equals(""))
					continue;
				st = new StringTokenizer(line, "~");
				while (st.hasMoreTokens()) {
					username = st.nextToken().trim();
					password = st.nextToken().trim();
					firstName = st.nextToken().trim();
					lastName = st.nextToken().trim();
					email = st.nextToken().trim();
					telephone = st.nextToken().trim();
					address = st.nextToken().trim();
					imageName = st.nextToken().trim();
					System.out.println(imageName);
					role = st.nextToken().trim();
					System.out.println(role);
					blocked = st.nextToken().trim().equals("true");
				}
				Person user = new Person(username, password, firstName, lastName, email, telephone, address, imageName, role, blocked);
				allUsers.put(username, user);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void readAdminsFromFile(BufferedReader in) {
		String line, username = "", password = "", firstName = "", lastName = "", telephone = "", email = "",
				imageName = "", role = "", address = "";
		boolean blocked = false;
		StringTokenizer st;
		try {
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.equals(""))
					continue;
				st = new StringTokenizer(line, "~");
				while (st.hasMoreTokens()) {
					username = st.nextToken().trim();
					password = st.nextToken().trim();
					firstName = st.nextToken().trim();
					lastName = st.nextToken().trim();
					email = st.nextToken().trim();
					telephone = st.nextToken().trim();
					address = st.nextToken().trim();
					imageName = st.nextToken().trim();
					System.out.println(imageName);
					role = st.nextToken().trim();
					System.out.println(role);
					blocked = st.nextToken().trim().equals("true");
				}
				Person user = new Person(username, password, firstName, lastName, email, telephone, address, imageName, role, blocked);
				allAdmins.put(username, user);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	private boolean saveUsersToFile(){
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("./Data/persons.txt"), "UTF-8"));
			String line;
			for (Person u : allUsers.values()) {
				line = u.getUsername() + " ~ " + u.getPassword() + " ~ " + u.getFirsName() + " ~ " + u.getLastName() + " ~ "
						+ u.getEmail() + " ~ " + u.getTelephone() + " ~ " + u.getAddress() + " ~ " + u.getImageName() + " ~ "
						+ u.getRole() + " ~ " + u.getBlocked();
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
	
//	private boolean saveAdminsToFile(){
//		BufferedWriter out = null;
//		try {
//			out = new BufferedWriter(new OutputStreamWriter(
//					new FileOutputStream("./Data/admins.txt"), "UTF-8"));
//			String line;
//			for (Person u : allAdmins.values()) {
//				line = u.getUsername() + " ~ " + u.getPassword() + " ~ " + u.getFirsName() + " ~ " + u.getLastName() + " ~ "
//						+ u.getEmail() + " ~ " + u.getTelephone() + " ~ " + u.getAddress() + " ~ " + u.getImageName() + " ~ "
//						+ u.getRole() + " ~ " + u.getBlocked();
//				out.write(line);
//				out.newLine();
//			}
//			out.flush();
//			out.close();
//			return true;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
	
	public synchronized boolean isUsernameAvailable(String username){
		return !allUsers.containsKey(username) && !allAdmins.containsKey(username);
	}
	
	public synchronized boolean addUser(Person u){
		if (isUsernameAvailable(u.getUsername())) {
			allUsers.put(u.getUsername(), u);
			if(saveUsersToFile()){
				return true;
			}else{
				return false;
			}
		}else {
			return false;
		}
	}
	
	public synchronized Person getUser(String username){
		Person p = allUsers.get(username);
		if ( p == null) {
			System.out.println(username);
			p = allAdmins.get(username);
			System.out.println(allAdmins.toString());
			System.out.println(p);
		}
		return p;
	}
	
	public synchronized Collection<Person> getUsers(String username){
		HashMap<String, Person> result = new HashMap<String, Person>();
		
		for (Person p : allUsers.values()) {
			if (p.getUsername().contains(username)) {
				result.put(p.getUsername(), p);
			}
		}
		
		for (Person p : allAdmins.values()) {
			if (p.getUsername().contains(username)) {
				result.put(p.getUsername(), p);
			}
		}
		return result.values();
	}
	
	public synchronized Collection<Person> getAllUsers(){
		HashMap<String, Person> result = new HashMap<String, Person>();
		for (Person p : allUsers.values()) {
			result.put(p.getUsername(), p);
		}
		for (Person p : allAdmins.values()) {
			result.put(p.getUsername(), p);
		}
		return result.values();
	}
	
	public synchronized boolean updateUser(Person u){
		if (allUsers.containsKey(u.getUsername())) {
			allUsers.remove(u.getUsername());
			allUsers.put(u.getUsername(), u);
			if(saveUsersToFile()){
				return true;
			}else{
				return false;
			}
		}else {
			return false;
		}	
	}
}
