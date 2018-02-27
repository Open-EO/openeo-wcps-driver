package eu.openeo.api;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import eu.openeo.model.Service1;
import eu.openeo.model.Service2;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public abstract class ServicesApiService {
	public abstract Response servicesOptions(SecurityContext securityContext) throws NotFoundException;

	public abstract Response servicesPost(Service1 service, SecurityContext securityContext) throws NotFoundException;

	public abstract Response servicesServiceIdDelete(String serviceId, SecurityContext securityContext)
			throws NotFoundException;

	public abstract Response servicesServiceIdGet(String serviceId, SecurityContext securityContext)
			throws NotFoundException;

	public abstract Response servicesServiceIdOptions(String serviceId, SecurityContext securityContext)
			throws NotFoundException;

	public abstract Response servicesServiceIdPatch(String serviceId, Service2 service, SecurityContext securityContext)
			throws NotFoundException;
}
