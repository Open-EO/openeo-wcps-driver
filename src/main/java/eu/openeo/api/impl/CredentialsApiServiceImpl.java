package eu.openeo.api.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Iterator;

import javax.crypto.KeyGenerator;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import eu.openeo.api.CredentialsApiService;
import eu.openeo.api.NotFoundException;
import eu.openeo.backend.wcps.ConvenienceHelper;
import eu.openeo.model.AuthKeys;
import eu.openeo.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.InvalidKeyException;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class CredentialsApiServiceImpl extends CredentialsApiService {
	
    private KeyGenerator keyGenerator;
    
    private ConnectionSource connection = null;
	private Dao<AuthKeys, String> authDao = null;
	private Dao<User, String> userDao =  null;
	
	Logger log = LogManager.getLogger();
	
	public CredentialsApiServiceImpl() {
		try {
			this.keyGenerator = KeyGenerator.getInstance("HmacSHA512");
			String dbURL = "jdbc:sqlite:" + ConvenienceHelper.readProperties("job-database");
			connection = new JdbcConnectionSource(dbURL);
			try {
				TableUtils.createTable(connection, AuthKeys.class);				
			} catch (SQLException sqle) {
				log.debug("Create Table auth failed, probably exists already: " + sqle.getMessage());
			}
			
			authDao = DaoManager.createDao(connection, AuthKeys.class);
			
		} catch (NoSuchAlgorithmException e) {
			log.error("An error occured while instantiating key generator: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		} catch (IOException e) {
			log.error("An error occured while reading from properties file: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		} catch (SQLException e) {
			log.error("An error occured while accesing auth table in db: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}
		//TODO eventually substitute this block with access to a generic user database 
		try {
			try {
				TableUtils.createTable(connection, User.class);
			} catch (SQLException sqle) {
				log.debug("Create Table user failed, probably exists already: " + sqle.getMessage());
			}
			userDao = DaoManager.createDao(connection, User.class);
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			InputStream stream = classLoader.getResourceAsStream("user.json");
			JSONArray users = new JSONArray(IOUtils.toString(stream, StandardCharsets.UTF_8.name()));
			ObjectMapper mapper = new ObjectMapper();
			Iterator<Object> usersIt = users.iterator();
			while(usersIt.hasNext()) {
				JSONObject userObject = (JSONObject) usersIt.next();
				User user = mapper.readValue(userObject.toString(), User.class);
				userDao.createOrUpdate(user);
			}
		} catch (IOException e) {
			log.error("An error occured while reading from user file: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		} catch (SQLException e) {
			log.error("An error occured while accesing user table in db: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}
	}
	
    @Override
    public Response credentialsBasicGet(SecurityContext securityContext) throws NotFoundException {
    	String authenticatedUser = securityContext.getUserPrincipal().getName();
    	log.info("The following user authenticated successfully: " + authenticatedUser);
		String token = issueToken(authenticatedUser);
		if(token != null) {
		    JSONObject authObject = new JSONObject();
		    authObject.put("user_id", authenticatedUser);
		    authObject.put("access_token", token);
			return Response.ok(authObject.toString(4), MediaType.APPLICATION_JSON).build();
		}else {
			log.error("An error occured while generating jwt token for: " + authenticatedUser);
			return Response.serverError().entity("An error occured while generating jwt token for: " + authenticatedUser)
					.build();
		}
    }
    
    @Override
    public Response credentialsOidcGet(SecurityContext securityContext) throws NotFoundException {
    	try {
			String oidcEnpoint = ConvenienceHelper.readProperties("oidc-provider") + "/.well-known/openid-configuration";
			return Response.status(303).header("location", oidcEnpoint).build();
		} catch (IOException e) {
			log.error("An error occured while sending oidc location: " + e.getMessage());
			return Response.serverError().entity("An error occured while sending oidc location").build();
		}       
    }
    
    private String issueToken(String login) {
    	Key key = null;
    	if(keyGenerator != null) {
    		key = keyGenerator.generateKey();
    	}else {
    		return null;
    	}
        String jwtToken = null;        
		try {
			User user = userDao.queryForId(login);
			String[] roles = user.getRoles();
			StringBuilder roleString =  new StringBuilder();
			for(String role: roles) {
				roleString.append(role + ",");
			}
			jwtToken = Jwts.builder()
			        .setSubject(user.getUserName())
			        .setIssuer(ConvenienceHelper.readProperties("openeo-endpoint") + "/credentials/basic")
			        .setIssuedAt(new Date())
			        .setExpiration(java.sql.Timestamp.valueOf(LocalDateTime.now().plusMinutes(Long.parseLong(ConvenienceHelper.readProperties("auth-expiry")))))
			        .claim("name", user.getFirstName() + " " + user.getLastName())
			        .claim("scope", roleString.toString().substring(0,roleString.toString().length()-1))
			        .signWith(key)
			        .compact();
		AuthKeys authKeys = new AuthKeys(jwtToken);
		authKeys.setKey(key);
		authDao.create(authKeys);
		} catch (InvalidKeyException | IOException e) {
			log.error("An error occured while reading properties file: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		} catch (SQLException e) {
			log.error("An error occured while saving auth to table in db: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}
        return jwtToken;
    }
}
