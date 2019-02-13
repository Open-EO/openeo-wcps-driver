package eu.openeo.api;

import eu.openeo.api.*;
import eu.openeo.model.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import eu.openeo.model.Error;
import java.io.File;
import eu.openeo.model.InlineResponse20015;

import java.util.List;
import eu.openeo.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-02-12T13:52:55.621+01:00[Europe/Rome]")
public abstract class FilesApiService {
    public abstract Response filesUserIdGet(String userId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response filesUserIdPathDelete(String userId,String path,SecurityContext securityContext) throws NotFoundException;
    public abstract Response filesUserIdPathGet(String userId,String path,SecurityContext securityContext) throws NotFoundException;
    public abstract Response filesUserIdPathPut(String userId,String path,File body,SecurityContext securityContext) throws NotFoundException;
}
