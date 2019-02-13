package eu.openeo.api.factories;

import eu.openeo.api.WellKnownApiService;
import eu.openeo.api.impl.WellKnownApiServiceImpl;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-02-12T13:52:55.621+01:00[Europe/Rome]")
public class WellKnownApiServiceFactory {
    private final static WellKnownApiService service = new WellKnownApiServiceImpl();

    public static WellKnownApiService getWellKnownApi() {
        return service;
    }
}
