package io.swagger.api;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-23T11:10:18.550+01:00")
public abstract class UsersApiService {
    public abstract Response usersUserIdCreditsGet(String userId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response usersUserIdFilesGet(String userId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response usersUserIdFilesPathDelete(String userId,String path,SecurityContext securityContext) throws NotFoundException;
    public abstract Response usersUserIdFilesPathGet(String userId,String path,SecurityContext securityContext) throws NotFoundException;
    public abstract Response usersUserIdFilesPathPut(String userId,String path,InputStream fileInputStream, FormDataContentDisposition fileDetail,SecurityContext securityContext) throws NotFoundException;
    public abstract Response usersUserIdJobsGet(String userId,SecurityContext securityContext) throws NotFoundException;
}
