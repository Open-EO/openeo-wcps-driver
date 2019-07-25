package eu.openeo.api.impl;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import eu.openeo.api.NotFoundException;
import eu.openeo.api.OutputFormatsApiService;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class OutputFormatsApiServiceImpl extends OutputFormatsApiService {
    @Override
    public Response outputFormatsGet(SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
    }
}
