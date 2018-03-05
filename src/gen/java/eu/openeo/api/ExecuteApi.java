package eu.openeo.api;

import javax.servlet.ServletConfig;
import javax.ws.rs.Consumes;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import eu.openeo.api.factories.ExecuteApiServiceFactory;
import eu.openeo.model.JobFull;
import io.swagger.annotations.ApiParam;

@Path("/execute")
@Consumes({ "application/json" })
@Produces({ "application/json" })
@io.swagger.annotations.Api(description = "the execute API")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class ExecuteApi {
	private final ExecuteApiService delegate;

	public ExecuteApi(@Context ServletConfig servletContext) {
		ExecuteApiService delegate = null;

		if (servletContext != null) {
			String implClass = servletContext.getInitParameter("ExecuteApi.implementation");
			if (implClass != null && !"".equals(implClass.trim())) {
				try {
					delegate = (ExecuteApiService) Class.forName(implClass).newInstance();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}

		if (delegate == null) {
			delegate = ExecuteApiServiceFactory.getExecuteApi();
		}

		this.delegate = delegate;
	}

	@OPTIONS

	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Response to allow Cross-Origin Resource Sharing.", notes = "Response for the preflight requests made by some clients due to Cross-Origin Resource Sharing restrictions. It sends the appropriate headers for this endpoint as defined in the section \"Responses\". See https://www.w3.org/TR/cors/ for more information.", response = Void.class, tags = {
			"CORS", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Gives internet browsers the permission to access the requested resource.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 405, message = "The requested HTTP method is not supported or allowed to be requested.", response = Void.class) })
	public Response executeOptions(@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.executeOptions(securityContext);
	}

	@POST

	@Consumes({ "application/json" })
	@Produces({ "*/*" })
	@io.swagger.annotations.ApiOperation(value = "Execute a process graph synchronously.", notes = "Process graphs will be executed directly and the result will be downloaded in the specified format. The process graph must be specified either directy in the request body or by its URI as query parameter", response = Void.class, authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "Result Access", "Job Management", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Result data in the specified output format", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 406, message = "The server is not capable to deliver the requested format.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response executePost(
			@ApiParam(value = "Specifies the job details, e.g. the process graph and the output format.", required = true) JobFull job,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.executePost(job, securityContext);
	}
}
