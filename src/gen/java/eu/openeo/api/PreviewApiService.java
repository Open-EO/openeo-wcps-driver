package eu.openeo.api;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import eu.openeo.model.JobFull;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public abstract class PreviewApiService {

	public abstract Response previewOptions(SecurityContext securityContext) throws NotFoundException;

	public abstract Response previewPost(JobFull job, SecurityContext securityContext) throws NotFoundException;
}
