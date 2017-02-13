package webProg2015.project.services;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import webProg2015.project.dao.PersonDao;
import webProg2015.project.dao.SessionDao;
import webProg2015.project.model.Person;
import webProg2015.project.utilities.BCrypt;
import webProg2015.project.utilities.SessionIdGenerator;

@Path("/session")
public class SessionService {

	private PersonDao uDao = PersonDao.getInstance();
	private SessionDao sDao = SessionDao.getInstance();
	
		
	
	@POST
	@Path("/logIn")
	@Consumes(MediaType.APPLICATION_JSON)	
	public Response logIn(Person p){
		boolean success = false;
		String output = "";
		boolean rememberMe = ((p.getFirsName().equals("rememberMe")) ?  true : false);
		String sessionId;
		NewCookie cookie = null;
		
		Person u = uDao.getUser(p.getUsername());
		if(u!=null){
			if(BCrypt.checkpw(p.getPassword(), u.getPassword()) && !u.getBlocked()){
				success = true;
				
				output = u.getRole();
				sessionId = SessionIdGenerator.randomString(12);
				cookie = new NewCookie(new Cookie("ssid", sessionId, "/", null));
				sDao.addSession(sessionId, p.getUsername(), rememberMe);
			}
		}
		
		if(success){
			return Response.status(200).entity(output).cookie(cookie).build();
		}else {
			output = "error";
			return Response.status(Response.Status.FORBIDDEN).entity(output).build();
		}		
	}
	
	@GET
	@Path("/logIn")
	@Produces(MediaType.TEXT_PLAIN)
	public Response redirectAddress(@CookieParam("ssid") Cookie cookie) {
	    if (cookie != null) {
	    	String username = sDao.getUsernameBySessionId(cookie.getValue());
	    	Person person = uDao.getUser(username);
	    	if (person!=null) {
	    		return Response.ok(person.getRole()).build();
	    	}
	    }
	    return Response.status(Response.Status.FORBIDDEN).build();
	}
	
	
	@GET
	@Path("/logOut")
	@Produces(MediaType.TEXT_PLAIN)
	public Response logout(@CookieParam("ssid") Cookie cookie) {
	    if (cookie != null) {
	    	sDao.removeSession(cookie.getValue());
	        NewCookie newCookie = new NewCookie("ssid", "deleted", "/", null, 0, null, 0, new Date(0), false, false);
	        return Response.ok("OK").cookie(newCookie).build();
	    }
	    return Response.ok("OK - No session").build();
	}
	
}
