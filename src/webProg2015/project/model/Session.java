package webProg2015.project.model;

import java.util.Date;

public class Session {
	
	private String sessionId;
	
	private String username;
	
	private Date lastLogInTime;
	
	private boolean persistent;

	public Session() {
		super();
	}

	public Session(String sessionId, String username, Date lastLogInTime, boolean persistent) {
		super();
		this.sessionId = sessionId;
		this.username = username;
		this.lastLogInTime = lastLogInTime;
		this.persistent = persistent;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}	
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getLastLogInTime() {
		return lastLogInTime;
	}

	public void setLastLogInTime(Date lastLogInTime) {
		this.lastLogInTime = lastLogInTime;
	}

	public boolean isPersistent() {
		return persistent;
	}

	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}
	
}
