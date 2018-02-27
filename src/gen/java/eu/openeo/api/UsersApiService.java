package eu.openeo.api;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import eu.openeo.model.ProcessGraph;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public abstract class UsersApiService {
	public abstract Response usersUserIdCreditsGet(String userId, SecurityContext securityContext)
			throws NotFoundException;

	public abstract Response usersUserIdCreditsOptions(String userId, SecurityContext securityContext)
			throws NotFoundException;

	public abstract Response usersUserIdFilesGet(String userId, SecurityContext securityContext)
			throws NotFoundException;

	public abstract Response usersUserIdFilesOptions(String userId, SecurityContext securityContext)
			throws NotFoundException;

	public abstract Response usersUserIdFilesPathDelete(String userId, String path, SecurityContext securityContext)
			throws NotFoundException;

	public abstract Response usersUserIdFilesPathGet(String userId, String path, SecurityContext securityContext)
			throws NotFoundException;

	public abstract Response usersUserIdFilesPathOptions(String userId, String path, SecurityContext securityContext)
			throws NotFoundException;

	public abstract Response usersUserIdFilesPathPut(String userId, String path, InputStream fileInputStream,
			FormDataContentDisposition fileDetail, SecurityContext securityContext) throws NotFoundException;

	public abstract Response usersUserIdJobsGet(String userId, SecurityContext securityContext)
			throws NotFoundException;

	public abstract Response usersUserIdJobsOptions(String userId, SecurityContext securityContext)
			throws NotFoundException;

	public abstract Response usersUserIdProcessGraphsGet(String userId, SecurityContext securityContext)
			throws NotFoundException;

	public abstract Response usersUserIdProcessGraphsOptions(String userId, SecurityContext securityContext)
			throws NotFoundException;

	public abstract Response usersUserIdProcessGraphsPost(String userId, ProcessGraph processGraph,
			SecurityContext securityContext) throws NotFoundException;

	public abstract Response usersUserIdProcessGraphsProcessGraphIdDelete(String userId, String processGraphId,
			SecurityContext securityContext) throws NotFoundException;

	public abstract Response usersUserIdProcessGraphsProcessGraphIdGet(String userId, String processGraphId,
			SecurityContext securityContext) throws NotFoundException;

	public abstract Response usersUserIdProcessGraphsProcessGraphIdOptions(String userId, String processGraphId,
			SecurityContext securityContext) throws NotFoundException;

	public abstract Response usersUserIdProcessGraphsProcessGraphIdPut(String userId, String processGraphId,
			ProcessGraph processGraph, SecurityContext securityContext) throws NotFoundException;

	public abstract Response usersUserIdServicesGet(String userId, SecurityContext securityContext)
			throws NotFoundException;

	public abstract Response usersUserIdServicesOptions(String userId, SecurityContext securityContext)
			throws NotFoundException;
}
