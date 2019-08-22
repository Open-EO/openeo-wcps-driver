package eu.openeo.api;

import javax.annotation.security.RolesAllowed;
import javax.servlet.ServletConfig;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import eu.openeo.api.factories.ProcessGraphsApiServiceFactory;
import eu.openeo.backend.auth.filter.RequireToken;
import eu.openeo.model.Error;
import eu.openeo.model.StoreProcessGraphRequest;
import eu.openeo.model.StoredProcessGraphListResponse;
import eu.openeo.model.StoredProcessGraphResponse;
import eu.openeo.model.UpdateStoredProcessGraphRequest;
import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.PATCH;

@Path("/process_graphs")
@RequireToken
@RolesAllowed({"PUBLIC", "EURAC"})
@io.swagger.annotations.Api(description = "the process_graphs API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class ProcessGraphsApi  {
   private final ProcessGraphsApiService delegate;

   public ProcessGraphsApi(@Context ServletConfig servletContext) {
      ProcessGraphsApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("ProcessGraphsApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (ProcessGraphsApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = ProcessGraphsApiServiceFactory.getProcessGraphsApi();
      }

      this.delegate = delegate;
   }

    @GET
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "List all stored process graphs", notes = "This service lists all process graphs of the authenticated user that are stored at the back-end.", response = StoredProcessGraphListResponse.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "Bearer")
    }, tags={ "Process Graph Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "JSON array with stored process graph meta data", response = StoredProcessGraphListResponse.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response processGraphsGet(@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.processGraphsGet(securityContext);
    }
    @POST
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Store a process graph", notes = "Creates a unique resource for a provided process graph that can be reused in other process graphs.", response = Void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "Bearer")
    }, tags={ "Process Graph Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 201, message = "The resource has been created successfully and the location of the newly created resource is advertized by the back-end.  Examples: * `POST /services` redirects to `GET /services/{service_id}` * `POST /jobs` redirects to `GET /jobs/{job_id}`", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response processGraphsPost(@ApiParam(value = "" ) @Valid StoredProcessGraphResponse storeProcessGraphRequest
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.processGraphsPost(storeProcessGraphRequest, securityContext);
    }
    @DELETE
    @Path("/{process_graph_id}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Delete a stored process graph", notes = "", response = Void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "Bearer")
    }, tags={ "Process Graph Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 204, message = "The process graph has been successfully deleted", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response processGraphsProcessGraphIdDelete(@ApiParam(value = "Unique process graph identifier.",required=true) @PathParam("process_graph_id") String processGraphId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.processGraphsProcessGraphIdDelete(processGraphId, securityContext);
    }
    @GET
    @Path("/{process_graph_id}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Full metadata for a stored process graph", notes = "Returns all information about a process graph.", response = StoredProcessGraphResponse.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "Bearer")
    }, tags={ "Process Graph Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "JSON object with process graph", response = StoredProcessGraphResponse.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response processGraphsProcessGraphIdGet(@ApiParam(value = "Unique process graph identifier.",required=true) @PathParam("process_graph_id") String processGraphId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.processGraphsProcessGraphIdGet(processGraphId, securityContext);
    }
    @PATCH
    @Path("/{process_graph_id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Modify a stored process graph", notes = "Replaces a process graph or modifies the title, but maintains the identifier.", response = Void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "Bearer")
    }, tags={ "Process Graph Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 204, message = "The process graph data has been updated successfully.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response processGraphsProcessGraphIdPatch(@ApiParam(value = "Unique process graph identifier.",required=true) @PathParam("process_graph_id") String processGraphId
,@ApiParam(value = "" ) @Valid UpdateStoredProcessGraphRequest updateStoredProcessGraphRequest
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.processGraphsProcessGraphIdPatch(processGraphId, updateStoredProcessGraphRequest, securityContext);
    }
}
