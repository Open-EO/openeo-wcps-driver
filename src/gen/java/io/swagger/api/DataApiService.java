package io.swagger.api;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-23T11:10:18.550+01:00")
public abstract class DataApiService {
    public abstract Response dataGet( String qname, String qgeom, String qstartdate, String qenddate,SecurityContext securityContext) throws NotFoundException;
    public abstract Response dataOpensearchGet( String q, Integer start, Integer rows,SecurityContext securityContext) throws NotFoundException;
    public abstract Response dataProductIdGet(String productId,SecurityContext securityContext) throws NotFoundException;
}
