package webProg2015.project.model;
import java.io.Serializable;

public class ProgrammingLanguage implements Serializable {

	private static final long serialVersionUID = -7334789758498527788L;
	private String name;
	
	public ProgrammingLanguage() {
		super();
	}
	
	
	public ProgrammingLanguage(String name) {
		super();
		this.name = name;
	}
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
