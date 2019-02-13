package eu.openeo.api.factories;

import eu.openeo.api.ProcessesApiService;
import eu.openeo.api.impl.ProcessesApiServiceImpl;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-02-12T13:52:55.621+01:00[Europe/Rome]")
public class ProcessesApiServiceFactory {
    private final static ProcessesApiService service = new ProcessesApiServiceImpl();

    public static ProcessesApiService getProcessesApi() {
        return service;
    }
}
