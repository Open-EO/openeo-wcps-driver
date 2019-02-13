package eu.openeo.api.factories;

import eu.openeo.api.UdfRuntimesApiService;
import eu.openeo.api.impl.UdfRuntimesApiServiceImpl;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-02-12T13:52:55.621+01:00[Europe/Rome]")
public class UdfRuntimesApiServiceFactory {
    private final static UdfRuntimesApiService service = new UdfRuntimesApiServiceImpl();

    public static UdfRuntimesApiService getUdfRuntimesApi() {
        return service;
    }
}
