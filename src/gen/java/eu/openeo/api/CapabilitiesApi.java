package eu.openeo.api;

import javax.servlet.ServletConfig;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import eu.openeo.api.factories.CapabilitiesApiServiceFactory;
import eu.openeo.model.InlineResponse200;

@Path("/capabilities")
@Consumes({ "application/json" })
@Produces({ "application/json" })
@io.swagger.annotations.Api(description = "the capabilities API")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class CapabilitiesApi {
	private final CapabilitiesApiService delegate;

	public CapabilitiesApi(@Context ServletConfig servletContext) {
		CapabilitiesApiService delegate = null;

		if (servletContext != null) {
			String implClass = servletContext.getInitParameter("CapabilitiesApi.implementation");
			if (implClass != null && !"".equals(implClass.trim())) {
				try {
					delegate = (CapabilitiesApiService) Class.forName(implClass).newInstance();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}

		if (delegate == null) {
			delegate = CapabilitiesApiServiceFactory.getCapabilitiesApi();
		}

		this.delegate = delegate;
	}

	@GET

	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Returns the capabilities, i.e., which OpenEO API features are supported  by the back-end.", notes = "The request will ask the back-end which features of the OpenEO API are supported and return a simple JSON description with available endpoints. Variables in the paths must be placed in curly braces, e.g. `{process_id}`, and have a self-speaking name.", response = Void.class, tags = {
			"Capabilities", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "An array of implemented API endpoints", response = Void.class) })
	public Response capabilitiesGet(@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.capabilitiesGet(securityContext);
	}

	@GET
	@Path("/output_formats")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Returns output formats supported by the back-end.", notes = "The request will ask the back-end for supported output formats, e.g. PNG, GTiff and time series, and its default output format. The response is an object of the default and all available output formats and their options. This does not include the supported web services. **Note**: Whenever available, the output format names and options should be fully aligned with GDAL, see http://www.gdal.org/formats_list.html. Some file formats not available through GDAL might be defined centrally for openEO in the documentation. Custom output formats ot options can be defined wherever needed. Not all output format options supported by GDAL need to be supported.", response = InlineResponse200.class, tags = {
			"Capabilities", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "An object with the default output format and a map containing all output formats as keys and an object that defines available options.", response = InlineResponse200.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response capabilitiesOutputFormatsGet(@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.capabilitiesOutputFormatsGet(securityContext);
	}

	@OPTIONS
	@Path("/output_formats")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Response to allow Cross-Origin Resource Sharing.", notes = "Response for the preflight requests made by some clients due to Cross-Origin Resource Sharing restrictions. It sends the appropriate headers for this endpoint as defined in the section \"Responses\". See https://www.w3.org/TR/cors/ for more information.", response = Void.class, tags = {
			"CORS", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Gives internet browsers the permission to access the requested resource.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 405, message = "The requested HTTP method is not supported or allowed to be requested.", response = Void.class) })
	public Response capabilitiesOutputFormatsOptions(@Context SecurityContext securityContext)
			throws NotFoundException {
		return delegate.capabilitiesOutputFormatsOptions(securityContext);
	}

	@GET
	@Path("/services")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Returns web service types supported by the back-end.", notes = "The request will ask the back-end for supported web service types, e.g. wms, tms, wmts or wcs. The response is an array of available web services types.", response = String.class, responseContainer = "List", tags = {
			"Capabilities", "Services", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "An array of implemented web service types", response = String.class, responseContainer = "List"),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response capabilitiesServicesGet(@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.capabilitiesServicesGet(securityContext);
	}

	@OPTIONS
	@Path("/services")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Response to allow Cross-Origin Resource Sharing.", notes = "Response for the preflight requests made by some clients due to Cross-Origin Resource Sharing restrictions. It sends the appropriate headers for this endpoint as defined in the section \"Responses\". See https://www.w3.org/TR/cors/ for more information.", response = Void.class, tags = {
			"CORS", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Gives internet browsers the permission to access the requested resource.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 405, message = "The requested HTTP method is not supported or allowed to be requested.", response = Void.class) })
	public Response capabilitiesServicesOptions(@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.capabilitiesServicesOptions(securityContext);
	}
}
