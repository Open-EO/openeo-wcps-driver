package eu.openeo.api.factories;

import eu.openeo.api.CredentialsApiService;
import eu.openeo.api.impl.CredentialsApiServiceImpl;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class CredentialsApiServiceFactory {
    private final static CredentialsApiService service = new CredentialsApiServiceImpl();

    public static CredentialsApiService getCredentialsApi() {
        return service;
    }
}
