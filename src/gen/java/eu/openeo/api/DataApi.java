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

import eu.openeo.api.factories.DataApiServiceFactory;
import eu.openeo.model.InlineResponse2001;
import eu.openeo.model.InlineResponse2002;
import io.swagger.annotations.ApiParam;

@Path("/data")
@Consumes({ "application/json" })
@Produces({ "application/json" })
@io.swagger.annotations.Api(description = "the data API")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class DataApi {
	private final DataApiService delegate;

	public DataApi(@Context ServletConfig servletContext) {
		DataApiService delegate = null;

		if (servletContext != null) {
			String implClass = servletContext.getInitParameter("DataApi.implementation");
			if (implClass != null && !"".equals(implClass.trim())) {
				try {
					delegate = (DataApiService) Class.forName(implClass).newInstance();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}

		if (delegate == null) {
			delegate = DataApiServiceFactory.getDataApi();
		}

		this.delegate = delegate;
	}

	@GET

	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Returns basic information about EO datasets that are available at the back-end.", notes = "Requests will ask the back-end for available data and will return an array of available datasets with very basic information such as their unique identifiers. Results can be filtered by space, time, and product name with very simple search expressions.", response = InlineResponse2001.class, responseContainer = "List", authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "EO Data Discovery", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "An array of EO datasets including their unique identifiers and some basic metadata.", response = InlineResponse2001.class, responseContainer = "List"),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response dataGet(
			@ApiParam(value = "string expression to search available datasets by name") @QueryParam("qname") String qname,
			@ApiParam(value = "WKT polygon to search for available datasets that spatially intersect with the polygon") @QueryParam("qgeom") String qgeom,
			@ApiParam(value = "ISO 8601 date/time string to find datasets with any data acquired after the given date/time") @QueryParam("qstartdate") String qstartdate,
			@ApiParam(value = "ISO 8601 date/time string to find datasets with any data acquired before the given date/time") @QueryParam("qenddate") String qenddate,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.dataGet(qname, qgeom, qstartdate, qenddate, securityContext);
	}

	@GET
	@Path("/opensearch")
	@Consumes({ "application/json" })
	@Produces({ "application/atom+xml" })
	@io.swagger.annotations.ApiOperation(value = "OpenSearch endpoint to receive standardized data search results.", notes = "This service offers more complex search functionality and returns results in an OpenSearch compliant Atom XML format.", response = Void.class, authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "EO Data Discovery", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "An array of EO datasets including their unique identifiers and some basic metadata.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response dataOpensearchGet(
			@ApiParam(value = "string expression to search available datasets") @QueryParam("q") String q,
			@ApiParam(value = "page start value") @QueryParam("start") Integer start,
			@ApiParam(value = "page size value") @QueryParam("rows") Integer rows,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.dataOpensearchGet(q, start, rows, securityContext);
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
	public Response dataOpensearchOptions(@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.dataOpensearchOptions(securityContext);
	}

	@OPTIONS

	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Response to allow Cross-Origin Resource Sharing.", notes = "Response for the preflight requests made by some clients due to Cross-Origin Resource Sharing restrictions. It sends the appropriate headers for this endpoint as defined in the section \"Responses\". See https://www.w3.org/TR/cors/ for more information.", response = Void.class, tags = {
			"CORS", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Gives internet browsers the permission to access the requested resource.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 405, message = "The requested HTTP method is not supported or allowed to be requested.", response = Void.class) })
	public Response dataOptions(@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.dataOptions(securityContext);
	}

	@GET
	@Path("/{product_id}")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Returns further information on a given EO product available at the back-end.", notes = "The request will ask the back-end for further details about a product specified by the identifier `product_id`", response = InlineResponse2002.class, authorizations = {
			@io.swagger.annotations.Authorization(value = "Bearer") }, tags = { "EO Data Discovery", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "JSON object with metadata of the EO dataset.", response = InlineResponse2002.class),

			@io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 404, message = "EO dataset with specified identifier is not available", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
	public Response dataProductIdGet(
			@ApiParam(value = "product identifier string such as `MOD18Q1`", required = true) @PathParam("product_id") String productId,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.dataProductIdGet(productId, securityContext);
	}

	@OPTIONS
	@Path("/{product_id}")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@io.swagger.annotations.ApiOperation(value = "Response to allow Cross-Origin Resource Sharing.", notes = "Response for the preflight requests made by some clients due to Cross-Origin Resource Sharing restrictions. It sends the appropriate headers for this endpoint as defined in the section \"Responses\". See https://www.w3.org/TR/cors/ for more information.", response = Void.class, tags = {
			"CORS", })
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Gives internet browsers the permission to access the requested resource.", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 405, message = "The requested HTTP method is not supported or allowed to be requested.", response = Void.class) })
	public Response dataProductIdOptions(
			@ApiParam(value = "product identifier string such as `MOD18Q1`", required = true) @PathParam("product_id") String productId,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.dataProductIdOptions(productId, securityContext);
	}
}
