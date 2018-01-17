package io.swagger.api;

import io.swagger.model.*;
import io.swagger.api.DownloadApiService;
import io.swagger.api.factories.DownloadApiServiceFactory;

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

@Path("/download")
@Consumes({ "application/json" })
@Produces({ "application/json" })
@io.swagger.annotations.Api(description = "the download API")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-16T14:36:16.100+01:00")
public class DownloadApi  {
   private final DownloadApiService delegate;

   public DownloadApi(@Context ServletConfig servletContext) {
      DownloadApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("DownloadApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (DownloadApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = DownloadApiServiceFactory.getDownloadApi();
      }

      this.delegate = delegate;
   }

    @GET
    @Path("/wcs/{job_id}")
    @Consumes({ "application/json" })
    
    @io.swagger.annotations.ApiOperation(value = "WCS compliant endpoint to download job results", notes = "This request will ask the back-end to fetch job result data as a WCS service.", response = Void.class, tags={ "Data Download", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "A valid WCS response", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Job with specified identifier is not available", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
    public Response downloadWcsJobIdGet(@ApiParam(value = "job identifier string",required=true) @PathParam("job_id") String jobId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.downloadWcsJobIdGet(jobId,securityContext);
    }
    @GET
    @Path("/wmts/{job_id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "WMTS compliant endpoint to download job results", notes = "This request will ask the back-end to fetch job result data as a WMTS service that takes zoom level, tile indexes, visuallization parameters, and time if needed as input arguments.", response = Void.class, tags={ "Data Download", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "A valid WMTS response", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Job with specified identifier is not available", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
    public Response downloadWmtsJobIdGet(@ApiParam(value = "job identifier string",required=true) @PathParam("job_id") String jobId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.downloadWmtsJobIdGet(jobId,securityContext);
    }
}
