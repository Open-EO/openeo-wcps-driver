package eu.openeo.api;

import eu.openeo.model.*;
import eu.openeo.api.OutputFormatsApiService;
import eu.openeo.api.factories.OutputFormatsApiServiceFactory;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import eu.openeo.model.Error;
import eu.openeo.model.OutputFormat;

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

@Path("/output_formats")


@io.swagger.annotations.Api(description = "the output_formats API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class OutputFormatsApi  {
   private final OutputFormatsApiService delegate;

   public OutputFormatsApi(@Context ServletConfig servletContext) {
      OutputFormatsApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("OutputFormatsApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (OutputFormatsApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = OutputFormatsApiServiceFactory.getOutputFormatsApi();
      }

      this.delegate = delegate;
   }

    @GET
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Supported output formats", notes = "The request will ask the back-end for supported output formats, e.g. PNG, GTiff and JSON. The response is an object all available output formats and their options. This does not include the supported secondary web services.  **Note**: Output format names and parameters MUST be fully aligned with the GDAL codes if available, see [GDAL Raster Formats](http://www.gdal.org/formats_list.html) and [OGR Vector Formats](http://www.gdal.org/ogr_formats.html). It is OPTIONAL to support all output format parameters supported by GDAL. Some file formats not available through GDAL may be defined centrally for openEO. Custom output formats or parameters MAY be defined.  Back-ends are MUST NOT support aliases, for example it is not allowed to support `geotiff` instead of `gtiff`. Nevertheless, openEO Clients MAY translate user input input for convenience (e.g. translate `geotiff` to `gtiff`).  Output format names are allowed to be *case insensitive* throughout the API.", response = OutputFormat.class, responseContainer = "Map", authorizations = {
        @io.swagger.annotations.Authorization(value = "Bearer")
    }, tags={ "Capabilities","Batch Job Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "An object with containing all output format names as keys and an object that defines supported paramaters.", response = OutputFormat.class, responseContainer = "Map"),
        
        @io.swagger.annotations.ApiResponse(code = 4XX, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request usually does not respond with HTTP status codes 401 and 403 due to missing authorization. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 5XX, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response outputFormatsGet(@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.outputFormatsGet(securityContext);
    }
}
