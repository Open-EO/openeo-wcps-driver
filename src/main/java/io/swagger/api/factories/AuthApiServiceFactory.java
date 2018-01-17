package io.swagger.api.factories;

import io.swagger.api.AuthApiService;
import io.swagger.api.impl.AuthApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-16T14:36:16.100+01:00")
public class AuthApiServiceFactory {
    private final static AuthApiService service = new AuthApiServiceImpl();

    public static AuthApiService getAuthApi() {
        return service;
    }
}
