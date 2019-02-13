package eu.openeo.api.impl;

import eu.openeo.api.*;
import eu.openeo.model.*;

import java.math.BigDecimal;
import eu.openeo.model.Error;

import java.util.List;
import eu.openeo.api.NotFoundException;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-02-12T13:52:55.621+01:00[Europe/Rome]")
public class SubscriptionApiServiceImpl extends SubscriptionApiService {
    @Override
    public Response subscriptionGet(String connection, String upgrade, String secWebSocketProtocol, BigDecimal secWebSocketVersion, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
}
