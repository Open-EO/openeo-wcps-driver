package eu.openeo.backend.auth.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Iterator;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.nimbusds.jose.util.X509CertUtils;

import eu.openeo.backend.wcps.ConvenienceHelper;
import eu.openeo.model.AuthKeys;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

@Provider
@Priority(Priorities.AUTHENTICATION)
@RequireToken
public class RequireTokenFilter implements ContainerRequestFilter {
	
	Logger log = Logger.getLogger(this.getClass());

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		log.info("The following authorization header has been received: " + authHeader);

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			log.error("Authorization header is not valid: " + authHeader);
			throw new NotAuthorizedException("Authorization header must be provided");
		}
		
		String token = authHeader.substring("Bearer".length()).trim();
		
		String encodedHeader = new String(token.split("\\.")[0]);
		log.debug("encoded header: " + encodedHeader);
		String decodedHeader = new String(Base64.getDecoder().decode(encodedHeader));
		log.debug("decoded header: " + decodedHeader);
		JSONObject decodedHeaderJSON = new JSONObject(decodedHeader);
		
		log.debug("token header: " + decodedHeaderJSON.toString(4));
		
		String kid = null;
		try {
			kid = decodedHeaderJSON.getString("kid");
		}catch(JSONException e) {
			
		}
		
		if(kid != null) {
			log.debug("token will be verified using external token provider");
			verifyOIDCToken(requestContext, token, kid);
		}else {
			log.debug("token will be verified using internal token provider");
			verifyBasicToken(requestContext, token);
		}
	}
	
	private void verifyBasicToken(ContainerRequestContext requestContext, String token) throws IOException{		
		Dao<AuthKeys, String> authDao = null;
		try {
			String dbURL = "jdbc:sqlite:" + ConvenienceHelper.readProperties("job-database");
			ConnectionSource connection = new JdbcConnectionSource(dbURL);
			authDao = DaoManager.createDao(connection, AuthKeys.class);
		} catch (SQLException e) {
			log.error("An error occured while reading auth from table in db: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}
		try {
			Key key = authDao.queryForId(token).getKey();
			final Jws<Claims> verifiedClaims = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
			log.info("Validity of token has been confirmed: " + token);
			requestContext.setSecurityContext(new SecurityContext() {
                @Override
                public Principal getUserPrincipal() {
                    return new Principal() {
                        @Override
                        public String getName() {
                            return verifiedClaims.getBody().getSubject();
                        }
                    };
                }
                @Override
                public boolean isUserInRole(String role) {
                    String roles = verifiedClaims.getBody().get("scope", String.class);
                    if(roles.contains(role)) {
                    	log.debug("User has confirmed to be part of: " + role);
                    	return true;
                    }else {
                    	log.error("User is not part of: " + role);
                    	return false;
                    }
                }
                @Override
                public boolean isSecure() { 
                    return verifiedClaims.getBody().getIssuer().startsWith("https");
                }
                @Override
                public String getAuthenticationScheme() {
                    return "Token-Based-Auth-Scheme";
                }
            });
			return;
		} catch (Exception e) {
			log.error("The provided token is not valid: " + token);
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
			return;
		}
	}
	
	private void verifyOIDCToken(ContainerRequestContext requestContext, String token, String kid){		
		try {
			URL keyURL = new URL(ConvenienceHelper.readProperties("oidc-keys-provider"));		
			BufferedReader rd = new BufferedReader(new InputStreamReader( keyURL.openStream(), Charset.forName("UTF-8")));
			StringBuilder jsonText = new StringBuilder();
			while(rd.read() != -1){
				String line = rd.readLine();
				log.debug("reading line: " + line);
				jsonText.append(line);
			}
			JSONObject json = new JSONObject("{" + jsonText + "}");
			JSONArray keys = json.getJSONArray("keys");
			Jws<Claims> claims = null;
			Iterator<Object> iterator = keys.iterator();
			//TODO check if it is possible to request the correct token of the array directly or if this iteration is always necessary...
			while(iterator.hasNext()) {
				JSONObject privateKey = (JSONObject) iterator.next();
				if(privateKey.getString("kid").equals(kid)) {
					log.debug("checking key: " + privateKey.toString(4));
					String x5c = privateKey.getJSONArray("x5c").getString(0);
					log.debug("x5c: "+ x5c);
					byte[] certChain = Base64.getDecoder().decode(x5c);
					X509Certificate cert = X509CertUtils.parse(certChain); 
					Key key = cert.getPublicKey();
					claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
					log.info("Validity of token has been confirmed: " + token);
				}
			}
			if(claims != null) {
				final Jws<Claims> verifiedClaims =  claims;
				requestContext.setSecurityContext(new SecurityContext() {
	                @Override
	                public Principal getUserPrincipal() {
	                    return new Principal() {
	                        @Override
	                        public String getName() {
	                            return verifiedClaims.getBody().get("preferred_username", String.class);
	                        }
	                    };
	                }
	                @Override
	                public boolean isUserInRole(String role) {
	                    String roles = verifiedClaims.getBody().get("roles", String.class);
	                    if(roles != null && roles.contains(role)) {
	                    	log.debug("User has confirmed to be part of: " + role);
	                    	return true;
	                    }else {
	                    	log.error("User is not part of: " + role);
	                    	return false;
	                    }
	                }
	                @Override
	                public boolean isSecure() { 
	                    return verifiedClaims.getBody().getIssuer().startsWith("https");
	                }
	                @Override
	                public String getAuthenticationScheme() {
	                    return "Token-Based-Auth-Scheme";
	                }
	            });
				return;
			}else {
				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("token not registered").build());
			}
		} catch(ExpiredJwtException e) {
			log.error("The provided token is expired: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build());
		} catch(UnsupportedJwtException e) {
			log.error("The provided token is not supported: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build());
		}catch (MalformedJwtException e) {
			log.error("The provided token is malformed: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build());
		}catch( SignatureException e) {
			log.error("The provided signature is not correct: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build());
		} catch (MalformedURLException e) {
			log.error("URL to key provider is not correct: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build());
		} catch (IOException e) {
			log.error("Error reading properties file: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build());
		}
		
	}

}
