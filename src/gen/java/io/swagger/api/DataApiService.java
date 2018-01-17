package io.swagger.api;

import io.swagger.api.*;
import io.swagger.model.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import io.swagger.model.InlineResponse200;
import io.swagger.model.InlineResponse2001;

import java.util.List;
import io.swagger.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-16T14:36:16.100+01:00")
public abstract class DataApiService {
    public abstract Response dataGet( String qname, String qgeom, String qstartdate, String qenddate,SecurityContext securityContext) throws NotFoundException;
    public abstract Response dataOpensearchGet( String q, Integer start, Integer rows,SecurityContext securityContext) throws NotFoundException;
    public abstract Response dataProductIdGet(String productId,SecurityContext securityContext) throws NotFoundException;
}
