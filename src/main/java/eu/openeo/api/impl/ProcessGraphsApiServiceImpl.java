package eu.openeo.api.impl;

import eu.openeo.api.*;
import eu.openeo.model.*;

import eu.openeo.model.Error;
import eu.openeo.model.InlineObject2;
import eu.openeo.model.InlineObject3;
import eu.openeo.model.InlineResponse2007;
import eu.openeo.model.InlineResponse2008;

import java.util.List;
import eu.openeo.api.NotFoundException;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-02-12T13:52:55.621+01:00[Europe/Rome]")
public class ProcessGraphsApiServiceImpl extends ProcessGraphsApiService {
    @Override
    public Response processGraphsGet(SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response processGraphsPost(InlineObject2 inlineObject2, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response processGraphsProcessGraphIdDelete(String processGraphId, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response processGraphsProcessGraphIdGet(String processGraphId, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response processGraphsProcessGraphIdPatch(String processGraphId, InlineObject3 inlineObject3, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
}
