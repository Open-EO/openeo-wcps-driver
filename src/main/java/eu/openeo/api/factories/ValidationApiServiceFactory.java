package eu.openeo.api.factories;

import eu.openeo.api.ValidationApiService;
import eu.openeo.api.impl.ValidationApiServiceImpl;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class ValidationApiServiceFactory {
    private final static ValidationApiService service = new ValidationApiServiceImpl();

    public static ValidationApiService getValidationApi() {
        return service;
    }
}
