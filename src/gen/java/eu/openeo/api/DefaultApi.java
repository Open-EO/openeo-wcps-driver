package eu.openeo.api;

import eu.openeo.model.*;
import eu.openeo.api.DefaultApiService;
import eu.openeo.api.factories.DefaultApiServiceFactory;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import eu.openeo.model.Error;
import eu.openeo.model.InlineResponse200;

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

@Path("/")


@io.swagger.annotations.Api(description = "the default API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-02-12T13:52:55.621+01:00[Europe/Rome]")
public class DefaultApi  {
   private final DefaultApiService delegate;

   public DefaultApi(@Context ServletConfig servletContext) {
      DefaultApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("DefaultApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (DefaultApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = DefaultApiServiceFactory.getDefaultApi();
      }

      this.delegate = delegate;
   }

    @GET
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Information about the back-end", notes = "Returns general information about the back-end, including which version and endpoints of the openEO API are supported. May also include billing information.", response = InlineResponse200.class, tags={ "Capabilities", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Information about the API version and supported endpoints / features.", response = InlineResponse200.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request usually does not respond with HTTP status codes 401 and 403 due to missing authorization. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response rootGet(@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.rootGet(securityContext);
    }
}
