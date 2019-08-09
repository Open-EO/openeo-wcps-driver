package eu.openeo.model;

import java.security.Key;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "auth")
public class AuthKeys {
	
	@DatabaseField(canBeNull = false, dataType=DataType.SERIALIZABLE)
	private Key key;
	
	@DatabaseField(id = true)
	private String token;
	
	
	public Key getKey() {
		return key;
	}


	public void setKey(Key key) {
		this.key = key;
	}


	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}


	public AuthKeys(String token) {
		this.token = token;
	}
	
	public AuthKeys() {
		
	}

}
