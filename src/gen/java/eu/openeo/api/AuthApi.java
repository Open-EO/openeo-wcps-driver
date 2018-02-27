package eu.openeo.api;

import javax.servlet.ServletConfig;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import eu.openeo.api.factories.AuthApiServiceFactory;
import io.swagger.annotations.ApiParam;

@Path("/auth")
@Consumes({ "application/json" })
@Produces({ "application/json" })
@io.swagger.annotations.Api(description = "the auth API")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class AuthApi {
	private final AuthApiService delegate;

	public AuthApi(@Context ServletConfig servletContext) {
		AuthApiService delegate = null;

		if (servletContext != null) {
			String implClass = servletContext.getInitParameter("AuthApi.implementation");
			if (implClass != null && !"".equals(implClass.trim())) {
				try {
					delegate = (AuthApiService) Class.forName(implClass).newInstance();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}

		if (delegate == null) {
			delegate = AuthApiServiceFactory.getAuthApi();
		}

		this.delegate = delegate;
	}

	@GET
	@Path("/login")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Check whether the user is registered with the specified credentials at the back-end.", notes = "***Work in progress.** The general authentication procedure is to be defined and will probably change in the future.* This request checks whether the credentials provided in the HTTP Basic header are valid. Returns the corresponding internal user ID and a bearer token. Back-ends that do not require authentication such as a local file-based implementation may always return the generic user ID `me` and no bearer token.", response = String.class, authorizations = {
			@io.swagger.annotations.Authorization(value = "Basic") }, tags = { "Authentication", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Login successful. Returns the user ID and a bearer token for future API calls. ", response = String.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Login failed", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response authLoginGet(@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.authLoginGet(securityContext);
	}

	@OPTIONS
	@Path("/login")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Response to allow Cross-Origin Resource Sharing.", notes = "Response for the preflight requests made by some clients due to Cross-Origin Resource Sharing restrictions. It sends the appropriate headers for this endpoint as defined in the section \"Responses\". See https://www.w3.org/TR/cors/ for more information.", response = Void.class, tags = {
			"CORS", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Gives internet browsers the permission to access the requested resource.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 405, message = "The requested HTTP method is not supported or allowed to be requested.", response = Void.class) })
	public Response authLoginOptions(@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.authLoginOptions(securityContext);
	}

	@OPTIONS
	@Path("/register")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Response to allow Cross-Origin Resource Sharing.", notes = "Response for the preflight requests made by some clients due to Cross-Origin Resource Sharing restrictions. It sends the appropriate headers for this endpoint as defined in the section \"Responses\". See https://www.w3.org/TR/cors/ for more information.", response = Void.class, tags = {
			"CORS", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Gives internet browsers the permission to access the requested resource.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 405, message = "The requested HTTP method is not supported or allowed to be requested.", response = Void.class) })
	public Response authRegisterOptions(@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.authRegisterOptions(securityContext);
	}

	@POST
	@Path("/register")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Registers a new user account.", notes = "***Work in progress.** The general authentication procedure is to be defined and will probably change in the future.* This request registers a new user account.", response = String.class, tags = {
			"Authentication", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Registration successful. Returns the newly created user ID. ", response = String.class),

			@io.swagger.annotations.ApiResponse(code = 420, message = "Registration failed, e.g. user name not available.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 500, message = "Registration failed", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response authRegisterPost(
			@ApiParam(value = "Password to be used for the newly created user account.", required = true) String password,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.authRegisterPost(password, securityContext);
	}
}
