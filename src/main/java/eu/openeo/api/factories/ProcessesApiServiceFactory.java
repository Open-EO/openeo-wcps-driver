package eu.openeo.api.factories;

import eu.openeo.api.ProcessesApiService;
import eu.openeo.api.impl.ProcessesApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class ProcessesApiServiceFactory {
	private final static ProcessesApiService service = new ProcessesApiServiceImpl();

	public static ProcessesApiService getProcessesApi() {
		return service;
	}
}
