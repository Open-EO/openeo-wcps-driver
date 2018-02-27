package eu.openeo.api.factories;

import eu.openeo.api.UsersApiService;
import eu.openeo.api.impl.UsersApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class UsersApiServiceFactory {
	private final static UsersApiService service = new UsersApiServiceImpl();

	public static UsersApiService getUsersApi() {
		return service;
	}
}
