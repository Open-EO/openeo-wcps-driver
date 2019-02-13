package eu.openeo.api.factories;

import eu.openeo.api.CredentialsApiService;
import eu.openeo.api.impl.CredentialsApiServiceImpl;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-02-12T13:52:55.621+01:00[Europe/Rome]")
public class CredentialsApiServiceFactory {
    private final static CredentialsApiService service = new CredentialsApiServiceImpl();

    public static CredentialsApiService getCredentialsApi() {
        return service;
    }
}
