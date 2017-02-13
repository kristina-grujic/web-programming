package webProg2015.project.services;

import java.io.InputStream;
import java.util.Collection;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataParam;

import webProg2015.project.dao.PersonDao;
import webProg2015.project.dao.ProgrammingLanguageDao;
import webProg2015.project.dao.SessionDao;
import webProg2015.project.dao.SnippetDao;
import webProg2015.project.model.Person;
import webProg2015.project.model.ProgrammingLanguage;
import webProg2015.project.model.Snippet;
import webProg2015.project.utilities.SessionIdGenerator;

@Path("/snippets")
public class SnippetService {

	private PersonDao uDao = PersonDao.getInstance();
	private SessionDao sDao = SessionDao.getInstance();
	private ProgrammingLanguageDao languageDao = ProgrammingLanguageDao.getInstance();
	private SnippetDao snippetDao = SnippetDao.getInstance();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllSnippets() {
		Collection<Snippet> snippets = snippetDao.getAllSnippets();
		return Response.ok(snippets).build();
	}
	
	@GET
	@Path("/{snippetDescription}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response filterUsers(@PathParam("snippetDescription") String description) {
		Collection<Snippet> snippets = snippetDao.filterSnippets(description);
		return Response.ok(snippets).build();
	}
	
	@PUT
	@Path("/block/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response blockSnippetComments(
			@PathParam("id") String id, @CookieParam("ssid") Cookie cookie
			) {
		Person u;
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
	    	if ( u.getRole().equals("user")) {
	    		return Response.status(Response.Status.UNAUTHORIZED).build();	
	    	}
		} else {
			return Response.status(Response.Status.UNAUTHORIZED).build();		
		}

		Snippet snippet = snippetDao.getSnippet(id);
		if (snippet == null) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		if (snippet.isCommentsEnabled()==false) {
			return Response.status(Response.Status.BAD_REQUEST).build();	
		}
		snippet.setCommentsEnabled(false);
		boolean success = snippetDao.updateSnippet(snippet);
		if (success) {
			return Response.ok().build();		
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}	}
	
	@PUT
	@Path("/unblock/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response unblockSnippetComments(
			@PathParam("id") String id, @CookieParam("ssid") Cookie cookie
			) {
		Person u;
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
	    	if (u.getRole().equals("user")) {
	    		return Response.status(Response.Status.UNAUTHORIZED).build();	
	    	}
		} else {
			return Response.status(Response.Status.UNAUTHORIZED).build();		
		}

		Snippet snippet = snippetDao.getSnippet(id);
		if (snippet == null) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		if (snippet.isCommentsEnabled()==true) {
			return Response.status(Response.Status.BAD_REQUEST).build();	
		}
		
		snippet.setCommentsEnabled(true);
		boolean success = snippetDao.updateSnippet(snippet);
		if (success) {
			return Response.ok().build();		
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response createSnippet(			
			@FormDataParam("description") String description, @FormDataParam("code") String code,
			@FormDataParam("repository") String repository, @FormDataParam("languageName") String languageName,
			@CookieParam("ssid") Cookie cookie
			) {
		Person u = null;
	    if (cookie != null) {
	    	String loggedIn = sDao.getUsernameBySessionId(cookie.getValue());
	    	if (loggedIn != null) {
	    		u = uDao.getUser(loggedIn);
	    	}	
	    }
	    String id = SessionIdGenerator.randomString(12);
	    ProgrammingLanguage language = languageDao.getLanguage(languageName);
	    Snippet newSnippet = new Snippet(id, description, code, language, repository, u, new Date(), true);
	    boolean success = snippetDao.addSnippet(newSnippet);
		if (success) {
			return Response.ok(newSnippet).build();
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteSnippet(			
			@PathParam("id") String id, @CookieParam("ssid") Cookie cookie
			) {
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
	    }else{
	    	return Response.status(Response.Status.UNAUTHORIZED).build();
	    }
		
		Snippet snippet = snippetDao.getSnippet(id);
		if (snippet == null) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		boolean success;
		if ( u.getRole().equals("user") ) {
			if (snippet.getUser()!=null && snippet.getUser().getUsername().equals(u.getUsername())) {
				success = snippetDao.removeSnippet(id);
			} else {
				return Response.status(Response.Status.UNAUTHORIZED).build();	
			}
		} else {
			success = snippetDao.removeSnippet(id);
		}
		
		if (success) {
			return Response.ok(snippet).build();
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
}
