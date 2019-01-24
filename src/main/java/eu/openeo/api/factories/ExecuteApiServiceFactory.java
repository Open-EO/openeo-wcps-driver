package eu.openeo.api.factories;

import eu.openeo.api.PreviewApiService;
import eu.openeo.api.impl.PreviewApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class ExecuteApiServiceFactory {
	private final static PreviewApiService service = new PreviewApiServiceImpl();

	public static PreviewApiService getExecuteApi() {
		return service;
	}
}
