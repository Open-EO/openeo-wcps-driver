package eu.openeo.api;

import eu.openeo.api.*;
import eu.openeo.model.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import eu.openeo.model.Error;
import eu.openeo.model.InlineObject4;
import eu.openeo.model.InlineObject5;
import eu.openeo.model.InlineResponse20010;
import eu.openeo.model.InlineResponse2009;

import java.util.List;
import eu.openeo.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-02-12T13:52:55.621+01:00[Europe/Rome]")
public abstract class ServicesApiService {
    public abstract Response servicesGet(SecurityContext securityContext) throws NotFoundException;
    public abstract Response servicesPost(InlineObject4 inlineObject4,SecurityContext securityContext) throws NotFoundException;
    public abstract Response servicesServiceIdDelete(String serviceId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response servicesServiceIdGet(String serviceId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response servicesServiceIdPatch(String serviceId,InlineObject5 inlineObject5,SecurityContext securityContext) throws NotFoundException;
}
