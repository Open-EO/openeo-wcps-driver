package eu.openeo.api.factories;

import eu.openeo.api.OutputFormatsApiService;
import eu.openeo.api.impl.OutputFormatsApiServiceImpl;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class OutputFormatsApiServiceFactory {
    private final static OutputFormatsApiService service = new OutputFormatsApiServiceImpl();

    public static OutputFormatsApiService getOutputFormatsApi() {
        return service;
    }
}
