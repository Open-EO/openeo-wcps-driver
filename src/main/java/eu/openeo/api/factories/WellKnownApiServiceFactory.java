package eu.openeo.api.factories;

import eu.openeo.api.WellKnownApiService;
import eu.openeo.api.impl.WellKnownApiServiceImpl;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class WellKnownApiServiceFactory {
    private final static WellKnownApiService service = new WellKnownApiServiceImpl();

    public static WellKnownApiService getWellKnownApi() {
        return service;
    }
}
