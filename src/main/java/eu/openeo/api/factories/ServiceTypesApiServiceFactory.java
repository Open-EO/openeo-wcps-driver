package eu.openeo.api.factories;

import eu.openeo.api.ServiceTypesApiService;
import eu.openeo.api.impl.ServiceTypesApiServiceImpl;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class ServiceTypesApiServiceFactory {
    private final static ServiceTypesApiService service = new ServiceTypesApiServiceImpl();

    public static ServiceTypesApiService getServiceTypesApi() {
        return service;
    }
}
