package eu.openeo.api.impl;

import eu.openeo.api.*;
import eu.openeo.model.*;

import eu.openeo.model.Error;
import eu.openeo.model.StoreProcessGraphRequest;
import eu.openeo.model.StoredProcessGraphListResponse;
import eu.openeo.model.StoredProcessGraphResponse;
import eu.openeo.model.UpdateStoredProcessGraphRequest;

import java.util.List;
import eu.openeo.api.NotFoundException;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class ProcessGraphsApiServiceImpl extends ProcessGraphsApiService {
    @Override
    public Response processGraphsGet(SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response processGraphsPost(StoreProcessGraphRequest storeProcessGraphRequest, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response processGraphsProcessGraphIdDelete( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String processGraphId, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response processGraphsProcessGraphIdGet( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String processGraphId, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response processGraphsProcessGraphIdPatch( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String processGraphId, UpdateStoredProcessGraphRequest updateStoredProcessGraphRequest, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
}
