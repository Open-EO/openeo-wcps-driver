package eu.openeo.api;

import javax.servlet.ServletConfig;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import eu.openeo.api.factories.DownloadApiServiceFactory;
import eu.openeo.model.Error;
import eu.openeo.model.WorkspaceFilesListResponse;
import io.swagger.annotations.ApiParam;

@Path("/download")

@io.swagger.annotations.Api(description = "the files API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class DownloadApi  {
   private final DownloadApiService delegate;

   public DownloadApi(@Context ServletConfig servletContext) {
	   DownloadApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("DownloadApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (DownloadApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = DownloadApiServiceFactory.getDownloadApi();
      }

      this.delegate = delegate;
   }
   
   @GET
   @Path("/{job_id}")
   
   @Produces({ "application/json" })
   @io.swagger.annotations.ApiOperation(value = "Download file from service", notes = "This service lists all user-uploaded files that are stored at the back-end.", response = WorkspaceFilesListResponse.class, authorizations = {
       @io.swagger.annotations.Authorization(value = "Bearer")
   }, tags={ "File Management", })
   @io.swagger.annotations.ApiResponses(value = { 
       @io.swagger.annotations.ApiResponse(code = 200, message = "Flattened file tree with path relative to the user's root directory and some basic properties such as the file size and the timestamp of the last modification. All properties except the name are optional. Folders MUST NOT be listed separately so each element in the list MUST be a downloadable file.", response = WorkspaceFilesListResponse.class),
       
       @io.swagger.annotations.ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
       
       @io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
   public Response downloadFileNameGet(@ApiParam(value = "Unique user identifier.",required=true) @PathParam("job_id") String fileName
,@Context SecurityContext securityContext)
   throws NotFoundException {
       return delegate.downloadFileNameGet(fileName, securityContext);
   }
}
