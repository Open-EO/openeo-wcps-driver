package eu.openeo.api.factories;

import eu.openeo.api.UdfRuntimesApiService;
import eu.openeo.api.impl.UdfRuntimesApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class UdfRuntimesApiServiceFactory {
	private final static UdfRuntimesApiService service = new UdfRuntimesApiServiceImpl();

	public static UdfRuntimesApiService getUdfRuntimesApi() {
		return service;
	}
}
