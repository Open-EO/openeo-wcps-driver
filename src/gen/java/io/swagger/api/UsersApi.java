package io.swagger.api;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;

import javax.servlet.ServletConfig;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import io.swagger.annotations.ApiParam;
import io.swagger.api.factories.UsersApiServiceFactory;
import io.swagger.model.InlineResponse2003;
import io.swagger.model.Job;

@Path("/users")
@Consumes({ "application/json" })
@Produces({ "application/json" })
@io.swagger.annotations.Api(description = "the users API")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-23T11:10:18.550+01:00")
public class UsersApi  {
   private final UsersApiService delegate;

   public UsersApi(@Context ServletConfig servletContext) {
      UsersApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("UsersApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (UsersApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = UsersApiServiceFactory.getUsersApi();
      }

      this.delegate = delegate;
   }

    @GET
    @Path("/{user_id}/credits")
    @Consumes({ "application/json" })
    @Produces({ "text/plain; charset=utf-8" })
    @io.swagger.annotations.ApiOperation(value = "Returns available user credits", notes = "For back-ends that involve accounting, this service will return the currently available credits. Other back-ends may simply return `Infinity`.", response = BigDecimal.class, tags={ "User Content", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Available credits", response = BigDecimal.class),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "User with specified identifier is not available.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
    public Response usersUserIdCreditsGet(@ApiParam(value = "user identifier, the special value `me` automatically refers to the own ID based on the provided HTTP `Authorization` header.",required=true) @PathParam("user_id") String userId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.usersUserIdCreditsGet(userId,securityContext);
    }
    @GET
    @Path("/{user_id}/files")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "List user-uploaded files", notes = "This service lists all user-uploaded files that are stored at the back-end.", response = InlineResponse2003.class, responseContainer = "List", tags={ "User Content", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Flattened file tree with path relative to the user's root directory and some basic properties such as the file size and the timestamp of the last modification. All properties except the name are optional.", response = InlineResponse2003.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "User with specified user id is not available.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
    public Response usersUserIdFilesGet(@ApiParam(value = "user identifier, the special value `me` automatically refers to the own ID based on the provided HTTP `Authorization` header.",required=true) @PathParam("user_id") String userId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.usersUserIdFilesGet(userId,securityContext);
    }
    @DELETE
    @Path("/{user_id}/files/{path}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Deletes a user file", notes = "This service deletes an existing user-uploaded file specified by its path.", response = Void.class, tags={ "User Content", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "The file has been successfully deleted at the back-end.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "File or user with specified identifier is not available.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 423, message = "The file that is being accessed is locked.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
    public Response usersUserIdFilesPathDelete(@ApiParam(value = "user identifier, the special value `me` automatically refers to the own ID based on the provided HTTP `Authorization` header.",required=true) @PathParam("user_id") String userId
,@ApiParam(value = "path relative to the user's root directory, must be URL encoded",required=true) @PathParam("path") String path
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.usersUserIdFilesPathDelete(userId,path,securityContext);
    }
    @GET
    @Path("/{user_id}/files/{path}")
    @Consumes({ "application/json" })
    @Produces({ "application/octet-stream" })
    @io.swagger.annotations.ApiOperation(value = "Download a user file", notes = "This service downloads a user files identified by its path relative to the user's root directory.", response = File.class, tags={ "User Content", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "file from user storage", response = File.class),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "File or user with specified identifier is not available", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
    public Response usersUserIdFilesPathGet(@ApiParam(value = "user identifier, the special value `me` automatically refers to the own ID based on the provided HTTP `Authorization` header.",required=true) @PathParam("user_id") String userId
,@ApiParam(value = "path relative to the user's root directory, must be URL encoded",required=true) @PathParam("path") String path
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.usersUserIdFilesPathGet(userId,path,securityContext);
    }
    @PUT
    @Path("/{user_id}/files/{path}")
    @Consumes({ "multipart/form-data" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Upload a new or update an existing user file", notes = "This service uploads a new or updates an existing file at a given path.", response = Void.class, tags={ "User Content", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "The file upload has been successful.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "User with specified identifier is not available.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 422, message = "File is rejected.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 423, message = "The file that is being accessed is locked.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 507, message = "User exceeded his storage limit.", response = Void.class) })
    public Response usersUserIdFilesPathPut(@ApiParam(value = "user identifier, the special value `me` automatically refers to the own ID based on the provided HTTP `Authorization` header.",required=true) @PathParam("user_id") String userId
,@ApiParam(value = "path relative to the user's root directory, must be URL encoded",required=true) @PathParam("path") String path
,
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.usersUserIdFilesPathPut(userId,path,fileInputStream, fileDetail,securityContext);
    }
    @GET
    @Path("/{user_id}/jobs")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "List all jobs that have been submitted by the user", notes = "Requests to this service will list all jobs submitted by a user with given id.", response = Job.class, responseContainer = "List", tags={ "Job Management","User Content", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Array of job descriptions", response = Job.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "The back-end requires clients to authenticate in order to process this request.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 403, message = "Authorization failed, access to the requested resource has been denied.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "User with specified identifier is not available.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 501, message = "This API feature is not supported by the back-end.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 503, message = "The service is currently unavailable.", response = Void.class) })
    public Response usersUserIdJobsGet(@ApiParam(value = "user identifier, the special value `me` automatically refers to the own ID based on the provided HTTP `Authorization` header.",required=true) @PathParam("user_id") String userId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.usersUserIdJobsGet(userId,securityContext);
    }
}
