package eu.openeo.api;

import eu.openeo.model.*;
import eu.openeo.api.ValidationApiService;
import eu.openeo.api.factories.ValidationApiServiceFactory;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import eu.openeo.model.Error;
import eu.openeo.model.ValidationRequest;
import eu.openeo.model.ValidationResponse;

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

@Path("/validation")


@io.swagger.annotations.Api(description = "the validation API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class ValidationApi  {
   private final ValidationApiService delegate;

   public ValidationApi(@Context ServletConfig servletContext) {
      ValidationApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("ValidationApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (ValidationApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = ValidationApiServiceFactory.getValidationApi();
      }

      this.delegate = delegate;
   }

    @POST
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Validate a process graph", notes = "Validates a process graph without executing it. A process graph is considered valid unless the `errors` array in the response contains at least one error.  Checks whether the process graph is schematically correct and the processes are supported by the back-end. It MUST also checks the parameter values against the schema, but checking whether the values are adequate in the context of data is OPTIONAL. For example, a non-existing band name may may get rejected only by a few back-ends.  Errors that usually occure during processing MAY NOT get reported, e.g. if a referenced file is accessible at the time of execution.  Back-ends can either report all errors at once or stop the validation once they found the first error.   Please note that a validation always returns with HTTP status code 200. Error codes in the 4xx and 5xx ranges MUST be returned only when the general validation request is invalid (e.g. server is busy or no process graph parameter specified), but never if the process graph validation found an error (e.g. an unsupported process).", response = ValidationResponse.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "Bearer")
    }, tags={ "Process Graph Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Returns the validation result as a list of errors. An empty list indicates a successful validation.", response = ValidationResponse.class),
        
        @io.swagger.annotations.ApiResponse(code = 4XX, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 5XX, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response validationPost(@ApiParam(value = "" ) @Valid ValidationRequest validationRequest
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.validationPost(validationRequest, securityContext);
    }
}
