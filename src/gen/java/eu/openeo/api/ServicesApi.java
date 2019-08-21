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

import eu.openeo.api.factories.ServicesApiServiceFactory;
import eu.openeo.backend.auth.filter.RequireToken;
import eu.openeo.model.Error;
import eu.openeo.model.SecondaryWebServiceResponse;
import eu.openeo.model.SecondaryWebServicesListResponse;
import eu.openeo.model.StoreSecondaryWebServiceRequest;
import eu.openeo.model.UpdateSecondaryWebServiceRequest;
import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.PATCH;

@Path("/services")
@RequireToken
@RolesAllowed({"PUBLIC", "EURAC"})
@io.swagger.annotations.Api(description = "the services API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class ServicesApi  {
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

    @GET
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "List all web services", notes = "Requests to this endpoint will list all running secondary web services submitted by a user with given id.", response = SecondaryWebServicesListResponse.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "Bearer")
    }, tags={ "Secondary Services Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Array of service descriptions", response = SecondaryWebServicesListResponse.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response servicesGet(@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.servicesGet(securityContext);
    }
    @POST
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Publish a new service", notes = "Calling this endpoint will create a secondary web service such as WMTS, TMS or WCS. The underlying data is processes on-demand, but a process graph may simply access results from a batch job. Computations should be performed in the sense that it is only evaluated for the requested spatial / temporal extent and resolution.  **Note:** Costs incurred by shared secondary web services are usually paid by the owner, but this depends on the service type and whether it supports charging fees or not.", response = Void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "Bearer")
    }, tags={ "Secondary Services Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 201, message = "The resource has been created successfully and the location of the newly created resource is advertized by the back-end.  Examples: * `POST /services` redirects to `GET /services/{service_id}` * `POST /jobs` redirects to `GET /jobs/{job_id}`", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response servicesPost(@ApiParam(value = "" ) @Valid StoreSecondaryWebServiceRequest storeSecondaryWebServiceRequest
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.servicesPost(storeSecondaryWebServiceRequest, securityContext);
    }
    @DELETE
    @Path("/{service_id}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Delete a service", notes = "Calling this endpoint will stop a given secondary web service to access result data.", response = Void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "Bearer")
    }, tags={ "Secondary Services Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 204, message = "The service has been successfully deleted.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response servicesServiceIdDelete(@ApiParam(value = "Unique secondary web service identifier.",required=true) @PathParam("service_id") String serviceId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.servicesServiceIdDelete(serviceId, securityContext);
    }
    @GET
    @Path("/{service_id}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Full metadata for a service", notes = "Requests to this endpoint will return JSON description of the secondary web service.", response = SecondaryWebServiceResponse.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "Bearer")
    }, tags={ "Secondary Services Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Details of the created service", response = SecondaryWebServiceResponse.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response servicesServiceIdGet(@ApiParam(value = "Unique secondary web service identifier.",required=true) @PathParam("service_id") String serviceId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.servicesServiceIdGet(serviceId, securityContext);
    }
    @PATCH
    @Path("/{service_id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Modify a service", notes = "Calling this endpoint will change the specified secondary web service, but maintain its identifier. Changes can be grouped in a single request. To change the service type create a new service.", response = Void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "Bearer")
    }, tags={ "Secondary Services Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 204, message = "Changes to the service were applied successfully.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response servicesServiceIdPatch(@ApiParam(value = "Unique secondary web service identifier.",required=true) @PathParam("service_id") String serviceId
,@ApiParam(value = "" ) @Valid UpdateSecondaryWebServiceRequest updateSecondaryWebServiceRequest
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.servicesServiceIdPatch(serviceId, updateSecondaryWebServiceRequest, securityContext);
    }
}
