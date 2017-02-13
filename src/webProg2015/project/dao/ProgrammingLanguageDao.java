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

import webProg2015.project.model.ProgrammingLanguage;

public class ProgrammingLanguageDao {
	private HashMap<String, ProgrammingLanguage> allLanguages = new HashMap<String, ProgrammingLanguage>();
	private static ProgrammingLanguageDao instance = new ProgrammingLanguageDao();

	private ProgrammingLanguageDao() {
		BufferedReader in = null;
		try {
			File theDir = new File("./Data");

			if (!theDir.exists()) {
				 theDir.mkdir();
			}
			
			File file = new File("./Data/languages.txt");
			
			if(file.exists()){
				in = new BufferedReader(new InputStreamReader(
	                      new FileInputStream(file), "UTF-8"));
	
				readLanguagesFromFile(in);
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
	
	public static ProgrammingLanguageDao getInstance(){
		return instance;
	}

	private void readLanguagesFromFile(BufferedReader in) {
		String line = "";
		try {
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.equals(""))
					continue;
				
				ProgrammingLanguage programmingLanguage = new ProgrammingLanguage(line);
				allLanguages.put(line, programmingLanguage);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private boolean saveLanguagesToFile(){
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("./Data/languages.txt"), "UTF-8"));
			String line;
			for (ProgrammingLanguage u : allLanguages.values()) {
				line = u.getName();
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
	
	public synchronized boolean isLanguageAvailable(String language){
		return !allLanguages.containsKey(language);
	}
	
	public synchronized boolean addLanguage(ProgrammingLanguage u){
		if (!allLanguages.containsKey(u.getName())) {
			allLanguages.put(u.getName(), u);
			if(saveLanguagesToFile()){
				return true;
			}else{
				return false;
			}
		}else {
			return false;
		}
	}
	
	public synchronized ProgrammingLanguage getLanguage(String name){
		return allLanguages.get(name);
	}
	
	public synchronized Collection<ProgrammingLanguage>getAllLanguages(){
		return allLanguages.values();
	}
	
}
