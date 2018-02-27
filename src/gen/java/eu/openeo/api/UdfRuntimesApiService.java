package eu.openeo.api;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public abstract class UdfRuntimesApiService {
	public abstract Response udfRuntimesGet(SecurityContext securityContext) throws NotFoundException;

	public abstract Response udfRuntimesLangUdfTypeGet(String lang, String udfType, SecurityContext securityContext)
			throws NotFoundException;

	public abstract Response udfRuntimesLangUdfTypeOptions(String lang, String udfType, SecurityContext securityContext)
			throws NotFoundException;

	public abstract Response udfRuntimesOptions(SecurityContext securityContext) throws NotFoundException;
}
