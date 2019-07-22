package eu.openeo.api.factories;

import eu.openeo.api.UdfRuntimesApiService;
import eu.openeo.api.impl.UdfRuntimesApiServiceImpl;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class UdfRuntimesApiServiceFactory {
    private final static UdfRuntimesApiService service = new UdfRuntimesApiServiceImpl();

    public static UdfRuntimesApiService getUdfRuntimesApi() {
        return service;
    }
}
