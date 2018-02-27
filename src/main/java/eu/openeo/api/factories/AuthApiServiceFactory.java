package eu.openeo.api.factories;

import eu.openeo.api.AuthApiService;
import eu.openeo.api.impl.AuthApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class AuthApiServiceFactory {
	private final static AuthApiService service = new AuthApiServiceImpl();

	public static AuthApiService getAuthApi() {
		return service;
	}
}
