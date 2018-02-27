package eu.openeo.api;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public abstract class CapabilitiesApiService {
	public abstract Response capabilitiesGet(SecurityContext securityContext) throws NotFoundException;

	public abstract Response capabilitiesOutputFormatsGet(SecurityContext securityContext) throws NotFoundException;

	public abstract Response capabilitiesOutputFormatsOptions(SecurityContext securityContext) throws NotFoundException;

	public abstract Response capabilitiesServicesGet(SecurityContext securityContext) throws NotFoundException;

	public abstract Response capabilitiesServicesOptions(SecurityContext securityContext) throws NotFoundException;
}
