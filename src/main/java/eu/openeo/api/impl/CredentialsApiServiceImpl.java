package eu.openeo.api.impl;

import java.io.IOException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;

import javax.crypto.KeyGenerator;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import eu.openeo.api.CredentialsApiService;
import eu.openeo.api.NotFoundException;
import eu.openeo.backend.wcps.ConvenienceHelper;
import eu.openeo.model.AuthKeys;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.InvalidKeyException;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class CredentialsApiServiceImpl extends CredentialsApiService {
	
    private KeyGenerator keyGenerator;
    
    private ConnectionSource connection = null;
	private Dao<AuthKeys, String> authDao = null;
	
	Logger log = Logger.getLogger(this.getClass());
	
	public CredentialsApiServiceImpl() {
		try {
			this.keyGenerator = KeyGenerator.getInstance("HmacSHA512");
			String dbURL = "jdbc:sqlite:" + ConvenienceHelper.readProperties("job-database");
			connection = new JdbcConnectionSource(dbURL);
			try {
				TableUtils.createTable(connection, AuthKeys.class);
			} catch (SQLException sqle) {
				log.debug("Create Table failed, probably exists already: " + sqle.getMessage());
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
	}
	
    @Override
    public Response credentialsBasicGet(SecurityContext securityContext) throws NotFoundException {
    	String authenticatedUser = securityContext.getUserPrincipal().getName();
    	log.debug("The following user authenticated successfully: " + authenticatedUser);
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
        return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
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
			jwtToken = Jwts.builder()
			        .setSubject(login)
			        .setIssuer(ConvenienceHelper.readProperties("openeo-endpoint") + "/credentials/basic")
			        .setIssuedAt(new Date())
			        .setExpiration(java.sql.Timestamp.valueOf(LocalDateTime.now().plusMinutes(15L)))
			        .signWith(SignatureAlgorithm.HS512, key)
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
