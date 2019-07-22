package eu.openeo.api.factories;

import eu.openeo.api.MeApiService;
import eu.openeo.api.impl.MeApiServiceImpl;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class MeApiServiceFactory {
    private final static MeApiService service = new MeApiServiceImpl();

    public static MeApiService getMeApi() {
        return service;
    }
}
