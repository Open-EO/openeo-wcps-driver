package eu.openeo.api;

import eu.openeo.api.*;
import eu.openeo.model.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import eu.openeo.model.BatchJobEstimateResponse;
import eu.openeo.model.BatchJobListResponse;
import eu.openeo.model.BatchJobResponse;
import eu.openeo.model.BatchJobResultsResponse;
import eu.openeo.model.Error;
import eu.openeo.model.JobError;
import eu.openeo.model.StoreBatchJobRequest;
import eu.openeo.model.UpdateBatchJobRequest;

import java.util.List;
import eu.openeo.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public abstract class JobsApiService {
    public abstract Response jobsGet(SecurityContext securityContext) throws NotFoundException;
    public abstract Response jobsJobIdDelete( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String jobId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response jobsJobIdEstimateGet( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String jobId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response jobsJobIdGet( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String jobId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response jobsJobIdPatch( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String jobId,UpdateBatchJobRequest updateBatchJobRequest,SecurityContext securityContext) throws NotFoundException;
    public abstract Response jobsJobIdResultsDelete( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String jobId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response jobsJobIdResultsGet( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String jobId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response jobsJobIdResultsPost( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String jobId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response jobsPost(StoreBatchJobRequest storeBatchJobRequest,SecurityContext securityContext) throws NotFoundException;
}
