package eu.openeo.backend.auth.filter;

import java.io.IOException;
import java.security.Key;
import java.security.Principal;
import java.sql.SQLException;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import eu.openeo.backend.wcps.ConvenienceHelper;
import eu.openeo.model.AuthKeys;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;

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
		
		String dbURL = "jdbc:sqlite:" + ConvenienceHelper.readProperties("job-database");
		Dao<AuthKeys, String> authDao = null;
		try {
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

		String token = authHeader.substring("Bearer".length()).trim();

		try {
			Key key = authDao.queryForId(token).getKey();
			Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
			log.info("Validity of token has been confirmed: " + token);
			
			//final SecurityContext securityContext = requestContext.getSecurityContext();
            requestContext.setSecurityContext(new SecurityContext() {
                        @Override
                        public Principal getUserPrincipal() {
                            return new Principal() {
                                @Override
                                public String getName() {
                                    return claims.getBody().getSubject();
                                }
                            };
                        }
                        @Override
                        public boolean isUserInRole(String role) {
                            //TODO implement checking of user's role for confirmation
                            return true;
                        }
                        @Override
                        public boolean isSecure() {
                        	//TODO implement checking for https: uriInfo.getAbsolutePath().toString().startsWith("https"); 
                            return claims.getBody().getIssuer().startsWith("https");
                        }
                        @Override
                        public String getAuthenticationScheme() {
                            return "Token-Based-Auth-Scheme";
                        }
                    });
			
		} catch (Exception e) {
			log.error("The provided token is not valid: " + token);
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}
	}

}
