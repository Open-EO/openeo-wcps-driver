package eu.openeo.api;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;

import javax.servlet.ServletConfig;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import eu.openeo.api.factories.UsersApiServiceFactory;
import eu.openeo.model.InlineResponse2004;
import eu.openeo.model.JobMeta;
import eu.openeo.model.ProcessGraph;
import eu.openeo.model.Service;
import io.swagger.annotations.ApiParam;

@Path("/users")
@Consumes({ "application/json" })
@Produces({ "application/json" })
@io.swagger.annotations.Api(description = "the users API")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class UsersApi {
	private final UsersApiService delegate;

	public UsersApi(@Context ServletConfig servletContext) {
		UsersApiService delegate = null;

		if (servletContext != null) {
			String implClass = servletContext.getInitParameter("UsersApi.implementation");
			if (implClass != null && !"".equals(implClass.trim())) {
				try {
					delegate = (UsersApiService) Class.forName(implClass).newInstance();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}

		if (delegate == null) {
			delegate = UsersApiServiceFactory.getUsersApi();
		}

		this.delegate = delegate;
	}

	@GET
	@Path("/{user_id}/credits")
	@Consumes({ "application/json" })
	@Produces({ "text/plain; charset=utf-8" })
	@io.swagger.annotations.ApiOperation(value = "Returns available user credits.", notes = "***Reserved for later use.** The credit system and payment procedures are to be defined.* For back-ends that involve accounting, this service will return the currently available credits. Other back-ends may simply return `Infinity`", response = BigDecimal.class, authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "User Content", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Available credits", response = BigDecimal.class),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 404, message = "User with specified identifier is not available.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response usersUserIdCreditsGet(
			@ApiParam(value = "user identifier, the special value `me` automatically refers to the own ID based on the provided HTTP `Authorization` header.", required = true) @PathParam("user_id") String userId,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.usersUserIdCreditsGet(userId, securityContext);
	}

	@OPTIONS
	@Path("/{user_id}/credits")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Response to allow Cross-Origin Resource Sharing.", notes = "Response for the preflight requests made by some clients due to Cross-Origin Resource Sharing restrictions. It sends the appropriate headers for this endpoint as defined in the section \"Responses\". See https://www.w3.org/TR/cors/ for more information.", response = Void.class, tags = {
			"CORS", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Gives internet browsers the permission to access the requested resource.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 405, message = "The requested HTTP method is not supported or allowed to be requested.", response = Void.class) })
	public Response usersUserIdCreditsOptions(
			@ApiParam(value = "user identifier, the special value `me` automatically refers to the own ID based on the provided HTTP `Authorization` header.", required = true) @PathParam("user_id") String userId,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.usersUserIdCreditsOptions(userId, securityContext);
	}

	@GET
	@Path("/{user_id}/files")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "List user-uploaded files.", notes = "This service lists all user-uploaded files that are stored at the back-end.", response = InlineResponse2004.class, responseContainer = "List", authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "User Content", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Flattened file tree with path relative to the user's root directory and some basic properties such as the file size and the timestamp of the last modification. All properties except the name are optional.", response = InlineResponse2004.class, responseContainer = "List"),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 404, message = "User with specified user id is not available.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response usersUserIdFilesGet(
			@ApiParam(value = "user identifier, the special value `me` automatically refers to the own ID based on the provided HTTP `Authorization` header.", required = true) @PathParam("user_id") String userId,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.usersUserIdFilesGet(userId, securityContext);
	}

	@OPTIONS
	@Path("/{user_id}/files")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Response to allow Cross-Origin Resource Sharing.", notes = "Response for the preflight requests made by some clients due to Cross-Origin Resource Sharing restrictions. It sends the appropriate headers for this endpoint as defined in the section \"Responses\". See https://www.w3.org/TR/cors/ for more information.", response = Void.class, tags = {
			"CORS", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Gives internet browsers the permission to access the requested resource.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 405, message = "The requested HTTP method is not supported or allowed to be requested.", response = Void.class) })
	public Response usersUserIdFilesOptions(
			@ApiParam(value = "user identifier, the special value `me` automatically refers to the own ID based on the provided HTTP `Authorization` header.", required = true) @PathParam("user_id") String userId,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.usersUserIdFilesOptions(userId, securityContext);
	}

	@DELETE
	@Path("/{user_id}/files/{path}")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Deletes a user file.", notes = "This service deletes an existing user-uploaded file specified by its path.", response = Void.class, authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "User Content", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "The file has been successfully deleted at the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 404, message = "File or user with specified identifier is not available.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 423, message = "The file that is being accessed is locked.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response usersUserIdFilesPathDelete(
			@ApiParam(value = "user identifier, the special value `me` automatically refers to the own ID based on the provided HTTP `Authorization` header.", required = true) @PathParam("user_id") String userId,
			@ApiParam(value = "Path relative to the user's root directory, must be URL encoded", required = true) @PathParam("path") String path,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.usersUserIdFilesPathDelete(userId, path, securityContext);
	}

	@GET
	@Path("/{user_id}/files/{path}")
	@Consumes({ "application/json" })
	@Produces({ "application/octet-stream" })
	@io.swagger.annotations.ApiOperation(value = "Download a user file.", notes = "This service downloads a user files identified by its path relative to the user's root directory.", response = File.class, authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "User Content", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "file from user storage", response = File.class),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 404, message = "File or user with specified identifier is not available", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response usersUserIdFilesPathGet(
			@ApiParam(value = "user identifier, the special value `me` automatically refers to the own ID based on the provided HTTP `Authorization` header.", required = true) @PathParam("user_id") String userId,
			@ApiParam(value = "Path relative to the user's root directory, must be URL encoded", required = true) @PathParam("path") String path,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.usersUserIdFilesPathGet(userId, path, securityContext);
	}

	@OPTIONS
	@Path("/{user_id}/files/{path}")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Response to allow Cross-Origin Resource Sharing.", notes = "Response for the preflight requests made by some clients due to Cross-Origin Resource Sharing restrictions. It sends the appropriate headers for this endpoint as defined in the section \"Responses\". See https://www.w3.org/TR/cors/ for more information.", response = Void.class, tags = {
			"CORS", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Gives internet browsers the permission to access the requested resource.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 405, message = "The requested HTTP method is not supported or allowed to be requested.", response = Void.class) })
	public Response usersUserIdFilesPathOptions(
			@ApiParam(value = "user identifier, the special value `me` automatically refers to the own ID based on the provided HTTP `Authorization` header.", required = true) @PathParam("user_id") String userId,
			@ApiParam(value = "Path relative to the user's root directory, must be URL encoded", required = true) @PathParam("path") String path,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.usersUserIdFilesPathOptions(userId, path, securityContext);
	}

	@PUT
	@Path("/{user_id}/files/{path}")
	@Consumes({ "multipart/form-data" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Upload a new or update an existing user file.", notes = "This service uploads a new or updates an existing file at a given path.", response = Void.class, authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "User Content", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "The file upload has been successful.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 404, message = "User with specified identifier is not available.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 422, message = "File is rejected.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 423, message = "The file that is being accessed is locked.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 507, message = "User exceeded his storage limit.", response = Void.class) })
	public Response usersUserIdFilesPathPut(
			@ApiParam(value = "user identifier, the special value `me` automatically refers to the own ID based on the provided HTTP `Authorization` header.", required = true) @PathParam("user_id") String userId,
			@ApiParam(value = "Path relative to the user's root directory, must be URL encoded", required = true) @PathParam("path") String path,
			@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail, @Context SecurityContext securityContext)
			throws NotFoundException {
		return delegate.usersUserIdFilesPathPut(userId, path, fileInputStream, fileDetail, securityContext);
	}

	@GET
	@Path("/{user_id}/jobs")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "List all jobs that have been submitted by the user.", notes = "Requests to this service will list all jobs submitted by a user with given id.", response = JobMeta.class, responseContainer = "List", authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "Job Management", "User Content", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Array of job descriptions", response = JobMeta.class, responseContainer = "List"),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 404, message = "User with specified identifier is not available.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response usersUserIdJobsGet(
			@ApiParam(value = "user identifier, the special value `me` automatically refers to the own ID based on the provided HTTP `Authorization` header.", required = true) @PathParam("user_id") String userId,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.usersUserIdJobsGet(userId, securityContext);
	}

	@OPTIONS
	@Path("/{user_id}/jobs")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Response to allow Cross-Origin Resource Sharing.", notes = "Response for the preflight requests made by some clients due to Cross-Origin Resource Sharing restrictions. It sends the appropriate headers for this endpoint as defined in the section \"Responses\". See https://www.w3.org/TR/cors/ for more information.", response = Void.class, tags = {
			"CORS", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Gives internet browsers the permission to access the requested resource.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 405, message = "The requested HTTP method is not supported or allowed to be requested.", response = Void.class) })
	public Response usersUserIdJobsOptions(
			@ApiParam(value = "user identifier, the special value `me` automatically refers to the own ID based on the provided HTTP `Authorization` header.", required = true) @PathParam("user_id") String userId,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.usersUserIdJobsOptions(userId, securityContext);
	}

	@GET
	@Path("/{user_id}/process_graphs")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Lists user-defined process graphs.", notes = "This service lists all process graphs of the user that are stored at the back-end.", response = Void.class, authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "User Content", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "JSON array with process graph ids", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response usersUserIdProcessGraphsGet(
			@ApiParam(value = "user identifier, the special value `me` automatically refers to the own ID based on the provided HTTP `Authorization` header.", required = true) @PathParam("user_id") String userId,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.usersUserIdProcessGraphsGet(userId, securityContext);
	}

	@OPTIONS
	@Path("/{user_id}/process_graphs")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Response to allow Cross-Origin Resource Sharing.", notes = "Response for the preflight requests made by some clients due to Cross-Origin Resource Sharing restrictions. It sends the appropriate headers for this endpoint as defined in the section \"Responses\". See https://www.w3.org/TR/cors/ for more information.", response = Void.class, tags = {
			"CORS", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Gives internet browsers the permission to access the requested resource.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 405, message = "The requested HTTP method is not supported or allowed to be requested.", response = Void.class) })
	public Response usersUserIdProcessGraphsOptions(
			@ApiParam(value = "user identifier, the special value `me` automatically refers to the own ID based on the provided HTTP `Authorization` header.", required = true) @PathParam("user_id") String userId,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.usersUserIdProcessGraphsOptions(userId, securityContext);
	}

	@POST
	@Path("/{user_id}/process_graphs")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Submits a new process graph and returns a unqiue identifier.", notes = "Creates a unique resource for a provided process graph that can be reused in other process graphs.", response = Void.class, authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "User Content", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "JSON object with process graph identifier", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response usersUserIdProcessGraphsPost(
			@ApiParam(value = "user identifier, the special value `me` automatically refers to the own ID based on the provided HTTP `Authorization` header.", required = true) @PathParam("user_id") String userId,
			@ApiParam(value = "Description of one or more (chained) processes including their input arguments", required = true) ProcessGraph processGraph,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.usersUserIdProcessGraphsPost(userId, processGraph, securityContext);
	}

	@DELETE
	@Path("/{user_id}/process_graphs/{process_graph_id}")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Deletes a process graph with given id.", notes = "Returns a JSON processs graph from its ID.", response = Void.class, authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "User Content", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "The job has been successfully deleted", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 404, message = "Process graph with given id is not available", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response usersUserIdProcessGraphsProcessGraphIdDelete(
			@ApiParam(value = "user identifier, the special value `me` automatically refers to the own ID based on the provided HTTP `Authorization` header.", required = true) @PathParam("user_id") String userId,
			@ApiParam(value = "Process graph identifier string", required = true) @PathParam("process_graph_id") String processGraphId,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.usersUserIdProcessGraphsProcessGraphIdDelete(userId, processGraphId, securityContext);
	}

	@GET
	@Path("/{user_id}/process_graphs/{process_graph_id}")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Get a process graph by id.", notes = "Returns a JSON processs graph from its id.", response = ProcessGraph.class, authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "User Content", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "JSON object with process graph", response = ProcessGraph.class),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 404, message = "Process graph with given id is not available", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response usersUserIdProcessGraphsProcessGraphIdGet(
			@ApiParam(value = "user identifier, the special value `me` automatically refers to the own ID based on the provided HTTP `Authorization` header.", required = true) @PathParam("user_id") String userId,
			@ApiParam(value = "Process graph identifier string", required = true) @PathParam("process_graph_id") String processGraphId,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.usersUserIdProcessGraphsProcessGraphIdGet(userId, processGraphId, securityContext);
	}

	@OPTIONS
	@Path("/{user_id}/process_graphs/{process_graph_id}")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Response to allow Cross-Origin Resource Sharing.", notes = "Response for the preflight requests made by some clients due to Cross-Origin Resource Sharing restrictions. It sends the appropriate headers for this endpoint as defined in the section \"Responses\". See https://www.w3.org/TR/cors/ for more information.", response = Void.class, tags = {
			"CORS", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Gives internet browsers the permission to access the requested resource.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 405, message = "The requested HTTP method is not supported or allowed to be requested.", response = Void.class) })
	public Response usersUserIdProcessGraphsProcessGraphIdOptions(
			@ApiParam(value = "user identifier, the special value `me` automatically refers to the own ID based on the provided HTTP `Authorization` header.", required = true) @PathParam("user_id") String userId,
			@ApiParam(value = "Process graph identifier string", required = true) @PathParam("process_graph_id") String processGraphId,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.usersUserIdProcessGraphsProcessGraphIdOptions(userId, processGraphId, securityContext);
	}

	@PUT
	@Path("/{user_id}/process_graphs/{process_graph_id}")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Replaces a process graph.", notes = "Replaces a process graph, but maintains the identifier.", response = Void.class, authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "User Content", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "The process graph has been replaced successfully.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response usersUserIdProcessGraphsProcessGraphIdPut(
			@ApiParam(value = "user identifier, the special value `me` automatically refers to the own ID based on the provided HTTP `Authorization` header.", required = true) @PathParam("user_id") String userId,
			@ApiParam(value = "Process graph identifier string", required = true) @PathParam("process_graph_id") String processGraphId,
			@ApiParam(value = "Description of one or more (chained) processes including their input arguments", required = true) ProcessGraph processGraph,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.usersUserIdProcessGraphsProcessGraphIdPut(userId, processGraphId, processGraph,
				securityContext);
	}

	@GET
	@Path("/{user_id}/services")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "List all running services that have been submitted by the user.", notes = "Requests to this endpoint will list all running services submitted by a user with given id.", response = Service.class, responseContainer = "List", authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "Services", "User Content", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Array of service descriptions", response = Service.class, responseContainer = "List"),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 404, message = "User with specified identifier is not available.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response usersUserIdServicesGet(
			@ApiParam(value = "user identifier, the special value `me` automatically refers to the own ID based on the provided HTTP `Authorization` header.", required = true) @PathParam("user_id") String userId,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.usersUserIdServicesGet(userId, securityContext);
	}

	@OPTIONS
	@Path("/{user_id}/services")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Response to allow Cross-Origin Resource Sharing.", notes = "Response for the preflight requests made by some clients due to Cross-Origin Resource Sharing restrictions. It sends the appropriate headers for this endpoint as defined in the section \"Responses\". See https://www.w3.org/TR/cors/ for more information.", response = Void.class, tags = {
			"CORS", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Gives internet browsers the permission to access the requested resource.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 405, message = "The requested HTTP method is not supported or allowed to be requested.", response = Void.class) })
	public Response usersUserIdServicesOptions(
			@ApiParam(value = "user identifier, the special value `me` automatically refers to the own ID based on the provided HTTP `Authorization` header.", required = true) @PathParam("user_id") String userId,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.usersUserIdServicesOptions(userId, securityContext);
	}
}
