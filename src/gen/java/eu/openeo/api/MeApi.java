package eu.openeo.api;

import javax.servlet.ServletConfig;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import eu.openeo.api.factories.MeApiServiceFactory;
import eu.openeo.backend.auth.filter.RequireToken;
import eu.openeo.model.Error;
import eu.openeo.model.UserDataResponse;

@Path("/me")


@io.swagger.annotations.Api(description = "the me API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class MeApi  {
   private final MeApiService delegate;

   public MeApi(@Context ServletConfig servletContext) {
      MeApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("MeApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (MeApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = MeApiServiceFactory.getMeApi();
      }

      this.delegate = delegate;
   }

    @GET
    
    @RequireToken
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Information about the authenticated user", notes = "This endpoint always returns the user id and MAY return the disk quota available to the user. It MAY also return additional links related to the user, e.g. where payments are handled or the user profile could be changed. For back-ends that involve accounting, this service MAY also return the currently available money or credits in the currency the back-end is working with. This endpoint MAY be extended to fulfil the specification of the [OpenID Connect UserInfo Endpoint](http://openid.net/specs/openid-connect-core-1_0.html#UserInfo).", response = UserDataResponse.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "Bearer")
    }, tags={ "Account Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Information about the logged in user.", response = UserDataResponse.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response meGet(@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.meGet(securityContext);
    }
}
