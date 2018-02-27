package eu.openeo.api;

import javax.servlet.ServletConfig;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import eu.openeo.api.factories.ServicesApiServiceFactory;
import eu.openeo.model.Service;
import eu.openeo.model.Service1;
import eu.openeo.model.Service2;
import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.PATCH;

@Path("/services")
@Consumes({ "application/json" })
@Produces({ "application/json" })
@io.swagger.annotations.Api(description = "the services API")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class ServicesApi {
	private final ServicesApiService delegate;

	public ServicesApi(@Context ServletConfig servletContext) {
		ServicesApiService delegate = null;

		if (servletContext != null) {
			String implClass = servletContext.getInitParameter("ServicesApi.implementation");
			if (implClass != null && !"".equals(implClass.trim())) {
				try {
					delegate = (ServicesApiService) Class.forName(implClass).newInstance();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}

		if (delegate == null) {
			delegate = ServicesApiServiceFactory.getServicesApi();
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
	public Response servicesOptions(@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.servicesOptions(securityContext);
	}

	@POST

	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Publish an interactive web service.", notes = "Calling this endpoint will enable to access results of a job as a web service such as WMTS, TMS or WCS. Depending on the specified job the service bases on pre-computed data (batch job) or needs to compute them on demand (lazy job). For lazy jobs the evaluation should pe performed in the sense that it is only evaluated for the requested spatial / temporal extent and resolution.", response = Service.class, authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "Services", "Job Management", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Details of the created service", response = Service.class),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response servicesPost(@ApiParam(value = "The base data for the service to create") Service1 service,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.servicesPost(service, securityContext);
	}

	@DELETE
	@Path("/{service_id}")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Stop a given service by id", notes = "Calling this endpoint will stop a given web service to access result data.", response = Void.class, authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "Services", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "The service has been successfully deleted.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 404, message = "A service with given id is not available", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response servicesServiceIdDelete(
			@ApiParam(value = "Service identifier string", required = true) @PathParam("service_id") String serviceId,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.servicesServiceIdDelete(serviceId, securityContext);
	}

	@GET
	@Path("/{service_id}")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Get service information by id.", notes = "Requests to this endpoint will return JSON description of the service.", response = Service.class, authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "Services", "Result Access", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Details of the created service", response = Service.class),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 404, message = "A service with the given id is not available.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response servicesServiceIdGet(
			@ApiParam(value = "Service identifier string", required = true) @PathParam("service_id") String serviceId,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.servicesServiceIdGet(serviceId, securityContext);
	}

	@OPTIONS
	@Path("/{service_id}")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Response to allow Cross-Origin Resource Sharing.", notes = "Response for the preflight requests made by some clients due to Cross-Origin Resource Sharing restrictions. It sends the appropriate headers for this endpoint as defined in the section \"Responses\". See https://www.w3.org/TR/cors/ for more information.", response = Void.class, tags = {
			"CORS", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Gives internet browsers the permission to access the requested resource.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 405, message = "The requested HTTP method is not supported or allowed to be requested.", response = Void.class) })
	public Response servicesServiceIdOptions(
			@ApiParam(value = "Service identifier string", required = true) @PathParam("service_id") String serviceId,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.servicesServiceIdOptions(serviceId, securityContext);
	}

	@PATCH
	@Path("/{service_id}")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Modifies a web service.", notes = "***Reserved for later use.** A protocol to modify a service is to be defined.* Calling this endpoint will change the specified web service, but maintain its identifier.", response = Void.class, authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "Services", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Changes to the service applied successfully.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 404, message = "A service with the given id is not available.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response servicesServiceIdPatch(
			@ApiParam(value = "Service identifier string", required = true) @PathParam("service_id") String serviceId,
			@ApiParam(value = "The data to change for the spcified service") Service2 service,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.servicesServiceIdPatch(serviceId, service, securityContext);
	}
}
