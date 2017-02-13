package webProg2015.project.services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import webProg2015.project.dao.PersonDao;
import webProg2015.project.dao.SessionDao;
import webProg2015.project.model.Person;
import webProg2015.project.utilities.BCrypt;
import webProg2015.project.utilities.Scalr;
import webProg2015.project.utilities.SessionIdGenerator;
import webProg2015.project.utilities.Scalr.Method;

@Path("/users")
public class PersonService {
	
	private PersonDao uDao = PersonDao.getInstance();
	private SessionDao sDao = SessionDao.getInstance();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response allUsers() {
		Collection<Person> users = uDao.getAllUsers();
		return Response.ok(users).build();
	}
	
	@GET
	@Path("/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response filterUsers(@PathParam("username") String username) {
		Collection<Person> users = uDao.getUsers(username);
		return Response.ok(users).build();
	}
	
	@GET
	@Path("/signup/{username}")
	@Produces(MediaType.TEXT_PLAIN)
	public String checkIfUsernameIsAvailable(@PathParam("username") String username) {
		if(uDao.isUsernameAvailable(username)){
			return "available";
		}else{
			return "taken";
		}
	}
	
	@PUT
	@Path("/block/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response blockUser(@PathParam("username") String username, @CookieParam("ssid") Cookie cookie) {
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
	    	if ( u.getRole() == "user") {
	    		return Response.status(Response.Status.UNAUTHORIZED).build();	
	    	}
	    }else{
	    	return Response.status(Response.Status.UNAUTHORIZED).build();
	    } 
	    try {
			u = uDao.getUser(username);
			if ( u == null || u.getBlocked() == true) {
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
			u.setBlocked(true);
			uDao.updateUser(u);
			u = (Person) u.clone();
			u.setPassword("");
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	    return Response.ok().entity(u).build();
	}
	

	@PUT
	@Path("/unblock/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response unblockUser(@PathParam("username") String username, @CookieParam("ssid") Cookie cookie) {
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
	    	if ( u.getRole() == "user") {
	    		return Response.status(Response.Status.UNAUTHORIZED).build();	
	    	}
	    }else{
	    	return Response.status(Response.Status.UNAUTHORIZED).build();
	    } 
	    try {
			u = uDao.getUser(username);
			if ( u == null || u.getBlocked() == false) {
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
			u.setBlocked(false);
			uDao.updateUser(u);
			u = (Person) u.clone();
			u.setPassword("");
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	    return Response.ok().entity(u).build();
	}

	
	@GET
	@Path("/self-data")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLogedUserData(@CookieParam("ssid") Cookie cookie) {
		Person u = null;
	    if (cookie != null) {
	    	String username = sDao.getUsernameBySessionId(cookie.getValue());
	    	if(username==null){
	    		NewCookie newCookie = new NewCookie("ssid", "deleted", "/", null, 0, null, 0, new Date(0), false, false);
	    		return Response.status(Response.Status.UNAUTHORIZED).cookie(newCookie).build();
	    	}
	    	u = uDao.getUser(username);
	    	if(u==null){
	    		NewCookie newCookie = new NewCookie("ssid", "deleted", "/", null, 0, null, 0, new Date(0), false, false);
	    		return Response.status(Response.Status.UNAUTHORIZED).cookie(newCookie).build();
	    	}
	    }else{
	    	return Response.status(Response.Status.UNAUTHORIZED).build();
	    } 
	    try {
			u = (Person) u.clone();
			u.setPassword("");
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	    return Response.ok().entity(u).build();
	}
	

	@POST
	@Path("/signup/{username}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response registerUser(			
			@FormDataParam("username") String username, @FormDataParam("password") String password,
			@FormDataParam("firstName") String firstName, @FormDataParam("lastName") String lastName,
			@FormDataParam("telephone") String telephone, @FormDataParam("email") String email,
			@FormDataParam("address") String address,
			@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition contentDispositionHeader) {

		String originalFileName = contentDispositionHeader.getFileName();
		String newFileName = username + "." +  originalFileName.substring(originalFileName.lastIndexOf('.') + 1);
		
		// save the profile image to the server
		saveFile(fileInputStream, newFileName);
		
		password = BCrypt.hashpw(password, BCrypt.gensalt());
		
		Person newUser = new Person(username, password, firstName, lastName, email, telephone, address, newFileName, "user", false);
		boolean success = uDao.addUser(newUser);
		
		NewCookie cookie = null; 
		if(success){
			String sessionId = SessionIdGenerator.randomString(12);
			cookie = new NewCookie(new Cookie("ssid", sessionId, "/", null));
			success = sDao.addSession(sessionId, username, false);
		}
		
		String output = "";
		if(success){
			output = "User successfuly created. File saved to server under name : " + newFileName;
			return Response.status(200).entity(output).cookie(cookie).build();
		}else {
			output = "There was an error wiht creating user profile.";
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(output).build();
		}
	}

	
	// save uploaded file to a defined location on the server
	private void saveFile(InputStream uploadedInputStream,	String fileName) {
		try {
			File theDir = new File("./Data/Images/Users");

			if (!theDir.exists()) {
				 theDir.mkdirs();
			}
			
			File f = new File("./Data/Images/Users/" + fileName );
			String path = f.getCanonicalPath();
			OutputStream outpuStream = new FileOutputStream(new File(path));
			int read = 0;
			byte[] bytes = new byte[1024];
			
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				outpuStream.write(bytes, 0, read);
			}

			outpuStream.flush();
			outpuStream.close();			
			uploadedInputStream.close();
			
			// Creating thumbnail Image
			BufferedImage thumbnailImage = null;
			
			String extension =  fileName.substring(fileName.lastIndexOf('.') + 1);
			String thumbnailName = fileName.substring(0, fileName.lastIndexOf('.'));
			thumbnailName += "-thumbnail." + extension; 
					
			File thumbnailImageFile = new File("./Data/Images/Users/" + thumbnailName);
			
			try {
				BufferedImage fullImage = ImageIO.read(new File("./Data/Images/Users/" + fileName)); 
				thumbnailImage = Scalr.resize(fullImage, Method.QUALITY, 120, 120, Scalr.OP_ANTIALIAS);
				ImageIO.write(thumbnailImage, extension, thumbnailImageFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	@GET
	@Path("/image-by-username/{username}/thumbnail")
	@Produces({"image/png", "image/jpg"})
	public Response getThumbnailImage(@PathParam("username") String username) {
		BufferedImage thumbnailImage = null;
		String imageName = "";		
		if(uDao.getUser(username)!=null){
			imageName = uDao.getUser(username).getImageName();
		}
		
		String extension =  imageName.substring(imageName.lastIndexOf('.') + 1);
		String thumbnailName = imageName.substring(0, imageName.lastIndexOf('.'));
		thumbnailName += "-thumbnail." + extension; 
		
		File theDir = new File("./Data/Images/Users");

		if (!theDir.exists()) {
			 theDir.mkdirs();
		}
		
		File thumbnailImageFile = new File("./Data/Images/Users/" + thumbnailName);
		
		if(thumbnailImageFile.exists()){
			try {
				thumbnailImage = ImageIO.read(thumbnailImageFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			return Response.status(Response.Status.NOT_FOUND).build();
		}	
		CacheControl cc = new CacheControl();
		cc.setNoCache(true);
		return Response.ok(thumbnailImage).cacheControl(cc).build();
	}

	
	@GET
	@Path("/image-by-username/{username}")
	@Produces({"image/png", "image/jpg"})
	public Response getImage(@PathParam("username") String username) {
		BufferedImage image = null;
		String imageName = "";		
		if(uDao.getUser(username)!=null){
			imageName = uDao.getUser(username).getImageName();
		}		
		
		File theDir = new File("./Data/Images/Users");

		if (!theDir.exists()) {
			 theDir.mkdirs();
		}
		
		File imageFile = new File("./Data/Images/Users/" + imageName);
		
		if(imageFile.exists()){
			try {
				image = ImageIO.read(imageFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			return Response.status(Response.Status.NOT_FOUND).build();
		}		
		return Response.ok(image).build();
	}
	
}
