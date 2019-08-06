package eu.openeo.api;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import eu.openeo.model.SynchronousResultRequest;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public abstract class ResultApiService {
    public abstract Response resultPost(SynchronousResultRequest synchronousResultRequest,SecurityContext securityContext) throws NotFoundException;
}
