package eu.openeo.api.factories;

import eu.openeo.api.ResultApiService;
import eu.openeo.api.impl.ResultApiServiceImpl;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-02-12T13:52:55.621+01:00[Europe/Rome]")
public class ResultApiServiceFactory {
    private final static ResultApiService service = new ResultApiServiceImpl();

    public static ResultApiService getResultApi() {
        return service;
    }
}
