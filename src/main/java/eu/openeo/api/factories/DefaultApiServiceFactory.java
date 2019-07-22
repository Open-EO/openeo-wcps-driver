package eu.openeo.api.factories;

import eu.openeo.api.DefaultApiService;
import eu.openeo.api.impl.DefaultApiServiceImpl;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class DefaultApiServiceFactory {
    private final static DefaultApiService service = new DefaultApiServiceImpl();

    public static DefaultApiService getDefaultApi() {
        return service;
    }
}
