package io.swagger.api;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-23T11:10:18.550+01:00")
public abstract class DownloadApiService {
    public abstract Response downloadFormatJobIdGet(String format,String jobId,SecurityContext securityContext) throws NotFoundException;
}
