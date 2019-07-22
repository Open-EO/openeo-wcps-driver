package eu.openeo.api;

import eu.openeo.api.*;
import eu.openeo.model.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import java.math.BigDecimal;
import eu.openeo.model.Error;

import java.util.List;
import eu.openeo.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public abstract class SubscriptionApiService {
    public abstract Response subscriptionGet( @NotNull String connection, @NotNull String upgrade, @NotNull String secWebSocketProtocol, @NotNull BigDecimal secWebSocketVersion,SecurityContext securityContext) throws NotFoundException;
}
