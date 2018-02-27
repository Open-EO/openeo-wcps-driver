package eu.openeo.api;

import javax.servlet.ServletConfig;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import eu.openeo.api.factories.ProcessesApiServiceFactory;
import eu.openeo.model.InlineResponse2003;
import eu.openeo.model.ProcessDescription;
import io.swagger.annotations.ApiParam;

@Path("/processes")
@Consumes({ "application/json" })
@Produces({ "application/json" })
@io.swagger.annotations.Api(description = "the processes API")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class ProcessesApi {
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
	@io.swagger.annotations.ApiOperation(value = "Returns processes supported by the back-end.", notes = "The request will ask the back-end for available processes and will return an array of available processes with their unique identifiers and description.", response = InlineResponse2003.class, responseContainer = "List", authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "Process Discovery", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "An array of EO processes including their unique identifiers and a description.", response = InlineResponse2003.class, responseContainer = "List"),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response processesGet(
			@ApiParam(value = "string expression to search for available processes by name") @QueryParam("qname") String qname,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.processesGet(qname, securityContext);
	}

	@GET
	@Path("/opensearch")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "OpenSearch endpoint to request standardized process search results.", notes = "This service offers more complex search functionality and returns results in an OpenSearch compliant Atom XML format.", response = Void.class, authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "Process Discovery", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "OpenSearch response", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response processesOpensearchGet(
			@ApiParam(value = "string expression to search available processes") @QueryParam("q") String q,
			@ApiParam(value = "page start value") @QueryParam("start") Integer start,
			@ApiParam(value = "page size value") @QueryParam("rows") Integer rows,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.processesOpensearchGet(q, start, rows, securityContext);
	}

	@OPTIONS
	@Path("/opensearch")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Response to allow Cross-Origin Resource Sharing.", notes = "Response for the preflight requests made by some clients due to Cross-Origin Resource Sharing restrictions. It sends the appropriate headers for this endpoint as defined in the section \"Responses\". See https://www.w3.org/TR/cors/ for more information.", response = Void.class, tags = {
			"CORS", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Gives internet browsers the permission to access the requested resource.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 405, message = "The requested HTTP method is not supported or allowed to be requested.", response = Void.class) })
	public Response processesOpensearchOptions(@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.processesOpensearchOptions(securityContext);
	}

	@OPTIONS

	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Response to allow Cross-Origin Resource Sharing.", notes = "Response for the preflight requests made by some clients due to Cross-Origin Resource Sharing restrictions. It sends the appropriate headers for this endpoint as defined in the section \"Responses\". See https://www.w3.org/TR/cors/ for more information.", response = Void.class, tags = {
			"CORS", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Gives internet browsers the permission to access the requested resource.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 405, message = "The requested HTTP method is not supported or allowed to be requested.", response = Void.class) })
	public Response processesOptions(@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.processesOptions(securityContext);
	}

	@GET
	@Path("/{process_id}")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Returns further information on a given EO process available at the back-end.", notes = "The request will ask the back-end for further details about a process specified by identifier", response = ProcessDescription.class, authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "Process Discovery", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "JSON object with metadata of the EO process.", response = ProcessDescription.class),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 404, message = "Process with specified identifier is not available", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response processesProcessIdGet(
			@ApiParam(value = "process identifier string such as `NDVI`", required = true) @PathParam("process_id") String processId,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.processesProcessIdGet(processId, securityContext);
	}

	@OPTIONS
	@Path("/{process_id}")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Response to allow Cross-Origin Resource Sharing.", notes = "Response for the preflight requests made by some clients due to Cross-Origin Resource Sharing restrictions. It sends the appropriate headers for this endpoint as defined in the section \"Responses\". See https://www.w3.org/TR/cors/ for more information.", response = Void.class, tags = {
			"CORS", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Gives internet browsers the permission to access the requested resource.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 405, message = "The requested HTTP method is not supported or allowed to be requested.", response = Void.class) })
	public Response processesProcessIdOptions(
			@ApiParam(value = "process identifier string such as `NDVI`", required = true) @PathParam("process_id") String processId,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.processesProcessIdOptions(processId, securityContext);
	}
}
