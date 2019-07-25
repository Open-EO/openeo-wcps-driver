package eu.openeo.api.impl;

import eu.openeo.api.*;
import eu.openeo.model.*;

import eu.openeo.model.Error;
import eu.openeo.model.WellKnownDiscoveryResponse;

import java.util.List;
import eu.openeo.api.NotFoundException;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class WellKnownApiServiceImpl extends WellKnownApiService {
    @Override
    public Response wellKnownOpeneoGet(SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
    }
}
