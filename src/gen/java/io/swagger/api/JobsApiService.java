package io.swagger.api;

import java.math.BigDecimal;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-23T11:10:18.550+01:00")
public abstract class JobsApiService {
    public abstract Response jobsJobIdCancelGet(String jobId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response jobsJobIdDelete(String jobId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response jobsJobIdGet(String jobId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response jobsJobIdSubscribeGet(String jobId,String upgrade,String connection,String secWebSocketKey,String secWebSocketProtocol,BigDecimal secWebSocketVersion,SecurityContext securityContext) throws NotFoundException;
    public abstract Response jobsPost( String evaluate,String processGraph, String format,SecurityContext securityContext) throws NotFoundException;
}
