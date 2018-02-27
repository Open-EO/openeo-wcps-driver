package eu.openeo.api.factories;

import eu.openeo.api.CapabilitiesApiService;
import eu.openeo.api.impl.CapabilitiesApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class CapabilitiesApiServiceFactory {
	private final static CapabilitiesApiService service = new CapabilitiesApiServiceImpl();

	public static CapabilitiesApiService getCapabilitiesApi() {
		return service;
	}
}
