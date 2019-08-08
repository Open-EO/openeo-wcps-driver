package eu.openeo.api.impl;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

import eu.openeo.api.CredentialsApiService;
import eu.openeo.api.NotFoundException;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class CredentialsApiServiceImpl extends CredentialsApiService {
	
	Logger log = Logger.getLogger(this.getClass());
	
    @Override
    public Response credentialsBasicGet(SecurityContext securityContext) throws NotFoundException {
    	//TODO change to something more save, than the user name.
    	try {
    		log.debug("The following user tries to authenticate: " + securityContext.getUserPrincipal().getName());
    	    Algorithm algorithm = Algorithm.HMAC256(securityContext.getUserPrincipal().getName());
    	    String token = JWT.create().withIssuer("Eurac-EO-ACEO").sign(algorithm);    	    
    	    JSONObject authObject = new JSONObject();
    	    authObject.put("user_id", securityContext.getUserPrincipal().getName());
    	    authObject.put("access_token", token);
			return Response.ok(authObject.toString(4), MediaType.APPLICATION_JSON).build();
    	} catch (JWTCreationException e){
    		log.error("An error occured while serializing job to json: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
			return Response.serverError().entity("An error occured while authenticating user:" + securityContext.getUserPrincipal().getName() + " with error: " + e.getMessage())
					.build();
    	} 
    }
    
    @Override
    public Response credentialsOidcGet(SecurityContext securityContext) throws NotFoundException {
        return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
    }
}
