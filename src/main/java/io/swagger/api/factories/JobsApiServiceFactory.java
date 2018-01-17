package io.swagger.api.factories;

import io.swagger.api.JobsApiService;
import io.swagger.api.impl.JobsApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-16T14:36:16.100+01:00")
public class JobsApiServiceFactory {
    private final static JobsApiService service = new JobsApiServiceImpl();

    public static JobsApiService getJobsApi() {
        return service;
    }
}
