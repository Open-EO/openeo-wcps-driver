package eu.openeo.api.factories;

import eu.openeo.api.OutputFormatsApiService;
import eu.openeo.api.impl.OutputFormatsApiServiceImpl;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-02-12T13:52:55.621+01:00[Europe/Rome]")
public class OutputFormatsApiServiceFactory {
    private final static OutputFormatsApiService service = new OutputFormatsApiServiceImpl();

    public static OutputFormatsApiService getOutputFormatsApi() {
        return service;
    }
}
