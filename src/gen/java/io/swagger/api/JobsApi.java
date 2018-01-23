package io.swagger.api;

import java.math.BigDecimal;

import javax.servlet.ServletConfig;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import io.swagger.annotations.ApiParam;
import io.swagger.api.factories.JobsApiServiceFactory;
import io.swagger.model.Job;

@Path("/jobs")
@Consumes({ "application/json" })
@Produces({ "application/json" })
@io.swagger.annotations.Api(description = "the jobs API")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-23T11:10:18.550+01:00")
public class JobsApi  {
   private final JobsApiService delegate;

   public JobsApi(@Context ServletConfig servletContext) {
      JobsApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("JobsApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (JobsApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = JobsApiServiceFactory.getJobsApi();
      }

      this.delegate = delegate;
   }

    @GET
    @Path("/{job_id}/cancel")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Cancels any back-end computations of a job", notes = "This request cancels all related computations at the back-end. It will stop generating additional costs for processing. Results of batch jobs might still be accessible whereas lazy jobs will generally not respond to s following download calls.  ", response = Void.class, tags={ "Job Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "The job has been successfully canceled.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Job with specified identifier is not available", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
    public Response jobsJobIdCancelGet(@ApiParam(value = "job identifier string",required=true) @PathParam("job_id") String jobId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.jobsJobIdCancelGet(jobId,securityContext);
    }
    @DELETE
    @Path("/{job_id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Deletes a submitted job", notes = "Deleting a job  will cancel execution at the back-end regardless of its status. For finished jobs, this will also delete resulting data.", response = Void.class, tags={ "Job Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "JSON object with job information.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Job with specified identifier is not available", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
    public Response jobsJobIdDelete(@ApiParam(value = "job identifier string",required=true) @PathParam("job_id") String jobId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.jobsJobIdDelete(jobId,securityContext);
    }
    @GET
    @Path("/{job_id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Returns information about a submitted job", notes = "Returns detailed information about a submitted job including its current status and the underlying task", response = Job.class, tags={ "Job Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "JSON object with job information.", response = Job.class),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Job with specified identifier is not available", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
    public Response jobsJobIdGet(@ApiParam(value = "job identifier string",required=true) @PathParam("job_id") String jobId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.jobsJobIdGet(jobId,securityContext);
    }
    @GET
    @Path("/{job_id}/subscribe")
    @Consumes({ "application/json" })
    
    @io.swagger.annotations.ApiOperation(value = "Subscribes to job execution updates that are communicated over WebSockets", notes = "THE PROTOCOL FOR COMMUNICATION OF JOB UPDATES IS TO BE DEFINED.", response = Void.class, tags={ "Job Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 101, message = "Successful subscription to job updates returns in a protocol change to a web socket connection.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Job with specified identifier is not available", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
    public Response jobsJobIdSubscribeGet(@ApiParam(value = "job identifier string",required=true) @PathParam("job_id") String jobId
,@ApiParam(value = "WebSocket handshake request header" ,required=true, allowableValues="websocket")@HeaderParam("Upgrade") String upgrade
,@ApiParam(value = "WebSocket handshake request header" ,required=true, allowableValues="Upgrade")@HeaderParam("Connection") String connection
,@ApiParam(value = "WebSocket handshake request header" ,required=true)@HeaderParam("Sec-WebSocket-Key") String secWebSocketKey
,@ApiParam(value = "WebSocket handshake request header" ,required=true, allowableValues="job_subscribe")@HeaderParam("Sec-WebSocket-Protocol") String secWebSocketProtocol
,@ApiParam(value = "WebSocket handshake request header" ,required=true)@HeaderParam("Sec-WebSocket-Version") BigDecimal secWebSocketVersion
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.jobsJobIdSubscribeGet(jobId,upgrade,connection,secWebSocketKey,secWebSocketProtocol,secWebSocketVersion,securityContext);
    }
    @POST
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "submits a new job to the back-end", notes = "creates a new job from one or more (chained) processes at the back-end, which will eventually run the computations", response = Void.class, tags={ "Job Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Depending on the job evaluation type, the result of posting jobs can be either a json description of the job (for lazy and batch jobs) or a result object such as a NetCDF file (for sync jobs).", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 406, message = "The server is not capable to deliver the requested format.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
    public Response jobsPost(@ApiParam(value = "Defines how the job should be evaluated. Can be `lazy` (the default), `batch`, or `sync` where lazy means that the job runs computations only on download requests considering dynamically provided views. Batch jobs are immediately scheduled for execution by the back-end. Synchronous jobs will be immediately executed and return the result data.", allowableValues="lazy, batch, sync", defaultValue="lazy") @DefaultValue("lazy") @QueryParam("evaluate") String evaluate
,@ApiParam(value = "Description of one or more (chained) processes including their input arguments" ) String processGraph
,@ApiParam(value = "Description of the desired output format. Required in case `evaluate` is set to `sync`. If not specified the format has to be specified in the download request.", allowableValues="nc, json, wcs, wmts, tms, tif, png, jpeg") @QueryParam("format") String format
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.jobsPost(evaluate,processGraph,format,securityContext);
    }
}
