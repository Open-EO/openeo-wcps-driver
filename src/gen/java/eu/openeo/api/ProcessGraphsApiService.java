package eu.openeo.api;

import eu.openeo.api.*;
import eu.openeo.model.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import eu.openeo.model.Error;
import eu.openeo.model.InlineObject2;
import eu.openeo.model.InlineObject3;
import eu.openeo.model.InlineResponse2007;
import eu.openeo.model.InlineResponse2008;

import java.util.List;
import eu.openeo.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-02-12T13:52:55.621+01:00[Europe/Rome]")
public abstract class ProcessGraphsApiService {
    public abstract Response processGraphsGet(SecurityContext securityContext) throws NotFoundException;
    public abstract Response processGraphsPost(InlineObject2 inlineObject2,SecurityContext securityContext) throws NotFoundException;
    public abstract Response processGraphsProcessGraphIdDelete(String processGraphId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response processGraphsProcessGraphIdGet(String processGraphId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response processGraphsProcessGraphIdPatch(String processGraphId,InlineObject3 inlineObject3,SecurityContext securityContext) throws NotFoundException;
}
