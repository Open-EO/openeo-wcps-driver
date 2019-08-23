package eu.openeo.api;

import javax.validation.constraints.Pattern;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public abstract class DownloadApiService {
   
    public abstract Response downloadFileNameGet( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String FileName,SecurityContext securityContext) throws NotFoundException;
}
