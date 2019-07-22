package eu.openeo.api;

import eu.openeo.model.*;
import eu.openeo.api.FilesApiService;
import eu.openeo.api.factories.FilesApiServiceFactory;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import eu.openeo.model.Error;
import java.io.File;
import eu.openeo.model.WorkspaceFilesListResponse;

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

@Path("/files")


@io.swagger.annotations.Api(description = "the files API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class FilesApi  {
   private final FilesApiService delegate;

   public FilesApi(@Context ServletConfig servletContext) {
      FilesApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("FilesApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (FilesApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = FilesApiServiceFactory.getFilesApi();
      }

      this.delegate = delegate;
   }

    @GET
    @Path("/{user_id}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "List all files in the workspace", notes = "This service lists all user-uploaded files that are stored at the back-end.", response = WorkspaceFilesListResponse.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "Bearer")
    }, tags={ "File Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Flattened file tree with path relative to the user's root directory and some basic properties such as the file size and the timestamp of the last modification. All properties except the name are optional. Folders MUST NOT be listed separately so each element in the list MUST be a downloadable file.", response = WorkspaceFilesListResponse.class),
        
        @io.swagger.annotations.ApiResponse(code = 4XX, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 5XX, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response filesUserIdGet(@ApiParam(value = "Unique user identifier.",required=true) @PathParam("user_id") String userId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.filesUserIdGet(userId, securityContext);
    }
    @DELETE
    @Path("/{user_id}/{path}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Delete a file from the workspace", notes = "This service deletes an existing user-uploaded file specified by its path. Resulting empty folders MUST be deleted automatically.  Back-ends MAY support deleting folders including its files and subfolders.  If not supported by the back-end a `FileOperationUnsupported` error MUST be sent as response.", response = Void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "Bearer")
    }, tags={ "File Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 204, message = "The file has been successfully deleted at the back-end.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 4XX, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 5XX, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response filesUserIdPathDelete(@ApiParam(value = "Unique user identifier.",required=true) @PathParam("user_id") String userId
,@ApiParam(value = "Path of the file, relative to the user's root directory.    **Note:** Folder and file names in the path MUST be url-encoded. The path separator `/` and the file extension separator `.` MUST NOT be url-encoded. This may be shown incorrectly in rendered versions due to [OpenAPI 3 not supporting path parameters which contain slashes](https://github.com/OAI/OpenAPI-Specification/issues/892).",required=true) @PathParam("path") String path
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.filesUserIdPathDelete(userId, path, securityContext);
    }
    @GET
    @Path("/{user_id}/{path}")
    
    @Produces({ "application/octet-stream", "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Download a file from the workspace", notes = "This service downloads a user files identified by its path relative to the user's root directory. If a folder is specified as path a `FileOperationUnsupported` error MUST be sent as response.", response = File.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "Bearer")
    }, tags={ "File Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "A file from the workspace.", response = File.class),
        
        @io.swagger.annotations.ApiResponse(code = 4XX, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 5XX, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response filesUserIdPathGet(@ApiParam(value = "Unique user identifier.",required=true) @PathParam("user_id") String userId
,@ApiParam(value = "Path of the file, relative to the user's root directory.    **Note:** Folder and file names in the path MUST be url-encoded. The path separator `/` and the file extension separator `.` MUST NOT be url-encoded. This may be shown incorrectly in rendered versions due to [OpenAPI 3 not supporting path parameters which contain slashes](https://github.com/OAI/OpenAPI-Specification/issues/892).",required=true) @PathParam("path") String path
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.filesUserIdPathGet(userId, path, securityContext);
    }
    @PUT
    @Path("/{user_id}/{path}")
    @Consumes({ "application/octet-stream" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Upload a file to the workspace", notes = "This service uploads a new or updates an existing file at a given path.  Folders are created once required by a file upload. Empty folders can't be created.", response = File.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "Bearer")
    }, tags={ "File Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "The file has been uploaded successfully.", response = File.class),
        
        @io.swagger.annotations.ApiResponse(code = 4XX, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 5XX, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response filesUserIdPathPut(@ApiParam(value = "Unique user identifier.",required=true) @PathParam("user_id") String userId
,@ApiParam(value = "Path of the file, relative to the user's root directory.    **Note:** Folder and file names in the path MUST be url-encoded. The path separator `/` and the file extension separator `.` MUST NOT be url-encoded. This may be shown incorrectly in rendered versions due to [OpenAPI 3 not supporting path parameters which contain slashes](https://github.com/OAI/OpenAPI-Specification/issues/892).",required=true) @PathParam("path") String path
,@ApiParam(value = "" ) File body
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.filesUserIdPathPut(userId, path, body, securityContext);
    }
}
