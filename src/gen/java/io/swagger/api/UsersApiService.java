package io.swagger.api;

import io.swagger.api.*;
import io.swagger.model.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import java.math.BigDecimal;
import java.io.File;
import io.swagger.model.InlineResponse2004;
import io.swagger.model.Job;

import java.util.List;
import io.swagger.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-16T14:36:16.100+01:00")
public abstract class UsersApiService {
    public abstract Response usersUserIdCreditsGet(String userId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response usersUserIdFilesGet(String userId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response usersUserIdFilesPathDelete(String userId,String path,SecurityContext securityContext) throws NotFoundException;
    public abstract Response usersUserIdFilesPathGet(String userId,String path,SecurityContext securityContext) throws NotFoundException;
    public abstract Response usersUserIdFilesPathPut(String userId,String path,InputStream fileInputStream, FormDataContentDisposition fileDetail,SecurityContext securityContext) throws NotFoundException;
    public abstract Response usersUserIdJobsGet(String userId,SecurityContext securityContext) throws NotFoundException;
}
