package eu.openeo.api.factories;

import eu.openeo.api.ValidationApiService;
import eu.openeo.api.impl.ValidationApiServiceImpl;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-02-12T13:52:55.621+01:00[Europe/Rome]")
public class ValidationApiServiceFactory {
    private final static ValidationApiService service = new ValidationApiServiceImpl();

    public static ValidationApiService getValidationApi() {
        return service;
    }
}
