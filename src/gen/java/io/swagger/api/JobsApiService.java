package io.swagger.api;

import io.swagger.api.*;
import io.swagger.model.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import java.math.BigDecimal;
import io.swagger.model.InlineResponse2003;
import io.swagger.model.Job;
import io.swagger.model.ProcessGraph;

import java.util.List;
import io.swagger.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-16T14:36:16.100+01:00")
public abstract class JobsApiService {
    public abstract Response jobsJobIdCancelGet(String jobId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response jobsJobIdDelete(String jobId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response jobsJobIdGet(String jobId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response jobsJobIdSubscribeGet(String jobId,String upgrade,String connection,String secWebSocketKey,String secWebSocketProtocol,BigDecimal secWebSocketVersion,SecurityContext securityContext) throws NotFoundException;
    public abstract Response jobsPost( String evaluate,ProcessGraph processGraph,SecurityContext securityContext) throws NotFoundException;
}
