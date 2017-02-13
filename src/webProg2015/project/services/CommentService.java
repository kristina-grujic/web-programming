package webProg2015.project.services;

import java.util.Collection;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataParam;

import webProg2015.project.dao.CommentDao;
import webProg2015.project.dao.PersonDao;
import webProg2015.project.dao.SessionDao;
import webProg2015.project.dao.SnippetDao;
import webProg2015.project.model.Comment;
import webProg2015.project.model.Person;
import webProg2015.project.model.Rating;
import webProg2015.project.model.Snippet;
import webProg2015.project.utilities.SessionIdGenerator;

@Path("/comments")
public class CommentService {

	private PersonDao uDao = PersonDao.getInstance();
	private SessionDao sDao = SessionDao.getInstance();
	private SnippetDao snippetDao = SnippetDao.getInstance();
	private CommentDao commentDao = CommentDao.getInstance();
	
	@GET
	@Path("/{snippetId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSnippetComments(
			@PathParam("snippetId") String snippetId
			) {
		Collection<Comment> comments = commentDao.getSnippetComments(snippetId);
		return Response.ok(comments).build();
	}

	@POST
	@Path("/{snippetId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response createComment(
			@FormDataParam("text") String text,
			@PathParam("snippetId") String snippetId,
			@CookieParam("ssid") Cookie cookie
			) {
		Person u = null;
	    if (cookie != null) {
	    	String loggedIn = sDao.getUsernameBySessionId(cookie.getValue());
	    	if (loggedIn != null) {
	    		u = uDao.getUser(loggedIn);
	    	}	
	    }
	    Snippet snippet = snippetDao.getSnippet(snippetId);
	    if (snippet==null || snippet.isCommentsEnabled()==false) {
	    	return Response.status(Response.Status.BAD_REQUEST).build();
	    }
	    
	    String id = SessionIdGenerator.randomString(12);
	    Comment newComment = new Comment(id, text, u, new Date(), snippet, new Rating());
	    boolean success = commentDao.addComment(newComment);
	    if (success) {
			return Response.ok(newComment).build();
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteComment(			
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
		
		Comment comment = commentDao.getComment(id);
		if (comment == null) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		boolean success;
		if ( u.getRole().equals("user")) {
			if (comment.getUser()!=null && comment.getUser().getUsername().equals(u.getUsername())) {
				success = commentDao.removeComment(id);
			} else {
				return Response.status(Response.Status.UNAUTHORIZED).build();	
			}
		} else {
			success = commentDao.removeComment(id);
		}
		
		if (success) {
			return Response.ok(comment).build();
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

}
