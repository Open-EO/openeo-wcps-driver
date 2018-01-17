package io.swagger.api.factories;

import io.swagger.api.DataApiService;
import io.swagger.api.impl.DataApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-16T14:36:16.100+01:00")
public class DataApiServiceFactory {
    private final static DataApiService service = new DataApiServiceImpl();

    public static DataApiService getDataApi() {
        return service;
    }
}
