package eu.openeo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import eu.openeo.dao.UserDaoImpl;

@DatabaseTable(daoClass = UserDaoImpl.class, tableName = "user")
public class User {
	
	@JsonProperty("roles")
	@DatabaseField(canBeNull = false, dataType=DataType.SERIALIZABLE)
	private String[] roles;
	
	@JsonProperty("id")
	@DatabaseField(id = true)
	private String userName;
	
	@JsonProperty("first_name")
	@DatabaseField(canBeNull = false)
	private String firstName;
	
	@JsonProperty("last_name")
	@DatabaseField(canBeNull = false)
	private String lastName;
	
	
	public String[] getRoles() {
		return roles;
	}

	public void setRoles(String[] roles) {
		this.roles = roles;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

}
