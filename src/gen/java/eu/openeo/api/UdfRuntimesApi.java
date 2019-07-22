package eu.openeo.api;

import eu.openeo.model.*;
import eu.openeo.api.UdfRuntimesApiService;
import eu.openeo.api.factories.UdfRuntimesApiServiceFactory;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import eu.openeo.model.Error;
import eu.openeo.model.OneOfobjectobject;

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

@Path("/udf_runtimes")


@io.swagger.annotations.Api(description = "the udf_runtimes API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class UdfRuntimesApi  {
   private final UdfRuntimesApiService delegate;

   public UdfRuntimesApi(@Context ServletConfig servletContext) {
      UdfRuntimesApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("UdfRuntimesApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (UdfRuntimesApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = UdfRuntimesApiServiceFactory.getUdfRuntimesApi();
      }

      this.delegate = delegate;
   }

    @GET
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Supported UDF runtimes", notes = "Returns a list of supported runtimes for user-defined functions (UDFs), which includes either the programming languages including version numbers and available libraries including version numbers or docker containers.", response = OneOfobjectobject.class, responseContainer = "Map", authorizations = {
        @io.swagger.annotations.Authorization(value = "Bearer")
    }, tags={ "Capabilities", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Description of UDF runtime support", response = OneOfobjectobject.class, responseContainer = "Map"),
        
        @io.swagger.annotations.ApiResponse(code = 4XX, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 5XX, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response udfRuntimesGet(@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.udfRuntimesGet(securityContext);
    }
}
