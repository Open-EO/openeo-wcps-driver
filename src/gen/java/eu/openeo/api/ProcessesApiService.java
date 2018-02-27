package eu.openeo.api;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public abstract class ProcessesApiService {
	public abstract Response processesGet(String qname, SecurityContext securityContext) throws NotFoundException;

	public abstract Response processesOpensearchGet(String q, Integer start, Integer rows,
			SecurityContext securityContext) throws NotFoundException;

	public abstract Response processesOpensearchOptions(SecurityContext securityContext) throws NotFoundException;

	public abstract Response processesOptions(SecurityContext securityContext) throws NotFoundException;

	public abstract Response processesProcessIdGet(String processId, SecurityContext securityContext)
			throws NotFoundException;

	public abstract Response processesProcessIdOptions(String processId, SecurityContext securityContext)
			throws NotFoundException;
}
