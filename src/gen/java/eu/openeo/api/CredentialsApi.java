package eu.openeo.api;

import eu.openeo.model.*;
import eu.openeo.api.CredentialsApiService;
import eu.openeo.api.factories.CredentialsApiServiceFactory;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import eu.openeo.model.Error;
import eu.openeo.model.HTTPBasicAuthenticationResponse;

import java.util.Map;
import java.util.List;
import eu.openeo.api.NotFoundException;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.ServletConfig;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.*;
import javax.validation.constraints.*;
import javax.validation.Valid;

@Path("/credentials")


@io.swagger.annotations.Api(description = "the credentials API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class CredentialsApi  {
   private final CredentialsApiService delegate;

   public CredentialsApi(@Context ServletConfig servletContext) {
      CredentialsApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("CredentialsApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (CredentialsApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = CredentialsApiServiceFactory.getCredentialsApi();
      }

      this.delegate = delegate;
   }

    @GET
    @Path("/basic")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "HTTP Basic authentication", notes = "This request checks whether the credentials provided in the HTTP Basic header are valid. Returns the user ID and a bearer token for authorization in subsequent API calls. It is RECOMMENDED to implement this authentication method for non-public services only.", response = HTTPBasicAuthenticationResponse.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "Basic")
    }, tags={ "Account Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Credentials are correct and authentication succeeded.", response = HTTPBasicAuthenticationResponse.class),
        
        @io.swagger.annotations.ApiResponse(code = 4XX, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 5XX, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response credentialsBasicGet(@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.credentialsBasicGet(securityContext);
    }
    @GET
    @Path("/oidc")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "OpenID Connect authentication", notes = "This endpoint redirects to the [OpenID Connect discovery](http://openid.net/specs/openid-connect-discovery-1_0.html) document, which is usually located at `https://{{domain}}/.well-known/openid-configuration` and provides all information required to authenticate using [OpenID Connect](http://openid.net/connect/). It is highly RECOMMENDED to implement OpenID Connect for public services in favor of Basic authentication.", response = Void.class, tags={ "Account Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 303, message = "Specifies the location of the OpenID Connect discovery document.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 4XX, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 5XX, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response credentialsOidcGet(@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.credentialsOidcGet(securityContext);
    }
}
