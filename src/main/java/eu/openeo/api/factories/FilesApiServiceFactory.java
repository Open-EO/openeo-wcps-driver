package eu.openeo.api.factories;

import eu.openeo.api.FilesApiService;
import eu.openeo.api.impl.FilesApiServiceImpl;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class FilesApiServiceFactory {
    private final static FilesApiService service = new FilesApiServiceImpl();

    public static FilesApiService getFilesApi() {
        return service;
    }
}
