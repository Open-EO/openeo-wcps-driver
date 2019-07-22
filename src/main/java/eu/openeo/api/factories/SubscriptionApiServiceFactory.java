package eu.openeo.api.factories;

import eu.openeo.api.SubscriptionApiService;
import eu.openeo.api.impl.SubscriptionApiServiceImpl;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class SubscriptionApiServiceFactory {
    private final static SubscriptionApiService service = new SubscriptionApiServiceImpl();

    public static SubscriptionApiService getSubscriptionApi() {
        return service;
    }
}
