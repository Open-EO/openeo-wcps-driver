package eu.openeo.api.factories;

import eu.openeo.api.ResultApiService;
import eu.openeo.api.impl.ResultApiServiceImpl;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class ResultApiServiceFactory {
    private final static ResultApiService service = new ResultApiServiceImpl();

    public static ResultApiService getResultApi() {
        return service;
    }
}
