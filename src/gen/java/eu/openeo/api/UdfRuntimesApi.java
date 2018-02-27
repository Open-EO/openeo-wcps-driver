package eu.openeo.api;

import java.util.Map;

import javax.servlet.ServletConfig;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import eu.openeo.api.factories.UdfRuntimesApiServiceFactory;
import eu.openeo.model.UdfDescription;
import io.swagger.annotations.ApiParam;

@Path("/udf_runtimes")
@Consumes({ "application/json" })
@Produces({ "application/json" })
@io.swagger.annotations.Api(description = "the udf_runtimes API")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class UdfRuntimesApi {
	private final UdfRuntimesApiService delegate;

	public UdfRuntimesApi(@Context ServletConfig servletContext) {
		UdfRuntimesApiService delegate = null;

		if (servletContext != null) {
			String implClass = servletContext.getInitParameter("UdfRuntimesApi.implementation");
			if (implClass != null && !"".equals(implClass.trim())) {
				try {
					delegate = (UdfRuntimesApiService) Class.forName(implClass).newInstance();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}

		if (delegate == null) {
			delegate = UdfRuntimesApiServiceFactory.getUdfRuntimesApi();
		}

		this.delegate = delegate;
	}

	@GET

	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Returns the programming languages including their environments and UDF types supported.", notes = "Describes how custom user-defined functions can be exposed to the data and which programming languages and environments are supported by the back-end.", response = Map.class, responseContainer = "List", authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "UDF Runtime Discovery", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Description of UDF runtime support", response = Map.class, responseContainer = "List"),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response udfRuntimesGet(@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.udfRuntimesGet(securityContext);
	}

	@GET
	@Path("/{lang}/{udf_type}")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Returns the process description of UDF schemas.", notes = "Returns the process description of UDF schemas, which offer different possibilities how user-defined scripts can be applied to the data.", response = UdfDescription.class, authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "UDF Runtime Discovery", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Process description", response = UdfDescription.class),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 404, message = "UDF type with specified identifier is not available", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature, language or UDF type is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response udfRuntimesLangUdfTypeGet(
			@ApiParam(value = "Language identifier such as `R`", required = true, allowableValues = "python, R") @PathParam("lang") String lang,
			@ApiParam(value = "The UDF types define how UDFs can be exposed to the data, how they can be parallelized, and how the result schema should be structured.", required = true, allowableValues = "apply_pixel, apply_scene, reduce_time, reduce_space, window_time, window_space, window_spacetime, aggregate_time, aggregate_space, aggregate_spacetime") @PathParam("udf_type") String udfType,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.udfRuntimesLangUdfTypeGet(lang, udfType, securityContext);
	}

	@OPTIONS
	@Path("/{lang}/{udf_type}")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Response to allow Cross-Origin Resource Sharing.", notes = "Response for the preflight requests made by some clients due to Cross-Origin Resource Sharing restrictions. It sends the appropriate headers for this endpoint as defined in the section \"Responses\". See https://www.w3.org/TR/cors/ for more information.", response = Void.class, tags = {
			"CORS", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Gives internet browsers the permission to access the requested resource.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 405, message = "The requested HTTP method is not supported or allowed to be requested.", response = Void.class) })
	public Response udfRuntimesLangUdfTypeOptions(
			@ApiParam(value = "Language identifier such as `R`", required = true, allowableValues = "python, R") @PathParam("lang") String lang,
			@ApiParam(value = "The UDF types define how UDFs can be exposed to the data, how they can be parallelized, and how the result schema should be structured.", required = true, allowableValues = "apply_pixel, apply_scene, reduce_time, reduce_space, window_time, window_space, window_spacetime, aggregate_time, aggregate_space, aggregate_spacetime") @PathParam("udf_type") String udfType,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.udfRuntimesLangUdfTypeOptions(lang, udfType, securityContext);
	}

	@OPTIONS

	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Response to allow Cross-Origin Resource Sharing.", notes = "Response for the preflight requests made by some clients due to Cross-Origin Resource Sharing restrictions. It sends the appropriate headers for this endpoint as defined in the section \"Responses\". See https://www.w3.org/TR/cors/ for more information.", response = Void.class, tags = {
			"CORS", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Gives internet browsers the permission to access the requested resource.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 405, message = "The requested HTTP method is not supported or allowed to be requested.", response = Void.class) })
	public Response udfRuntimesOptions(@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.udfRuntimesOptions(securityContext);
	}
}
