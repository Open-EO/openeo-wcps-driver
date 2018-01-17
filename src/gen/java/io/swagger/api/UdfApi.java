package io.swagger.api;

import io.swagger.model.*;
import io.swagger.api.UdfApiService;
import io.swagger.api.factories.UdfApiServiceFactory;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import java.util.Map;
import io.swagger.model.UdfDescription;

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

@Path("/udf")
@Consumes({ "application/json" })
@Produces({ "application/json" })
@io.swagger.annotations.Api(description = "the udf API")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-16T14:36:16.100+01:00")
public class UdfApi  {
   private final UdfApiService delegate;

   public UdfApi(@Context ServletConfig servletContext) {
      UdfApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("UdfApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (UdfApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = UdfApiServiceFactory.getUdfApi();
      }

      this.delegate = delegate;
   }

    @GET
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Describes how custom user-defined functions can be exposed to the data and which languages are supported by the back-end.", notes = "", response = Map.class, responseContainer = "List", tags={ "UDF", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Description of UDF support", response = Map.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Job with specified identifier is not available", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
    public Response udfGet(@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.udfGet(securityContext);
    }
    @GET
    @Path("/{lang}/{udf_type}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Returns the process description of UDF schemas, which offer different possibilities how user-defined scripts can be applied to the data.", notes = "", response = UdfDescription.class, tags={ "UDF", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Process description", response = UdfDescription.class),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Job with specified identifier is not available", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
    public Response udfLangUdfTypeGet(@ApiParam(value = "Language identifier such as `'R'`",required=true, allowableValues="python, R") @PathParam("lang") String lang
,@ApiParam(value = "The UDF types define how UDFs can be exposed to the data, how they can be parallelized, and how the result schema should be structured.",required=true, allowableValues="apply_pixel, apply_scene, reduce_time, reduce_space, window_time, window_space, window_spacetime, aggregate_time, aggregate_space, aggregate_spacetime") @PathParam("udf_type") String udfType
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.udfLangUdfTypeGet(lang,udfType,securityContext);
    }
}
