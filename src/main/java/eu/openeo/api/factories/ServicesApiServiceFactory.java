package eu.openeo.api.factories;

import eu.openeo.api.ServicesApiService;
import eu.openeo.api.impl.ServicesApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class ServicesApiServiceFactory {
	private final static ServicesApiService service = new ServicesApiServiceImpl();

	public static ServicesApiService getServicesApi() {
		return service;
	}
}
