package eu.openeo.api.factories;

import eu.openeo.api.ExecuteApiService;
import eu.openeo.api.impl.ExecuteApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class ExecuteApiServiceFactory {
	private final static ExecuteApiService service = new ExecuteApiServiceImpl();

	public static ExecuteApiService getExecuteApi() {
		return service;
	}
}
