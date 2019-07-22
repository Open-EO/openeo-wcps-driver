package eu.openeo.api.factories;

import eu.openeo.api.JobsApiService;
import eu.openeo.api.impl.JobsApiServiceImpl;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class JobsApiServiceFactory {
    private final static JobsApiService service = new JobsApiServiceImpl();

    public static JobsApiService getJobsApi() {
        return service;
    }
}
