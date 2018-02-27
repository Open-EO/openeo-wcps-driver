package eu.openeo.api.factories;

import eu.openeo.api.JobsApiService;
import eu.openeo.api.impl.JobsApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class JobsApiServiceFactory {
	private final static JobsApiService service = new JobsApiServiceImpl();

	public static JobsApiService getJobsApi() {
		return service;
	}
}
