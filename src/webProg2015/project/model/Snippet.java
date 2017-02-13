package webProg2015.project.model;

import java.util.Date;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Snippet implements Serializable {

	private static final long serialVersionUID = -5231893594400608437L;
	private String id;
	private String description;
	private String code;
	private ProgrammingLanguage language;
	private String repository;
	private Person user;
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="YYYY-MM-dd HH:mm", timezone="GMT")
	private Date createdAt;
	private int lasts;
	private boolean commentsEnabled;
	
	
	public Snippet() {
		super();
		this.createdAt = new Date();
		this.lasts = 120;
		this.commentsEnabled = true;
	}
	
	public Snippet(String id, String description, String code, ProgrammingLanguage language, String repository, Person person, Date createdAt, boolean commentsEnabled) {
		super();
		this.id = id;
		this.description = description;
		this.code = code;
		this.language = language;
		this.repository = repository;
		this.user = person;
		this.lasts = 120;
		this.createdAt = createdAt;
		this.commentsEnabled = commentsEnabled;
	}

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public ProgrammingLanguage getLanguage() {
		return language;
	}

	public void setLanguage(ProgrammingLanguage language) {
		this.language = language;
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public Person getUser() {
		return user;
	}

	public void setUser(Person user) {
		this.user = user;
	}

	public int getLasts() {
		return lasts;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public boolean isCommentsEnabled() {
		return commentsEnabled;
	}

	public void setCommentsEnabled(boolean commentsEnabled) {
		this.commentsEnabled = commentsEnabled;
	}
	
	
}
