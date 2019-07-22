package eu.openeo.api.factories;

import eu.openeo.api.ServicesApiService;
import eu.openeo.api.impl.ServicesApiServiceImpl;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class ServicesApiServiceFactory {
    private final static ServicesApiService service = new ServicesApiServiceImpl();

    public static ServicesApiService getServicesApi() {
        return service;
    }
}
