package eu.openeo.api;

import java.math.BigDecimal;

import javax.servlet.ServletConfig;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import eu.openeo.api.factories.JobsApiServiceFactory;
import eu.openeo.model.JobFull;
import eu.openeo.model.JobMeta;
import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.PATCH;

@Path("/jobs")
@Consumes({ "application/json" })
@Produces({ "application/json" })
@io.swagger.annotations.Api(description = "the jobs API")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class JobsApi {
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

	@OPTIONS
	@Path("/{job_id}/cancel")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Response to allow Cross-Origin Resource Sharing.", notes = "Response for the preflight requests made by some clients due to Cross-Origin Resource Sharing restrictions. It sends the appropriate headers for this endpoint as defined in the section \"Responses\". See https://www.w3.org/TR/cors/ for more information.", response = Void.class, tags = {
			"CORS", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Gives internet browsers the permission to access the requested resource.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 405, message = "The requested HTTP method is not supported or allowed to be requested.", response = Void.class) })
	public Response jobsJobIdCancelOptions(
			@ApiParam(value = "Job identifier string", required = true) @PathParam("job_id") String jobId,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.jobsJobIdCancelOptions(jobId, securityContext);
	}

	@PATCH
	@Path("/{job_id}/cancel")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Cancels any back-end computations of a job", notes = "For batch jobs this request cancels all related computations at the back-end. Lazy jobs will not respond to any further on demand requests, e.g. download calls or service requests. This job won't generate additional costs for processing unless started again using `GET /jobs/{job_id}/queue` (for batch jobs) or updated using `PATCH /jobs`. Results of batch jobs might be discarded and deleted by the back-end. Any service associated with this job might be deleted from the back-end.", response = Void.class, authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "Job Management", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "The job has been successfully canceled", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 404, message = "A job with the specified identifier is not available.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response jobsJobIdCancelPatch(
			@ApiParam(value = "Job identifier string", required = true) @PathParam("job_id") String jobId,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.jobsJobIdCancelPatch(jobId, securityContext);
	}

	@GET
	@Path("/{job_id}/download")
	@Consumes({ "application/json" })
	@Produces({ "application/json", "application/metalink+xml" })
	@io.swagger.annotations.ApiOperation(value = "Request download links for results of batch jobs.", notes = "This request will provide links to download results of batch jobs. Depending on the Content-Type header, the response is either a simple JSON array with URLs as strings or a metalink XML document.", response = Void.class, authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "Result Access", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Valid download links have been returned. The download links doesn't necessarily need to be located under the API base url.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 404, message = "A job with the specified identifier is not available.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 410, message = "Job with specified identifier has been canceled.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response jobsJobIdDownloadGet(
			@ApiParam(value = "Job identifier string", required = true) @PathParam("job_id") String jobId,
			@ApiParam(value = "Output format to be used. Supported formats and options can be retrieved using the `GET /capabilities/output_formats` endpoint. If no output format has been specified here or for the job in general, the back-end falls back to its default format, which is  specified in the `GET /capabilities/output_formats` endpoint. **Note:** The options available to the specified output format can be added as individual query parameters to the request.") @QueryParam("format") String format,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.jobsJobIdDownloadGet(jobId, format, securityContext);
	}

	@OPTIONS
	@Path("/{job_id}/download")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Response to allow Cross-Origin Resource Sharing.", notes = "Response for the preflight requests made by some clients due to Cross-Origin Resource Sharing restrictions. It sends the appropriate headers for this endpoint as defined in the section \"Responses\". See https://www.w3.org/TR/cors/ for more information.", response = Void.class, tags = {
			"CORS", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Gives internet browsers the permission to access the requested resource.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 405, message = "The requested HTTP method is not supported or allowed to be requested.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 406, message = "The server is not capable to deliver the requested format.", response = Void.class) })
	public Response jobsJobIdDownloadOptions(
			@ApiParam(value = "Job identifier string", required = true) @PathParam("job_id") String jobId,
			@ApiParam(value = "Output format to be used. Supported formats and options can be retrieved using the `GET /capabilities/output_formats` endpoint. If no output format has been specified here or for the job in general, the back-end falls back to its default format, which is  specified in the `GET /capabilities/output_formats` endpoint. **Note:** The options available to the specified output format can be added as individual query parameters to the request.") @QueryParam("format") String format,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.jobsJobIdDownloadOptions(jobId, format, securityContext);
	}

	@GET
	@Path("/{job_id}")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Returns information about a submitted job.", notes = "Returns detailed information about a submitted job including its current status and the underlying process graph", response = JobFull.class, authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "Job Management", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Full job information.", response = JobFull.class),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 404, message = "A job with the specified identifier is not available.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response jobsJobIdGet(
			@ApiParam(value = "Job identifier string", required = true) @PathParam("job_id") String jobId,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.jobsJobIdGet(jobId, securityContext);
	}

	@OPTIONS
	@Path("/{job_id}")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Response to allow Cross-Origin Resource Sharing.", notes = "Response for the preflight requests made by some clients due to Cross-Origin Resource Sharing restrictions. It sends the appropriate headers for this endpoint as defined in the section \"Responses\". See https://www.w3.org/TR/cors/ for more information.", response = Void.class, tags = {
			"CORS", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Gives internet browsers the permission to access the requested resource.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 405, message = "The requested HTTP method is not supported or allowed to be requested.", response = Void.class) })
	public Response jobsJobIdOptions(
			@ApiParam(value = "Job identifier string", required = true) @PathParam("job_id") String jobId,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.jobsJobIdOptions(jobId, securityContext);
	}

	@PATCH
	@Path("/{job_id}")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Modifies a job at the back-end.", notes = "***Reserved for later use.** A protocol to modify a job is to be defined.* Modifies an existing job at the back-end but maintains the identifier. All running calculations are stopped if possible.", response = Void.class, authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "Job Management", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Changes to the job applied successfully.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 404, message = "A job with the specified identifier is not available.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 423, message = "The job is currently running and could not be canceled by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response jobsJobIdPatch(
			@ApiParam(value = "Job identifier string", required = true) @PathParam("job_id") String jobId,
			@ApiParam(value = "Specifies the job details to update, e.g. the process graph or the output format.") JobFull job,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.jobsJobIdPatch(jobId, job, securityContext);
	}

	@OPTIONS
	@Path("/{job_id}/pause")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Response to allow Cross-Origin Resource Sharing.", notes = "Response for the preflight requests made by some clients due to Cross-Origin Resource Sharing restrictions. It sends the appropriate headers for this endpoint as defined in the section \"Responses\". See https://www.w3.org/TR/cors/ for more information.", response = Void.class, tags = {
			"CORS", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Gives internet browsers the permission to access the requested resource.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 405, message = "The requested HTTP method is not supported or allowed to be requested.", response = Void.class) })
	public Response jobsJobIdPauseOptions(
			@ApiParam(value = "Job identifier string", required = true) @PathParam("job_id") String jobId,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.jobsJobIdPauseOptions(jobId, securityContext);
	}

	@PATCH
	@Path("/{job_id}/pause")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Pausing a batch job.", notes = "This request pauses a batch job. Execution is stopped, but might be resumed later by queueing it again. Intermediate results are stored for resuming. This job won't generate additional costs for processing.", response = Void.class, authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "Job Management", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "The job has been successfully paused.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 404, message = "A job with the specified identifier is not available.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 428, message = "Job with specified identifier is queued or is not running.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response jobsJobIdPausePatch(
			@ApiParam(value = "Job identifier string", required = true) @PathParam("job_id") String jobId,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.jobsJobIdPausePatch(jobId, securityContext);
	}

	@OPTIONS
	@Path("/{job_id}/queue")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Response to allow Cross-Origin Resource Sharing.", notes = "Response for the preflight requests made by some clients due to Cross-Origin Resource Sharing restrictions. It sends the appropriate headers for this endpoint as defined in the section \"Responses\". See https://www.w3.org/TR/cors/ for more information.", response = Void.class, tags = {
			"CORS", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Gives internet browsers the permission to access the requested resource.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 405, message = "The requested HTTP method is not supported or allowed to be requested.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 406, message = "The server is not capable to deliver the requested format.", response = Void.class) })
	public Response jobsJobIdQueueOptions(
			@ApiParam(value = "Job identifier string", required = true) @PathParam("job_id") String jobId,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.jobsJobIdQueueOptions(jobId, securityContext);
	}

	@PATCH
	@Path("/{job_id}/queue")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Running a job in batch mode.", notes = "This request converts a job into a batch job and queues it for execution. A paused job can be resumed with this request.", response = Void.class, authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "Job Management", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "The job has been successfully queued.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 404, message = "A job with the specified identifier is not available.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 410, message = "Job with specified identifier can't be resumed.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response jobsJobIdQueuePatch(
			@ApiParam(value = "Job identifier string", required = true) @PathParam("job_id") String jobId,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.jobsJobIdQueuePatch(jobId, securityContext);
	}

	@GET
	@Path("/{job_id}/subscribe")
	@Consumes({ "application/json" })
	@Produces({ "" })
	@io.swagger.annotations.ApiOperation(value = "Subscribes to job execution updates that are communicated over WebSockets.", notes = "***Reserved for later use.** The WebSocket-based protocol communicating the job updates is to be defined.*", response = Void.class, authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "Job Management", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 101, message = "Successful subscription to job updates returns in a protocol change to a web socket connection.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 404, message = "A job with the specified identifier is not available.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response jobsJobIdSubscribeGet(
			@ApiParam(value = "Job identifier string", required = true) @PathParam("job_id") String jobId,
			@ApiParam(value = "WebSocket handshake request header", required = true, allowableValues = "websocket") @HeaderParam("Upgrade") String upgrade,
			@ApiParam(value = "WebSocket handshake request header", required = true, allowableValues = "Upgrade") @HeaderParam("Connection") String connection,
			@ApiParam(value = "WebSocket handshake request header", required = true) @HeaderParam("Sec-WebSocket-Key") String secWebSocketKey,
			@ApiParam(value = "WebSocket handshake request header", required = true, allowableValues = "job_subscribe") @HeaderParam("Sec-WebSocket-Protocol") String secWebSocketProtocol,
			@ApiParam(value = "WebSocket handshake request header", required = true) @HeaderParam("Sec-WebSocket-Version") BigDecimal secWebSocketVersion,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.jobsJobIdSubscribeGet(jobId, upgrade, connection, secWebSocketKey, secWebSocketProtocol,
				secWebSocketVersion, securityContext);
	}

	@OPTIONS
	@Path("/{job_id}/subscribe")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Response to allow Cross-Origin Resource Sharing.", notes = "Response for the preflight requests made by some clients due to Cross-Origin Resource Sharing restrictions. It sends the appropriate headers for this endpoint as defined in the section \"Responses\". See https://www.w3.org/TR/cors/ for more information.", response = Void.class, tags = {
			"CORS", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Gives internet browsers the permission to access the requested resource.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 405, message = "The requested HTTP method is not supported or allowed to be requested.", response = Void.class) })
	public Response jobsJobIdSubscribeOptions(
			@ApiParam(value = "Job identifier string", required = true) @PathParam("job_id") String jobId,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.jobsJobIdSubscribeOptions(jobId, securityContext);
	}

	@OPTIONS

	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Response to allow Cross-Origin Resource Sharing.", notes = "Response for the preflight requests made by some clients due to Cross-Origin Resource Sharing restrictions. It sends the appropriate headers for this endpoint as defined in the section \"Responses\". See https://www.w3.org/TR/cors/ for more information.", response = Void.class, tags = {
			"CORS", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Gives internet browsers the permission to access the requested resource.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 405, message = "The requested HTTP method is not supported or allowed to be requested.", response = Void.class) })
	public Response jobsOptions(@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.jobsOptions(securityContext);
	}

	@POST

	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Submits a new job to the back-end.", notes = "Creates a new job from one or more (chained) processes at the back-end. Jobs are initially always lazy jobs and will not run the computations until on demand  requests or separately queueing it. Queueing it converts a lazy job to a batch job.", response = JobMeta.class, authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "Job Management", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Details of the created job", response = JobMeta.class),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response jobsPost(
			@ApiParam(value = "Specifies the job details, e.g. the process graph and _optionally_ the output format. The output format might be also specified later during download and is not necessary for web services at all.") JobFull job,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.jobsPost(job, securityContext);
	}
}
