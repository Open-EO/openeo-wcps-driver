package eu.openeo.api.impl;

import java.io.IOException;
import java.security.Principal;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import eu.openeo.api.MeApiService;
import eu.openeo.api.NotFoundException;
import eu.openeo.backend.auth.filter.RequireToken;
import eu.openeo.backend.wcps.ConvenienceHelper;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class MeApiServiceImpl extends MeApiService {
	
	Logger log = Logger.getLogger(this.getClass());
	
    @Override
    @RequireToken
    public Response meGet(SecurityContext securityContext) throws NotFoundException {
    	try {
    		Principal principal = securityContext.getUserPrincipal();
    		if(principal != null) {
		    	String userId = principal.getName();
		    	JSONObject linkProcessGraph = new JSONObject();
				linkProcessGraph.put("user_id", userId);
				
				JSONArray links = new JSONArray();
				JSONObject link = new JSONObject();
				link.put("href", ConvenienceHelper.readProperties("openeo-endpoint") + "/me");
				link.put("type", "self");
		
				links.put(link);
		
				linkProcessGraph.put("links", links);
				
				return Response.ok(linkProcessGraph.toString().getBytes("UTF-8"), "application/json").build();
    		}else {
    			return Response.status(Response.Status.UNAUTHORIZED).build();
    		}
    	}catch(IOException e) {
    		log.error("An error occured while reading openeo endpoint from properties file: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
			return Response.serverError().entity("An error occured while reading openeo endpoint from properties file: " + e.getMessage()).build();
    	}
    }
}
