package io.swagger.api.factories;

import io.swagger.api.ProcessesApiService;
import io.swagger.api.impl.ProcessesApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-16T14:36:16.100+01:00")
public class ProcessesApiServiceFactory {
    private final static ProcessesApiService service = new ProcessesApiServiceImpl();

    public static ProcessesApiService getProcessesApi() {
        return service;
    }
}
