package eu.openeo.api;

import java.math.BigDecimal;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import eu.openeo.model.JobFull;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public abstract class JobsApiService {
	
	public abstract Response jobsJobIdCancelOptions(String jobId, SecurityContext securityContext)
			throws NotFoundException;

	public abstract Response jobsJobIdCancelPatch(String jobId, SecurityContext securityContext)
			throws NotFoundException;

	public abstract Response jobsJobIdDownloadGet(String jobId, String format, SecurityContext securityContext)
			throws NotFoundException;

	public abstract Response jobsJobIdDownloadOptions(String jobId, String format, SecurityContext securityContext)
			throws NotFoundException;

	public abstract Response jobsJobIdGet(String jobId, SecurityContext securityContext) throws NotFoundException;

	public abstract Response jobsJobIdOptions(String jobId, SecurityContext securityContext) throws NotFoundException;

	public abstract Response jobsJobIdPatch(String jobId, JobFull job, SecurityContext securityContext)
			throws NotFoundException;

	public abstract Response jobsJobIdPauseOptions(String jobId, SecurityContext securityContext)
			throws NotFoundException;

	public abstract Response jobsJobIdPausePatch(String jobId, SecurityContext securityContext)
			throws NotFoundException;

	public abstract Response jobsJobIdQueueOptions(String jobId, SecurityContext securityContext)
			throws NotFoundException;

	public abstract Response jobsJobIdQueuePatch(String jobId, SecurityContext securityContext)
			throws NotFoundException;

	public abstract Response jobsJobIdSubscribeGet(String jobId, String upgrade, String connection,
			String secWebSocketKey, String secWebSocketProtocol, BigDecimal secWebSocketVersion,
			SecurityContext securityContext) throws NotFoundException;

	public abstract Response jobsJobIdSubscribeOptions(String jobId, SecurityContext securityContext)
			throws NotFoundException;

	public abstract Response jobsOptions(SecurityContext securityContext) throws NotFoundException;

	public abstract Response jobsPost(JobFull job, SecurityContext securityContext) throws NotFoundException;
}
