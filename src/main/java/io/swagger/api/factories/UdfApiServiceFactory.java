package io.swagger.api.factories;

import io.swagger.api.UdfApiService;
import io.swagger.api.impl.UdfApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-16T14:36:16.100+01:00")
public class UdfApiServiceFactory {
    private final static UdfApiService service = new UdfApiServiceImpl();

    public static UdfApiService getUdfApi() {
        return service;
    }
}
