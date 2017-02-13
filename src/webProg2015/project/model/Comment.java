package webProg2015.project.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Comment implements Serializable {

	private static final long serialVersionUID = -1609042825690314506L;
	
	private String id;
		private String text;
		private Person user;
		@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="YYYY-MM-dd HH:mm", timezone="GMT")
		private Date createdAt;
		private Snippet snippet;
		private Rating rating;
		
		public Comment() {
			super();
			rating = new Rating();
			createdAt = new Date();
		}

		public Comment(String id, String text, Person user, Date createdAt, Snippet snippet, Rating rating) {
			super();
			this.id = id;
			this.text = text;
			this.user = user;
			this.createdAt = createdAt;
			this.snippet = snippet;
			this.rating = rating;
		}
		
		
		
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public Person getUser() {
			return user;
		}
		public void setUser(Person user) {
			this.user = user;
		}
		public Date getCreatedAt() {
			return createdAt;
		}
		public void setCreatedAt(Date createdAt) {
			this.createdAt = createdAt;
		}

		public Snippet getSnippet() {
			return snippet;
		}

		public void setSnippet(Snippet snippet) {
			this.snippet = snippet;
		}

		public Rating getRating() {
			return rating;
		}

		public void setRating(Rating rating) {
			this.rating = rating;
		}
		
}
