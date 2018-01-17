package io.swagger.api;

import io.swagger.model.*;
import io.swagger.api.AuthApiService;
import io.swagger.api.factories.AuthApiServiceFactory;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;


import java.util.Map;
import java.util.List;
import io.swagger.api.NotFoundException;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.ServletConfig;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.*;
import javax.validation.constraints.*;

@Path("/auth")
@Consumes({ "application/json" })
@Produces({ "application/json" })
@io.swagger.annotations.Api(description = "the auth API")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-16T14:36:16.100+01:00")
public class AuthApi  {
   private final AuthApiService delegate;

   public AuthApi(@Context ServletConfig servletContext) {
      AuthApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("AuthApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (AuthApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = AuthApiServiceFactory.getAuthApi();
      }

      this.delegate = delegate;
   }

    @GET
    @Path("/login")
    @Consumes({ "application/json" })
    @Produces({ "text/plain; charset=utf-8" })
    @io.swagger.annotations.ApiOperation(value = "Check whether a user is registered at the back-end.  ", notes = "This request simply checks whether the provided HTTP `Authorization` header refers to a valid user at the back-end and returns his/her internal user ID. It is not needed to call login before sending any other API request, which will also expect the HTTP `Authorization` header if needed. Back-ends that do not require authentication such as a local file-based implementation may always return a generic user ID such as `'me'`.", response = String.class, tags={ "Authentication", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "User ID of the user that refers to the provided HTTP `Authorization` header. ", response = String.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Login failed", response = Void.class) })
    public Response authLoginGet(@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.authLoginGet(securityContext);
    }
}
