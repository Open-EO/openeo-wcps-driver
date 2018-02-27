package eu.openeo.api;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public abstract class AuthApiService {
	public abstract Response authLoginGet(SecurityContext securityContext) throws NotFoundException;

	public abstract Response authLoginOptions(SecurityContext securityContext) throws NotFoundException;

	public abstract Response authRegisterOptions(SecurityContext securityContext) throws NotFoundException;

	public abstract Response authRegisterPost(String password, SecurityContext securityContext)
			throws NotFoundException;
}
