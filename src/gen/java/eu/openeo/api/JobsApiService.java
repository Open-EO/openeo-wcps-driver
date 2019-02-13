package eu.openeo.api;

import eu.openeo.api.*;
import eu.openeo.model.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import eu.openeo.model.Error;
import eu.openeo.model.InlineObject6;
import eu.openeo.model.InlineObject7;
import eu.openeo.model.InlineResponse20011;
import eu.openeo.model.InlineResponse20012;
import eu.openeo.model.InlineResponse20013;
import eu.openeo.model.InlineResponse20014;

import java.util.List;
import eu.openeo.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-02-12T13:52:55.621+01:00[Europe/Rome]")
public abstract class JobsApiService {
    public abstract Response jobsGet(SecurityContext securityContext) throws NotFoundException;
    public abstract Response jobsJobIdDelete(String jobId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response jobsJobIdEstimateGet(String jobId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response jobsJobIdGet(String jobId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response jobsJobIdPatch(String jobId,InlineObject7 inlineObject7,SecurityContext securityContext) throws NotFoundException;
    public abstract Response jobsJobIdResultsDelete(String jobId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response jobsJobIdResultsGet(String jobId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response jobsJobIdResultsPost(String jobId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response jobsPost(InlineObject6 inlineObject6,SecurityContext securityContext) throws NotFoundException;
}
