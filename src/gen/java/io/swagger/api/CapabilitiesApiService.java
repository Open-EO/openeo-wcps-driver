package io.swagger.api;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-23T11:10:18.550+01:00")
public abstract class CapabilitiesApiService {
    public abstract Response capabilitiesGet(SecurityContext securityContext) throws NotFoundException;
}
