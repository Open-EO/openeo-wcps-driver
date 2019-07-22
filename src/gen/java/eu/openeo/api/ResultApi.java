package eu.openeo.api;

import eu.openeo.model.*;
import eu.openeo.api.ResultApiService;
import eu.openeo.api.factories.ResultApiServiceFactory;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import eu.openeo.model.Error;
import eu.openeo.model.SynchronousResultRequest;

import java.util.Map;
import java.util.List;
import eu.openeo.api.NotFoundException;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.ServletConfig;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.*;
import javax.validation.constraints.*;
import javax.validation.Valid;

@Path("/result")


@io.swagger.annotations.Api(description = "the result API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class ResultApi  {
   private final ResultApiService delegate;

   public ResultApi(@Context ServletConfig servletContext) {
      ResultApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("ResultApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (ResultApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = ResultApiServiceFactory.getResultApi();
      }

      this.delegate = delegate;
   }

    @POST
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Process and download data synchronously", notes = "Process graphs will be executed directly and the result will be downloaded in the format specified in the process graph. To specify the format use a process such as `save_result`. Requests might time-out after a certain amount of time by sending openEO error `RequestTimeout`. A header named `OpenEO-Costs` MAY be sent with all responses, which MUST include the costs for processing and downloading the data. This endpoint can be used to generate small previews or test process graphs before starting a batch job.", response = Void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "Bearer")
    }, tags={ "Process Graph Management","Batch Job Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Result data in the requested output format", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 4XX, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 5XX, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response resultPost(@ApiParam(value = "" ,required=true) @NotNull @Valid SynchronousResultRequest synchronousResultRequest
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.resultPost(synchronousResultRequest, securityContext);
    }
}
