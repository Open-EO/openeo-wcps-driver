package eu.openeo.api;

import eu.openeo.model.*;
import eu.openeo.api.JobsApiService;
import eu.openeo.api.factories.JobsApiServiceFactory;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import eu.openeo.model.Error;
import eu.openeo.model.InlineObject6;
import eu.openeo.model.InlineObject7;
import eu.openeo.model.InlineResponse20011;
import eu.openeo.model.InlineResponse20012;
import eu.openeo.model.InlineResponse20013;
import eu.openeo.model.InlineResponse20014;

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

@Path("/jobs")


@io.swagger.annotations.Api(description = "the jobs API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-02-12T13:52:55.621+01:00[Europe/Rome]")
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
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "List all batch jobs", notes = "Requests to this endpoint will list all batch jobs submitted by a user with given id.", response = InlineResponse20011.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "Bearer")
    }, tags={ "Batch Job Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Array of job descriptions", response = InlineResponse20011.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response jobsGet(@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.jobsGet(securityContext);
    }
    @DELETE
    @Path("/{job_id}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Delete a batch job", notes = "Deletes all data related to this job. Computations are stopped and computed results are deleted. This job won't generate additional costs for processing.", response = Void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "Bearer")
    }, tags={ "Batch Job Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 204, message = "The job has been successfully deleted.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response jobsJobIdDelete(@ApiParam(value = "Unique job identifier.",required=true, defaultValue="null") @PathParam("job_id") String jobId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.jobsJobIdDelete(jobId,securityContext);
    }
    @GET
    @Path("/{job_id}/estimate")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Get an estimate for a batch job", notes = "Clients can ask for an estimate for a batch job. Back-ends can decide to either calculate the duration, the costs, the size or a combination of them. This MUST be the upper limit of the incurring costs. Clients can be charged less than specified, but never more. Back-end providers MAY specify an expiry time for the estimate. Starting to process data afterwards MAY be charged at a higher cost. Costs MAY NOT include downloading costs. This can be indicated with the `downloads_included` flag.", response = InlineResponse20013.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "Bearer")
    }, tags={ "Batch Job Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "The estimated costs with regard to money, processing time and storage capacity. At least one of `costs`, `duration` or `size` MUST be provided.", response = InlineResponse20013.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response jobsJobIdEstimateGet(@ApiParam(value = "Unique job identifier.",required=true, defaultValue="null") @PathParam("job_id") String jobId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.jobsJobIdEstimateGet(jobId,securityContext);
    }
    @GET
    @Path("/{job_id}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Full metadata for a batch job", notes = "Returns detailed information about a submitted batch job.", response = InlineResponse20012.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "Bearer")
    }, tags={ "Batch Job Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Full job information.", response = InlineResponse20012.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response jobsJobIdGet(@ApiParam(value = "Unique job identifier.",required=true, defaultValue="null") @PathParam("job_id") String jobId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.jobsJobIdGet(jobId,securityContext);
    }
    @PATCH
    @Path("/{job_id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Modify a batch job", notes = "Modifies an existing job at the back-end but maintains the identifier. Changes can be grouped in a single request.  Jobs can only be modified when the job is not queued or running. Otherweise requests to this endpoint MUST be rejected with openEO error `JobLocked`.", response = Void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "Bearer")
    }, tags={ "Batch Job Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 204, message = "Changes to the job applied successfully.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response jobsJobIdPatch(@ApiParam(value = "Unique job identifier.",required=true, defaultValue="null") @PathParam("job_id") String jobId
,@ApiParam(value = "" ) @Valid InlineObject7 inlineObject7
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.jobsJobIdPatch(jobId,inlineObject7,securityContext);
    }
    @DELETE
    @Path("/{job_id}/results")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Cancel processing a batch job", notes = "Cancels all related computations for this job at the back-end. It will stop generating additional costs for processing.  A subset of processed results may be available for downloading depending on the state of the job as it was canceled. Finished results MUST NOT be deleted until the job is deleted or job processing is started again.  This endpoint only has an effect if the job status is `queued` or `running`.  The job status is set to `canceled` if the status was `running` beforehand and partial or preliminary results are available to be downloaded. Otherwise the status ist set to `submitted`. ", response = Void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "Bearer")
    }, tags={ "Batch Job Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 204, message = "Processing the job has been successfully canceled.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response jobsJobIdResultsDelete(@ApiParam(value = "Unique job identifier.",required=true, defaultValue="null") @PathParam("job_id") String jobId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.jobsJobIdResultsDelete(jobId,securityContext);
    }
    @GET
    @Path("/{job_id}/results")
    
    @Produces({ "application/json", "application/metalink+xml" })
    @io.swagger.annotations.ApiOperation(value = "Download results for a completed batch job", notes = "After finishing processing, this request will provide signed URLs to the processed files of the batch job.  Title, description and the date and time of the last update from the job SHOULD be included in the response.  URL signing is a way to protect files from unauthorized access with a key instead of HTTP header based authorization. The URL signing key is similar to a password and it's inclusion in the URL allows to download files using simple GET requests supported by a wide range of programs, e.g. web browsers or download managers. Back-ends are responsible to generate the URL signing keys and their appropriate expiration. The back-end MAY indicate an expiration time by sending an `Expires` header.  Depending on the `Accept` header, the response is either a JSON array containing links or a metalink XML document.  If processing has not finished yet requests to this endpoint MUST be rejected with openEO error `JobNotFinished`.  A header named `OpenEO-Costs` MAY be sent with all responses to indicate the costs for downloading the data.", response = InlineResponse20014.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "Bearer")
    }, tags={ "Batch Job Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Valid download links have been returned. The download links doesn't necessarily need to be located under the API base url.", response = InlineResponse20014.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response jobsJobIdResultsGet(@ApiParam(value = "Unique job identifier.",required=true, defaultValue="null") @PathParam("job_id") String jobId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.jobsJobIdResultsGet(jobId,securityContext);
    }
    @POST
    @Path("/{job_id}/results")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Start processing a batch job", notes = "Adds a batch job to the processing queue to compute the results.  This endpoint has no effect if the job status is already `queued` or `running`. In particular, it doesn't restart a running job. Processing MUST be canceled before to restart it.  The job status is set to `queued`, if processing doesn't start instantly. * Once the processing starts the status is set to `running`.   * Once the data is available to download the status is set to `finished`.      * Whenever an error occurs during processing, the status must be set to `error`.", response = Void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "Bearer")
    }, tags={ "Batch Job Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 202, message = "The creation of the resource has been queued successfully.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response jobsJobIdResultsPost(@ApiParam(value = "Unique job identifier.",required=true, defaultValue="null") @PathParam("job_id") String jobId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.jobsJobIdResultsPost(jobId,securityContext);
    }
    @POST
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Create a new batch job", notes = "Creates a new batch processing task (job) from one or more (chained) processes at the back-end.  Processing the data doesn't start yet. The job status gets initialized as `submitted` by default.", response = Void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "Bearer")
    }, tags={ "Batch Job Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 201, message = "The resource has been created successfully and the location of the newly created resource is advertized by the back-end.  Examples: * `POST /services` redirects to `GET /services/{service_id}` * `POST /jobs` redirects to `GET /jobs/{job_id}`", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response jobsPost(@ApiParam(value = "" ) @Valid InlineObject6 inlineObject6
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.jobsPost(inlineObject6,securityContext);
    }
}
