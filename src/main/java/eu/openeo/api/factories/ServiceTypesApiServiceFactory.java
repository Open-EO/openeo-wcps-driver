package eu.openeo.api.factories;

import eu.openeo.api.ServiceTypesApiService;
import eu.openeo.api.impl.ServiceTypesApiServiceImpl;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-02-12T13:52:55.621+01:00[Europe/Rome]")
public class ServiceTypesApiServiceFactory {
    private final static ServiceTypesApiService service = new ServiceTypesApiServiceImpl();

    public static ServiceTypesApiService getServiceTypesApi() {
        return service;
    }
}
