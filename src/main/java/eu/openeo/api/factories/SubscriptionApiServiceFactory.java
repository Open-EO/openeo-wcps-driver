package eu.openeo.api.factories;

import eu.openeo.api.SubscriptionApiService;
import eu.openeo.api.impl.SubscriptionApiServiceImpl;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-02-12T13:52:55.621+01:00[Europe/Rome]")
public class SubscriptionApiServiceFactory {
    private final static SubscriptionApiService service = new SubscriptionApiServiceImpl();

    public static SubscriptionApiService getSubscriptionApi() {
        return service;
    }
}
