package eu.openeo.api.factories;

import eu.openeo.api.ProcessesApiService;
import eu.openeo.api.impl.ProcessesApiServiceImpl;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class ProcessesApiServiceFactory {
    private final static ProcessesApiService service = new ProcessesApiServiceImpl();

    public static ProcessesApiService getProcessesApi() {
        return service;
    }
}
