package eu.openeo.api;

import eu.openeo.api.*;
import eu.openeo.model.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import eu.openeo.model.Error;
import java.io.File;
import eu.openeo.model.WorkspaceFilesListResponse;

import java.util.List;
import eu.openeo.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public abstract class FilesApiService {
    public abstract Response filesUserIdGet( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String userId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response filesUserIdPathDelete( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String userId,String path,SecurityContext securityContext) throws NotFoundException;
    public abstract Response filesUserIdPathGet( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String userId,String path,SecurityContext securityContext) throws NotFoundException;
    public abstract Response filesUserIdPathPut( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String userId,String path,File body,SecurityContext securityContext) throws NotFoundException;
}
