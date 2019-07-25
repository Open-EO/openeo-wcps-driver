package eu.openeo.api.impl;

import eu.openeo.api.*;
import eu.openeo.model.*;

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

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class JobsApiServiceImpl extends JobsApiService {
    @Override
    public Response jobsGet(SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
    }
    @Override
    public Response jobsJobIdDelete( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String jobId, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
    }
    @Override
    public Response jobsJobIdEstimateGet( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String jobId, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
    }
    @Override
    public Response jobsJobIdGet( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String jobId, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
    }
    @Override
    public Response jobsJobIdPatch( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String jobId, UpdateBatchJobRequest updateBatchJobRequest, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
    }
    @Override
    public Response jobsJobIdResultsDelete( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String jobId, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
    }
    @Override
    public Response jobsJobIdResultsGet( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String jobId, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
    }
    @Override
    public Response jobsJobIdResultsPost( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String jobId, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
    }
    @Override
    public Response jobsPost(StoreBatchJobRequest storeBatchJobRequest, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
    }
}
