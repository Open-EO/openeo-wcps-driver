package eu.openeo.api.factories;

import eu.openeo.api.ProcessGraphsApiService;
import eu.openeo.api.impl.ProcessGraphsApiServiceImpl;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class ProcessGraphsApiServiceFactory {
    private final static ProcessGraphsApiService service = new ProcessGraphsApiServiceImpl();

    public static ProcessGraphsApiService getProcessGraphsApi() {
        return service;
    }
}
