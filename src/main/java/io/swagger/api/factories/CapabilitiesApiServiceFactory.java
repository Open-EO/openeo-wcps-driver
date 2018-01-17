package io.swagger.api.factories;

import io.swagger.api.CapabilitiesApiService;
import io.swagger.api.impl.CapabilitiesApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-16T14:36:16.100+01:00")
public class CapabilitiesApiServiceFactory {
    private final static CapabilitiesApiService service = new CapabilitiesApiServiceImpl();

    public static CapabilitiesApiService getCapabilitiesApi() {
        return service;
    }
}
