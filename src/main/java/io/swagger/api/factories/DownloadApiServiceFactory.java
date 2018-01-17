package io.swagger.api.factories;

import io.swagger.api.DownloadApiService;
import io.swagger.api.impl.DownloadApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-16T14:36:16.100+01:00")
public class DownloadApiServiceFactory {
    private final static DownloadApiService service = new DownloadApiServiceImpl();

    public static DownloadApiService getDownloadApi() {
        return service;
    }
}
