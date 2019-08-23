package eu.openeo.api.factories;

import eu.openeo.api.DownloadApiService;
import eu.openeo.api.impl.DownloadApiServiceImpl;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class DownloadApiServiceFactory {
    private final static DownloadApiService service = new DownloadApiServiceImpl();

    public static DownloadApiService getDownloadApi() {
        return service;
    }
}
