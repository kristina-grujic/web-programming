package webProg2015.project.services;

import java.io.InputStream;
import java.util.Collection;
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

import org.glassfish.jersey.media.multipart.FormDataParam;

import webProg2015.project.dao.PersonDao;
import webProg2015.project.dao.ProgrammingLanguageDao;
import webProg2015.project.dao.SessionDao;
import webProg2015.project.model.Person;
import webProg2015.project.model.ProgrammingLanguage;

@Path("/programming_languages")
public class ProgrammingLanguageService {
	
	private PersonDao uDao = PersonDao.getInstance();
	private SessionDao sDao = SessionDao.getInstance();
	private ProgrammingLanguageDao languageDao = ProgrammingLanguageDao.getInstance();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllLanguages() {
		Collection<ProgrammingLanguage> languages = languageDao.getAllLanguages();
		return Response.ok(languages).build();
	}
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addProgrammingLanguage(@FormDataParam("name") String name, @CookieParam("ssid") Cookie cookie) {
		Person u = null;
	    if (cookie != null) {
	    	String loggedIn = sDao.getUsernameBySessionId(cookie.getValue());
	    	if (loggedIn == null) {
	    		NewCookie newCookie = new NewCookie("ssid", "deleted", "/", null, 0, null, 0, new Date(0), false, false);
	    		return Response.status(Response.Status.UNAUTHORIZED).cookie(newCookie).build();
	    	}
	    	u = uDao.getUser(loggedIn);
	    	if ( u == null ){
	    		NewCookie newCookie = new NewCookie("ssid", "deleted", "/", null, 0, null, 0, new Date(0), false, false);
	    		return Response.status(Response.Status.UNAUTHORIZED).cookie(newCookie).build();
	    	}
	    	System.out.println(u.getRole());
	    	if ( u.getRole().equals("user")) {
	    		System.out.println(u.getRole());
	    		NewCookie newCookie = new NewCookie("ssid", "deleted", "/", null, 0, null, 0, new Date(0), false, false);
	    		return Response.status(Response.Status.UNAUTHORIZED).cookie(newCookie).build();	
	    	}
	    }else{
	    	return Response.status(Response.Status.UNAUTHORIZED).build();
	    }
	    boolean success = false;
	    ProgrammingLanguage language = new ProgrammingLanguage(name);
	    success = languageDao.addLanguage(language);
	    if (success) {
			return Response.ok().build();
		}else {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}
}
