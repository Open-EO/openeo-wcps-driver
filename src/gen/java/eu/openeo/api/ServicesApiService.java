package eu.openeo.api;

import eu.openeo.api.*;
import eu.openeo.model.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import eu.openeo.model.Error;
import eu.openeo.model.SecondaryWebServiceResponse;
import eu.openeo.model.SecondaryWebServicesListResponse;
import eu.openeo.model.StoreSecondaryWebServiceRequest;
import eu.openeo.model.UpdateSecondaryWebServiceRequest;

import java.util.List;
import eu.openeo.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public abstract class ServicesApiService {
    public abstract Response servicesGet(SecurityContext securityContext) throws NotFoundException;
    public abstract Response servicesPost(StoreSecondaryWebServiceRequest storeSecondaryWebServiceRequest,SecurityContext securityContext) throws NotFoundException;
    public abstract Response servicesServiceIdDelete( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String serviceId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response servicesServiceIdGet( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String serviceId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response servicesServiceIdPatch( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String serviceId,UpdateSecondaryWebServiceRequest updateSecondaryWebServiceRequest,SecurityContext securityContext) throws NotFoundException;
}
