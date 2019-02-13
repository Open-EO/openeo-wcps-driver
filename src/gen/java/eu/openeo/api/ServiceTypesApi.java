package eu.openeo.api;

import eu.openeo.model.*;
import eu.openeo.api.ServiceTypesApiService;
import eu.openeo.api.factories.ServiceTypesApiServiceFactory;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import eu.openeo.model.Error;
import java.util.Map;

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

@Path("/service_types")


@io.swagger.annotations.Api(description = "the service_types API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-02-12T13:52:55.621+01:00[Europe/Rome]")
public class ServiceTypesApi  {
   private final ServiceTypesApiService delegate;

   public ServiceTypesApi(@Context ServletConfig servletContext) {
      ServiceTypesApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("ServiceTypesApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (ServiceTypesApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = ServiceTypesApiServiceFactory.getServiceTypesApi();
      }

      this.delegate = delegate;
   }

    @GET
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Supported secondary web service protocols", notes = "The request will ask the back-end for supported secondary web service protocols, e.g. WMS or WCS. The response is an object of all available secondary web service protocols, including their parameters and attributes. Parameters configure the service and therefore need to be defined upon creation of a service. Attributes are read-only characteristics of the service and may be computed based on the parameters, e.g. available layers for a WMS based on the bands in the underlying GeoTiff. To improve interoperability between back-ends common names for the services SHOULD be used, e.g. the abbreviations used in the official [OGC Schema Repository](http://schemas.opengis.net/) for the respective services. Service names are allowed to be *case insensitive* throughout the API.", response = Object.class, responseContainer = "Map", authorizations = {
        @io.swagger.annotations.Authorization(value = "Bearer")
    }, tags={ "Capabilities","Secondary Services Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "An object with a map containing all service names as keys and an object that defines supported paramaters and attributes.", response = Map.class, responseContainer = "Map"),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request usually does not respond with HTTP status codes 401 and 403 due to missing authorization. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response serviceTypesGet(@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.serviceTypesGet(securityContext);
    }
}
