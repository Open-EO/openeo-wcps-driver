package io.swagger.api;

import io.swagger.model.*;
import io.swagger.api.ProcessesApiService;
import io.swagger.api.factories.ProcessesApiServiceFactory;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import io.swagger.model.InlineResponse2002;
import io.swagger.model.ProcessDescription;

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

@Path("/processes")
@Consumes({ "application/json" })
@Produces({ "application/json" })
@io.swagger.annotations.Api(description = "the processes API")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-16T14:36:16.100+01:00")
public class ProcessesApi  {
   private final ProcessesApiService delegate;

   public ProcessesApi(@Context ServletConfig servletContext) {
      ProcessesApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("ProcessesApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (ProcessesApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = ProcessesApiServiceFactory.getProcessesApi();
      }

      this.delegate = delegate;
   }

    @GET
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Returns processes supported by the back-end", notes = "The request will ask the back-end for available processes and will return an array of available processes with their unique identifiers and description", response = InlineResponse2002.class, responseContainer = "List", tags={ "Process Discovery", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "An array of EO processes including their unique identifiers and a description.", response = InlineResponse2002.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
    public Response processesGet(@ApiParam(value = "string expression to search for available processes by name") @QueryParam("qname") String qname
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.processesGet(qname,securityContext);
    }
    @GET
    @Path("/opensearch")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "OpenSearch endpoint to request standardized process search results.", notes = "This service offers more complex search functionality and returns results in an OpenSearch compliant RSS XML format.", response = Void.class, tags={ "Process Discovery", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "OpenSearch response", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
    public Response processesOpensearchGet(@ApiParam(value = "string expression to search available processes") @QueryParam("q") String q
,@ApiParam(value = "page start value") @QueryParam("start") Integer start
,@ApiParam(value = "page size value") @QueryParam("rows") Integer rows
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.processesOpensearchGet(q,start,rows,securityContext);
    }
    @GET
    @Path("/{process_id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Returns further information on a given EO process available at the back-end.", notes = "The request will ask the back-end for further details about a process specified by identifier", response = ProcessDescription.class, tags={ "Process Discovery", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "JSON object with metadata of the EO process.", response = ProcessDescription.class),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Process with specified identifier is not available", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
    public Response processesProcessIdGet(@ApiParam(value = "process identifier string such as 'NDVI'",required=true) @PathParam("process_id") String processId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.processesProcessIdGet(processId,securityContext);
    }
}
