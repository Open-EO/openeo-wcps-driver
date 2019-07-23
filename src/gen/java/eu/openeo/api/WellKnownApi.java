package eu.openeo.api;

import eu.openeo.model.*;
import eu.openeo.api.WellKnownApiService;
import eu.openeo.api.factories.WellKnownApiServiceFactory;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import eu.openeo.model.Error;
import eu.openeo.model.WellKnownDiscoveryResponse;

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

@Path("/.well-known")


@io.swagger.annotations.Api(description = "the .well-known API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class WellKnownApi  {
   private final WellKnownApiService delegate;

   public WellKnownApi(@Context ServletConfig servletContext) {
      WellKnownApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("WellKnownApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (WellKnownApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = WellKnownApiServiceFactory.getWellKnownApi();
      }

      this.delegate = delegate;
   }

    @GET
    @Path("/openeo")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Supported openEO versions", notes = "Well-Known URI for openEO, listing all implemented openEO versions supported by the service provider. This allows clients to easily identify the best suited openEO implementation they can use. The Well-Known URI is the entry point for clients and users, so make sure it is permanent and easy to use and remember. Please note that this URL MUST NOT be versioned as the other endpoints. See [RFC 5785](https://tools.ietf.org/html/rfc5785) for more information about Well-Known URIs.", response = WellKnownDiscoveryResponse.class, tags={ "Capabilities", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "List of all available service instances, each with URL and the implemented openEO API version.", response = WellKnownDiscoveryResponse.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request usually does not respond with HTTP status codes 401 and 403 due to missing authorization. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response wellKnownOpeneoGet(@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.wellKnownOpeneoGet(securityContext);
    }
}
