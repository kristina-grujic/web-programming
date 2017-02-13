package webProg2015.project.model;
import java.io.Serializable;

public class Person implements Cloneable, Serializable{
	
	private static final long serialVersionUID = -1096609097155505810L;

	private String username;
	
	private String password;
	
	private String firsName;
	
	private String lastName;
	
	private String email;
	
	private String telephone;
	
	private String address;
	
	private String imageName;
	
	private String role;
	
	private boolean blocked;
	
	public Person() {
		super();
	}

	
	 public Person(String username, String password, String firsName, String lastName, String email, String telephone,
			String address, String imageName, String role, boolean blocked) {
		super();
		this.username = username;
		this.password = password;
		this.firsName = firsName;
		this.lastName = lastName;
		this.email = email;
		this.telephone = telephone;
		this.address = address;
		this.imageName = imageName;
		this.role = role;
		this.blocked = blocked;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getFirsName() {
		return firsName;
	}


	public void setFirsName(String firsName) {
		this.firsName = firsName;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getTelephone() {
		return telephone;
	}


	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getImageName() {
		return imageName;
	}


	public void setImageName(String imageName) {
		this.imageName = imageName;
	}


	public String getRole() {
		return role;
	}


	public void setRole(String role) {
		this.role = role;
	}
	

	public boolean getBlocked() {
		return blocked;
	}


	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}


	@Override
	 public Object clone() throws CloneNotSupportedException {
	        return super.clone();
	  }
		
}
