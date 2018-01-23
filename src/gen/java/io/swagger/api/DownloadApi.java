package io.swagger.api;

import javax.servlet.ServletConfig;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import io.swagger.annotations.ApiParam;
import io.swagger.api.factories.DownloadApiServiceFactory;

@Path("/download")
@Consumes({ "application/json" })
@Produces({ "application/json" })
@io.swagger.annotations.Api(description = "the download API")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-23T11:10:18.550+01:00")
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
    @Path("/{format}/{job_id}")
    @Consumes({ "application/json" })
    
    @io.swagger.annotations.ApiOperation(value = "Endpoint to download job results in the specified format.", notes = "This request will ask the back-end to fetch job result data in the specified format.  Input arguments depend on the format requested, e.g., for a WMTS service it takes zoom level, tile indexes, visuallization parameters, and time if needed.", response = Void.class, tags={ "Data Download", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "A valid response", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Job with specified identifier is not available.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 406, message = "The server is not capable to deliver the requested format.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
    public Response downloadFormatJobIdGet(@ApiParam(value = "string specifying the data format to deliver",required=true, allowableValues="nc, json, wcs, wmts, tms, tif, png, jpeg") @PathParam("format") String format
,@ApiParam(value = "job identifier string",required=true) @PathParam("job_id") String jobId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.downloadFormatJobIdGet(format,jobId,securityContext);
    }
}
