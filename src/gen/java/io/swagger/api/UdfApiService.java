package io.swagger.api;

import io.swagger.api.*;
import io.swagger.model.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import java.util.Map;
import io.swagger.model.UdfDescription;

import java.util.List;
import io.swagger.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-16T14:36:16.100+01:00")
public abstract class UdfApiService {
    public abstract Response udfGet(SecurityContext securityContext) throws NotFoundException;
    public abstract Response udfLangUdfTypeGet(String lang,String udfType,SecurityContext securityContext) throws NotFoundException;
}
