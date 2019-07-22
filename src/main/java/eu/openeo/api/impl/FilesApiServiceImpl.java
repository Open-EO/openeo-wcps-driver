package eu.openeo.api.impl;

import eu.openeo.api.*;
import eu.openeo.model.*;

import eu.openeo.model.Error;
import java.io.File;
import eu.openeo.model.WorkspaceFilesListResponse;

import java.util.List;
import eu.openeo.api.NotFoundException;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class FilesApiServiceImpl extends FilesApiService {
    @Override
    public Response filesUserIdGet( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String userId, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response filesUserIdPathDelete( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String userId, String path, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response filesUserIdPathGet( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String userId, String path, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response filesUserIdPathPut( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String userId, String path, File body, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
}
